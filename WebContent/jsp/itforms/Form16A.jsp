<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">

<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
Map hmSalaryHeadMap = (Map)request.getAttribute("hmSalaryHeadMap");
Map hmEmployeeMap = (Map)request.getAttribute("hmEmployeeMap");
Map hmPayrollDetails = (Map)request.getAttribute("hmPayrollDetails");
Map hmForm16GeneratedFiles = (Map)request.getAttribute("hmForm16GeneratedFiles");

String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
	strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
}
%>
<link type="text/css" rel="stylesheet" href="<%= request.getContextPath()%>/css/highslide/highslide.css" />
<style type="text/css">
.highslide-wrapper .highslide-html-content {
    width: 650px;
}
</style>

<script type="text/javascript" charset="utf-8">

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var f_strWLocation = document.getElementById("f_strWLocation").value;
	var f_department = document.getElementById("f_department").value;
	var f_level = document.getElementById("f_level").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_level='+f_level
		+'&financialYear='+financialYear;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'Form16A.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

function generatePdf(empid) {
		var financialYear=document.frm_from16A.financialYear.value;
		var url='Form16A.action?formType=form16A&strEmpId='+empid;
		url+="&financialYear="+financialYear;
		window.location = url; 
	}
</script>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<script type="text/javascript">
	hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
	hs.outlineType = 'rounded-white';
	hs.wrapperClassName = 'draggable-header';
</script>

		<div class="box-header with-border">
			<h3 class="box-title">Form 16A for financial year <%=strFinancialYearStart%> to <%=strFinancialYearEnd%></h3>
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
					<s:form name="frm_from16A" action="Form16A" theme="simple">
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
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" headerKey="" headerValue="All Locations" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" headerKey="" headerValue="All Departments" listKey="deptId" listValue="deptName" list="departmentList" key="" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" headerKey="" headerValue="All Levels" listKey="levelId" listValue="levelCodeName" list="levelList" key="" onchange="submitForm('2');"/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
			
			
			<table width="60%" class="table table-bordered" style="margin-top: 20px;">
				<tr>
					<td class="reportHeading">Employee Name</td>
					<td class="reportHeading">Financial Year</td>
					<td class="reportHeading"></td>
				</tr> 
				<%
					Set set = hmEmployeeMap.keySet();
					Iterator it = set.iterator();
					while(it.hasNext()){
						String strEmpId = (String)it.next();
				%>
					<tr>
						<td class="reportLabel"><%=(String)hmEmployeeMap.get(strEmpId) %></td>
						<td class="reportLabel"><%=strFinancialYearStart%> - <%=strFinancialYearEnd%> </td>
						<td class="reportLabel">			
							<a href="javascript:void(0)" title="Generate Form 16" class="fa fa-file-pdf-o" onclick="generatePdf('<%=strEmpId%>')"></a>
						</td>
					</tr>
				<% }
					if(hmEmployeeMap.size()==0){
				%>
					<tr><td class="reportLabel alignCenter" colspan="5">No employee found in this financial year</td></tr>
				<% } %>        
			</table>
		</div>
		<!-- /.box-body -->
	</div>

   
