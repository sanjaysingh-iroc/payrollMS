<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	$("#f_wLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
	$("#f_grade").multiselect().multiselectfilter();
});
function generateReportExcel(){
	window.location = "ExportExcelReport.action";
}

function submitForm(type){
	document.frm.exportType.value='';
	if(type == '1'){
		document.getElementById("strWeek").selectedIndex = "0";
	}
	var org = document.getElementById("f_org").value;
	var calendarYear = document.getElementById("calendarYear").value;
	var strMonth = document.getElementById("strMonth").value;
	var strWeek = document.getElementById("strWeek").value;
	var location = getSelectedValue("f_wLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	
	var paramValues = "";
	if(type == '1' || type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&calendarYear='+calendarYear+'&strMonth='+strMonth+'&strWeek='+strWeek+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'WeeklyHoursReport.action?f_org='+org+paramValues,
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

</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	
	List<String> alDates = (List<String>)request.getAttribute("alDates");
	if(alDates == null) alDates = new ArrayList<String>();
	
	String strDateFormt = (String)request.getAttribute("DATE_FORMAT");
	
	List<List<DataStyle>> reportListExport = (List<List<DataStyle>>)request.getAttribute("reportListExport");
	if(reportListExport == null) reportListExport = new ArrayList<List<DataStyle>>();
	session.setAttribute("reportListExport", reportListExport);
%>

 <%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	
		<div class="desgn" style="margin-bottom: 5px;color:#232323;">
            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <s:form name="frm" id="frm" action="WeeklyHoursReport" theme="simple">
                        <s:hidden name="exportType"></s:hidden>
                        <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                                	<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('0');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
                                	<s:select name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
                                	<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
								</div>

								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
                               		<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
                                	<s:select theme="simple" name="f_level" id="f_level"listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Grade</p>
									<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
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
									<p style="padding-left: 5px;">Calendar Year</p>
									<s:select label="Select Calendar Year" name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" 
									headerKey="0" onchange="submitForm('1');" list="calendarYearList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" onchange="submitForm('2');" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Week</p>
									<s:select theme="simple" name="strWeek" id="strWeek" list="weekDayList" listKey="weekDayId" listValue="weekDayName" key="" headerKey="" headerValue="Select Week" onchange="submitForm('2');"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
                    </s:form>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
<BR>
		<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
	<!-- 		<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
	<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div>
			<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt">
			    <display:setProperty name="export.excel.filename" value="WeeklyHoursReport.xls" />
				<display:setProperty name="export.xml.filename" value="WeeklyHoursReport.xml" />
				<display:setProperty name="export.csv.filename" value="WeeklyHoursReport.csv" />
			    <display:column style="text-align:center;" nowrap="nowrap" title="Employee Code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Manager's Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<%
					int nDateSize = alDates.size();
					for(int i = 0; i < nDateSize; i++){
						int count = 4 + i;
						String strDate = uF.getDateFormat(alDates.get(i),IConstants.DATE_FORMAT, "EEEE") + "<br/>("+uF.getDateFormat(alDates.get(i),IConstants.DATE_FORMAT, strDateFormt) + ")";
				%>
						<display:column style="text-align:right;" title="<%=strDate %>"> <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<%	} 
					int totalOT = 4 + nDateSize;
				%>
				<display:column style="text-align:right;" nowrap="nowrap" title="Total Hours Worked" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(totalOT)%></display:column>
			</display:table>
	</div>

</div>