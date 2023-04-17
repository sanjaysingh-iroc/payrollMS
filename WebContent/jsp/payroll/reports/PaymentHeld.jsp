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
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>  --%>
 
 
 <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script> 
 
<script type="text/javascript">
    $(function(){
    	$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter();
    	$("#f_employeType").multiselect().multiselectfilter();
    });   
   /*  $('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	}); */
    $('#lt1').DataTable( {
        dom: 'lBfrtip',
        buttons: [
            'copy',
            {
                extend: 'csv',
                title: 'Payments Held Report'
            },
            {
                extend: 'excel',
                title: 'Payments Held Report'
            },
            {
                extend: 'pdf',
                title: 'Payments Held Report'
            },
            {
                extend: 'print',
                title: 'Payments Held Report'
            }
        ]
    } );  
    
</script>



<script type="text/javascript" charset="utf-8">
 
function submitForm(type){
	document.frm_fromPaymentHeld.exportType.value='';
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = document.getElementById("strMonth").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var paramSelection = getCheckedValue("paramSelection");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&financialYear='+financialYear+'&strMonth='+strMonth+'&strEmployeType='+strEmployeType+'&paramSelection='+paramSelection;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PaymentHeld.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	// console.log(result);
        	$("#divResult").html(result);
   		}
	});
}


function getCheckedValue(checkId) {
    var radioObj = document.getElementsByName(checkId);
    var radioLength = radioObj.length;
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
	
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

	function generatePaymentHeldPdfReport() {
		/* var financialYear=document.frm_fromPaymentHeld.financialYear.value;
		var strMonth=document.frm_fromPaymentHeld.strMonth.value;
		var f_strWLocation=document.frm_fromPaymentHeld.f_strWLocation.value;
		var f_department=document.frm_fromPaymentHeld.f_department.value;
		//var f_level=document.frm_fromPaymentHeld.f_level.value;
		var f_org=document.frm_fromPaymentHeld.f_org.value;
		var f_service=document.frm_fromPaymentHeld.f_service.value;
		var paramSelection=document.frm_fromPaymentHeld.paramSelection.value;
		
		var url='PaymentHeldPdfReports.action?&financialYear='+financialYear;
		url+="&strMonth="+strMonth;
		url+="&f_strWLocation="+f_strWLocation+"&f_department="+f_department;
		//url+="&f_level="+f_level;
		url+="&f_service="+f_service+"&f_org="+f_org+"&paramSelection="+paramSelection; 
		window.location = url; */
		document.frm_fromPaymentHeld.exportType.value = 'pdf';
		document.frm_fromPaymentHeld.submit();
	}

	function generateReportExcel() {
		/* var financialYear=document.frm_fromPaymentHeld.financialYear.value;
		var strMonth=document.frm_fromPaymentHeld.strMonth.value;
		var f_strWLocation=document.frm_fromPaymentHeld.f_strWLocation.value;
		var f_department=document.frm_fromPaymentHeld.f_department.value;
		//var f_level=document.frm_fromPaymentHeld.f_level.value;
		var f_org=document.frm_fromPaymentHeld.f_org.value;
		var f_service=document.frm_fromPaymentHeld.f_service.value;
		var paramSelection=document.frm_fromPaymentHeld.paramSelection.value;
		
		var url='PaymentHeldExcelReports.action?&financialYear='+financialYear;
		url+="&strMonth="+strMonth;
		url+="&f_strWLocation="+f_strWLocation+"&f_department="+f_department;
		//url+="&f_level="+f_level;
		url+="&f_service="+f_service+"&f_org="+f_org+"&paramSelection="+paramSelection;
		window.location = url;  */

		document.frm_fromPaymentHeld.exportType.value = 'excel';
		document.frm_fromPaymentHeld.submit();
	}

</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	String strYear = (String) request.getAttribute("strYear");
	String strMonth = (String) request.getAttribute("strMonth");

	Map hmEmpPTax = (Map) request.getAttribute("hmEmpPTax");
	Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
	Map hmEmpName = (Map) request.getAttribute("hmEmpName");

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}

	String strOrg = (String) request.getAttribute("f_org");
	Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
	if (hmOrg == null)
		hmOrg = new HashMap<String, String>();

	Map<String, String> hmWLocation = (Map<String, String>) request.getAttribute("hmWLocation");
	Map<String, String> hmDept = (Map<String, String>) request.getAttribute("hmDept");
	Map<String, String> hmLevelMap = (Map<String, String>) request.getAttribute("hmLevelMap");
	Map<String, String> hmServicesMap = (Map<String, String>) request.getAttribute("hmServicesMap");

	Map hmEmpLevelMap = (Map) request.getAttribute("hmEmpLevelMap");
	Map hmLevelMap1 = (Map) request.getAttribute("hmLevelMap1");
	Map hmEmpDept = (Map) request.getAttribute("hmEmpDept");
	Map hmDeptMap = (Map) request.getAttribute("hmDeptMap");

	Map<String, String> hmEmpOrgName = (Map<String, String>) request.getAttribute("hmEmpOrgName");
	if (hmEmpOrgName == null)
		hmEmpOrgName = new HashMap<String, String>();

	String strParamSelection = (String) request.getAttribute("paramSelection");
