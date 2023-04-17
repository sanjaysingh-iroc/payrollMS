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
 
 
<%
 String month = (String)request.getAttribute("selectedMonth");
 String year = (String)request.getAttribute("selectedYear");
%>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#f_salaryhead").multiselect().multiselectfilter();
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
    var monthvalue = <%=month %>;
    var headtype = getSelectedValue("f_salaryheadtype");
    
    if(monthvalue==1){
    	monthvalue = 'January';
    }
    else if (monthvalue==2){
    	monthvalue = 'February';
    }
    else if (monthvalue==3){
    	monthvalue = 'March';
    }
    else if (monthvalue==4){
    	monthvalue = 'April';
    }
    else if (monthvalue==5){
    	monthvalue = 'May';
    }
    else if (monthvalue==6){
    	monthvalue = 'June';
    }
    else if (monthvalue==7){
    	monthvalue = 'July';
    }
    else if (monthvalue==8){
    	monthvalue = 'August';
    }
    else if (monthvalue==9){
    	monthvalue = 'September';
    }
    else if (monthvalue==10){
    	monthvalue = 'October';
    }
    else if (monthvalue==11){
    	monthvalue = 'November';
    }
    else if (monthvalue==12){
    	monthvalue = 'December';
    }
    else{
    	monthvalue="";
    }
    var yearvalue = <%=year %>;
   
    if(headtype == "D"){
	$('#lt1').DataTable( {
        dom: 'lBfrtip',
        buttons: [
            'copy',
            {
                extend: 'csv',
                title: 'Departmentwise Salary Summary Part II for General Employees for the month of-'+monthvalue+""+yearvalue
            },
            {
                extend: 'excel',
                title: 'Departmentwise Salary Summary Part II for General Employees for the month of-'+monthvalue+""+yearvalue
            },
            {
                extend: 'pdf',
                title: 'Departmentwise Salary Summary Part II for General Employees for the month of-'+monthvalue+""+yearvalue
            },
            {
                extend: 'print',
                title: 'Departmentwise Salary Summary Part II for General Employees for the month of-'+monthvalue+""+yearvalue
            }
        ]
    } );     
}
	
    if(headtype == "E"){
    	$('#lt1').DataTable( {
            dom: 'lBfrtip',
            buttons: [
                'copy',
                {
                    extend: 'csv',
                    title: 'Departmentwise Salary Summary Part I for General Employees for the month of-'+monthvalue+""+yearvalue
                },
                {
                    extend: 'excel',
                    title: 'Departmentwise Salary Summary Part I for General Employees for the month of-'+monthvalue+""+yearvalue
                },
                {
                    extend: 'pdf',
                    title: 'Departmentwise Salary Summary Part I for General Employees for the month of-'+monthvalue+""+yearvalue
                },
                {
                    extend: 'print',
                    title: 'Departmentwise Salary Summary Part I for General Employees for the month of-'+monthvalue+""+yearvalue
                }
            ]
        } );     
    }
    
    else{
    	$('#lt1').DataTable( {
            dom: 'lBfrtip',
            buttons: [
                'copy',
                {
                    extend: 'csv',
                    title: 'Departmentwise Salary Summary for General Employees for the month of-'+monthvalue+""+yearvalue
                },
                {
                    extend: 'excel',
                    title: 'Departmentwise Salary Summary for General Employees for the month of-'+monthvalue+""+yearvalue
                },
                {
                    extend: 'pdf',
                    title: 'Departmentwise Salary Summary for General Employees for the month of-'+monthvalue+""+yearvalue
                },
                {
                    extend: 'print',
                    title: 'Departmentwise Salary Summary for General Employees for the month of-'+monthvalue+""+yearvalue
                }
            ]
        } );     
    }
});

function submitForm(type){
	document.frm_formDeptwise.exportType.value='';
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = document.getElementById("strMonth").value;
	var salaryheadtype = document.getElementById("f_salaryheadtype").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department"); 
	var service = getSelectedValue("f_service");
	var salaryhead = getSelectedValue("f_salaryhead");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strSalaryhead='+salaryhead
			+'&financialYear='+financialYear+'&strMonth='+strMonth+'&f_salaryheadtype='+salaryheadtype;
	}else if(type == '3') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
			+'&financialYear='+financialYear+'&strMonth='+strMonth+'&f_salaryheadtype='+salaryheadtype;
	}

	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DepartmentwiseSalarySummaryReport.action?f_org='+org+paramValues,
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


