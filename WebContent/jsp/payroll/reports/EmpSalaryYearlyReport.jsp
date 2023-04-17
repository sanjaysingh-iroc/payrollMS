<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<style>
.table-bordered>thead>tr>th, .table-bordered>tbody>tr>th, .table-bordered>tfoot>tr>th, .table-bordered>thead>tr>td, .table-bordered>tbody>tr>td, .table-bordered>tfoot>tr>td {
	border: 1px solid #000000;
}
</style> 

<div id="divResult">

<script type="text/javascript" charset="utf-8">
/* $(document).ready(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
}); */

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strEmpId = document.getElementById("strEmpId").value;
	var location = document.getElementById("f_strWLocation").value;
	var department = document.getElementById("f_department").value; 
	var level = document.getElementById("f_level").value;
	var f_employeType = document.getElementById("f_employeType").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+location+'&f_department='+department+'&f_level='+level
			+'&financialYear='+financialYear+'&f_employeType='+f_employeType+'&strEmpId='+strEmpId;
	} /* else { 
		paramValues = '&f_strWLocation='+location+'&f_department='+department+'&f_level='+level
		+'&financialYear='+financialYear+'&f_employeType='+f_employeType;
	} */ 
		
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'EmpSalaryYearlyReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}


/* function getSelectedValue(selectId) {
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
} */

	function generateEmpYearlySalary() {
		var financialYear = document.frm_fromEmpYearly.financialYear.value;
		var strEmpId = document.frm_fromEmpYearly.strEmpId.value;
		var url = 'EmpSalaryYearlyPdfReports.action?financialYear='+ financialYear;
		url += "&strEmpId=" + strEmpId;
		window.location = url;
	}
	
	function generateReportExcel() {
		var financialYear = document.frm_fromEmpYearly.financialYear.value;
		var strEmpId = document.frm_fromEmpYearly.strEmpId.value;
		var url = 'EmpSalaryYearlyExcelReports.action?financialYear='+ financialYear;
		url += "&strEmpId=" + strEmpId;
		window.location = url;
	}
	
</script>
<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");

	Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
	Map hmDeductionSalaryMap = (Map) request.getAttribute("hmDeductionSalaryMap");
	Map hmEarningSalaryTotalMap = (Map) request.getAttribute("hmEarningSalaryTotalMap");
	Map hmDeductionSalaryTotalMap = (Map) request.getAttribute("hmDeductionSalaryTotalMap");
	Map hmSalaryHeadMap = (Map) request.getAttribute("hmSalaryHeadMap");
	Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
	Map hmEmpName = (Map) request.getAttribute("hmEmpName");
	List alMonth = (List) request.getAttribute("alMonth");

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
	
	String empPanNo = (String)request.getAttribute("empPanNo");
%>

