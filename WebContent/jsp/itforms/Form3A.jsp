<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {

	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
});

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var f_strWLocation = document.getElementById("f_strWLocation").value;
	var f_level = document.getElementById("f_level").value;
	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+f_strWLocation+'&f_level='+f_level+'&financialYear='+financialYear+'&strSelectedEmpId='+strSelectedEmpId;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form3A.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}


	function generateForm3A() {

		var financialYear = document.frm_from3A.financialYear.value;
		var strSelectedEmpId = document.frm_from3A.strSelectedEmpId.value;
		var url = 'ITFormReports.action?formType=form3A&financialYear='+ financialYear;
		url += "&strSelectedEmpId=" + strSelectedEmpId;
		window.location = url;
	}
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	Map<String, Map<String, String>> hmDetails = (Map<String, Map<String, String>>) request.getAttribute("hmDetails");
	if (hmDetails == null)
		hmDetails = new HashMap<String, Map<String, String>>();

	Map<String, String> hmEmployeeDetails = (Map<String, String>) request.getAttribute("hmEmployeeDetails");
	if (hmEmployeeDetails == null)
		hmEmployeeDetails = new HashMap<String, String>();

	Map<String, String> hmEmpAbsentDays = (Map<String, String>) request.getAttribute("hmEmpAbsentDays");
	if (hmEmpAbsentDays == null)
		hmEmpAbsentDays = new HashMap<String, String>();
	Map<String, String> hmEPFMap = (Map<String, String>) request.getAttribute("hmEPFMap");
	if (hmEPFMap == null)
		hmEPFMap = new HashMap<String, String>();

	List<String> alMonth = (List<String>) request.getAttribute("alMonth");
	if (alMonth == null)
		alMonth = new ArrayList<String>();

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}

	double dblTotalAmountWages = uF.parseToDouble((String) request.getAttribute("dblTotalAmountWages"));
	double dblTotalEmployeeAmount = uF.parseToDouble((String) request.getAttribute("dblTotalEmployeeAmount"));
	double dblTotalEmployerEPFAmount = uF.parseToDouble((String) request.getAttribute("dblTotalEmployerEPFAmount"));
	double dblTotalEmployerEPSAmount = uF.parseToDouble((String) request.getAttribute("dblTotalEmployerEPSAmount"));
%>

<!-- Custom form for adding new records -->
		<div class="box-header with-border">
			<h3 class="box-title"> Form 3A for financial year <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h3>
		</div>
		<!-- /.box-header -->
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
					<s:form name="frm_from3A" action="Form3A" theme="simple">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" headerValue="All Locations" headerKey="0" listValue="wLocationName" list="wLocationList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" headerValue="All Levels" headerKey="0" onchange="submitForm('2');" list="levelList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee</p>
									<s:select label="Select Employee" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId" headerValue="Select Employee" listValue="employeeCode" headerKey="0" list="empNamesList" onchange="submitForm('2');"/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
			
			<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				<a onclick="generateForm3A();" href="javascript:void(0)" class="fa fa-file-pdf-o"></a>
			</div> --> 