%>

<!-- Custom form for adding new records -->

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Payments Held Report" name="title"/>
</jsp:include> --%>

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
				<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
					<s:form name="frm_fromPaymentHeld" action="PaymentHeld" theme="simple">
						<s:hidden name="exportType"></s:hidden>
						<!-- <div class="filter_div"> -->
						<!-- <div class="filter_caption">Filter</div> -->
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
								</div>
	
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
							</div>
						</div>
						<br/>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div id="financialYearDIV" class="col-lg-5 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key=""/>
								</div>
	
								<div id="monthDIV" class="col-lg-6 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" onchange="submitForm('2');" list="monthList" key=""/>
								</div>
							</div>
						</div>
						<br/>
						<div class="row row_without_margin">
							<s:radio name="paramSelection" id="paramSelection" list="#{'ORG':'By Organization','WL':'By Location','DEPART':'By Department','SBU':'By SBU','EMP':'By Employee'}" />
							<%-- <s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" /> --%>
							<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px;" onclick="submitForm('2');" />
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>

			 <!--<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generatePaymentHeldPdfReport();" href="javascript:void(0)"><i class="fa fa-file-excel-o" aria-hidden="true"></i> </a>
				 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> 
				 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			</div>-->
			
			
		<div class="col-md-2 pull-right">
			<a onclick="generatePaymentHeldPdfReport();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div>

				<%-- <div style="text-align:center;margin:10px" > <h2>Salary held statement for the month of <%=uF.getDateFormat(strMonth, "MM", "MMMM")%> <%=uF.getDateFormat(strYear, "yyyy", "yyyy")%> </h2></div> --%>
				<% if (strParamSelection == null || strParamSelection.equals("EMP")) { %>
				<display:table  name="reportList" class="table table-bordered" id="lt1">
					<%-- <display:column style="text-align:center;" valign="top" title="Sr. No."><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column> --%>
					<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
					<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
					<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Pan No."><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
					<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
					<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
					<display:column style="text-align:left; width: 25%;" valign="top" title="Level"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
					<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Net Pay"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
				</display:table> 
				<%
					} else {
						String title = "";
						if (strParamSelection != null && strParamSelection.equals("ORG")) {
							title = "Organization";
						} else if (strParamSelection != null && strParamSelection.equals("WL")) {
							title = "Location";
						} else if (strParamSelection != null && strParamSelection.equals("DEPART")) {
							title = "Department";
						} else if (strParamSelection != null && strParamSelection.equals("SBU")) {
							title = "Service";
						}
				%>
				<display:table name="reportList"  class="table table-bordered " id="lt1">
					<%-- <display:column style="text-align:center;" valign="top" title="Sr. No."><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column> --%>
					<display:column style="text-align:left;" valign="top" title="<%=title %>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
					<display:column style="text-align:left;" valign="top" title="Net Pay"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
				</display:table>
				<% } %>

		</div>
		<!-- /.box-body -->
	</div>


<%-- 
<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../../reports/ReportNavigation.jsp"></jsp:include>
   </div> --%>