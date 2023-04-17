<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">

<%-- <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script> 
  --%>
 
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
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();

	$('#lt1').DataTable({
		"order": [],
		"columnDefs": [ {
			"targets"  : 'no-sort',
			"orderable": false
		}],
        dom: 'lBfrtip',
        buttons: [
            'copy',
            {
                extend: 'csv',
                title: 'Payout Report'
            },
            {
                extend: 'excel',
                title: 'Payout Report'
            },
            {
                extend: 'pdf',
                title: 'Payout Report'
            },
            {
                extend: 'print',
                title: 'Payout Report'
            }
        ]
    } );  
	
});

function submitForm(type){
	document.frm_PayoutReport.exportType.value='';
	var org = document.getElementById("f_org").value;
	var f_paymentMode = document.getElementById("f_paymentMode").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&f_paymentMode='+f_paymentMode+'&paycycle='+paycycle+'&strEmployeType='+strEmployeType;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PayoutReport.action?f_org='+org+paramValues,
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

	function generateReportExcel() {
		window.location = "ExportExcelReport.action";
	}
	
</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");

	List<List<String>> empOuterList = (List<List<String>>) request.getAttribute("empOuterList");

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
	List<List<DataStyle>> reportListExport = (List<List<DataStyle>>) request.getAttribute("reportListExport");
	if (reportListExport == null)
		reportListExport = new ArrayList<List<DataStyle>>();
	session.setAttribute("reportListExport", reportListExport);
%>


<!-- Custom form for adding new records -->

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Payout Report" name="title"/>
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
				<s:form name="frm_PayoutReport" action="PayoutReport" theme="simple">
					<s:hidden name="exportType"></s:hidden>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Payment Type</p>
								<s:select theme="simple" name="f_paymentMode" id="f_paymentMode" listKey="payModeId" listValue="payModeName" onchange="submitForm('2');" list="paymentModeList" key=""/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">SBU</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
							</div>
						</div>
					</div>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key="" onchange="submitForm('2');" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
							</div>
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		<%	List<List<String>> reportList = (ArrayList<List<String>>)request.getAttribute("reportList");
			if(reportList == null) reportList = new  ArrayList<List<String>>();
		%>
		<% if(reportList != null && !reportList.isEmpty() && reportList.size() >0) { %>
			<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			</div>
	  <% } %>
		<display:table name="reportList" class="table table-bordered" id="lt1">
			<%-- <display:setProperty name="export.excel.filename" value="PayoutReport.xls" />
			<display:setProperty name="export.xml.filename" value="PayoutReport.xml" />
			<display:setProperty name="export.csv.filename" value="PayoutReport.csv" /> --%>
			<display:column style="text-align:center;" valign="top" title="Sr. No."><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
			<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Pan No"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
			<display:column style="text-align:left;" valign="top" title="Bank & Branch"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
			<display:column style="text-align:left;" valign="top" title="Bank IFSC Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
			<display:column style="text-align:left;" valign="top" title="Saving Account No."><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Net Salary"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>

		</display:table>
	</div>
		<!-- /.box-body -->
</div>


