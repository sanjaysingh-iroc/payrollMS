<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="com.konnect.jpms.reports.ReportList"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%-- <%@ taglib uri="http://displaytag.sf.net" prefix="display"%> --%>

 <div id="divResult">

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();

	$('#lt1').DataTable({
		aLengthMenu: [
			  			[25, 50, 100, 200, -1],
			  			[25, 50, 100, 200, "All"]
			  		],
		iDisplayLength: -1,
		dom: 'lBfrtip',
		"ordering": false,
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
});

function submitForm(type){

	/* document.frm_PayoutReport.exportType.value=''; */
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&financialYear='+financialYear;
	}
	
	//alert("paramValues ===>> " + paramValues);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form24QReport.action?f_org='+org+paramValues,
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
	window.location="ExportExcelMultiSheetReport.action";
}

</script>

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

List<Map<String, String>> reportList=(List<Map<String, String>>)request.getAttribute("reportList");
if(reportList==null) reportList=new ArrayList<Map<String,String>>();

String strFinancialYearStart =(String)request.getAttribute("strFinancialYearStart");
String strFinancialYearEnd =(String)request.getAttribute("strFinancialYearEnd");

Map<String, List<List<DataStyle>>> hmReportExport = (Map<String, List<List<DataStyle>>>)request.getAttribute("hmReportExport");
session.setAttribute("hmReportExport", hmReportExport);

