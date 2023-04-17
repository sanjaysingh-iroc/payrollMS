<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	var strMonth = document.getElementById("strMonth").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&financialYear='+financialYear+'&strSelectedEmpId='+strSelectedEmpId+'&strMonth='+strMonth;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form24Q.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

	function generateForm24Q() {

		var financialYear = document.frm_Form24Q.financialYear.value;
		var strSelectedEmpId = document.frm_Form24Q.strSelectedEmpId.value;
		var strMonth = document.frm_Form24Q.strMonth.value;

		var url = 'Form24Q.action?formType=txt&financialYear='+ financialYear;
		url += "&strSelectedEmpId=" + strSelectedEmpId+"&strMonth="+strMonth;
		window.location = url;
	}
</script>

<style type="text/css">
.tg  {border-collapse:collapse;border-spacing:0; margin-left:30px; margin-right:40px; margin-bottom:20px; margin-top:10px; }
.tg td
{
	font-family:Arial, sans-serif;
	font-size:14px;
	padding:10px 5px;
	border-style:solid;
	border-width:1px;
	overflow:hidden;
	word-break:normal;
}
.tg th
{
	font-family:Arial, sans-serif;
	font-size:14px;
	font-weight:normal;
	padding:10px 5px;
	border-style:solid;
	text-align:left;
	border-width:1px;
	overflow:hidden;
	word-break:normal;
}
.tg .tg-s6z2{text-align:center}

.tg1  {border-collapse:collapse;border-spacing:0; margin-left:21px; margin-top:10px;}
.tg1 td{font-family:Arial, sans-serif;font-size:14px;padding:7px 11px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg1 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:7px 11px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg1 .tg-s6z21
{
	text-align:center;
	width:54%;
}



.tg2  {border-collapse:collapse;border-spacing:0; margin:10px 40px 20px 30px;}
.tg2 td{font-family:Arial, sans-serif;font-size:14px;padding:7px 11px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg2 th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:7px 11px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;}
.tg2 .tg-s6z23{text-align:center}
</style>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");

	List alMonth = (List) request.getAttribute("alMonth");

	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd,IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}

	String strQuarter = (String) request.getAttribute("strQuarter");
	
	Map<String, String> hmOrg = (Map<String, String>) request.getAttribute("hmOrg");
	if(hmOrg == null) hmOrg = new HashMap<String, String>();
	Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
	if(hmEmp == null) hmEmp = new HashMap<String, String>();
	
	Map<String, String> hmStates = (Map<String, String>) request.getAttribute("hmStates");
	if(hmStates == null) hmStates = new HashMap<String, String>();
	Map<String, String> hmEmpCodeDesig = (Map<String, String>) request.getAttribute("hmEmpCodeDesig");
	if(hmEmpCodeDesig == null) hmEmpCodeDesig = new HashMap<String, String>();
	
	Map<String, String> hmOtherDetailsMap = (Map<String, String>) request.getAttribute("hmOtherDetailsMap");
	if(hmOtherDetailsMap == null) hmOtherDetailsMap = new HashMap<String, String>();
	
	String empCount = (String) request.getAttribute("empCount");
	String challanAmt = (String) request.getAttribute("challanAmt");
	String paidAmt = (String) request.getAttribute("paidAmt");
	String paidTDSAmt = (String) request.getAttribute("paidTDSAmt");
	
%>
<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="e-Tds Return" name="title"/>
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
					<s:form name="frm_Form24Q" action="Form24Q" theme="simple">
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
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" onchange="submitForm('2');" list="monthList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
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
			
			<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
				 <%--<a href="javascript:void(0)" style="background-image: url('images1/file-txt.png'); background-repeat: no-repeat; float: right; width: 30px; position: static;" title="Txt" onclick="generateForm24Q();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> --%>
				 <a href="javascript:void(0)" style="background-repeat: no-repeat; float: right; width: 30px; position: static;" title="Txt" onclick="generateForm24Q();"><i class="fa fa-file-text-o" aria-hidden="true"></i></a>
			</div>
			
		</div>
		<!-- /.box-body -->
	</div>


