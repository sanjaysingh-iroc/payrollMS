<%@page import="java.util.Iterator"%>
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

<style> 
	.listMenu1 .icon .fa {
		font-size: 45px;
		vertical-align: top;
		margin-top: 20px;
	}
	
</style>

<%
	UtilityFunctions uF = new UtilityFunctions(); 
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	List<String> alEmp = (List<String>)request.getAttribute("alEmp");
	if(alEmp == null) alEmp = new ArrayList<String>();
//===start parvez date: 22-11-2022===	
	//List<String> alEmpJoinDate = (List<String>)request.getAttribute("hmEmpNameMap");
	List<String> alEmpJoinDate = (List<String>)request.getAttribute("alEmpJoinDate");
	if(alEmpJoinDate == null) alEmpJoinDate = new ArrayList<String>();
//===end parvez date: 22-11-2022===	
	Map<String, Map<String, String>> hmEmpData = (Map<String, Map<String, String>>)request.getAttribute("hmEmpData");
	if(hmEmpData == null) hmEmpData = new HashMap<String, Map<String, String>>();
	Map<String, String> hmPaymentModeMap = (Map<String, String>) request.getAttribute("hmPaymentModeMap");
	if(hmPaymentModeMap == null) hmPaymentModeMap = new HashMap<String, String>();
	Map<String, String> hmTotalDays = (Map<String, String>)request.getAttribute("hmTotalDays");   
	if(hmTotalDays==null) hmTotalDays = new HashMap<String, String>();
	Map<String, String> hmEmpJoiningMap = (Map<String, String>)request.getAttribute("hmEmpJoiningMap");   
	if(hmEmpJoiningMap==null) hmEmpJoiningMap = new HashMap<String, String>();
	Map<String, String> hmPaidDays = (Map<String, String>) request.getAttribute("hmPaidDays");
	if(hmPaidDays == null) hmPaidDays = new HashMap<String, String>();
	Map<String, String> hmPresentDays = (Map<String, String>) request.getAttribute("hmPresentDays");
	if(hmPresentDays == null) hmPresentDays = new HashMap<String, String>();
	Map<String, Map<String, String>> hmLeaveDays = (Map<String, Map<String, String>>) request.getAttribute("hmLeaveDays");
	if(hmLeaveDays == null) hmLeaveDays = new HashMap<String, Map<String, String>>();
	Map<String, Map<String, String>> hmLeaveTypeDays = (Map<String, Map<String, String>>) request.getAttribute("hmLeaveTypeDays");
	//System.out.println("hmLeaveTypeDays=="+hmLeaveTypeDays.get(73+""));
	if(hmLeaveTypeDays == null) hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
	Map<String, String> hmWoHLeaves = (Map<String, String>) request.getAttribute("hmWoHLeaves");
	if(hmWoHLeaves == null) hmWoHLeaves = new HashMap<String, String>();
	
	List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
	List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
	String pageFrom = (String) request.getAttribute("pageFrom");
	
	Boolean isCalLeaveInAttendanceDependantNo = (Boolean)request.getAttribute("isCalLeaveInAttendanceDependantNo"); 
	Map<String, String> hmAttendanceDependent = (Map<String, String>) request.getAttribute("hmAttendanceDependent");
	if(hmAttendanceDependent == null) hmAttendanceDependent = new HashMap<String, String>();
	Map<String, String> hmUnPaidAbsentDays = (Map<String, String>) request.getAttribute("hmUnPaidAbsentDays");
	if(hmUnPaidAbsentDays == null) hmUnPaidAbsentDays = new HashMap<String, String>();
	
	List<List<String>> alPaycycleList = (List<List<String>>) request.getAttribute("alPaycycleList");
	
	Map hmEmpEffectiveDatesAllData = (Map) request.getAttribute("hmEmpEffectiveDatesAllData");
	if(hmEmpEffectiveDatesAllData == null) hmEmpEffectiveDatesAllData = new HashMap();
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
%>


<script type="text/javascript" charset="utf-8">

$(document).ready( function () {
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
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_grade").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
});


function submitForm(type, strPaycycle) {
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	if(strPaycycle.length>0) {
		paycycle = strPaycycle;
	}
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&paycycle='+paycycle+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ApproveAttendance.action?f_org='+org+paramValues,
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
	document.frm_ApproveAttendance.submit();
} */


