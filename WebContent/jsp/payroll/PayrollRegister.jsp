<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
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
	 $('#lt').DataTable({
		 "order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
		dom: 'lBfrtip',
		 buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Comprehensive Salary Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Comprehensive Salary Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Comprehensive Salary Report'
		            },
		            {
		                extend: 'print',
		                title: 'Comprehensive Salary Report'
		            }
		            
		        ]
	});  
	 
			/*  $('#lt').dataTable(
			 {
			 "bSort" : false
			 });
 	 */
 /* 	$('#lt').dataTable( {
 		  "ordering": false
 		} ); */
	 
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter(); 
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
});
function submitForm(type){
	document.frm_PayPayroll.exportType.value='';
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
		+'&paycycle='+paycycle+'&strEmployeType='+strEmployeType;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'PayrollRegister.action?f_org='+org+paramValues,
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
	Map hmEmpMap = (Map) request.getAttribute("hmEmpMap");
	Map hmPayPayroll = (Map) request.getAttribute("hmPayPayroll");
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
	Map hmIsApprovedSalary = (Map) request.getAttribute("hmIsApprovedSalary");
	if (hmIsApprovedSalary == null)
		hmIsApprovedSalary = new HashMap();
	List alEarnings = (List) request.getAttribute("alEarnings");
	List alDeductions = (List) request.getAttribute("alDeductions");
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Comprehensive Pay Report" name="title"/>
</jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size:14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body"
					style="padding: 5px; overflow-y: auto; display: none;">
					<div class="content1">
						<s:form name="frm_PayPayroll" action="PayrollRegister" theme="simple" method="post">
							<s:hidden name="exportType"></s:hidden>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key="" />
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
									<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
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
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
									</div>
								</div>
							</div>
						</s:form>
					</div>
				</div>
				<!-- /.box-body  -->
			</div>
				

			<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt">
				<display:column nowrap="nowrap" title="Sr.No."><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
				<display:column nowrap="nowrap" title="Code"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column nowrap="nowrap" title="Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column nowrap="nowrap" title="Pan No"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<display:column nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
				<display:column nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column nowrap="nowrap" title="Location"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
				<display:column nowrap="nowrap" title="Designation(Grade)"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
				<display:column nowrap="nowrap" title="Joining Date"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
				<display:column nowrap="nowrap" title="Paid Days" class="alignRight rightPad50"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
				
				<%
					for (int ii = 0; ii < alEarnings.size(); ii++) {
						int count = 12 + ii;
						String strEarning = (String)hmSalaryDetails.get((String)alEarnings.get(ii))+"\n(+)";
				%>
				<display:column nowrap="nowrap" class="alignRight rightPad50" title="<%=strEarning %>"><%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<% } %>
               <display:column nowrap="nowrap" title="Gross" class="alignRight rightPad50"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>
				<%
					for (int ii = 0; ii < alDeductions.size(); ii++) {
						int count = alEarnings.size() + 12 + ii;
						String strDeduction = (String)hmSalaryDetails.get((String)alDeductions.get(ii))+"\n(-)";
				%>
				<display:column nowrap="nowrap" class="alignRight rightPad50" title="<%=strDeduction %>"><%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
				<% } %>
				<display:column nowrap="nowrap" title="Net" class="alignRight rightPad50"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
 			 
			</display:table>
			
		</div>
		<!-- /.box-body -->
	</div>


<%-- 

<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../reports/ReportNavigation.jsp"></jsp:include>
   </div> --%>