<div class="col-md-2 pull-right">
					
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
</div>

			<p style="font-size: 13px; font-weight: bold; text-align: center;margin-top: 20px;">Form 3A (Revised)</p>
			<p style="font-size: 12px; text-align: center">The Employees' Provident Fund Scheme, 1952(Para 35 & 42) and The Employees' Pension Scheme, 1995(para 19)</p>
			<p style="font-size: 16px; font-weight: bold; text-align: center"> CONTRIBUTION CARD FOR CURRENCY PERIOD FROM <%=strFinancialYearStart%> TO <%=strFinancialYearEnd%></p>

			<table>
				<tr>
					<td style="font-size: 12px">PF Account No</td>
					<td style="font-size: 12px"><%=uF.showData((String) hmEmployeeDetails.get("EPF_ACC_NO"), "")%></td>
				</tr>
				<tr>
					<td style="font-size: 12px">Name/Surname</td>
					<td style="font-size: 12px"><%=uF.showData((String) hmEmployeeDetails.get("NAME"), "")%></td>
				</tr>
				<tr>
					<td style="font-size: 12px">Father's/ Husban's Name</td>
					<td style="font-size: 12px"><%=uF.showData((String) hmEmployeeDetails.get("FATHER_SPOUSE"), "")%></td>
				</tr>
				<tr>
					<td style="font-size: 12px">Statutory rate of P.F. Contribution, if any</td>
					<td style="font-size: 12px"><%=uF.showData((String) hmEmployeeDetails.get(""), "")%></td>
				</tr>
				<tr><td colspan="2" style="font-size: 12px">Contribution <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></td></tr>
			</table>


			<table width="100%" class="table table-bordered">
				<tr>
					<td width="10%" rowspan="2" align="center" class="reportHeading">Payment Month<br />(1)</td>
					<td width="10%" rowspan="2" align="center" class="reportHeading">Amount of Wages<br />(2)</td>
					<td align="center" class="reportHeading">Worker's Share</td>
					<td align="center" colspan="2" class="reportHeading">Employer's Share</td>
					<td width="10%" rowspan="2" align="center" class="reportHeading">Refund of Adv.<br />(5)</td>
					<td width="10%" rowspan="2" align="center" class="reportHeading">No of days/period of non contributing services<br />(6)</td>
					<td width="30%" rowspan="2" align="center" class="reportHeading">Remarks<br />(7)</td>
				</tr>
				<tr>
					<td width="10%" align="center" class="reportHeading">E.P.F.<br />(3)</td>
					<td width="10%" align="center" class="reportHeading">E.P.F. difference between <%=uF.showData(hmEPFMap.get("EEPF_CONTRIBUTION"), "0")%>% & <%=uF.showData(hmEPFMap.get("ERPS_CONTRIBUTION"), "0")%>%<br />(3)</td>
					<td width="10%" align="center" class="reportHeading">Pension Fund contribution <%=uF.showData(hmEPFMap.get("ERPS_CONTRIBUTION"), "0")%>%</td>
				</tr>

				<%
					double dblTotalAbsent = 0.0d;
					for (int i = 0; alMonth != null && i < alMonth.size(); i++) {
						Map<String, String> hmInner = (Map<String, String>) hmDetails.get((String) alMonth.get(i));
						if (hmInner == null)
							hmInner = new HashMap<String, String>();
							dblTotalAbsent += uF.parseToDouble(hmEmpAbsentDays.get((String) alMonth.get(i)));
				%>

				<tr>
					<td align="left" class="reportLabel"><%=uF.getDateFormat((String) alMonth.get(i), "MM", "MMMM")%></td>
					<td align="right" class="reportLabel padRight20"><%=uF.showData((String) hmInner.get("AMOUNT_WAGES"), "0")%></td>
					<td align="right" class="reportLabel padRight20"><%=uF.showData((String) hmInner.get("EMPLOYEE_CONTRIBUTION"), "0")%></td>
					<td align="right" class="reportLabel padRight20"><%=uF.showData((String) hmInner.get("EMPLOYER_DIFF_SHARE"), "0")%></td>
					<td align="right" class="reportLabel padRight20"><%=uF.showData((String) hmInner.get("EMPLOYER_SHARE_EPS"), "0")%></td>
					<td align="right" class="reportLabel padRight20"></td>
					<td align="right" class="reportLabel padRight20"><%=uF.showData(hmEmpAbsentDays.get((String) alMonth.get(i)), "0")%></td>
					<td align="right" class="reportLabel padRight20"></td>
				</tr>
				<% } %>
				<tr>
					<td align="center" class="reportLabel"><strong>Total</strong></td>
					<td align="right" class="reportLabel padRight20"><strong><%=uF.formatIntoTwoDecimal(dblTotalAmountWages)%></strong></td>
					<td align="right" class="reportLabel padRight20"><strong><%=uF.formatIntoTwoDecimal(dblTotalEmployeeAmount)%></strong></td>
					<td align="right" class="reportLabel padRight20"><strong><%=uF.formatIntoTwoDecimal(dblTotalEmployerEPFAmount)%></strong></td>
					<td align="right" class="reportLabel padRight20"><strong><%=uF.formatIntoTwoDecimal(dblTotalEmployerEPSAmount)%></strong></td>
					<td align="right" class="reportLabel padRight20"></td>
					<td align="right" class="reportLabel padRight20"><strong><%=uF.formatIntoTwoDecimal(dblTotalAbsent)%></strong></td>
					<td align="right" class="reportLabel padRight20"></td>
				</tr>
			</table>

		</div>
		<!-- /.box-body -->
	</div>

