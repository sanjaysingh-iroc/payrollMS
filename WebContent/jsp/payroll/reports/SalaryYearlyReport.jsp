<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
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
$(function() {
	/* $('#lt').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	}); */
	$('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
		buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Salary Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Salary Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Salary Report'
		            },
		            {
		                extend: 'print',
		                title: 'Salary Report'
		            }
		        ]
  	});
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter(); 
	$("#f_employeType").multiselect().multiselectfilter();
});
function submitForm(type){
	document.frm_SalaryReport.exportType.value='';
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;

	var location = getSelectedValue("f_strWLocation");
	
	var department = getSelectedValue("f_department");
	
	var service = getSelectedValue("f_service");
	
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&financialYear='+financialYear+'&strEmployeType='+strEmployeType;
	}
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'SalaryYearlyReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	// console.log(result);
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


function generateYearlySalary() {
		/* var financialYear=document.frm_SalaryReport.financialYear.value;
		var url='SalaryYearlyPdfReport.action?financialYear='+financialYear;
		window.location = url; */
		document.frm_SalaryReport.exportType.value = 'pdf';
		document.frm_SalaryReport.submit();
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
%>

<!-- Custom form for adding new records -->

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Yearly Salary Report" name="title"/>
</jsp:include> --%>

		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
					<s:form name="frm_SalaryReport" action="SalaryYearlyReport" theme="simple" method="post">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm();" list="orgList" key=""/>
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
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
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
				<a onclick="generateYearlySalary();" href="javascript:void(0)" class="fa fa-file-pdf-o"></a>
			</div>  -->
			
			<div class="col-md-2 pull-right">
					
<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>

</div>


			<%-- <div style="text-align:center;margin:10px" > <h2>Yearly Salary Summary for the period of <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h2></div> --%>

			<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt">
				<display:column style="text-align:left;" nowrap="nowrap" title="Components" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="April" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="May" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="June" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="July" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="August" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="September" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="October" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="November" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="December" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="January" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="February" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="March" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(12)%></display:column>
			</display:table>
		</div>
		<!-- /.box-body -->
	</div>