<!-- Custom form for adding new records -->

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Yearly Salary Report" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
				<div class="box-tools pull-right"><button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
				<s:form name="frm_fromEmpYearly" action="EmpSalaryYearlyReport" theme="simple">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select name="financialYear" id="financialYear" listKey="financialYearId" headerValue="Select Financial Year" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key=""/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key=""  onchange="submitForm('1');" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key=""  onchange="submitForm('1');" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key=""  onchange="submitForm('1');" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key="" headerValue="Select Employee Type" headerKey="" onchange="submitForm('1');"  />
							</div>
						</div>
					</div><br>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-user"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Employee</p>
								<s:select name="strEmpId" id="strEmpId" listKey="employeeId" headerValue="Select Employee" listValue="employeeCode" headerKey="0" onchange="submitForm('2');" list="empList" key="" />
							</div>
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>

		 <!--<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
			<a onclick="generateEmpYearlySalary();" href="javascript:void(0)" class="fa fa-file-pdf-o">&nbsp;&nbsp; </a>
			 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> 
			 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right; "><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			 
		</div> -->
		
		
	<div class="col-md-2 pull-right">
		<a onclick="generateEmpYearlySalary();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
		<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
	</div>
		


		<div style="text-align: center; margin: 10px"><h4>Yearly Salary Summary for the period of <%=strFinancialYearStart%> to <%=strFinancialYearEnd%> </br> Employee Pan No. <%=uF.showData(empPanNo,"") %> </h4></div>
		<table class="table table-hover table-bordered">  <!-- id="lt1" -->
			<thead>
				<tr>
					<td width="15%" style="border-bottom: solid 1px #000; border-top: solid 1px #000">Components</td>
					<% for (int i = 0; i < alMonth.size(); i++) { %>
						<td align="right" width="7%" style="border-bottom: solid 1px #000; border-top: solid 1px #000"><%=uF.getDateFormat((String) alMonth.get(i), "MM", "MMM")%></td>
					<% } %>
				</tr>
			</thead>
			<tbody>
				<tr><td colspan="13" style="border-bottom: solid 1px #000;">Earning</td></tr>
	
				<%
					Set set = hmEarningSalaryMap.keySet();
					Iterator it = set.iterator();
					while (it.hasNext()) {
						String strSalaryHeadId = (String) it.next();
						Map hmInner = (Map) hmEarningSalaryMap.get(strSalaryHeadId);
				%>
				<tr>
					<td style="border-bottom: dashed 1px #ccc"><%=uF.showData((String) hmSalaryHeadMap.get(strSalaryHeadId), "")%></td>
					<%
						for (int i = 0; i < alMonth.size(); i++) {
							String strAmount = (String) hmInner.get((String) alMonth.get(i));
					%>
					<td align="right" class="paddingRight20" style="border-bottom: dashed 1px #ccc"><%=uF.showData(strAmount, "0")%></td>
					<% } %>
				</tr>
				<% } %>
	
				<tr>
					<td><strong>Total</strong></td>
					<%
						for (int i = 0; i < alMonth.size(); i++) {
							String strTotalAmount = (String) hmEarningSalaryTotalMap.get((String) alMonth.get(i));
					%>
					<td align="right" class="paddingRight20"><strong><%=uF.showData(strTotalAmount, "0")%></strong></td>
					<% } %>
				</tr>
	
				<tr><td colspan="13">&nbsp;</td></tr>
	
				<tr><td colspan="13" style="border-bottom: solid 1px #000;">Deduction</td></tr>
	
				<%
					set = hmDeductionSalaryMap.keySet();
					it = set.iterator();
					while (it.hasNext()) {
						String strSalaryHeadId = (String) it.next();
						Map hmInner = (Map) hmDeductionSalaryMap.get(strSalaryHeadId);
				%>
				<tr>
					<td style="border-bottom: dashed 1px #ccc"><%=uF.showData((String) hmSalaryHeadMap.get(strSalaryHeadId), "")%></td>
					<%
						for (int i = 0; i < alMonth.size(); i++) {
							String strAmount = (String) hmInner.get((String) alMonth.get(i));
					%>
					<td align="right" class="paddingRight20"style="border-bottom: dashed 1px #ccc"><%=uF.showData(strAmount, "0")%></td>
					<% } %>
				</tr>
				<% } %>
	
				<tr>
					<td><strong>Total</strong></td>
					<%
						for (int i = 0; i < alMonth.size(); i++) {
							String strTotalAmount = (String) hmDeductionSalaryTotalMap.get((String) alMonth.get(i));
					%>
					<td align="right" class="paddingRight20"><strong><%=uF.showData(strTotalAmount, "0")%></strong></td>
					<% } %>
				</tr>
	
				<tr>
					<td style="border-bottom: solid 1px #000; border-top: solid 1px #000"><strong>Net Pay</strong></td>
					<%
						for (int i = 0; i < alMonth.size(); i++) {
							String strTotalEarAmount = (String) hmEarningSalaryTotalMap.get((String) alMonth.get(i));
							String strTotalDedAmount = (String) hmDeductionSalaryTotalMap.get((String) alMonth.get(i));
							String strNet = uF.formatIntoTwoDecimal((uF.parseToDouble(strTotalEarAmount) - uF.parseToDouble(strTotalDedAmount)));
					%>
					<td align="right" class="paddingRight20" style="border-bottom: solid 1px #000; border-top: solid 1px #000"><strong><%=uF.showData(strNet, "0")%></strong></td>
					<% } %>
				</tr>
			</tbody>
		</table>
	</div>
		<!-- /.box-body -->
</div>



<%-- <a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../../reports/ReportNavigation.jsp"></jsp:include>
   </div> --%>