%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Form 24Q Report" name="title"/>
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
					<s:form name="frm_Form24QReport" action="Form24QReport" theme="simple" method="post">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" list="financialYearList" key="" onchange="submitForm('2');"/>
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
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
	
			<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<!-- <a onclick="generateSalaryExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
				
				
			</div>
						
					
			<div style="text-align:center;margin:10px"><h3>Details of salary paid/credited during the financial year <%=strFinancialYearStart %> - <%=strFinancialYearEnd %> and next taxable</h3></div>
			
			<div>
				<table id ="lt1" class="table table-bordered overflowtable">
					<thead>
						<tr>
							<th class="alignCenter">Salary Details  Record No (Serial Number of Employee)</th>
							<th class="alignCenter">Permanent Account Number (PAN) of the Employee</th>
							<th class="alignCenter">Pan Ref. No</th>
							<th class="alignCenter">Name of the Employee</th>
							<th class="alignCenter">Category of Employee</th>
							<th class="alignCenter">Date from Which Employeed with the current employer (dd/mm/yyyy)</th>
							<th class="alignCenter">Date upto Which Employeed with the current employer (dd/mm/yyyy)</th>
							<th class="alignCenter">Taxable amount on Which Tax Deducted By Current Employer</th>
							<th class="alignCenter">Reported Taxable Amount by Previous Employer</th>
							<th class="alignCenter">Total Amount of Salary (333+ 334)</th>
							<th class="alignCenter">Entertainment Allowance 16(i)/(ii)</th><!-- Created By Dattatray 16-06-2022 -->
							<th class="alignCenter">Total Deduction u/s 16 (iii)- Professional Tax</th>
							<th class="alignCenter">Income chargeable under the head Salaries (Column 335-(336+337))</th>
							<th class="alignCenter">Income (including loss from house property) under any head other than income under the head "salaries" offered for TDS [section 192 (2B)]</th>
							<th class="alignCenter">Gross Total Income (Total of Column 338 + 339)</th>
							
							<th class="alignCenter">Aggregate amount of deductions under sections 80C, 80CCC and 80CCD (Total to be limited to amount specified in section 80CCE)</th>
							<th class="alignCenter">80CCG- Amount deductible under section 80CCG and the said amount should be <=25,000</th>
							<th class="alignCenter">Amount Deductible under any other provision(s) of Chapter VI A</th>
							<th class="alignCenter">Gross Total of 'Amount deductible under provisions of chapter VI-A' under  associated ' Salary Details  - Chapter VIA Detail ' (341+342)</th>
							<th class="alignCenter">Total Taxable Income (Column 340 - 343)</th>
							<th class="alignCenter">Income Tax on Total Income</th>
							<th class="alignCenter">Surcharge</th>
							<th class="alignCenter">Education Cess</th>
							<th class="alignCenter">Income Tax Relief u/s 89 when salary etc is paid in arrear or advance</th>
							<th class="alignCenter">Net Income Tax payable (345+346-347)</th>
							<th class="alignCenter">Total amount of tax deducted at source for the whole year</th>
							<th class="alignCenter">Reported amount of tax deducted at source by previous employer(s)</th>
							<th class="alignCenter">Total amount of Tax Deducted for Whole year (Total of column 349 +350)</th>
							<th class="alignCenter">Shortfall in tax deduction (+)/Excess tax deduction(-) [348-351]</th>
							<th class="alignCenter">Whether Tax Deducted at higher rate due to non furnishing of PAN by deductee</th>
							
							<th class="alignCenter">Whether house rent allowance claim (aggregate payment) exceeds rupees one lakh during previous</th>
							<th class="alignCenter">Count of PAN of the landlord</th>
							<th class="alignCenter">PAN of landlord 1</th>
							<th class="alignCenter">Name of landlord 1</th>
							<th class="alignCenter">PAN of landlord 2</th>
							<th class="alignCenter">Name of landlord 2</th>
							<th class="alignCenter">PAN of landlord 3</th>
							<th class="alignCenter">Name of landlord 3</th>
							<th class="alignCenter">PAN of landlord 4</th>
							<th class="alignCenter">Name of landlord 4</th>
							<th class="alignCenter">Whether Interest paid  to the lender under the head 'Income from house property'.</th>
							<th class="alignCenter">Count of PAN of the lender</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - PAN of lender 1</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - Name of lender 1</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - PAN of lender 2</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - Name of lender 2</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - PAN of lender 3</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - Name of lender 3</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - PAN of lender 4</th>
							<th class="alignCenter">In case of deduction of interest under the head income from house property - Name of lender 4</th>
							
							<th class="alignCenter">Whether contributions paid by the trustees of an approved superannuation fund</th>
							<th class="alignCenter">Name of the superannuation fund</th>
							<th class="alignCenter">Date from which the employee has contributed to the superannuation fund</th>
							<th class="alignCenter">Date to which the employee has contributed to the superannuation fund</th>
							<th class="alignCenter">The amount of contribution repaid on account of principal and interest from superannuation fund</th>
							<th class="alignCenter">The average rate of deduction of tax during the preceding three years</th>
							<th class="alignCenter">The amount of tax deducted on repayment of superannuation fund</th>
							<th class="alignCenter">Gross total income including contribution repaid on account of principal and interest from</th>
							
							<th class="alignCenter">Error description</th>
						</tr>
					</thead>
					<tbody> 
						<tr>
							<td class="alignCenter">328</td>
							<td class="alignCenter">329</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">330</td>
							<td class="alignCenter">331</td>
							<td class="alignCenter">332</td>
							<td class="alignCenter">332</td>
							<td class="alignCenter">333</td>
							<td class="alignCenter">334</td>
							<td class="alignCenter">335</td>
							<td class="alignCenter">336</td>
							<td class="alignCenter">337</td>
							<td class="alignCenter">338</td>
							<td class="alignCenter">339</td>
							<td class="alignCenter">340</td>
							
							<td class="alignCenter">341</td>
							<td class="alignCenter">341</td>
							<td class="alignCenter">342</td>
							<td class="alignCenter">343</td>
							<td class="alignCenter">344</td>
							<td class="alignCenter">345</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">346</td>
							<td class="alignCenter">347</td>
							<td class="alignCenter">348</td>
							<td class="alignCenter">349</td>
							<td class="alignCenter">350</td>
							<td class="alignCenter">351</td>
							<td class="alignCenter">352</td>
							<td class="alignCenter">353</td>
							
							<td class="alignCenter">357</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">358</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							<td class="alignCenter">&nbsp;</td>
							
							<td class="alignCenter">&nbsp;</td>
						</tr>	
					 
					<%
					for(int i=0;reportList!=null && i<reportList.size();i++){
						Map<String, String> hmInner=(Map<String, String>) reportList.get(i);
					%>
						<tr>
							<td class=""><%=hmInner.get("SRNO") %></td>
							<td class=""><%=hmInner.get("EMP_PAN_NO") %></td>
							<td class=""><%=hmInner.get("EMP_PAN_REF_NO") %></td>
							<td class="" nowrap="nowrap"><%=hmInner.get("EMP_NAME") %></td>
							<td class=" alignCenter"><%=hmInner.get("EMP_RESIDENT_AGE") %></td>
							<td class=" alignCenter"><%=hmInner.get("EMP_DATE_FROM") %></td>
							<td class=" alignCenter"><%=hmInner.get("EMP_DATE_UP") %></td>
							<td class=" alignRight"><%=hmInner.get("CURRENT_TAXABLE_TAX_AMT") %></td>
							<td class=" alignRight"><%=hmInner.get("PREVIOUS_TAXABLE_TAX_AMT") %></td>
							<td class=" alignRight"><%=hmInner.get("TOTAL_AMT_SALARY") %></td>
							<td class=" alignRight"><%=hmInner.get("ENTERTAINMENT_ALLOWANCE") %></td>
							<td class=" alignRight"><%=hmInner.get("PROFESSIONAL_TAX") %></td>
							<td class=" alignRight"><%=hmInner.get("INCOME_CHARGE_SALARY") %></td>
							<td class=" alignRight"><%=hmInner.get("INCOME_FROM_OTHER") %></td>
							<td class=" alignRight"><%=hmInner.get("GROSS_TOTAL") %></td>
							
							<td class=" alignRight"><%=hmInner.get("CHAPTER_VI_A1") %></td>
							<td class=" alignRight"><%=hmInner.get("CHAPTER_VI_A2") %></td>
							<td class=" alignRight"><%=hmInner.get("CHAPTER_VI_AOTHER") %></td>
							<td class=" alignRight"><%=hmInner.get("TOTAL_CHAPTER_VIA") %></td>
							<td class=" alignRight"><%=(uF.parseToDouble(hmInner.get("TOTAL_TAX_INCOME"))>0) ? hmInner.get("TOTAL_TAX_INCOME") : "0" %></td>
							<td class=" alignRight"><%=hmInner.get("INCOME_TAX") %></td>
							<td class=" alignRight"><%=hmInner.get("SURCHARGE") %></td>
							<td class=" alignRight"><%=hmInner.get("EDU_CESS") %></td>
							<td class=" alignRight"><%=hmInner.get("INCOME_TAX_RELIEF") %></td>
							<td class=" alignRight"><%=hmInner.get("NET_TAX_INCOME") %></td>
							<td class=" alignRight"><%=hmInner.get("TDS_AMT") %></td>
							<td class=" alignRight"><%=hmInner.get("PREV_EMP_TDS_AMT") %></td>
							<td class=" alignRight"><%=hmInner.get("TOTAL_TDS") %></td>
							<td class=" alignRight"><%=hmInner.get("SHORTFALL_TDS") %></td>
							<td class=" alignRight"><%=hmInner.get("FURNISH_PAN") %></td>
							
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
							<td class="reportLabel alignRight"></td>
											
							<td class=" alignRight"><%=hmInner.get("ERROR_DISCRIPTION") %></td>			
						</tr>
						<% } if(reportList.size()==0) { %>
						<tr><td class=" alignCenter" colspan="31">No employee found in this financial year</td></tr>
						<% } %>   
					</tbody>
				</table>
			</div>
					    
		</div>
		<!-- /.box-body -->
	</div>
