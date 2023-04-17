<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<%-- <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>  --%>
 
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
	$('#lt1').DataTable( {
        dom: 'lBfrtip',
        buttons: [
            'copy',
            {
                extend: 'csv',
                title: 'Bonus Report'
            },
            {
                extend: 'excel',
                title: 'Bonus Report'
            },
            {
                extend: 'pdf',
                title: 'Bonus Report'
            },
            {
                extend: 'print',
                title: 'Bonus Report'
            }
        ]
    } );  
	
});

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = document.getElementById("strMonth").value;
	var f_strWLocation = document.getElementById("f_strWLocation").value;
	var f_department = document.getElementById("f_department").value;
	var f_level = document.getElementById("f_level").value;
	var f_employeType = document.getElementById("f_employeType").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_level='+f_level
			+'&financialYear='+financialYear+'&strMonth='+strMonth+'&f_employeType='+f_employeType;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'BonusRegister.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	// console.log(result);
        	$("#divResult").html(result);
   		}
	});
}

	function generatePTaxReport() {
		var financialYear = document.frm_fromBonus.financialYear.value;
		var strMonth = document.frm_fromBonus.strMonth.value;
		var f_strWLocation = document.frm_fromBonus.f_strWLocation.value;
		var f_department = document.frm_fromBonus.f_department.value;
		var f_level = document.frm_fromBonus.f_level.value;
		var f_org = document.frm_fromBonus.f_org.value;

		var url = 'BonusPdfReport.action?financialYear=' + financialYear;
		url += "&strMonth=" + strMonth;
		url += "&f_strWLocation=" + f_strWLocation + "&f_department="+ f_department;
		url += "&f_level=" + f_level + "&f_org=" + f_org;

		window.location = url;
	}

	function generateReportExcel() {
		var financialYear = document.frm_fromBonus.financialYear.value;
		var strMonth = document.frm_fromBonus.strMonth.value;
		var f_strWLocation = document.frm_fromBonus.f_strWLocation.value;
		var f_department = document.frm_fromBonus.f_department.value;
		var f_level = document.frm_fromBonus.f_level.value;
		var f_org = document.frm_fromBonus.f_org.value;

		var url = 'PTaxExcelReports.action?financialYear=' + financialYear;
		url += "&strMonth=" + strMonth;
		url += "&f_strWLocation=" + f_strWLocation + "&f_department="+ f_department;
		url += "&f_level=" + f_level + "&f_org=" + f_org;

		//window.location = url;
	}
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	String strYear = (String) request.getAttribute("strYear");
	String strMonth = (String) request.getAttribute("strMonth");

	Map hmEmpBonusMap = (Map) request.getAttribute("hmEmpBonusMap");
	Map hmBonusMap = (Map) request.getAttribute("hmBonusMap");
	Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
	Map hmEmpName = (Map) request.getAttribute("hmEmpName");
	Map hmEmpLevel = (Map) request.getAttribute("hmEmpLevel");
	Map hmEmpPanNo = (Map) request.getAttribute("hmEmpPanNo");
	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
%>

<!-- Custom form for adding new records -->

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Bonus Register" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
				<s:form name="frm_fromBonus" action="BonusRegister" theme="simple">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-filter"></i>
						</div>

						<div class="col-lg-11 col-md-11 col-sm-11 inline" style="padding-left: 0px;">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key="" cssClass="inline" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key="" cssClass="inline" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" onchange="submitForm('2');" cssClass="inline"/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" onchange="submitForm('2');" list="departmentList" cssClass="inline" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" onchange="submitForm('2');" list="levelList" cssClass="inline" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key="" headerValue="All Employee Type" headerKey="0" onchange="submitForm('2');"  />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Month</p>
								<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" onchange="submitForm('2');" list="monthList" key="" cssClass="inline" />
							</div>
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		
		<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
			<a onclick="generatePTaxReport();" href="javascript:void(0)" class="pdf">&nbsp;&nbsp;</a> 
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
		</div> -->
		
		<div style="text-align: center; margin: 10px">
			<h4>Bonus Report as per Payments of Bonus Act for the month of <%=uF.getDateFormat(strMonth, "MM", "MMMM")%> <%=uF.getDateFormat(strYear, "yyyy", "yyyy")%> </h4>
		</div>


		<table class="table table-hover table-bordered" id="lt1">
			<thead>
			<tr>
				<th width="5%">Sr. No.</th>
				<th width="10%">Employee Code</th>
				<th width="25%">Employee Name</th>
				<th width="14%">Pan No</th>
				<th width="13%" align="right" class="padRight20">Bonus Salary</th>
				<th width="13%" align="right" class="padRight20">Calculated Salary</th>
				<th width="10%" align="right" class="padRight20">Rate</th>
				<th width="10%" align="right" class="padRight20">Bonus</th>
			</tr>
			</thead>
			
			<tbody>
			<%
				Set set = hmEmpBonusMap.keySet();
				Iterator it = set.iterator();
				int count = 0;
				while (it.hasNext()) {
					String strEmpId = (String) it.next();
					Map hmInner = (Map) hmEmpBonusMap.get(strEmpId);
					if (hmInner == null)
						hmInner = new HashMap();

					String strLevelId = (String) hmEmpLevel.get(strEmpId);

					Map hmBonusInner = (Map) hmBonusMap.get(strLevelId);
					if (hmBonusInner == null)
						hmBonusInner = new HashMap();
			%>

			<tr>
				<td><%=++count%></td>
				<td><%=uF.showData((String) hmEmpCode.get(strEmpId), "")%></td>
				<td><%=uF.showData((String) hmEmpName.get(strEmpId), "")%></td>
				<td><%=uF.showData((String) hmEmpPanNo.get(strEmpId), "")%></td>
				<td align="right" class="padRight20"><%=uF.showData((String) hmInner.get("GROSS_AMOUNT"), "0")%></td>
				<td align="right" class="padRight20"><%=uF.showData((String) hmBonusInner.get("MINIMUM_AMOUNT"), "0")%></td>
				<td align="right" class="padRight20"><%=uF.showData((String) hmBonusInner.get("BONUS_RATE"), "0")%></td>
				<td align="right" class="padRight20"><%=uF.showData((String) hmInner.get("BONUS_PAID"), "0")%></td>
			</tr>
			<% }%>
			</tbody>
		</table>
	</div>
	<!-- /.box-body -->
</div>


<%-- <a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../../reports/ReportNavigation.jsp"></jsp:include>
   </div> --%>