function selectall(x,strEmpId){
	var status=x.checked;
	var arr= document.getElementsByName(strEmpId);
	for(var i=0;i<arr.length;i++){
	  	arr[i].checked=status;
	}
	
	if(x.checked == true){
		document.getElementById("unSendSpan").style.display = 'none';
		document.getElementById("sendSpan").style.display = 'block';
	} else {
		document.getElementById("unSendSpan").style.display = 'block';
		document.getElementById("sendSpan").style.display = 'none';
	}
}

function checkAll(){
	var approveAll = document.getElementById("approveAll");		
	var strSendLogin = document.getElementsByName('strEmpIds');
	var cnt = 0;
	var chkCnt = 0;
	for(var i=0;i<strSendLogin.length;i++) {
		cnt++;
		 if(strSendLogin[i].checked) {
			 chkCnt++;
		 }
	 }
	if(parseInt(chkCnt) > 0) {
		document.getElementById("unSendSpan").style.display = 'none';
		document.getElementById("sendSpan").style.display = 'block';
	} else {
		document.getElementById("unSendSpan").style.display = 'block';
		document.getElementById("sendSpan").style.display = 'none';
	}
	
	if(parseInt(cnt) == parseInt(chkCnt) && parseInt(chkCnt) > 0) {
		approveAll.checked = true;
	} else {
		approveAll.checked = false;
	}
}

function approveClockEntries(type){
	if(confirm('Are you sure, you want to approve & close time entries of selected employee?')){
		var data = $("#frm_ApproveAttendance").serialize();
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ApproveAttendance.action?formType=approve',
			data: data,
			success: function(result){
	        	$("#divResult").html(result); 
	   		}
		}); 
		//$.post('ApproveAttendance.action', $('#frm_ApproveAttendance').serialize());
		/* document.getElementById("formType").value = "approve";
		document.frm_ApproveAttendance.submit(); */
		//showLoading(); 
	}		
}

function exportpdf(){
	window.location="ExportExcelReport.action";
}

function showLoading() {
    var div = document.createElement('div');
    var img = document.createElement('img');
    /* img.src = 'loading_bar.GIF'; */
    div.innerHTML = "Please wait...<br />";
    div.style.cssText = 'position: fixed; top: 50%; left: 40%; z-index: 5000; width: 222px; text-align: center; background: #EFEFEF; border: 1px solid #000';
    /* div.appendChild(img); */
    $(div).appendTo('body');
   // document.body.appendChild(div);
    return true;
}

