<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	var strMonth = document.getElementById("strMonth").value;
	var f_strWLocation = document.getElementById("f_strWLocation").value;
	var f_level = document.getElementById("f_level").value;
	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+f_strWLocation+'&f_level='+f_level+'&financialYear='+financialYear
		+'&strSelectedEmpId='+strSelectedEmpId+'&strMonth='+strMonth;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form27A.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}


	function generateForm27A() {

		var financialYear = document.frm_from27A.financialYear.value;
		var strSelectedEmpId = document.frm_from27A.strSelectedEmpId.value;
		var strMonth = document.frm_from27A.strMonth.value;

		var url = 'Form27A.action?formType=pdf&financialYear=' + financialYear;
		url += "&strSelectedEmpId=" + strSelectedEmpId + "&strMonth="
				+ strMonth;
		window.location = url;
	}
</script>

<style type="text/css">
.tg {
	border-collapse: collapse;
	border-spacing: 0;
	margin-left: 30px;
	margin-right: 40px;
	margin-bottom: 20px;
	margin-top: 10px;
}

.tg td {
	font-family: Arial, sans-serif;
	font-size: 14px;
	padding: 10px 5px;
	border-style: solid;
	border-width: 1px;
	overflow: hidden;
	word-break: normal;
}

.tg th {
	font-family: Arial, sans-serif;
	font-size: 14px;
	font-weight: normal;
	padding: 10px 5px;
	border-style: solid;
	text-align: left;
	border-width: 1px;
	overflow: hidden;
	word-break: normal;
}

.tg .tg-s6z2 {
	text-align: center
}

.tg1 {
	border-collapse: collapse;
	border-spacing: 0;
	margin-left: 21px;
	margin-top: 10px;
}

.tg1 td {
	font-family: Arial, sans-serif;
	font-size: 14px;
	padding: 7px 11px;
	border-style: solid;
	border-width: 1px;
	overflow: hidden;
	word-break: normal;
}

.tg1 th {
	font-family: Arial, sans-serif;
	font-size: 14px;
	font-weight: normal;
	padding: 7px 11px;
	border-style: solid;
	border-width: 1px;
	overflow: hidden;
	word-break: normal;
}

.tg1 .tg-s6z21 {
	text-align: center;
	width: 54%;
}

.tg2 {
	border-collapse: collapse;
	border-spacing: 0;
	margin: 10px 40px 20px 30px;
}

.tg2 td {
	font-family: Arial, sans-serif;
	font-size: 14px;
	padding: 7px 11px;
	border-style: solid;
	border-width: 1px;
	overflow: hidden;
	word-break: normal;
}

.tg2 th {
	font-family: Arial, sans-serif;
	font-size: 14px;
	font-weight: normal;
	padding: 7px 11px;
	border-style: solid;
	border-width: 1px;
	overflow: hidden;
	word-break: normal;
}

.tg2 .tg-s6z23 {
	text-align: center
}
</style>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");

	List alMonth = (List) request.getAttribute("alMonth");

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}

	String strQuarter = (String) request.getAttribute("strQuarter");

	Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
	if (hmOrg == null)
		hmOrg = new HashMap<String, String>();
	Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
	if (hmEmp == null)
		hmEmp = new HashMap<String, String>();

	Map<String, String> hmStates = (Map<String, String>) request.getAttribute("hmStates");
	if (hmStates == null)
		hmStates = new HashMap<String, String>();
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	if (hmEmpCodeDesig == null)
		hmEmpCodeDesig = new HashMap<String, String>();

	Map<String, String> hmOtherDetailsMap = (Map<String, String>) request.getAttribute("hmOtherDetailsMap");
	if (hmOtherDetailsMap == null)
		hmOtherDetailsMap = new HashMap<String, String>();

	String empCount = (String) request.getAttribute("empCount");
	String challanAmt = (String) request.getAttribute("challanAmt");
	String paidAmt = (String) request.getAttribute("paidAmt");
	String paidTDSAmt = (String) request.getAttribute("paidTDSAmt");
