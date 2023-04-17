<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	UtilityFunctions uF = new UtilityFunctions(); 
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	List<Map<String,String>> alEmp = (List<Map<String,String>>) request.getAttribute("alEmp");
	if(alEmp == null) alEmp = new ArrayList<Map<String,String>>();
	
	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	
	String pageFrom = (String) request.getAttribute("pageFrom");
	
%>

<script type="text/javascript" charset="utf-8">

$(document).ready( function () {
	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
});

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&paycycle='+paycycle;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ClockEntriesReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
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


/* function submitForm(type){
	if(type == '1'){
		document.getElementById("paycycle").selectedIndex = "0";
	}
	document.getElementById("formType").value = "";
	document.frm_AttendanceReport.submit();
} */

function revokeAllEmp(x,strEmpId){
	var status=x.checked;
	var arr= document.getElementsByName(strEmpId);
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
		document.getElementById("unSendSpan").style.display = 'inline';
		document.getElementById("sendSpan").style.display = 'none';
	}
	
	if(parseInt(cnt) == parseInt(chkCnt) && parseInt(chkCnt) > 0) {
		revokeAll.checked = true;
	} else {
		revokeAll.checked = false;
	}
}

function revokeClockEntries(){
	if(confirm('Are you sure, you want to Revoke & Open Time Entries of selected employee?')){
		var data = $("#frm_AttendanceReport").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ClockEntriesReport.action?formType=revoke',
			data: data,
			success: function(result){
	        	$("#divResult").html(result); 
	   		}
		}); 
		/* document.getElementById("formType").value = "revoke";
		document.frm_AttendanceReport.submit(); */
	}		
}

function exportpdf(){
	window.location="ExportExcelReport.action";
}

</script>

	<%-- <div>		
		<% if(pageFrom != null && pageFrom.equals("THREESTEP")) { %>
			<span style="float: right; margin-right: 50px;"><b>Step 1</b> &nbsp; <a href="ApprovePay.action?pageFrom=<%=pageFrom %>"><input type="button" class="input_button" value="<%="Next >" %>" style="margin: 0px;"/></a></span>
		<% } %>
	</div> --%>

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
    <% session.setAttribute(IConstants.MESSAGE, ""); %>
	
	<!-- /.box-header -->
          <div class="box-body" style="width: 100%;padding: 5px; overflow-y: auto;min-height:600px;">
		<s:form theme="simple" name="frm_AttendanceReport" id="frm_AttendanceReport" action="ClockEntriesReport" method="post">
			<s:hidden name="formType" id="formType" />
			<s:hidden name="pageFrom" id="pageFrom" />
			<div class="box box-default">  <!-- collapsed-box -->
				<%-- <div class="box-header with-border">
				    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
				    <div class="box-tools pull-right">
				        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
				        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				    </div>
				</div> --%>
				<div class="box-body" style="padding: 5px; overflow-y: auto;">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter" aria-hidden="true"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organisation</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"
								onchange="submitForm('1');" list="organisationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName"
								 headerKey="" headerValue="Select Paycycle" onchange="submitForm('2');" list="paycycleList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId"
								listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" value="Reset" class="btn btn-info" onclick="submitForm('1');"/>
							</div>
						</div>
					</div>
				</div>
			</div>	
			<div class="row row_without_margin">
				<div class="col-lg-6">
					<span id="unSendSpan" style="display: none;">
						<input type="button" name="unSend" class="btn btn-info" value="Revoke & Open Time Entries" onclick="alert('Please select employee or approve time entries.');"/>
					</span>
					<span id="sendSpan">
						<input type="button" value="Revoke & Open Time Entries" name="revokeSubmit" class="btn btn-primary" onclick="revokeClockEntries();"/>
					</span>
				</div>
				<div class="col-lg-6">
					<p style="font-size: 14px;" class="pull-right">
						<a href="javascript:void(0)" title="Export to Excel" class="excel" onclick="exportpdf();">&nbsp;&nbsp;</a>
	            	</p>
				</div>
			</div>	
			<div class="clr margintop20"></div>
			<div>
				<table class="table table-bordered" id="lt">
					<thead>
						<tr>
							<th class="alignCenter" nowrap>Revoke<br/><input type="checkbox" name="revokeAll" id="revokeAll" onclick="revokeAllEmp(this,'revokeEmpId')" checked="checked"/></th>
							<th class="alignCenter" nowrap>Employee Code</th>
							<th class="alignCenter" nowrap>Employee Name</th>
							<th class="alignLeft" nowrap>Payment Mode</th>
							<th class="alignCenter" nowrap>Total Days</th>
							<th class="alignCenter" nowrap>Paid Days</th>
							<th class="alignCenter" nowrap>Present</th>
							<th class="alignCenter" nowrap>Leaves</th>
							<th class="alignCenter" nowrap>Absent/Unpaid</th>
						<%
							alInnerExport.add(new DataStyle("Approved Atendance Report " + request.getAttribute("strD1") + " - " + request.getAttribute("strD2"),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Payment Mode", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Total Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Paid Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Present", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Leaves", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Absent/Unpaid", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
						%>
						</tr>
					</thead>
					<%
						reportListExport.add(alInnerExport);
					%>
					<tbody>
					<%
						int nEmpSize = alEmp.size();
						for (int i = 0; i < nEmpSize; i++) {
							Map<String, String> hmEmpPay = (Map<String, String>) alEmp.get(i);
							if(hmEmpPay == null) hmEmpPay = new HashMap<String, String>();
							String strEmpId = hmEmpPay.get("EMP_ID");
							
							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(hmEmpPay.get("EMPCODE"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(hmEmpPay.get("EMP_NAME"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(hmEmpPay.get("EMP_PAYMENT_MODE"), Element.ALIGN_CENTER, "NEW_ROMAN",6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"))), Element.ALIGN_CENTER, "NEW_ROMAN",6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(hmEmpPay.get("EMP_ABSENT_DAYS"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
					%>
							<tr>
								<td class="alignCenter"><input type="checkbox" name="revokeEmpId" onclick="checkRevokeAll();" style="width:10px; height:10px" value="<%=strEmpId%>" checked="checked"/></td>
								<td class="alignCenter" nowrap><%=hmEmpPay.get("EMPCODE")%></td>
								<td class="alignLeft" nowrap><%=hmEmpPay.get("EMP_NAME")%></td>
								<td class="alignCenter"><input type="hidden" name="paymentMode" value="<%=uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID"))%>"/><%=hmEmpPay.get("EMP_PAYMENT_MODE") %></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"))%></td>
								<td class="alignCenter" nowrap="nowrap"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"))%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_ABSENT_DAYS"))%></td>
							</tr>							
						<%
							reportListExport.add(alInnerExport);
						} 
						%>
					</tbody>
				</table>
			</div>
		</s:form>
		<% session.setAttribute("reportListExport", reportListExport); %>
	</div>
				

	<script type="text/javascript">
		checkRevokeAll();
	</script>
	
	<script>
	$(function(){
		$("#lt").DataTable({
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
	
	</script>