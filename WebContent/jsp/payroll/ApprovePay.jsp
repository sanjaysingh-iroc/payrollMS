<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.LinkedHashMap"%>
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
	Map<String, String> hmSalaryDetails = (Map<String, String>)request.getAttribute("hmSalaryDetails");
	if(hmSalaryDetails == null) hmSalaryDetails = new HashMap<String, String>();
	Map<String, Map<String, String>> hmEmpSalary = (Map<String, Map<String, String>>)request.getAttribute("hmEmpSalary");
	if(hmEmpSalary == null) hmEmpSalary = new LinkedHashMap<String, Map<String, String>>();
	List<String> alEmpSalaryDetailsEarning = (List<String>)request.getAttribute("alEmpSalaryDetailsEarning");
	if(alEmpSalaryDetailsEarning == null) alEmpSalaryDetailsEarning = new ArrayList<String>();
	List<String> alEmpSalaryDetailsDeduction = (List<String>)request.getAttribute("alEmpSalaryDetailsDeduction");
	if(alEmpSalaryDetailsDeduction == null) alEmpSalaryDetailsDeduction = new ArrayList<String>();
	Map<String, String> hmLoanAmt = (Map<String, String>)request.getAttribute("hmLoanAmt");
	if(hmLoanAmt == null) hmLoanAmt = new HashMap<String, String>();
	List<String> alLoans = (List<String>) request.getAttribute("alLoans");
	if (alLoans == null) alLoans = new ArrayList<String>();
	Map<String, Map<String, String>> hmEmpLoan = (Map<String, Map<String, String>>) request.getAttribute("hmEmpLoan");
	if(hmEmpLoan == null) hmEmpLoan = new HashMap<String, Map<String, String>>();
	Map<String, String> hmLoanPoliciesMap = (Map<String, String>)request.getAttribute("hmLoanPoliciesMap");
	if(hmLoanPoliciesMap == null) hmLoanPoliciesMap = new HashMap<String, String>();
	LinkedHashMap<String, Map<String, String>> hmTotalSalary = (LinkedHashMap<String, Map<String, String>>) request.getAttribute("hmTotalSalary");
	if(hmTotalSalary == null) hmTotalSalary = new LinkedHashMap<String, Map<String, String>>();
	
	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	
	String pageFrom = (String) request.getAttribute("pageFrom");
	
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
	
	List<List<String>> alPaycycleList = (List<List<String>>) request.getAttribute("alPaycycleList");
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
%>

<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_grade").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
	
});    

$(document).ready(function() {
	$('#lt').DataTable({
		aLengthMenu: [
			[25, 50, 100, 200, -1],
			[25, 50, 100, 200, "All"]
		],
		iDisplayLength: -1,
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ],
        order: [],
		columnDefs: [ {
	      "targets"  : 'no-sort',
	      "orderable": false
	    }]
	});
});

function submitForm(type, strPaycycle) {
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	if(strPaycycle.length>0) {
		paycycle = strPaycycle;
	}
	var strPaycycleDuration = document.getElementById("strPaycycleDuration").value;
	var f_paymentMode = document.getElementById("f_paymentMode").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	if(type == '2' || type == '3') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&paycycle='+paycycle
		+'&strPaycycleDuration='+strPaycycleDuration+'&f_paymentMode='+f_paymentMode+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
	}
	/* if(type == '3') {
		paramValues = paramValues + '&strProcess=Process';
	} */
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage" style="text-align:center;">Processing & Displaying</div></div>');
	$.ajax({
		type : 'POST',
		url: 'ApprovePay.action?f_org='+org+paramValues,
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


function checkRevokeAll() {
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
		var data = $("#frm_ApprovePay").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ApprovePay.action?formType=revoke',
			data: data,
			success: function(result){
	        	$("#divResult").html(result); 
	   		}
		});
		/* document.getElementById("formType").value = "revoke";
		document.frm_ApprovePay.submit(); */
	}		
}

function exportpdf(){
	window.location="ExportExcelReport.action";
}