%>

		<div class="box-header with-border">
			<h3 class="box-title"> Form 27A for financial year <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h3>
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
					<s:form name="frm_from27A" action="Form27A" theme="simple">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" headerValue="All Organization" headerKey="" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" headerKey="" headerValue="All Locations" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" headerKey="" headerValue="All Levels" listKey="levelId" listValue="levelCodeName" list="levelList" key="" onchange="submitForm('2');"/>
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
				<a onclick="generateForm27A();" href="javascript:void(0)" class="fa fa-file-pdf-o"></a>
			</div> -->
			
<div class="col-md-2 pull-right">
					
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
</div>

			
			<div style="margin: 0 auto; width: 100%; border: 2px solid #666666; clear: both;">
				<h3 style="text-align: center;">Form No. 27A</h3>
				<h5 style="text-align: center;">Form for furnishing information with the statement of deduction / collection of tax at
					source ( tick whichever is applicable ) filed on computer media for the period (From 01/01/14 to 31/03/14 (dd/mm/yy)#</h5>
				<div style="clear: both;">
					<table border="0" style="border-collapse: collapse; float: left;">
						<tr>
							<td style="padding: 2px 30px 2px 10px; border: 0px;">1 (a) Tax Deduction Account No.</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 20%;">
									<tr>
										<td style="padding: 2px 72px; min-width: 177px; text-align: center; height: 17px;"><%=uF.showData(hmOtherDetailsMap.get("DEDUCTOR_TAN"), " ")%></td>
									</tr>
								</table></td>
						</tr>
					</table>
					<table border="0" style="border-collapse: collapse; float: left; margin-left: 6%;">
						<tr>
							<td style="padding: 2px 33px 2px 9px; border: 0px;">(d) Financial Year</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 23%;">
									<tr>
										<td style="padding: 2px 72px; min-width: 177px; text-align: center; height: 17px;"><%=uF.getDateFormat(strFinancialYearStart, CF.getStrReportDateFormat(), "yyyy") + " - " + uF.getDateFormat(strFinancialYearEnd, CF.getStrReportDateFormat(), "yy")%></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>

					<table border="0" style="border-collapse: collapse; float: left; margin-top: 10px;">
						<tr>
							<td style="padding: 2px 32px 2px 21px; border: 0px;">(b) Permanent Account No.</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 23%;">
									<tr>
										<td style="padding: 2px 72px; min-width: 177px; text-align: center; height: 17px;"><%=uF.showData(hmOtherDetailsMap.get("DEDUCTOR_PAN"), " ")%></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					<table border="0" style="border-collapse: collapse; float: left; margin-left: 6%; margin-top: 10px;">
						<tr>
							<td style="padding: 2px 33px 2px 9px; border: 0px;">(e) Assessment year</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 20%;">
									<tr>
										<td style="padding: 2px 72px; min-width: 177px; text-align: center; height: 17px;"><%=(uF.parseToInt(uF.getDateFormat(strFinancialYearStart, CF.getStrReportDateFormat(), "yyyy")) + 1) + " - " + (uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, CF.getStrReportDateFormat(), "yy")) + 1)%></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>

					<table border="0" style="border-collapse: collapse; float: left; margin-top: 10px;">
						<tr>
							<td style="padding: 2px 34px 2px 21px; border: 0px;">(c) Form No.</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 35%;">
									<tr>
										<td style="padding: 2px 72px; min-width: 177px; text-align: center; height: 17px;">24Q</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					<table border="0" style="border-collapse: collapse; float: left; margin-left: 6%; margin-top: 10px; margin-bottom: 15px;">
						<tr>
							<td style="padding: 2px 33px 2px 9px; border: 0px; width: 161px;">(f) Previous receipt number</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 9%;">
									<tr>
										<td style="padding: 2px 72px; min-width: 177px; text-align: center; height: 17px;">NA</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td style="padding: 2px 0px 2px 9px;">(In case return/statement has been filed earlier)</td>
							<td>&nbsp;</td>
						</tr>
					</table>
				</div>
				
				<div style="width: 56%; float: left;">
					<span style="padding-left: 10px;">2 Particulars of the deductor / collector</span>
					<table class="tg">
						<tr>
							<th class="tg-031e">(a) Name</th>
							<th class="tg-031e" style="min-width: 380px;"><%=uF.showData(hmOrg.get("ORG_NAME"), "")%></th>
						</tr>
						<tr>
							<td class="tg-031e">(b) Type of deductor*</td>
							<td class="tg-s6z2" style="min-width: 380px;">COMPANY</td>
						</tr>
						<tr>
							<td class="tg-031e">(c) Branch / division (if any)</td>
							<td class="tg-031e" style="min-width: 380px;"></td>
						</tr>
						<tr>
							<td class="tg-031e">(d) Address</td>
							<td class="tg-031e" style="min-width: 380px;"><%=uF.showData(hmOrg.get("ORG_ADDRESS"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Flat No.</td>
							<td class="tg-s6z2" style="min-width: 380px;"></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Name of the premises/building</td>
							<td class="tg-s6z2" style="min-width: 380px;"></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Road / street / lane</td>
							<td class="tg-s6z2" style="min-width: 380px;"></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Area / location</td>
							<td class="tg-s6z2" style="min-width: 380px;"></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Town / City / District</td>
							<td class="tg-s6z2" style="min-width: 380px;"><%=uF.showData(hmOrg.get("ORG_CITY"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;State</td>
							<td class="tg-s6z2" style="min-width: 380px;"><%=uF.showData(hmStates.get(hmOrg.get("ORG_STATE_ID")), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Pin code</td>
							<td class="tg-s6z2" style="min-width: 380px;"><%=uF.showData(hmOrg.get("ORG_PINCODE"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Telephone No.</td>
							<td class="tg-s6z2" style="min-width: 380px;"><%=uF.showData(hmOrg.get("ORG_CONTACT"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;E-mail</td>
							<td class="tg-s6z2" style="min-width: 380px;"><%=uF.showData(hmEmp.get("EMP_EMAIL"), "")%></td>
						</tr>
					</table>
				</div>
				
				<div style="width: 44%; float: left;">
					<span>3 Name of the person responsible for deduction / collection of tax</span>
					<table class="tg1">
						<tr>
							<th class="tg-031e1" style="text-align: left;">(a)Name</th>
							<th class="tg-s6z21"><%=uF.showData(hmEmp.get("EMP_NAME"), "")%></th>
						</tr>
						<tr>
							<td class="tg-031e1">(b)Address</td>
							<td class="tg-031e1"><%=uF.showData(hmEmp.get("EMP_ADDRESS"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; Flat No.</td>
							<td class="tg-s6z21"></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; Name of the premises/building</td>
							<td class="tg-s6z21"></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; Road / street / lane</td>
							<td class="tg-s6z21"></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; Area / location</td>
							<td class="tg-s6z21"></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; Town / City / District</td>
							<td class="tg-s6z21"><%=uF.showData(hmEmp.get("EMP_CITY_ID"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; State</td>
							<td class="tg-s6z21"><%=uF.showData(hmStates.get(hmEmp.get("EMP_STATE_ID")), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; Pin code</td>
							<td class="tg-s6z21"><%=uF.showData(hmEmp.get("EMP_PIN_CODE"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; Telephone No.</td>
							<td class="tg-s6z21"><%=uF.showData(hmEmp.get("EMP_CONTACT_NO"), "")%></td>
						</tr>
						<tr>
							<td class="tg-031e1">&nbsp;&nbsp;&nbsp; E-mail</td>
							<td class="tg-s6z21"><%=uF.showData(hmEmp.get("EMP_EMAIL"), "")%></td>
						</tr>
					</table>
				</div>
				
				<div style="clear: both;">
					<span style="padding-left: 10px;">4 Control totals</span>
					<table class="tg2">
						<tr>
							<th class="tg-031e3">Sr. No.</th>
							<th class="tg-s6z23">Return Type<br>(Regular / Correction type)</th>
							<th class="tg-s6z23">No. of deductee / party<br>records</th>
							<th class="tg-s6z23">Amount paid<br>(Rs.)</th>
							<th class="tg-s6z23">Tax deducted / collected<br>(Rs.)</th>
							<th class="tg-s6z23">Tax deposited<br>(Total challan amount)<br>(Rs.)</th>
						</tr>
						<tr>
							<td class="tg-s6z23">1</td>
							<td class="tg-s6z23">REGULAR</td>
							<td class="tg-s6z23"><%=uF.showData(empCount, "0")%></td>
							<td class="tg-s6z23"><%=uF.showData(paidAmt, "0")%></td>
							<td class="tg-s6z23"><%=uF.showData(paidTDSAmt, "0")%></td>
							<td class="tg-s6z23"><%=uF.showData(challanAmt, "0")%></td>
						</tr>
						<tr>
							<td class="tg-s6z23">Total</td>
							<td class="tg-031e3"></td>
							<td class="tg-s6z23"><%=uF.showData(empCount, "0")%></td>
							<td class="tg-s6z23"><%=uF.showData(paidAmt, "0")%></td>
							<td class="tg-s6z23"><%=uF.showData(paidTDSAmt, "0")%></td>
							<td class="tg-s6z23"><%=uF.showData(challanAmt, "0")%></td>
						</tr>
					</table>

					<table border="0" style="border-collapse: collapse; margin-top: 10px;">
						<tr>
							<td style="padding: 2px 30px 2px 10px; border: 0px;">5 Total Number of Annexures enclosed</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 175px;">
									<tr>
										<td style="padding: 2px 72px; min-width: 120px; text-align: center; height: 17px;"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>

					<table border="0"
						style="border-collapse: collapse; margin-top: 10px;">
						<tr>
							<td style="padding: 2px 0px 2px 10px; border: 0px;">6 Other Information</td>
							<td>
								<table border="1" style="border-collapse: collapse; margin-left: 312px;">
									<tr>
										<td style="padding: 2px 72px; min-width: 420px; text-align: center; height: 17px;"></td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
					<div style="width: 100%; margin: 0 auto;">
						<h3 style="text-align: center;">VERIFICATION</h3>
						<p style="padding-left: 10px;">
							I,
							<%=uF.showData(hmEmp.get("EMP_NAME"), "")%>, hereby certify
							that all the particulars furnished above are correct and
							complete.
						</p>

						<table border="0" style="margin-left: 7px; padding-right: 13px;">
							<tr>
								<td style="padding-right: 90px;">Place:</td>
								<td>Signature of person responsible for deducting / collecting tax at source</td>
								<td>____________________________________________________</td>
							</tr>
						</table>
						<table border="0" style="margin-left: 7px; padding-right: 13px;">
							<tr>
								<td style="padding-right: 87px;">Date: 28/04/2014</td>
								<td>Name and designation of person responsible for deducting / collecting tax at source</td>
								<td><%=uF.showData(hmEmp.get("EMP_NAME"), "") + " ("+ uF.showData(hmEmpCodeDesig.get(hmEmp.get("EMP_ID")), "")+ ")"%></td>
							</tr>
						</table>
						<p style="padding-left: 10px;">* Mention type of deductor - Government or Others</p>
						<p style="padding-left: 10px;"># dd/mm/yy :- date/month/year</p>
					</div>

				</div>
			</div>
		</div>
		<!-- /.box-body -->
	</div>

