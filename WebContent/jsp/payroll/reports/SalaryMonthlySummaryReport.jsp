<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<div id="divResult">


<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
});
function submitForm(type){
	document.frm_SalaryReport.exportType.value='';
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&paycycle='+paycycle;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'SalaryMonthlySummaryReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
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

function generateMonthlySalary() {
	/* var paycycle=document.frm_SalaryReport.paycycle.value;
	var wlocation=document.frm_SalaryReport.wlocation.value;
	var url='SalaryYearlySummaryPdfReports.action?wlocation='+wlocation;
	url+="&paycycle="+paycycle;
	window.location = url; */
	document.frm_SalaryReport.exportType.value='pdf';
	document.frm_SalaryReport.submit();
}

function generateReportExcel(){
	document.frm_SalaryReport.exportType.value='excel';
	document.frm_SalaryReport.submit();
}

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strTotalEmp = (String) request.getAttribute("strTotalEmp");
	String strMonth = (String) request.getAttribute("strMonth");

	Map hmEarningSalaryMap = (Map) request.getAttribute("hmEarningSalaryMap");
	Map hmDeductionSalaryMap = (Map) request.getAttribute("hmDeductionSalaryMap");
	Map hmEarningSalaryTotalMap = (Map) request.getAttribute("hmEarningSalaryTotalMap");
	Map hmDeductionSalaryTotalMap = (Map) request.getAttribute("hmDeductionSalaryTotalMap");
	Map hmSalaryHeadMap = (Map) request.getAttribute("hmSalaryHeadMap");
	Map hmEmpCode = (Map) request.getAttribute("hmEmpCode");
	Map hmEmpName = (Map) request.getAttribute("hmEmpName");
	
	String roundOffCondition = (String)request.getAttribute("roundOffCondition");
%>

<!-- Custom form for adding new records -->

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Monthly Salary Summary" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;margin-top: 10px;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size:14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
               </div>
                <!-- /.box-header -->
               <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				<s:form name="frm_SalaryReport" action="SalaryMonthlySummaryReport" theme="simple" method="post">
					<s:hidden name="exportType"></s:hidden>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
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
						</div>
					</div><br>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');"/>
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
				<a onclick="generateMonthlySalary();" href="javascript:void(0)" class="fa fa-file-pdf-o"></a>
				 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
				 <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
				 
			</div> -->
			 
			 
			 <div class="col-md-2 pull-right">
				<a onclick="generateMonthlySalary();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a>
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			</div>
			 
			<%-- <div style="text-align:center;margin:10px" > <h4>Yearly Salary Summary for the period of <%=strMonth %></h4></div> --%>
			<table cellpadding="0" cellspacing="0" width="100%" class="table table-bordered" style="margin-bottom: 5px;">
				<tr>
					<td colspan="3" align="left"><h4>Grand Summary Details</h4></td>
					<td colspan="3" align="right"><h4>Summary Report for the Month &amp; Year : <%=strMonth%></h4></td>
				</tr>

				<tr>
					<td colspan="6"><h4>Total Employee: <%=uF.showData(strTotalEmp, "0")%></h4></td>
				</tr>

				<tr>
					<td colspan="2">
						<h4>Total Gross: <%=uF.showData((String) hmEarningSalaryTotalMap.get("TOTAL"), "0")%></h4>
					</td>
					<td colspan="2">
						<h4>Total Deduction: <%=uF.showData((String) hmDeductionSalaryTotalMap.get("TOTAL"), "0")%></h4>
					</td>
					<%
						double dblE = uF.parseToDouble((String) hmEarningSalaryTotalMap.get("TOTAL"));
						double dblD = uF.parseToDouble((String) hmDeductionSalaryTotalMap.get("TOTAL"));
					%>
					<td colspan="2" align="right">
						<h4>Total Net:<%=uF.formatIntoTwoDecimal(dblE - dblD)%></h4>
					</td>
				</tr>

			</table>


			<table class="table table-bordered" style="float: left; width: 48%;">
				<tr>
					<td>Earnings</td>
					<td align="right">Amount</td>
				</tr>
				<%
					Set setE = hmEarningSalaryMap.keySet();
					Iterator itE = setE.iterator();
					while (itE.hasNext()) {
						String strSalaryHeadId = (String) itE.next();
				%>
				<tr>
					<td><%=uF.showData((String) hmSalaryHeadMap.get(strSalaryHeadId), "")%></td>
					<td align="right"><%=(String) hmEarningSalaryMap.get(strSalaryHeadId)%></td>
				</tr>
				<% } %>
				<tr>
					<td><h4>Total Earnings</h4></td>
					<td align="right"><h4><%=uF.showData((String) hmEarningSalaryTotalMap.get("TOTAL"), "0")%></h4></td>
				</tr>
			</table>


			<table class="table table-bordered" style="float: right; width: 48%;">
				<tr>
					<td>Deductions</td>
					<td align="right">Amount</td>
				</tr>
				<%
					Set setD = hmDeductionSalaryMap.keySet();
					Iterator itD = setD.iterator();
					while (itD.hasNext()) {
						String strSalaryHeadId = (String) itD.next();
				%>
				<tr>
					<td><%=uF.showData((String) hmSalaryHeadMap.get(strSalaryHeadId), "")%></td>
					<td align="right"><%=(String) hmDeductionSalaryMap.get(strSalaryHeadId)%></td>
				</tr>
				<% } %>
				<tr>
					<td><h5>Total Deductions</h5></td>
					<td align="right"><h4><%=uF.showData((String) hmDeductionSalaryTotalMap.get("TOTAL"), "0")%></h4></td>
				</tr>
			</table>
	</div>
	<!-- /.box-body -->
</div>

