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
		dom: 'lBfrtip',
		buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Reimbursement Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Reimbursement Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Reimbursement Report'
		            },
		            {
		                extend: 'print',
		                title: 'Reimbursement Report'
		            }
		        ]
	});
	
});

function submitForm(type){
	document.frm_fromReimbursement.exportType.value='';
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var reimbursementType = document.getElementById("reimbursementType").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&financialYear='+financialYear+'&strEmployeType='+strEmployeType+'&reimbursementType='+reimbursementType;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ReimbursementStatement.action?f_org='+org+paramValues,
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

	function generateReimburesementReportPdf() {
		/* var financialYear=document.frm_fromReimbursement.financialYear.value;	
		var f_strWLocation=document.frm_fromReimbursement.f_strWLocation.value;
		var f_department=document.frm_fromReimbursement.f_department.value;
		var f_level=document.frm_fromReimbursement.f_level.value;
		var f_org=document.frm_fromReimbursement.f_org.value;
		var f_service=document.frm_fromReimbursement.f_service.value; 
		
		var url='ReimbursementPdfReports.action?financialYear='+financialYear;
		url+="&f_strWLocation="+f_strWLocation+"&f_department="+f_department;
		url+="&f_level="+f_level;
		url+="&f_service="+f_service+"&f_org="+f_org;
		window.location = url; */
		document.frm_fromReimbursement.exportType.value = 'pdf';
		document.frm_fromReimbursement.submit();
	}

</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");

	Map hmReimbursementMap = (Map) request.getAttribute("hmReimbursementMap");
	Map hmReimbursementType = (Map) request.getAttribute("hmReimbursementType");
	Map hmEmpName = (Map) request.getAttribute("hmEmpName");
	Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
	Map hmEmpPanNo = (Map) request.getAttribute("hmEmpPanNo");

	List alMonth = (List) request.getAttribute("alMonth");

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
%>


<!-- Custom form for adding new records -->

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Reimbursement Statement" name="title"/>
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
					<s:form name="frm_fromReimbursement" action="ReimbursementStatement" theme="simple">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
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
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key="" multiple="true" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Reimbursement Type</p>
									<s:select theme="simple" name="reimbursementType" id="reimbursementType" headerKey="" headerValue="All" list="#{'P':'Project', 'O':'Other'}"/>
								</div>
							</div>
						</div>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key=""/>
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
			
 
			<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generateReimburesementReportPdf();" href="javascript:void(0)" class="fa fa-file-pdf-o"></a>
			</div>  -->
			
			<div class="col-md-2 pull-right">
				<a onclick="generateReimburesementReportPdf();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
			</div>

			<%-- <div style="text-align:center;margin:10px" > <h2>Reimbursement statement for F.Y. <%=strFinancialYearStart%> to <%=strFinancialYearEnd%>  </h2></div> --%>
			<display:table name="reportList" class="table table-bordered" id="lt1">
				<%-- <display:setProperty name="export.excel.filename" value="ReimbursementStatement.xls" />
				<display:setProperty name="export.xml.filename" value="ReimbursementStatement.xml" />
				<display:setProperty name="export.csv.filename" value="ReimbursementStatement.csv" /> --%>
				
				<display:column style="text-align:left;" nowrap="nowrap" title="Employee Code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Pan No" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Reimbursement Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="April" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="May" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="June" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="July" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="August" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="September" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="October" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="November" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(11)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="December" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(12)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="January" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(13)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="February" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(14)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="March" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(15)%></display:column>
			</display:table>

	</div>
	<!-- /.box-body -->
</div>

