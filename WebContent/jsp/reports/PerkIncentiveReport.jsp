<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>

<div id="divResult">

<%-- <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>  --%>
 
 <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script>
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script>  
 
<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();

	$('#lt1').DataTable({
		dom: 'lBfrtip',
		 buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Perk Incentive Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Perk Incentive Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Perk Incentive Report'
		            },
		            {
		                extend: 'print',
		                title: 'Perk Incentive Report'
		            }
		        ]
	});
	
});

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var financialYear = document.getElementById("financialYear").value;
	var strMonth = document.getElementById("strMonth").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
			+'&financialYear='+financialYear+'&strMonth='+strMonth+'&strEmployeType='+strEmployeType;
	}
	
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PerkIncentiveReport.action?f_org='+org+paramValues,
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

</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
%>

<!-- Custom form for adding new records -->

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
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
					<s:form name="frm_fromPerkIncentiveReport" action="PerkIncentiveReport" theme="simple">
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
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
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
							</div>
						</div>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" onchange="submitForm('2');" list="financialYearList" key="" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" onchange="submitForm('2');" list="monthList" key=""/>
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div>
							</div>
						</div>
					</s:form>
				</div>
				<!-- /.box-body -->
			</div>
		

			<display:table name="reportList" class="table table-bordered" id="lt1">
				<%-- <display:setProperty name="export.excel.filename" value="PerkIncentiveReport.xls" />
				<display:setProperty name="export.xml.filename" value="PerkIncentiveReport.xml" />
				<display:setProperty name="export.csv.filename" value="PerkIncentiveReport.csv" /> --%>
				
				<display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
				<display:column style="text-align:left;" valign="top" title="Perk Policy"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
				<display:column style="text-align:center;" valign="top" title="Perk Payment Type"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
				<display:column style="text-align:right;" valign="top" title="Perk Limit"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Approveed By"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
				<display:column style="text-align:center;" valign="top" title="Approved Date"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Paid By"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
				<display:column style="text-align:center;" valign="top" title="Paid Date"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
				<display:column style="text-align:right;" valign="top" title="Paid Amount"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
			</display:table>
		</div>
		<!-- /.box-body -->
	</div>