function getLevelwiseGrade() {
	
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
	
	
function selectall(x,strEmpId){
	var status=x.checked;
	var arr= document.getElementsByName(strEmpId);
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
	var approveAll = document.getElementById("approveAll");		
	var strEmpIds = document.getElementsByName('strEmpIds');
	var cnt = 0;
	var chkCnt = 0;
	for(var i=0;i<strEmpIds.length;i++) {
		cnt++;
		 if(strEmpIds[i].checked) {
			 chkCnt++;
		 }
	}
	if(parseInt(chkCnt) > 0) {
		document.getElementById("unApproveSpan").style.display = 'none';
		document.getElementById("approveSpan").style.display = 'inline';
	} else {
		document.getElementById("unApproveSpan").style.display = 'inline';
		document.getElementById("approveSpan").style.display = 'none';
	}
	
	if(parseInt(cnt) == parseInt(chkCnt) && parseInt(chkCnt) > 0) {
		approveAll.checked = true;
	} else {
		approveAll.checked = false;
	}
}

function approvePayroll(){
	if(confirm('Are you sure, you want to approve payroll of selected employee?')){
		var data = $("#frm_ApprovePay").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ApprovePay.action?formType=approve',
			data: data,
			success: function(result){
	        	$("#divResult").html(result); 
	   		}
		});
		
		/* document.getElementById("formType").value = "approve";
		document.frm_ApprovePay.submit(); */
	}
}