/* function getSalaryHeadList(salHeadType) {
	var strMonth = document.getElementById("strMonth").value;
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DepartmentwiseSalarySummaryReport.action?headType='+salHeadType+'&month='+strMonth,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
	//window.location='DepartmentwiseSalarySummaryReport.action?headType='+salHeadType+'&month='+strMonth;
} */
</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	String strYear = (String) request.getAttribute("strYear");
	String strMonth = (String) request.getAttribute("strMonth");
	String strPrevMonth = (String) request.getAttribute("strPrevMonth");

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}

	/* Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
	Map<String, String> hmWLocation = (Map<String, String>) request.getAttribute("hmWLocation");
	Map<String, String> hmDept = (Map<String, String>) request.getAttribute("hmDept");
	Map<String, String> hmLevelMap = (Map<String, String>) request.getAttribute("hmLevelMap"); */
	
	Set<String> salaryHeadESet = (Set<String>)request.getAttribute("salaryHeadESet");
	Set<String> salaryHeadDSet = (Set<String>)request.getAttribute("salaryHeadDSet");
	
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");

%>

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
				<s:form name="frm_formDeptwise" action="DepartmentwiseSalarySummaryReport" theme="simple">
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
								<p style="padding-left: 5px;">Salary Heads Type</p>
								<s:select theme="simple" name="f_salaryheadtype" id="f_salaryheadtype" headerKey="" headerValue="All" list="#{'E':'Earnings','D':'Deductions' }" onchange="submitForm('3');"/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Salary Heads</p>
								<div id="salHeadDiv">
									<s:select theme="simple" name="f_salaryhead" id="f_salaryhead" listKey="salaryHeadId" listValue="salaryHeadName" list="salaryHeadList" multiple="true"/>
								</div>
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
								<p style="padding-left: 5px;">Month</p>
								<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" onchange="submitForm('2');" list="monthList" key="" />
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
		
			
<div class="col-md-2 pull-right">
					
<a onclick="" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>

</div>

		<br />

		<%-- <div style="text-align:center;margin:10px" > <h2>Reconciliation for the month of <%=uF.getDateFormat(strMonth, "MM", "MMMM")%> <%=uF.getDateFormat(strYear, "yyyy", "yyyy")%> </h2></div> --%>

		<display:table name="reportList" class="table table-bordered" cellspacing="1" id="lt1">
			<%-- <display:setProperty name="export.excel.filename" value="ReconciliationReport.xls" />
			<display:setProperty name="export.xml.filename" value="ReconciliationReport.xml" />
			<display:setProperty name="export.csv.filename" value="ReconciliationReport.csv" /> --%>
			
			<%-- <display:column style="text-align:center;" valign="top" title="Sr. No."><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column> --%>
			<!-- uF.getDateFormat(strPrevMonth, "MM", "MMMM") -->
			<display:column style="text-align:left;" valign="top" title="Department Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<%
			int cnt = 1;
				Iterator<String> itSalHeadE = salaryHeadESet.iterator();
				while(itSalHeadE.hasNext()) {
					cnt++;
					String strSalHead = itSalHeadE.next();
					String strEarning = (String)hmSalaryDetails.get(strSalHead)+"\n(+)";
			%>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="<%=strEarning %>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(cnt)%></display:column>
			<% } %>
			<%
				Iterator<String> itSalHeadD = salaryHeadDSet.iterator();
				while(itSalHeadD.hasNext()) {
					cnt++;
					String strSalHead = itSalHeadD.next();
					String strDeduct = (String)hmSalaryDetails.get(strSalHead)+"\n(-)";
			%>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="<%=strDeduct %>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(cnt)%></display:column>
			<% } %>
			<display:column style="text-align:right;" valign="top" title="Total"><%=((java.util.List) pageContext.getAttribute("lt1")).get((cnt+1))%></display:column>

		</display:table>

	</div>
		<!-- /.box-body -->
</div>
