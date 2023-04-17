<%@page import="java.util.*,com.konnect.jpms.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div id="divResult">

 <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script>

<script type="text/javascript" charset="utf-8">
$(function() {
	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter(); 
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
	$("#f_employeeStatus").multiselect().multiselectfilter();
	
	 $('#lt').DataTable({
		 aLengthMenu: [
   			[25, 50, 100, 200, -1],
   			[25, 50, 100, 200, "All"]
   		],
   		iDisplayLength: -1,
		dom: 'lBfrtip',
		 buttons: [
            'copy',
            {
                extend: 'csv',
                title: 'Comprehensive Salary Report'
            },
            {
                extend: 'excel',
                title: 'Comprehensive Salary Report'
            },
            {
                extend: 'pdf',
                title: 'Comprehensive Salary Report'
            },
            {
                extend: 'print',
                title: 'Comprehensive Salary Report'
            }
        ],
        "bSort": false
	});  
 	
	 
});


function submitForm(type) {
	document.frm_PayPayroll.exportType.value='';
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var strEmployeeStatus = getSelectedValue("f_employeeStatus");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&paycycle='+paycycle+'&strEmployeType='+strEmployeType+'&strEmployeeStatus='+strEmployeeStatus;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PayrollRegisterNew.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
	
}



function downloadPayrollRegisterExcel() {
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var strEmployeeStatus = getSelectedValue("f_employeeStatus");
	var paramValues = "";
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&paycycle='+paycycle+'&strEmployeType='+strEmployeType+'&strEmployeeStatus='+strEmployeeStatus+"&operation=download";
	
	window.location = 'PayrollRegisterNew.action?f_org='+org+paramValues;
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

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmEmpMap = (Map) request.getAttribute("hmEmpMap");
	Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
	Map<String, String> hmPresentDays = (Map<String, String>) request.getAttribute("hmPresentDays");
	if(hmPresentDays == null) hmPresentDays = new HashMap<String, String>();
	
	Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
	if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String, String>>();
	
	Map<String, String> hmWLocation = (Map<String, String>) request.getAttribute("hmWLocation");
	if(hmWLocation == null) hmWLocation = new HashMap<String, String>();
	
	Map<String, String> hmEmpWlocationMap = (Map<String, String>) request.getAttribute("hmEmpWlocationMap");
	if(hmEmpWlocationMap == null) hmEmpWlocationMap = new HashMap<String, String>();
	
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	if(hmEmpCodeDesig==null) hmEmpCodeDesig = new HashMap<String, String>();
	
	Map<String, String> hmCodeDesig = (Map<String, String>) request.getAttribute("hmCodeDesig");
	if(hmCodeDesig == null) hmCodeDesig = new HashMap<String, String>();
	
	Map<String, String> hmEmpGradeMap = (Map<String, String>) request.getAttribute("hmEmpGradeMap");
	if(hmEmpGradeMap == null) hmEmpGradeMap = new HashMap<String, String>();
	
	Map<String, String> hmGradeMap = (Map<String, String>) request.getAttribute("hmGradeMap");
	if(hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
	
	Map<String, String> hmGradeDesig = (Map<String, String>) request.getAttribute("hmGradeDesig");
	if(hmGradeDesig == null) hmGradeDesig = new HashMap<String, String>();
	
	Map hmPayPayroll = (Map) request.getAttribute("hmPayPayroll");
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
	Map hmIsApprovedSalary = (Map) request.getAttribute("hmIsApprovedSalary");
	if (hmIsApprovedSalary == null) hmIsApprovedSalary = new HashMap();
	
	List alEarnings = (List) request.getAttribute("alEarnings");
	List alDeductions = (List) request.getAttribute("alDeductions");
	
	List<String> alESalaryHeads = (List<String>) request.getAttribute("alESalaryHeads");
	List<String> alDSalaryHeads = (List<String>) request.getAttribute("alDSalaryHeads");
	
	Map<String, String> hmEmpEffectiveDateGradeId = (Map<String, String>) request.getAttribute("hmEmpEffectiveDateGradeId");
	if(hmEmpEffectiveDateGradeId == null) hmEmpEffectiveDateGradeId = new HashMap<String, String>();
	
%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size:14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body"
					style="padding: 5px; overflow-y: auto; display: none;">
					<div class="content1">
						<s:form name="frm_PayPayroll" action="PayrollRegisterNew" theme="simple" method="post">
							<s:hidden name="exportType"></s:hidden>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key="" />
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
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
									</div>
									<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
										<p style="padding-left: 5px;">Employee Type</p>
										<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
									</div>
									<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
										<p style="padding-left: 5px;">Employee Status</p>
										<s:select theme="simple" name="f_employeeStatus" id="f_employeeStatus" listKey="statusId" listValue="statusName" list="employeeStatusList" key=""  multiple="true"  />
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
										<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
									</div>
								</div>
							</div>
						</s:form>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
				
			<div class="col-lg-12 col-md-12 col-sm-12 autoWidth paddingright0">
				<a class="fa fa-file-excel-o" style="float: right; margin-right: 30px;" title="Download Payroll Register" href="javascript:void(0);" onclick="downloadPayrollRegisterExcel();">&nbsp;</a>
			</div>

			<% List alReportList = (List) request.getAttribute("alReportList"); 
				if(alReportList == null) alReportList = new ArrayList();
				int salHeadSize = 17;
				/* if(alEarnings != null && alDeductions != null) {
					if(alEarnings.size()<alDeductions.size()) {
						salHeadSize += alDeductions.size();
					} else {
						salHeadSize += alEarnings.size();
						if(alEarnings.size()>0 && alEarnings.contains(""+IConstants.VDA)) {
							salHeadSize += 1;
						}
					}
				} */
			%>
			
			<table class="table table-bordered overflowtable" id="lt">
				<thead>
					<tr>
				    	<% for(int i=0; i<(salHeadSize); i++) { %>
				    		<td>&nbsp;</td>
				    	<% } %>
				    	<td>&nbsp;</td>
			    	</tr>
				</thead>
				
				<tbody>
				<tr>
					<th nowrap="nowrap">Sr. No.</th>
					<th nowrap="nowrap">Emp Code</th>
					<th >Employee Name<br/>&nbsp;Designation</th>
					<!-- <th nowrap="nowrap">Location</th>
					<th nowrap="nowrap">Employee Status</th> -->
					<th nowrap="nowrap">Present Days</th>
					<% //System.out.println("alESalaryHeads ===>> " + alESalaryHeads);
					int salHeadCnt = alESalaryHeads.size();
					int maxSalHeadCnt=11;
					boolean flagEHSR = false;
					if(alESalaryHeads.size()>maxSalHeadCnt) {
						salHeadCnt = maxSalHeadCnt;
						/* if(alEarnings.contains(""+IConstants.VDA)) {
							salHeadCnt = 11;	
						} */
					}
					for (int ii = 0; ii < salHeadCnt; ii++) {
						if(alEarnings.contains(alESalaryHeads.get(ii))) {
							String strEarning = (String)hmSalaryDetails.get(alESalaryHeads.get(ii))+"<br/>(+)";
					%>
						<th nowrap="nowrap"><%=strEarning %></th>
						<% if(alEarnings.contains(""+IConstants.VDA) && ii==2) { %>
							<th nowrap="nowrap"><%="Total" %></th>
						<% } %>
					<% } } %>
					<% if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
						for(int i=0; i<(alDeductions.size()- (alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt)); i++) {
					%>
						<th nowrap="nowrap">&nbsp;</th>
					<% } } %>
					<% if(alESalaryHeads.size()>maxSalHeadCnt) { %>
					<th nowrap="nowrap">&nbsp;</th>
					<th nowrap="nowrap">&nbsp;</th>
					<% flagEHSR = true;
					} else { %>
						<th nowrap="nowrap">Total</th> <!-- Gross -->
						<th nowrap="nowrap">Net</th>
					<% } %>
				</tr>
				
				<% if(flagEHSR) { %>
					<tr>
						<th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th>
						<th>&nbsp;</th>
						<!-- <th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th> -->
						<th nowrap="nowrap">&nbsp;</th>
						<% //System.out.println("alESalaryHeads ===>> " + alESalaryHeads);
						for (int ii = maxSalHeadCnt; ii < alESalaryHeads.size(); ii++) {
							if(alEarnings.contains(alESalaryHeads.get(ii))) {
								String strEarning = (String)hmSalaryDetails.get(alESalaryHeads.get(ii))+"<br/>(+)";
						%>
							<th nowrap="nowrap"><%=strEarning %></th>
						<% } } %>
						<%-- <% if(alEarnings.contains(""+IConstants.VDA)) { %>
							<th nowrap="nowrap">&nbsp;</th>
						<% } %> --%>
						<% if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
							for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } else if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt)) < maxSalHeadCnt) {
							for(int i=0; i<(maxSalHeadCnt-(alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } %>
						<th nowrap="nowrap">Total</th> <!-- Gross -->
						<th nowrap="nowrap">Net</th>
					</tr>
				<% } %>
					
				<tr>
					<th nowrap="nowrap">&nbsp;</th>
					<th nowrap="nowrap">&nbsp;</th>
					<th nowrap="nowrap">&nbsp;</th>
					<!-- <th nowrap="nowrap">&nbsp;</th>
					<th nowrap="nowrap">&nbsp;</th> -->
					<th nowrap="nowrap">&nbsp;</th>
					<%
					for (int ii = 0; ii < alDSalaryHeads.size(); ii++) {
						if(alDeductions.contains(alDSalaryHeads.get(ii))) {
							String strDeduction = (String)hmSalaryDetails.get(alDSalaryHeads.get(ii))+"<br/>(-)";
					%>
						<th nowrap="nowrap"><%=strDeduction %></th>
					<% } } %>
					<% if(alEarnings.size()>0 && (alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt) > alDeductions.size()) {
						for(int i=0; i<((alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt)-alDeductions.size()); i++) {
					%>
						<th nowrap="nowrap">&nbsp;</th>
					<% } } %>
					<th nowrap="nowrap">Total</th>
					<th nowrap="nowrap"></th>
				</tr>
				
				<% Set set = hmPayPayroll.keySet();
					Iterator it = set.iterator();
					double totGross = 0.0d;
					double totNet = 0.0d;
					Map<String, String> hmTotAmtSalHead = new HashMap<String, String>();
					List<String> alEmpId = new ArrayList<String>();
					int cnt = 0;
					
					double dblBasicFDAVDATotOfTotal = 0.0d;
					double dblGrossAllTotal = 0.0d;
					double dblNetAllTotal = 0.0d;
					double dblDeductionAllTotal = 0.0d;
					while(it.hasNext()) {
						String strEmpIdWithSalEffectiveDate = (String)it.next();
						String[] strTmp = strEmpIdWithSalEffectiveDate.split("_");
						String strEmpId = strTmp[0];
						cnt++;
						Map hmPayroll = (Map)hmPayPayroll.get(strEmpIdWithSalEffectiveDate);
						if(hmPayroll==null)hmPayroll=new HashMap();
						
						Map<String, String> hm = hmEmpHistory.get(strEmpId);
						
						String strWLocation = uF.showData((String) hmWLocation.get((String)hmEmpWlocationMap.get(strEmpId)),"");
						if(hm != null && uF.parseToInt(hm.get("EMP_WLOCATION")) > 0) {
							strWLocation = uF.showData(hmWLocation.get(hm.get("EMP_WLOCATION")), "");
						}
						
						String strDesig = uF.showData((String) hmCodeDesig.get((String)hmEmpCodeDesig.get(strEmpId)),"");
						String strGrade = uF.showData((String) hmGradeMap.get((String)hmEmpGradeMap.get(strEmpId)),"");
						//System.out.println("hmEmpEffectiveDateGradeId --->> " + hmEmpEffectiveDateGradeId);
						if(hmEmpEffectiveDateGradeId != null && uF.parseToInt(hmEmpEffectiveDateGradeId.get(strEmpIdWithSalEffectiveDate)) > 0) {
							strDesig = uF.showData(hmCodeDesig.get(hmGradeDesig.get(hmEmpEffectiveDateGradeId.get(strEmpIdWithSalEffectiveDate))), "");
							strGrade = uF.showData(hmGradeMap.get(hmEmpEffectiveDateGradeId.get(strEmpIdWithSalEffectiveDate)), "");
						} else  if(hm != null && uF.parseToInt(hm.get("EMP_GRADE")) > 0) {
							strDesig = uF.showData(hmCodeDesig.get(hmGradeDesig.get(hm.get("EMP_GRADE"))), "");
							strGrade = uF.showData(hmGradeMap.get(hm.get("EMP_GRADE")), "");
						}
				%>
				<tr>
					<td nowrap="nowrap"><%=cnt %></td>
					<td nowrap="nowrap"><%=hmEmpCode.get(strEmpId) %></td>
					<td nowrap="nowrap"><%=hmEmpMap.get(strEmpId) %>,&nbsp;&nbsp;<br/>[<%=strDesig+" ("+strGrade+")" %>]</td>
					<%-- <td nowrap="nowrap"><%=strWLocation %></td>
					<td nowrap="nowrap"><%=hmPayroll.get("EMP_STATUS") %></td> --%>
					<td nowrap="nowrap"><%=hmPresentDays.get(strEmpIdWithSalEffectiveDate) %></td>
				
				<%	double dblBasicFDAVDATot = 0.0d;
					for (int i = 0; i < salHeadCnt; i++) {
						if(alEarnings.contains(alESalaryHeads.get(i))) {
							double salHeadAmt = uF.parseToDouble((String)hmPayroll.get(alESalaryHeads.get(i)));
							double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get(alESalaryHeads.get(i)));
							salHeadTotAmt += salHeadAmt;
							hmTotAmtSalHead.put(alESalaryHeads.get(i), ""+salHeadTotAmt);
							if(i<=2) {
								dblBasicFDAVDATot += salHeadAmt;
							}
					%>
						<td><%=uF.showData((String)hmPayroll.get(alESalaryHeads.get(i)), "0.00") %></td>
						<% if(alEarnings.contains(""+IConstants.VDA) && i==2) { %>
							<th><%=uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblBasicFDAVDATot), "0.00") %></th>
						<% } %>
					<% } } 
					dblBasicFDAVDATotOfTotal += dblBasicFDAVDATot;
					%>
					<% if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
						for(int i=0; i<(alDeductions.size()- (alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt)); i++) {
					%>
						<td nowrap="nowrap">&nbsp;</td>
					<% } } %>
					
					<% if(flagEHSR) { %>
						<th>&nbsp;</th>
						<th>&nbsp;</th>
					<% } else { %>
						<th><%=uF.showData((String)hmPayroll.get("GROSS"), "0.00") %></th>
						<th><%=uF.showData((String)hmPayroll.get("NET"), "0.00") %></th>
					<% } %>
					</tr>
					
					<% if(flagEHSR) { %>
						<tr>
						<td nowrap="nowrap">&nbsp;</td>
						<td nowrap="nowrap">&nbsp;</td>
						<td nowrap="nowrap">&nbsp;</td>
						<%-- <td nowrap="nowrap"><%=strWLocation %></td>
						<td nowrap="nowrap"><%=hmPayroll.get("EMP_STATUS") %></td> --%>
						<td nowrap="nowrap">&nbsp;</td>
						<%		
							for (int i = maxSalHeadCnt; i < alESalaryHeads.size(); i++) {
								if(alEarnings.contains(alESalaryHeads.get(i))) {
									double salHeadAmt = uF.parseToDouble((String)hmPayroll.get(alESalaryHeads.get(i)));
									double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get(alESalaryHeads.get(i)));
									salHeadTotAmt += salHeadAmt;
									hmTotAmtSalHead.put(alESalaryHeads.get(i), ""+salHeadTotAmt);
							%>
							<td><%=uF.showData((String)hmPayroll.get(alESalaryHeads.get(i)), "0.00") %></td>
						<% } } %>
						<%-- <% if(alEarnings.contains(""+IConstants.VDA)) { %>
							<th nowrap="nowrap">&nbsp;</th>
						<% } %> --%>
						<% if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
							for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } else if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt)) < maxSalHeadCnt) {
							for(int i=0; i<(maxSalHeadCnt-(alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } %>
						<th><%=uF.showData((String)hmPayroll.get("GROSS"), "0.00") %></th>
						<th><%=uF.showData((String)hmPayroll.get("NET"), "0.00") %></th>
						</tr>
					<% } %>
					
					<tr>
						<th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th>
						<!-- <th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th> -->
						<th nowrap="nowrap">&nbsp;</th>
					<%	double dblDeductionTot = 0.0d;
						for (int i = 0; i < alDSalaryHeads.size(); i++) {
							if(alDeductions.contains(alDSalaryHeads.get(i))) {
				    			double salHeadAmt = uF.parseToDouble((String)hmPayroll.get(alDSalaryHeads.get(i)));
								double salHeadTotAmt = uF.parseToDouble(hmTotAmtSalHead.get(alDSalaryHeads.get(i)));
								salHeadTotAmt += salHeadAmt;
								hmTotAmtSalHead.put(alDSalaryHeads.get(i), ""+salHeadTotAmt);
								dblDeductionTot += salHeadAmt;
						%>
							<td><%=uF.showData((String)hmPayroll.get(alDSalaryHeads.get(i)), "0.00") %></td>
						<% } } %>
						<% if(alEarnings.size()>0 && (alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt) > alDeductions.size()) {
							for(int i=0; i<((alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt)-alDeductions.size()); i++) {
						%>
							<td nowrap="nowrap">&nbsp;</td>
						<% } } %>
						<th><%=uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblDeductionTot), "0.00") %></th>
						<td nowrap="nowrap">&nbsp;</td>
			    	</tr>
			    	<% 	dblGrossAllTotal += uF.parseToDouble((String)hmPayroll.get("GROSS"));
			    		dblNetAllTotal += uF.parseToDouble((String)hmPayroll.get("NET"));
			    		dblDeductionAllTotal += dblDeductionTot;
			    	%>
			    	<tr>
				    	<% for(int i=0; i<salHeadSize; i++) { %>
				    		<td>&nbsp;</td>
				    	<% } %>
				    	<td nowrap="nowrap">&nbsp;</td>
			    	</tr>
			    	<% } %>
			    	
			    	
					<tr>
						<th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th>
						<!-- <th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th> -->
						<th nowrap="nowrap">&nbsp;</th>
						<th align="right">Total:</th>
						<% for (int i = 0; i < salHeadCnt; i++) {
							if(alEarnings.contains(alESalaryHeads.get(i))) { %>
							<th><%=uF.showData(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotAmtSalHead.get(alESalaryHeads.get(i)))), "0.00") %></th>
							<% if(i==2) { %>
								<th><%=uF.showData(uF.formatIntoTwoDecimalWithOutComma(dblBasicFDAVDATotOfTotal), "0.00") %></th>
							<% } %>
						<% } } %>
						<% if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
							for(int i=0; i<(alDeductions.size()- (alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt)); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } %>
						<% if(flagEHSR) { %>
							<th>&nbsp;</th>
							<th>&nbsp;</th>
						<% } else { %>
							<th><%=uF.formatIntoTwoDecimalWithOutComma(dblGrossAllTotal) %></th>
							<th><%=uF.formatIntoTwoDecimalWithOutComma(dblNetAllTotal) %></th>
						<% } %>
					</tr>
					
					<% if(flagEHSR) { %>
						<tr>
						<td nowrap="nowrap">&nbsp;</td>
						<td nowrap="nowrap">&nbsp;</td>
						<td nowrap="nowrap">&nbsp;</td>
						<%-- <td nowrap="nowrap"><%=strWLocation %></td>
						<td nowrap="nowrap"><%=hmPayroll.get("EMP_STATUS") %></td> --%>
						<td nowrap="nowrap">&nbsp;</td>
						<%		
							for (int i = maxSalHeadCnt; i < alESalaryHeads.size(); i++) {
								if(alEarnings.contains(alESalaryHeads.get(i))) { %>
								<th><%=uF.showData(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotAmtSalHead.get(alESalaryHeads.get(i)))), "0.00") %></th>
							<% } } %>
						<%-- <% if(alEarnings.contains(""+IConstants.VDA)) { %>
							<th nowrap="nowrap">&nbsp;</th>
						<% } %> --%>
						<% if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()+1) : alEarnings.size()) < alDeductions.size()) {
							for(int i=0; i<(alDeductions.size()-(alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } else if((alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt)) < maxSalHeadCnt) {
							for(int i=0; i<(maxSalHeadCnt-(alEarnings.contains(""+IConstants.VDA) ? (alEarnings.size()-(maxSalHeadCnt+1)) : (alEarnings.size()-maxSalHeadCnt))); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } %>
						<th><%=uF.formatIntoTwoDecimalWithOutComma(dblGrossAllTotal) %></th>
						<th><%=uF.formatIntoTwoDecimalWithOutComma(dblNetAllTotal) %></th>
						</tr>
					<% } %>
					
					<tr>
						<th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th>
						<!-- <th nowrap="nowrap">&nbsp;</th>
						<th nowrap="nowrap">&nbsp;</th> -->
						<th nowrap="nowrap">&nbsp;</th>
						<% for (int i = 0; i < alDSalaryHeads.size(); i++) {
							if(alDeductions.contains(alDSalaryHeads.get(i))) { %>
							<th><%=uF.showData(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble((String)hmTotAmtSalHead.get(alDSalaryHeads.get(i)))), "0.00") %></th>
						<% } } %>
						<% if(alEarnings.size()>0 && (alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt) > alDeductions.size()) {
							for(int i=0; i<((alEarnings.contains(""+IConstants.VDA) ? (salHeadCnt+1) : salHeadCnt)-alDeductions.size()); i++) {
						%>
							<th nowrap="nowrap">&nbsp;</th>
						<% } } %>
						<th><%=uF.formatIntoTwoDecimalWithOutComma(dblDeductionAllTotal) %></th>
						<th nowrap="nowrap">&nbsp;</th>
					</tr>
				</tbody>
			</table>
			
		</div>
		<!-- /.box-body -->
	</div>
	
	


<%-- <%@page import="java.util.*,com.konnect.jpms.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="/struts-tags" prefix="s" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<div id="divResult">
 <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script> 

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	 $('#lt').DataTable({
		dom: 'lBfrtip',
		 buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Comprehensive Salary Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Comprehensive Salary Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Comprehensive Salary Report'
		            },
		            {
		                extend: 'print',
		                title: 'Comprehensive Salary Report'
		            }
		        ],
		        "bSort": false
	});  
 	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter(); 
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
});
function submitForm(type){
	document.frm_PayPayroll.exportType.value='';
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&paycycle='+paycycle+'&strEmployeType='+strEmployeType;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PayrollRegisterNew.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
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

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmEmpMap = (Map) request.getAttribute("hmEmpMap");
	Map hmPayPayroll = (Map) request.getAttribute("hmPayPayroll");
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
	Map hmIsApprovedSalary = (Map) request.getAttribute("hmIsApprovedSalary");
	if (hmIsApprovedSalary == null)
		hmIsApprovedSalary = new HashMap();
	List alEarnings = (List) request.getAttribute("alEarnings");
	List alDeductions = (List) request.getAttribute("alDeductions");
%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size:14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body"
					style="padding: 5px; overflow-y: auto; display: none;">
					<div class="content1">
						<s:form name="frm_PayPayroll" action="PayrollRegisterNew" theme="simple" method="post">
							<s:hidden name="exportType"></s:hidden>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key="" />
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
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
									</div>
									<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
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
										<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
									</div>
								</div>
							</div>
						</s:form>
					</div>
				</div>
				<!-- /.box-body -->
			</div>
				

			<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt">
				<display:column nowrap="nowrap" title="Code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
				<display:column nowrap="nowrap" title="Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<display:column nowrap="nowrap" title="Location"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
				<display:column nowrap="nowrap" title="Designation(Grade)"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column nowrap="nowrap" title="Joining Date"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
				<display:column nowrap="nowrap" title="Present Days"class="alignRight rightPad50"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
				
				<%
					for (int ii = 0; ii < alEarnings.size(); ii++) {
						int count = 10 + ii;
						String strEarning = (String)hmSalaryDetails.get((String)alEarnings.get(ii))+"\n(+)";
				%>
				<display:column nowrap="nowrap" class="alignRight rightPad50" title="<%=strEarning %>"><%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<% } %>
               <display:column nowrap="nowrap" title="Gross"class="alignRight rightPad50"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
				<%
					for (int ii = 0; ii < alDeductions.size(); ii++) {
						int count = alEarnings.size() + 10 + ii;
						String strDeduction = (String)hmSalaryDetails.get((String)alDeductions.get(ii))+"\n(-)";
				%>
				<display:column nowrap="nowrap" class="alignRight rightPad50" title="<%=strDeduction %>"><%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<% } %>
				<display:column nowrap="nowrap" title="Net"class="alignRight rightPad50"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
			</display:table>

		</div>
		<!-- /.box-body -->
	</div> --%>
