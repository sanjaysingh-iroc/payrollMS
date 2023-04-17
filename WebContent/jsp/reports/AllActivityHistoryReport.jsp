<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter(); 
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
});

function submitForm(type) {
	//document.frm_AllActivityHistoryReport.exportType.value='';
	var org = document.getElementById("f_org").value;
	var strActivity = document.getElementById("strActivity").value;
	var calendarYear = document.getElementById("calendarYear").value;
	//var strMonth = document.getElementById("strMonth").value;
	//var strEmpId = document.getElementById("strEmpId").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&calendarYear='+calendarYear;//+'&strEmpId='+strEmpId+'&strMonth='+strMonth
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'AllActivityHistoryReport.action?f_org='+org+'&strActivity='+strActivity+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
}


function getEmployeeName() {
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var level = getSelectedValue("f_level");
	var strSBU = getSelectedValue("f_service");
	//alert("level ===>> " + level);
	xmlhttp = GetXmlHttpObject();
    if (xmlhttp == null) {
       alert("Browser does not support HTTP Request");
       return;
    } else {
    	var action = 'GetEmployeeList.action?fromPage=ACTIVITY_HISTORY_REPORT&f_org='+org+'&location='+location+'&strDepart='+department
    		+'&level='+level+'&strSBU='+strSBU;
    	//alert("action ===>> " + action);
       	var xhr = $.ajax({
           	url : action,
           	cache : false,
           	success : function(data) {
				if(data == "") {
				} else {
					document.getElementById("myEmployee").innerHTML = data;
				}
			}
       	});
    }
}


function GetXmlHttpObject() {
    if (window.XMLHttpRequest) {
        // code for IE7+, Firefox, Chrome, Opera, Safari
        return new XMLHttpRequest();
    }
    if (window.ActiveXObject) {
    	// code for IE6, IE5
        return new ActiveXObject("Microsoft.XMLHTTP");
    }
    return null;
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


function checkActivityAndEmployee(type) {
	document.getElementById("strActivity").selectedIndex = '0';
}

</script>

<%
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
 		<div class="desgn" style="margin-bottom: 5px; color:#232323;">
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
					<s:form name="frm_AllActivityHistoryReport" id="frm_AllActivityHistoryReport" action="AllActivityHistoryReport" theme="simple">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Activity</p>
									<s:select theme="simple" name="strActivity" id="strActivity" listKey="activityId" listValue="activityName" headerKey="" headerValue="All Activity" list="activityList" key="" onchange="submitForm('1');"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" onchange="getEmployeeName();"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" onchange="getEmployeeName();"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" onchange="getEmployeeName();"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level"listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" onchange="getEmployeeName();"/>
								</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee</p>
									<div id="myEmployee">
										<s:select name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="" headerValue="Select Employee" list="empList" key="" onchange="checkActivityAndEmployee('EMP');" />
									</div>
								</div> --%>
							</div>
						</div><br>
						 <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Calendar Year</p>
									<s:select name="calendarYear" id="calendarYear" headerKey="0" headerValue="Select Calendar Year" listKey="calendarYearId" listValue="calendarYearName" list="calendarYearList" key="" />
								</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" headerKey="0" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList" key="" />
								</div> --%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
	          <!-- /.box-body -->
			</div>
		</div>
      
	      <div style="width:100%; overflow: auto;">
			<% 
				String strActivity = (String) request.getAttribute("strActivity");
				String isDocActivity = (String) request.getAttribute("isDocActivity");
			%>	
			<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt">
				<display:column style="text-align:left;" nowrap="nowrap" title="Employee code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Designation" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(23)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Grade" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(24)%></display:column>
				<display:column style="text-align:center;" nowrap="nowrap" title="Activity" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column style="text-align:center;" nowrap="nowrap" title="Effective Date" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>

				<display:column style="text-align:right;" nowrap="nowrap" title="No. Of Days" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>

				<display:column style="text-align: right;" nowrap="nowrap" title="Increment Percentage" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>

				<display:column style="text-align:right;" nowrap="nowrap" title="Grade" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>

				<display:column style="text-align:left;" nowrap="nowrap" title="Increment Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="Increment Percentage" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>

				<display:column style="text-align:left;" nowrap="nowrap" title="Level" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Designation" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Grade" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>

				<display:column style="text-align:left;" nowrap="nowrap" title="Transfer Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Legal Entity" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(12)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Work Location" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(13)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="SBU" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(14)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(15)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Level" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(16)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Desig" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(17)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Grade" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(18)%></display:column>

				<display:column style="text-align:left;" nowrap="nowrap" title="Scale" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(22)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Reason" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column style="text-align:center;" nowrap="nowrap" title="Activity Date" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(19)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Activity By" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(20)%></display:column>

				<display:column style="text-align:center;" nowrap="nowrap" title="Document" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(21)%></display:column>

			</display:table>
		</div>
	</div>
                <!-- /.box-body -->
</div>
