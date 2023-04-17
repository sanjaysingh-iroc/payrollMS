<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_level").multiselect();
});    

function exportpdf(){
	  window.location="ExportExcelReport.action";
}
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strFinancialYearStart = (String) request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String) request.getAttribute("strFinancialYearEnd");
	String strYear = (String) request.getAttribute("strYear");
	String strMonth = (String) request.getAttribute("strMonth");

	List<List<String>> empTDSList=(List<List<String>>)request.getAttribute("empTDSList");
	if(empTDSList==null) empTDSList=new ArrayList<List<String>>();
	
	if (strFinancialYearStart != null && strFinancialYearEnd != null) {
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
	
	List<List<DataStyle>> reportListExport = (List<List<DataStyle>>) request.getAttribute("reportListExport");;
	if(reportListExport==null) reportListExport = new ArrayList<List<DataStyle>>();
	session.setAttribute("reportListExport", reportListExport);
%>





<!-- Custom form for adding new records -->

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="TDS Report" name="title"/>
</jsp:include>
   


<div id="printDiv" class="leftbox reportWidth">


	<s:form name="frm_fromTDS" action="TDSActualReport" theme="simple">
		<div class="filter_div">
			<div class="filter_caption">Filter</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Financial Year</p>

				<s:select label="Select Financial Year" name="financialYear"
					listKey="financialYearId" listValue="financialYearName"
					headerKey="0" onchange="document.frm_fromTDS.submit();"
					list="financialYearList" key="" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Month</p>
				<s:select label="Select Month" name="strMonth" listKey="monthId"
					listValue="monthName" headerKey="1"
					onchange="document.frm_fromTDS.submit();" list="monthList" key="" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: 50%;">
				<p style="padding-left: 5px;">Organisation</p>
				<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
							cssStyle="float:left;margin-right: 10px;" listValue="orgName"
							headerKey="" headerValue="All Organisations"
							onchange="document.frm_fromTDS.submit();"
							list="organisationList" key="" />
			</div>
			<br/>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Location</p>
				<s:select theme="simple" name="f_strWLocation" id="f_strWLocation"
					listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
					listValue="wLocationName" multiple="true" list="wLocationList"
					key="" />
			</div>
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Department</p>
				<s:select name="f_department" id="f_department"
					list="departmentList" listKey="deptId"
					cssStyle="float:left;margin-right: 10px;" listValue="deptName"
					multiple="true"></s:select>
			</div>

			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">Level</p>
				<s:select theme="simple" name="f_level" id="f_level"
					listKey="levelId"
					cssStyle="float:left;margin-right: 10px;width:100px;"
					listValue="levelCodeName" multiple="true" list="levelList" key="" />
			</div>
			
			<div
				style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
				<p style="padding-left: 5px;">&nbsp;</p>
				<s:submit value="Submit" cssClass="input_button"
					cssStyle="margin:0px" />
			</div>
			
			 <!-- <a href="javascript:void(0)"
				style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right; width: 30px; position: static;"
				title="Export to Excel" class="excel" onclick="exportpdf();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->

<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

				
				
		</div>
	</s:form>



	<br/>
		
		
		<div style="text-align:center;margin:10px" > <h2>TDS Report as per Payments for the month of <%=uF.getDateFormat(strMonth, "MM", "MMMM")%> <%=uF.getDateFormat(strYear, "yyyy", "yyyy")%> </h2></div>
		
		
		<table cellpadding="3" cellspacing="0" width="100%">
			<tr>
				<td width="5%" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Sr. No.</td>
				<td width="10%" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Employee Code</td>
				<td width="25%" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Employee Name</td>
				<td width="15%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">TDS</td>
				<td width="15%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Education Cess</td>
				<td width="15%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Standard Cess</td>
				<td width="15%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Total TDS</td>
			</tr>
			
			
			<%
				int i=0;
				for(;empTDSList!=null && i<empTDSList.size();i++){
					List<String> alInner=(List<String>)empTDSList.get(i);
			%>
			
			<tr id="<%=alInner.get(0) %>">
				<td style="border-bottom:dashed 1px #ccc"><%=(i+1)%></td>
				<td style="border-bottom:dashed 1px #ccc"><%=alInner.get(1) %></td>
				<td style="border-bottom:dashed 1px #ccc"><%=alInner.get(2) %></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=alInner.get(3) %></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=alInner.get(4) %></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=alInner.get(5) %></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=alInner.get(6) %></td>
			</tr>
			<%
				}
				if (i == 0) {
			%>
			<tr>
				<td colspan="5" align="center" style="border-bottom:dashed 1px #ccc">No Employees found</td>
			</tr>
			<%
				}
			%>
			
		</table>
		
		
		
		
		
    </div>
   


<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../../reports/ReportNavigation.jsp"></jsp:include>
   </div>