function importAttendance(pageFrom) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Import Attendance');
	if($(window).width() >= 900){
		 $(".modal-dialog").width(900);
	}
	$.ajax({
		url : 'ImportAttendance.action?pageFrom='+pageFrom,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

  
</script>
	<%-- <div>	
		<% if(pageFrom != null && pageFrom.equals("THREESTEP")) { %>
			<span style="float: right; margin-right: 50px;"><b>Step 1</b> &nbsp; <a href="ApprovePay.action?pageFrom=<%=pageFrom %>"><input type="button" class="btn btn-primary" value="<%="Next >" %>" style="margin: 0px;"/></a></span>
		<% } %>
	</div> --%>
	
		<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
       	<%session.setAttribute(IConstants.MESSAGE, ""); %>
       	
       	<% java.util.List couterlist = (java.util.List)session.getAttribute("alReport");
			if(couterlist!=null) {
		%>
			<div style="margin-top: 10px;">
				<table class="table table-border">
					<tbody style='background-color:#EFEFEF;'>
					   <% for (int i=0; i<couterlist.size(); i++) {%>
					    <tr>
					 		<td align="left"><%= couterlist.get(i) %></td>
					    </tr>
					   <% } %>
					</tbody>
				</table>
			</div>
	<%	}
		session.setAttribute("alReport", null);
	%>
		
		<div class="col-lg-12 col-md-12 col-sm-12 listMenu1" style="padding-left: 0px;">
			<% if(alPaycycleList != null && alPaycycleList.size()>0) { 
				for(int i=0; i<alPaycycleList.size(); i++) {
					List<String> innerList = alPaycycleList.get(i);
					String strBgClass = "bg-gray";
					if(uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) == 0) {
						strBgClass = "bg-red";
					} else if(uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) > 0) {
						strBgClass = "bg-yellow";
					} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
						strBgClass = "bg-green";
					}
			%>
				<div class="col-lg-2 col-xs-6 col-sm-12 paddingright0">
				<!-- small box -->
					<div class="small-box <%=strBgClass %>">
						<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
							<i class="fa fa-clock-o" aria-hidden="true"></i>
						</div>
						<div class="inner" style="padding: 0px 10px; text-align: right;">
							<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
							<div style="margin-top: -5px;">Approved</div>
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
		
		
		<div class="box-header with-border" style="float: left; width: 100%;color: #777777; margin-bottom: 10px;">
			<p style="background-color: #FFFF33; padding: 4px; border: 1px solid #cccccc;">
				<strong>Note:</strong> Once you Approve and Close the Time Entries, employees will not be able to either apply nor will the Workflow work.
			</p>
		</div> 
		
		<!-- /.box-header -->
           <div class="box-body" style="width: 100%; padding: 5px; overflow-y: auto; min-height:600px;">
			<s:form theme="simple" name="frm_ApproveAttendance" id="frm_ApproveAttendance" action="ApproveAttendance" method="post">
				<s:hidden name="formType" id="formType"/>
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
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
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
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Grade</p>
									<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2', '');"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" value="Reset" class="btn btn-info" onclick="JavaScript:window.location.href = window.location.href;"/>
								</div>
							</div>
						</div>
					</div>	
				</div>
				<div class="row row_without_margin">
					<div class="col-lg-6 col_no_padding">
						<span id="unSendSpan" style="display: none;">
							<input type="button" name="unSend" class="btn btn-info" value="Approve & Close Time Entries" />
						</span>
						<span id="sendSpan">
							<input type="button" value="Approve & Close Time Entries" name="approveSubmit" class="btn btn-primary" onclick="approveClockEntries('2');"/>
						</span>
					</div>
					<div class="col-lg-6">
						<p style="font-size: 14px;" class="pull-right">
							<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_UPDATE_TIME_ENTRIES_LINK))) { %>
								<a href="UpdateAttendance.action" ><i class="fa fa-pencil-square" aria-hidden="true"></i>Update Time Entries</a>
							<% } %>
			                <a href="javascript:void(0)" onclick="importAttendance('<%=pageFrom %>');"><i class="fa fa-upload" aria-hidden="true"></i>Import Attendance</a>&nbsp;&nbsp;
							<a href="javascript:void(0)" title="Export to Excel" class="excel" onclick="exportpdf();">&nbsp;&nbsp;</a>
		            	</p>
					</div>
				</div>	
				<div class="clr margintop20">
					<table class="table table-bordered" id="lt">
						<thead>
						<tr>
							<th class="alignCenter" nowrap>Approve<br/><input type="checkbox" name="approveAll" id="approveAll" onclick="selectall(this,'strEmpIds')" checked="checked"/></th>
							<th class="alignCenter" nowrap>Employee Code</th>
							<th class="alignCenter" nowrap>Employee Name</th>
							<th class="alignCenter" nowrap>Total Days</th>
							<th class="alignCenter" nowrap>Paid Days</th>
							<th class="alignCenter" nowrap>Present</th>
							<th class="alignCenter" nowrap>Leaves</th>
							<th class="alignCenter" nowrap>Absent/Unpaid</th>
						</tr>
						<%
							alInnerExport.add(new DataStyle("Attendance " + request.getAttribute("strD1") + " - " + request.getAttribute("strD2"),Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Employee Code", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Employee Name", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Total Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Paid Days", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Present", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Leaves", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle("Absent/Unpaid", Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							
							reportListExport.add(alInnerExport);
						%>
					</thead>
					<tbody>
						<%
						int nEmpSize = alEmp.size();
						for (int i = 0; i < nEmpSize; i++) { 
							String strEmpId = (String) alEmp.get(i);
							Map<String, String> hmEmp = hmEmpData.get(strEmpId);
							if(hmEmp == null) hmEmp = new HashMap<String, String>(); 
							
							Map<String,String> hmLeavesType = (Map<String,String>) hmLeaveTypeDays.get(strEmpId);
							if (hmLeavesType == null) hmLeavesType = new HashMap<String,String>();
							
							String strTotalDays = hmTotalDays.get(strEmpId); 
							
							/* if(alEmpJoinDate.contains(strEmpId) && uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId),IConstants.DATE_FORMAT,"dd")) > 1){
							//===start parvez date: 05-12-2022===	
								//strTotalDays = hmPaidDays.get(strEmpId); 
								strTotalDays = uF.parseToDouble(hmPaidDays.get(strEmpId))+uF.parseToDouble(hmUnPaidAbsentDays.get(strEmpId))+"";
							//===end parvez date: 05-12-2022===	
							} */
							
							double dblPresentDays = uF.parseToDouble(hmPresentDays.get(strEmpId));
							boolean isAttendance = uF.parseToBoolean(hmAttendanceDependent.get(strEmpId));
							double nAbsent = 0.0d;
							if(!isAttendance){
								dblPresentDays = 0.0d;
								if(isCalLeaveInAttendanceDependantNo){
									nAbsent = uF.parseToDouble(hmUnPaidAbsentDays.get(strEmpId));
								}
							} else {
								nAbsent = uF.parseToDouble(strTotalDays) - dblPresentDays - uF.parseToDouble(hmLeavesType.get("COUNT"));
							}
							
							String strWoHLeaves = uF.parseToDouble(hmWoHLeaves.get(strEmpId)) > 0.0d && uF.parseToDouble(hmWoHLeaves.get(strEmpId)) > uF.parseToDouble(hmLeavesType.get("COUNT")) ? "("+uF.parseToDouble(hmWoHLeaves.get(strEmpId))+")" : "";
							
							alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(hmEmp.get("EMP_CODE"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(hmEmp.get("EMP_NAME"), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(strTotalDays)), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmPaidDays.get(strEmpId))), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(dblPresentDays), Element.ALIGN_CENTER, "NEW_ROMAN",6, "0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(String.valueOf(uF.parseToDouble(hmLeavesType.get("COUNT")) +" "+strWoHLeaves), Element.ALIGN_CENTER, "NEW_ROMAN", 6,"0", "0", BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(""+((nAbsent < 0) ? 0 : nAbsent), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
							
							reportListExport.add(alInnerExport);
							
							Map hmEmpEffectiveDatesData = (Map)hmEmpEffectiveDatesAllData.get(strEmpId);
							if(hmEmpEffectiveDatesData == null) hmEmpEffectiveDatesData = new HashMap();
							
						%> 
							<tr>
								<td class="alignCenter"><input type="checkbox" name="strEmpIds" onclick="checkAll();" style="width:10px; height:10px" value="<%=strEmpId%>" checked="checked" />
									<input type="hidden" name="strTotalDays" id="strTotalDays" value="<%=strEmpId+"::::"+uF.parseToDouble(strTotalDays) %>"/>
									<input type="hidden" name="strPaidDays" id="strPaidDays" value="<%=strEmpId+"::::"+uF.parseToDouble(hmPaidDays.get(strEmpId)) %>"/>
									<input type="hidden" name="strPresentDays" id="strPresentDays" value="<%=strEmpId+"::::"+dblPresentDays %>"/>
									<input type="hidden" name="strLeaves" id="strLeaves" value="<%=strEmpId+"::::"+uF.parseToDouble(hmLeavesType.get("COUNT")) %>"/>
									<input type="hidden" name="strAbsent" id="strAbsent" value="<%=((nAbsent < 0) ? strEmpId+"::::"+0 : strEmpId+"::::"+nAbsent) %>"/>
									<%
									Iterator it = hmEmpEffectiveDatesData.keySet().iterator();
									while(it.hasNext()) {
										String strEffectiveDate = (String)it.next();
										List innetList = (List)hmEmpEffectiveDatesData.get(strEffectiveDate);
										Map<String, String> hmTotalDaysInn = (Map<String, String>)innetList.get(0);
										Map<String, String> hmPaidDaysInn = (Map<String, String>)innetList.get(1);
										Map<String, String> hmPresentDaysInn = (Map<String, String>)innetList.get(2);
										Map<String, Map<String, String>> hmLeaveDaysInn = (Map<String, Map<String, String>>)innetList.get(3);
										Map<String, Map<String, String>> hmLeaveTypeDaysInn = (Map<String, Map<String, String>>)innetList.get(4);
										Map<String, String> hmWoHLeavesInn = (Map<String, String>)innetList.get(5);
										Map<String, String> hmUnPaidAbsentDaysInn = (Map<String, String>)innetList.get(6);
									
										Map<String,String> hmLeavesTypeInn = (Map<String,String>) hmLeaveTypeDaysInn.get(strEmpId);
										if (hmLeavesTypeInn == null) hmLeavesTypeInn = new HashMap<String,String>();
										
										String strTotalDaysInn = hmTotalDaysInn.get(strEmpId); 
										/* if(alEmpJoinDate.contains(strEmpId) && uF.parseToInt(uF.getDateFormat(hmEmpJoiningMap.get(strEmpId),IConstants.DATE_FORMAT,"dd")) > 1){
										//===start parvez date: 05-12-2022===	 
											//strTotalDaysInn = hmPaidDaysInn.get(strEmpId);
											strTotalDaysInn = uF.parseToDouble(hmPaidDaysInn.get(strEmpId))+uF.parseToDouble(hmUnPaidAbsentDaysInn.get(strEmpId))+"";
										//===end parvez date: 05-12-2022===
										} */
										
										
										double dblPresentDaysInn = uF.parseToDouble(hmPresentDaysInn.get(strEmpId));
										double nAbsentInn = 0.0d;
										if(!isAttendance){
											dblPresentDaysInn = 0.0d;
											if(isCalLeaveInAttendanceDependantNo){
												nAbsentInn = uF.parseToDouble(hmUnPaidAbsentDaysInn.get(strEmpId));
											}
										} else {
											nAbsentInn = uF.parseToDouble(strTotalDaysInn) - dblPresentDaysInn - uF.parseToDouble(hmLeavesTypeInn.get("COUNT"));
										}
										
										String strWoHLeavesInn = uF.parseToDouble(hmWoHLeavesInn.get(strEmpId)) > 0.0d && uF.parseToDouble(hmWoHLeavesInn.get(strEmpId)) > uF.parseToDouble(hmLeavesTypeInn.get("COUNT")) ? "("+uF.parseToDouble(hmWoHLeavesInn.get(strEmpId))+")" : "";
										
									%>
										<input type="hidden" name="strEffectiveDates_<%=strEmpId %>" id="strEffectiveDates_<%=strEmpId %>" value="<%=strEffectiveDate %>"/>
										<input type="hidden" name="strTotalDays_<%=strEmpId %>" id="strTotalDays_<%=strEmpId %>" value="<%=strEffectiveDate+"::::"+uF.parseToDouble(strTotalDaysInn) %>"/>
										<input type="hidden" name="strPaidDays_<%=strEmpId %>" id="strPaidDays_<%=strEmpId %>" value="<%=strEffectiveDate+"::::"+uF.parseToDouble(hmPaidDaysInn.get(strEmpId)) %>"/>
										<input type="hidden" name="strPresentDays_<%=strEmpId %>" id="strPresentDays_<%=strEmpId %>" value="<%=strEffectiveDate+"::::"+dblPresentDaysInn %>"/>
										<input type="hidden" name="strLeaves_<%=strEmpId %>" id="strLeaves_<%=strEmpId %>" value="<%=strEffectiveDate+"::::"+uF.parseToDouble(hmLeavesTypeInn.get("COUNT")) %>"/>
										<input type="hidden" name="strAbsent_<%=strEmpId %>" id="strAbsent_<%=strEmpId %>" value="<%=((nAbsentInn < 0) ? strEffectiveDate+"::::"+0 : strEffectiveDate+"::::"+nAbsentInn) %>"/>
									<% } %>
									
									
								</td>
								<td class="alignCenter" nowrap><%=hmEmp.get("EMP_CODE")%></td>
								<td class="alignLeft" nowrap><%=hmEmp.get("EMP_NAME")%></td>
								<td class="alignCenter"><%=uF.parseToDouble(strTotalDays)%></td>
								<td class="alignCenter"><%=uF.parseToDouble(hmPaidDays.get(strEmpId))%></td>
								<td class="alignCenter"><%=dblPresentDays%></td>
								<td class="alignCenter" nowrap="nowrap"><%=uF.parseToDouble(hmLeavesType.get("COUNT")) +" "+strWoHLeaves%></td>
								<td class="alignCenter"><%=((nAbsent < 0) ? 0 : nAbsent)%></td>
							</tr>
						<%} %>
						<%if (nEmpSize == 0) {%>
							<tr><td colspan="8"><div class="nodata msg" style="width:97%"><span>No employees found.</span></div></td></tr>
						<%}%>
					</tbody>
					</table>
				</div>
			</s:form>
			
			<% session.setAttribute("reportListExport", reportListExport); %>
		
		</div>
 

			

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	        
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">&nbsp;</h4>
	            </div>
	            <div class="modal-body" style="height:350px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>

<script type="text/javascript">
$(function(){
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

</script>
