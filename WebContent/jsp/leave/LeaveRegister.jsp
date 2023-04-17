<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<style>
.ui-state-default{
	padding-top: 6px;
	padding-bottom: 7px;
	padding-right: 21px; 
	padding-left: 25px;
} 
.legend-info>div { padding: 0px 2px; }
</style>
<script type="text/javascript" charset="utf-8">

function submitForm(type){
	var paycycle = document.getElementById("paycycle").value;
	/* var calendarYear = document.getElementById("calendarYear").value;
	var strMonth = document.getElementById("strMonth").value; */
	var org = "";
	var location = "";
	var department = "";
	var service = "";
	if(document.getElementById("f_org")) {
		org = document.getElementById("f_org").value;
	}
	if(document.getElementById("f_strWLocation")) {
		location = getSelectedValue("f_strWLocation");
	}
	if(document.getElementById("f_department")) {
		department = getSelectedValue("f_department");
	}
	if(document.getElementById("f_service")) {
		service = getSelectedValue("f_service");
	}
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+"&paycycle="+paycycle; //+'&calendarYear='+calendarYear+'&strMonth='+strMonth
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LeaveRegister.action?f_org='+org+paramValues,
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


function generateReportPdf() {
	var url = 'ExportPdfReport.action';
	window.location = url;
}

function generateReportExcel() {
	var url = 'ExportExcelReport.action';
	window.location = url;
}

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);

	List alDates = (List) request.getAttribute("alDates");
	Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
	Map hmLeavesName = (Map) request.getAttribute("hmLeavesName");

	List alLegends = (List) request.getAttribute("alLegends");
%>


<!-- Custom form for adding new records -->

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Leave Calendar" name="title"/>
</jsp:include> --%>

		<div class="box-body" style="padding: 20px 5px 5px; overflow-y: auto;">
			<s:form name="frm_Leave" action="LeaveRegister" cssStyle="float:left;width:100%;" theme="simple">
				<div class="box box-default">  <!-- collapsed-box -->
					<%-- <div class="box-header with-border">
					    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					    <div class="box-tools pull-right">
					        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					    </div>
					</div> --%>
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
						<% if (strUserType != null && !strUserType.equals(IConstants.MANAGER)) { %>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true" />
								</div>

								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true" />
								</div>
							</div>
						</div><br>
						<% } %>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycycle</p>
									<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" list="paycycleList" key=""/>
								</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Calendar Year</p>
									<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" list="calendarYearList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" />
								</div> --%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
					</div>
				</div>
							
			</s:form>
			

			<!-- <div class="col-md-12" style="margin: 0px 0px 10px 0px; text-align: right;">
				<a title="Regularise Leave Balance" href="LeaveRegularise.action">Regularise Leave Balance</a>
			</div> -->

			<div class="col-md-12 export-table">
				<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt">
	
					<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
					<display:column style="text-align:left" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
					<%
						for (int ii = 0; ii < alDates.size(); ii++) {
							int count = 2 + ii;
							String strDate = uF.getDateFormat((String) alDates.get(ii), IConstants.DATE_FORMAT, "dd");
					%>
					<display:column title="<%=strDate%>">
						<%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
					<% } %>
				</display:table>
			</div>
		
		
			<div class="col-md-12" style="margin: 0px 0px 10px 0px;display:none;" text-align: right;">
				<display:table name="reportListPrint" class="table table-bordered overflowtable"  id="lt1" >
					<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
					<display:column style="text-align:left" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
					<%
						for (int ii = 0; ii < alDates.size(); ii++) {
							int count = 2 + ii;
							String strDate = uF.getDateFormat((String) alDates.get(ii), IConstants.DATE_FORMAT, "dd");
					%>
					<display:column title="<%=strDate %>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
					<% } %>
				</display:table>
			</div>
			<div class="custom-legends">
				<%
					Set set = hmLeavesColour.keySet();
					Iterator it = set.iterator();
					while (it.hasNext()) {
						String strLeave = (String) it.next();
				%>
					 <div class="custom-legend" style="border-color:<%=(String) hmLeavesColour.get(strLeave)%>">
					    <div class="legend-info"><%=(String) hmLeavesName.get(strLeave)%>[<%=strLeave%>]</div>
					  </div>
				<% } %>
				 <% for (int i = 0; i < alLegends.size(); i++) { %>
					
						<%=alLegends.get(i)%>
					
				<% } %>
			</div>
		</div>
		
<script>
$(function(){
	$("#lt").DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	});

	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
});

</script>