</script>

	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
    <% session.setAttribute(IConstants.MESSAGE, ""); %>
	
		<div class="col-lg-12 col-md-12 col-sm-12 paddingright0 listMenu1" style="padding-left: 0px;">
			<% 	if(alPaycycleList != null && alPaycycleList.size()>0) { 
				for(int i=0; i<alPaycycleList.size(); i++) {
					List<String> innerList = alPaycycleList.get(i);
					String strBgClass = "bg-gray";
					if(uF.parseToInt(innerList.get(2)) == 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
						strBgClass = "bg-green";
					}
			%>
				<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
				<!-- small box -->
					<div class="small-box <%=strBgClass %>">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-check" aria-hidden="true"></i>
						</div>
						<div class="inner" style="padding: 0px 10px; text-align: right;">
							<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
							<div style="margin-top: -5px;">Processed</div>
						</div>
						<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
							<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
							<div style="margin-top: -5px;">Waiting</div>
						</div>
						<a href="javascript:void(0);" style="font-size: 12px;" onclick="submitForm('2', '<%=innerList.get(0) %>');" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>
					</div>
				</div>
				<% } %>
			<% } %>
		</div>
		
	 <div class="box-body" style="width: 100%;padding: 5px; overflow-y: auto;min-height:600px;">
		<s:form theme="simple" name="frm_ApprovePay" id="frm_ApprovePay" action="ApprovePay" method="post">
			<s:hidden name="formType" id="formType" />
			<s:hidden name="pageFrom" id="pageFrom" />
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
								<p style="padding-left: 5px;">Duration</p>
								<s:select theme="simple" name="strPaycycleDuration" id="strPaycycleDuration" listKey="paycycleDurationId" listValue="paycycleDurationName" onchange="submitForm('2', '');" list="paycycleDurationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Payment Mode</p>
								<s:select theme="simple" name="f_paymentMode" id="f_paymentMode" listKey="payModeId" listValue="payModeName" headerKey="-1" headerValue="All Modes" onchange="submitForm('2', '');" list="paymentModeList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organisation</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1', '');" list="organisationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" onchange="submitForm('2', '');" list="paycycleList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
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
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""  onchange="getLevelwiseGrade();" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                <p style="padding-left: 5px;">Grade</p>
                                <div id="myGrade">
                                	<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"  />
                                </div>
                        		</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Process" class="btn btn-primary" onclick="submitForm('3', '');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" value="Reset" class="btn" onclick="JavaScript:window.location.href = window.location.href;"/>
							</div>
						</div>
					</div>
				</div>
			</div>	
			
			<%-- <%
			String strProcess = (String) request.getAttribute("strProcess");
			int nEmpCnt = uF.parseToInt((String) request.getAttribute("nEmpCnt"));
			String strTimeMsg = (String) request.getAttribute("strTimeMsg");
			if((strProcess == null || strProcess.trim().equals("") 
					|| strProcess.trim().equalsIgnoreCase("null")) && nEmpCnt > 0) {
			%>
				<div class="row row_without_margin">
					<div class="col-lg-12">
						<%=strTimeMsg %>
					</div>
				</div>	
			<%} else { %> --%>
			
				<div class="row row_without_margin">
					<div class="col-lg-6">
						<span id="unSendSpan" style="display: none;">
							<input type="button" name="unSend" class="btn btn-danger disabled" value="Revoke & Open Time Entries" onclick="alert('Please select employee or approve time entries.');"/>
						</span>
						<span id="sendSpan">
							<input type="button" value="Revoke & Open Time Entries" name="revokeSubmit" class="btn btn-danger" onclick="revokeClockEntries();"/>
						</span>
						<span id="unApproveSpan" style="display: none;">
							<input type="button" name="Submit" class="btn btn-primary disabled" value="Approve Payroll" onclick="alert('Please select employee or approve time entries.');"/>
						</span>
						<span id="approveSpan">
							<input type="button" value="Approve Payroll" name="Submit" class="btn btn-primary" onclick="approvePayroll();"/>
						</span>
					</div>
					<div class="col-lg-6">
						<a href="javascript:void(0)" title="Export to Excel" onclick="exportpdf();" class="pull-right"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
					</div>
				</div>
	
				<%-- <% if(pageFrom != null && pageFrom.equals("THREESTEP")) { %>
					<span style="float: right; margin-right: 20px;"><a href="ApproveAttendance.action?pageFrom=<%=pageFrom %>"> <%="< Back" %> </a> &nbsp; <b>Step 2</b> &nbsp; <a href="PayPayroll.action?pageFrom=<%=pageFrom %>"><input type="button" class="input_button" value="<%="Next >" %>"/></a></span>
				<% } %> --%>
				
				<%
				String strDiv = "width:100%; float:left;";
				if (hmSalaryDetails != null && hmSalaryDetails.size() > 0) {
					strDiv = "width:100%; overflow: scroll; height: 600px;";
				}
				%>
				<div class="clr margintop20"></div>
				<div style="<%=strDiv %>">
					<input type="hidden" name="approvePC" id="approvePC" value="<%=request.getParameter("paycycle")%>" />
					<table class="table table-bordered" id ="lt" ><!-- id ="lt" -->
						<thead>
							<tr>
								<th class="alignCenter no-sort" nowrap>Revoke<br/><input type="checkbox" name="revokeAll" id="revokeAll" onclick="revokeAllEmp(this,'revokeEmpId')" checked="checked"/></th>
								<th class="alignCenter" nowrap>Employee Code</th>
								<th class="alignCenter" nowrap>Employee Name</th>
							<!-- ===start parvez date: 27-12-2022=== -->
							<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){ %>	
								<th class="alignCenter" nowrap>Work Location</th>
							<% } %>	
							<!-- ===end parvez date: 27-12-2022=== -->	
								<th class="alignCenter no-sort" nowrap>Approve<br/><input type="checkbox" name="approveAll" id="approveAll" onclick="selectall(this,'strEmpIds')" checked="checked"/></th>
								<th class="alignLeft" nowrap>Payment Mode</th>
								<th class="alignCenter" nowrap>Total Days</th>
								<th class="alignCenter" nowrap>Paid Days</th>
								<th class="alignCenter" nowrap>Present</th>
								<th class="alignCenter" nowrap>Leaves</th>
								<th class="alignCenter" nowrap>Absent/Unpaid</th>
								<th class="alignCenter" nowrap>Net Pay</th>
								<th class="alignCenter" nowrap>Gross Pay</th>
								<%
								alInnerExport.add(new DataStyle("Payroll for paycycle " + request.getAttribute("strD1") + " - " + request.getAttribute("strD2"),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){
									alInnerExport.add(new DataStyle("Work Location", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								}
								alInnerExport.add(new DataStyle("Approve", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Payment Mode", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Total Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Paid Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Present", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Leaves", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Absent/Unpaid", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Net Pay", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Gross Pay", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								
								for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) {
									alInnerExport.add(new DataStyle((hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i))) + "(+)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									//System.out.println("Earning Head===>"+hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i))+"----head Id===>"+alEmpSalaryDetailsEarning.get(i));
								%>
									<th class="alignCenter" nowrap>
										<%=(String) hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i))%>
										<br/>(+)
									</th>
								<%
								}
								for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
									if (uF.parseToInt(alEmpSalaryDetailsDeduction.get(i)) == IConstants.LOAN && hmEmpLoan != null) {
										for (int l = 0; l < alLoans.size(); l++) {
											alInnerExport.add(new DataStyle((hmLoanPoliciesMap.get(alLoans.get(l))) + "(-)", Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY));
								%>
											<th class="alignCenter" nowrap>
												<%=hmLoanPoliciesMap.get(alLoans.get(l))%>
												<br/>(-)
											</th>
								<%
										}
									} else {
										alInnerExport.add(new DataStyle((hmSalaryDetails.get(alEmpSalaryDetailsDeduction.get(i))) + "(-)",Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										//System.out.println("Deduction Head===>"+hmSalaryDetails.get(alEmpSalaryDetailsEarning.get(i))+"----head Id===>"+alEmpSalaryDetailsEarning.get(i));
								%>
										<th class="alignCenter" nowrap>
											<%=hmSalaryDetails.get(alEmpSalaryDetailsDeduction.get(i))%>
											<br/>(-)
										</th>
							<%
									}
								}
							%>
							</tr>
						</thead>
						<%
							reportListExport.add(alInnerExport);
						%>
						<tbody>
						<%
								double dblNet = 0;
								double dblGross = 0;
								Map<String, String> totalSalaryHead = new HashMap<String, String>();
							
								int nEmpSize = alEmp.size();
								for (int i = 0; i < nEmpSize; i++) {
									Map<String, String> hmEmpPay = (Map<String, String>) alEmp.get(i);
									if(hmEmpPay == null) hmEmpPay = new HashMap<String, String>();
									String strEmpId = hmEmpPay.get("EMP_ID");
									String strSalEffectiveDate = hmEmpPay.get("SAL_EFFECTIVE_DATE");
									//System.out.println("strSalEffectiveDate=="+strSalEffectiveDate);
									Map<String, String> hmInner = (Map<String, String>) hmTotalSalary.get(strEmpId+"_"+strSalEffectiveDate);
									if (hmInner == null) hmInner = new HashMap<String, String>();
									
									dblNet += uF.parseToDouble((String) hmInner.get("NET"));
									dblGross += uF.parseToDouble((String) hmInner.get("GROSS"));
									
									alInnerExport = new ArrayList<DataStyle>();
									alInnerExport.add(new DataStyle(hmEmpPay.get("EMPCODE"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(hmEmpPay.get("EMP_NAME"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){
										alInnerExport.add(new DataStyle(hmEmpPay.get("EMP_WLOCATION_NAME"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									}
									alInnerExport.add(new DataStyle("Pending", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(hmEmpPay.get("EMP_PAYMENT_MODE"), Element.ALIGN_CENTER, "NEW_ROMAN",6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"))), Element.ALIGN_CENTER, "NEW_ROMAN",6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(hmEmpPay.get("EMP_ABSENT_DAYS"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmInner.get("NET"))), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
									alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmInner.get("GROSS"))), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							%>
									<tr>
										<td class="alignCenter"><input type="checkbox" name="revokeEmpId" onclick="checkRevokeAll();" style="width:10px; height:10px" value="<%=strEmpId+"_"+strSalEffectiveDate%>" checked="checked"/></td>
										<td class="alignCenter" nowrap><%=hmEmpPay.get("EMPCODE")%></td>
										<td class="alignLeft" nowrap><%=hmEmpPay.get("EMP_NAME")%></td>
										<% if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){ %>
											<td class="alignLeft" nowrap><%=hmEmpPay.get("EMP_WLOCATION_NAME")%></td>
										<% } %>
										<td class="alignCenter"><input type="checkbox" name="strEmpIds" onclick="checkAll();" style="width:10px; height:10px" value="<%=strEmpId+"_"+strSalEffectiveDate%>" checked="checked"/></td>
										<td class="alignCenter"><input type="hidden" name="paymentMode" value="<%=uF.parseToInt(hmEmpPay.get("EMP_PAYMENT_MODE_ID"))%>"/><%=hmEmpPay.get("EMP_PAYMENT_MODE") %></td>
										<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_TOTAL_DAYS"))%></td>
										<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_DAYS"))%></td>
										<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_PRESENT_DAYS"))%></td>
										<td class="alignCenter" nowrap="nowrap"><%=uF.parseToDouble(hmEmpPay.get("EMP_PAID_LEAVES"))%></td>
										<td class="alignCenter"><%=uF.parseToDouble(hmEmpPay.get("EMP_ABSENT_DAYS"))%></td>
										<td class="alignRight" nowrap="nowrap"><%= uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmInner.get("NET")))))%></td>
										<td class="alignRight" nowrap="nowrap"><%=  uF.formatIntoOneDecimal(uF.parseToDouble(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmInner.get("GROSS")))))%></td>
										<%
											for (int j = 0; j < alEmpSalaryDetailsEarning.size(); j++) {
												String strAmount = hmInner.get(alEmpSalaryDetailsEarning.get(j));
		
												double earningHead = uF.parseToDouble(hmInner.get(alEmpSalaryDetailsEarning.get(j))) + uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsEarning.get(j)));
												totalSalaryHead.put(alEmpSalaryDetailsEarning.get(j), earningHead + "");
												
												alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount)), Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY));
												//System.out.println("salary head id ===>> " + alEmpSalaryDetailsEarning.get(j)+"-----strAmount=="+strAmount);
											%>
												<td class="alignRight"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount))%></td>
											<%
											}
											for (int k = 0; k < alEmpSalaryDetailsDeduction.size(); k++) {
												if (uF.parseToInt(alEmpSalaryDetailsDeduction.get(k)) == IConstants.LOAN && hmEmpLoan != null) {
													Map<String,String> hmEmpLoanInner = (Map<String,String>) hmEmpLoan.get(strEmpId+"_"+strSalEffectiveDate);
													if (hmEmpLoanInner == null)hmEmpLoanInner = new HashMap<String,String>();
													
													for (int l = 0; l < alLoans.size(); l++) {
														double deductionHead = uF.parseToDouble(hmEmpLoanInner.get(alEmpSalaryDetailsDeduction.get(k) + "_"
																+ alLoans.get(l))) + uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsDeduction.get(k)));
														totalSalaryHead.put(alEmpSalaryDetailsDeduction.get(k) + "_" + alLoans.get(l), deductionHead + "");
														
														alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String) hmEmpLoanInner.get((String) alLoans.get(l)))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));												
											%>
														<td class="alignRight"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(hmEmpLoanInner.get(alLoans.get(l))))%></td>
											<%
													}
												} else {
													String strAmount = (String) hmInner.get(alEmpSalaryDetailsDeduction.get(k));
													double deductionHead = uF.parseToDouble(strAmount) + uF.parseToDouble(totalSalaryHead.get(alEmpSalaryDetailsDeduction.get(k)));
													totalSalaryHead.put(alEmpSalaryDetailsDeduction.get(k), deductionHead + "");
													
													alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount)), Element.ALIGN_CENTER,"NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
											%>
													<td class="alignRight"><%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(strAmount))%></td>
										<%
												}
											}
										%>
									</tr>							
								<%
									reportListExport.add(alInnerExport);
								} 
								
								alInnerExport = new ArrayList<DataStyle>();
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_WORK_LOCATION_IN_APPROVE_PAY_AND_PAY))){
									alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								}
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle("Total", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition), Math.round(dblNet)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
								alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition), Math.round(dblGross)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
		
								for (int i = 0; i < alEmpSalaryDetailsEarning.size(); i++) {
									alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(uF.showData(totalSalaryHead.get(alEmpSalaryDetailsEarning.get(i)), "0"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
								}
		
								for (int i = 0; i < alEmpSalaryDetailsDeduction.size(); i++) {
		
									if (uF.parseToInt((String) alEmpSalaryDetailsDeduction.get(i)) == IConstants.LOAN && hmEmpLoan != null) {
										for (int l = 0; l < alLoans.size(); l++) {
		
											alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(uF.showData(totalSalaryHead.get((String) alEmpSalaryDetailsDeduction.get(i) + "_" + (String) alLoans.get(l)), "0"))),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
										}
									} else {
										alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble(uF.showData(totalSalaryHead.get((String) alEmpSalaryDetailsDeduction.get(i)), "0"))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0","0", BaseColor.LIGHT_GRAY));
									}
								}
								reportListExport.add(alInnerExport);
							
							%>
						</tbody>
					</table>
				</div>
			<%-- <%} %> --%>
		</s:form>
		<% session.setAttribute("reportListExport", reportListExport); %>
	</div>

<script type="text/javascript">
checkRevokeAll();
checkAll();
</script>