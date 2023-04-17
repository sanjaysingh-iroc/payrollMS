<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="com.konnect.jpms.reports.ReportList"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">

 <%-- <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>  
 --%>
 
 <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script> 
 
 
<script type="text/javascript" charset="utf-8">
$(function() {
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
	
	$("#strStartDate").datepicker({
	    format: 'dd/mm/yyyy',
	    autoclose: true 
	}).on('changeDate', function (selected) {
	    var minDate = new Date(selected.date.valueOf());
	    $('#strEndDate').datepicker('setStartDate', minDate);
	});

	$("#strEndDate").datepicker({
		format: 'dd/mm/yyyy',
		autoclose: true
	}).on('changeDate', function (selected) {
	        var minDate = new Date(selected.date.valueOf());
	        $('#strStartDate').datepicker('setEndDate', minDate);
	});
	$('#lt').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
		buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Gross vs Net Salary Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Gross vs Net Salary Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Gross vs Net Salary Report'
		            },
		            {
		                extend: 'print',
		                title: 'Gross vs Net Salary Report'
		            }
		        ]
  	});
});

function submitForm(type){
	/* document.frm_DepartmentwiseReport.exportType.value=''; */
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strEmployeType = getSelectedValue("f_employeType");
	var paycycleDate = getCheckedValue("paycycleDate");
	var paramValues = "";
	
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&paycycleDate='+paycycleDate
		+'&strEmployeType='+strEmployeType;
	}
	
	if(paycycleDate == '1') {
		paramValues = paramValues+'&paycycle='+paycycle;
	} else if(paycycleDate == '2') {
		paramValues = paramValues+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate;
	}
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DepartmentwiseReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
}


function getCheckedValue(checkId) {
    var radioObj = document.getElementsByName(checkId);
    var radioLength = radioObj.length;
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
	
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


function generateSalaryExcel(){
	window.location="ExportExcelReport.action?type=type";
}


</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmEmpMap = (Map) request.getAttribute("hmEmpMap");
	Map hmPayPayroll = (Map) request.getAttribute("hmPayPayroll");
	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");
	Map hmIsApprovedSalary = (Map) request.getAttribute("hmIsApprovedSalary");
	if (hmIsApprovedSalary == null) hmIsApprovedSalary = new HashMap();
	List<ComparatorWeight> alEarnings = (List<ComparatorWeight>) request.getAttribute("alEarnings");
	if (alEarnings == null) alEarnings = new ArrayList<ComparatorWeight>();
	List<ComparatorWeight> alDeductions = (List<ComparatorWeight>) request.getAttribute("alDeductions");
	if (alDeductions == null) alDeductions = new ArrayList<ComparatorWeight>();

	List alReportList = (List) request.getAttribute("reportList");
	if (alReportList == null) alReportList = new ArrayList();

	List<List<DataStyle>> reportListExport = (List<List<DataStyle>>) request.getAttribute("reportListExport");

	session.setAttribute("reportListExport", reportListExport);
%>

<%-- 

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>

<script type="text/javascript">
$(function(){
	$("#idWLoc").multiselect();
	$("#idService").multiselect();
	$("#idDept").multiselect();
	$("#idLevel").multiselect();
});
</script> --%>



<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Departmentwise Report" name="title"/>
</jsp:include> --%>

		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
				<%
					String paycycleDate = (String) request.getAttribute("paycycleDate");
					String check1 = "";
					String check2 = "";
					if (paycycleDate != null && paycycleDate.equals("2")) {
						check1 = "";
						check2 = "checked=\"checked\"";
					} else {
						check1 = "checked=\"checked\"";
						check2 = "";
					}
				%>

			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size:14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
	                <!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<s:form name="frm_DepartmentwiseReport" id="frm_DepartmentwiseReport" action="DepartmentwiseReport" theme="simple">
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
									<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
								</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
									<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key=""/>
								</div> --%>
							</div>
						</div><br>
						<div class="row row_without_margin">
							
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0" style="margin-left: 10px;">
								 <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<input type="radio" name="paycycleDate" id="paycycleDate" value="1" <%=check1%> />
									<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<input type="radio" name="paycycleDate" id="paycycleDate" value="2" <%=check2%> />
									<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 100px !important;"/>
									<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
								</div> 
							</div>
						</div>
					</s:form>
				</div>
	            <!-- /.box-body -->
			</div>
			<%if(alReportList != null && !alReportList.isEmpty() && alReportList.size()>0) {%>		
				<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
					<a onclick="generateSalaryExcel();" href="javascript:void(0)" style="background-repeat: no-repeat; float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
				</div>
			<%} %>
			<table class="table table-bordered" id="lt">
				<thead>
				<tr>
					<th class="alignCenter">Sr. No.</th>
					<th nowrap="nowrap" class="alignCenter">UAN</th>
					<th nowrap="nowrap" class="alignCenter">Employee Code</th>
					<th nowrap="nowrap" class="alignCenter">Employee Name</th>
					<th nowrap="nowrap" class="alignCenter">Employee PAN No.</th><!-- added by parvez -->
					<th nowrap="nowrap" class="alignCenter">Joining Date</th>
					<th nowrap="nowrap" class="alignCenter">Paid Days</th>
					<th nowrap="nowrap" class="alignCenter">Department</th>

					<%
						boolean flag = false;
						for (int ii = 0; alEarnings != null && ii < alEarnings.size(); ii++) {
							ComparatorWeight comparatorWeight = alEarnings.get(ii);

							if (flag != comparatorWeight.isVariable()) {
								flag = true;
					%>
					<!-- <th class="alignCenter">Gross Earning</th> -->
					<% } %>
					<th nowrap="nowrap" class="alignCenter"><%=(String) hmSalaryDetails.get(comparatorWeight.getStrName()) + "(+)"%></th>

					<% } %>
					<th class="alignCenter">Gross Salary</th>
					<% for (int ii = 0; alDeductions != null && ii < alDeductions.size(); ii++) { %>
					<th nowrap="nowrap" class="alignCenter"><%=(String) hmSalaryDetails.get(((ComparatorWeight) alDeductions.get(ii)).getStrName())+ "(-)"%></th>
					<% } %>
					<th class="alignCenter">Net</th>
				</tr>
				</thead>
				<tbody>
				<%
					double dblGrossAmountTotal = 0;
					double dblTDSAmountTotal = 0;
					for (int i = 0; alReportList != null && i < alReportList.size(); i++) {
						List alReportInner = (List) alReportList.get(i);
				%>

				<tr>
					<td class="alignCenter"><%=uF.showData((String) alReportInner.get(0), "")%></td>
					<td nowrap="nowrap" class="alignCenter"><%=uF.showData((String) alReportInner.get(1), "")%></td>
					<td nowrap="nowrap" class="alignCenter" nowrap="nowrap"><%=uF.showData((String) alReportInner.get(2), "")%></td>
					<td nowrap="nowrap" class="alignCenter"><%=uF.showData((String) alReportInner.get(3), "")%></td>
					<td nowrap="nowrap" class="alignCenter"><%=uF.showData((String) alReportInner.get(4), "")%></td>
					<td nowrap="nowrap" class="alignCenter" nowrap="nowrap"><%=uF.showData((String) alReportInner.get(5), "")%></td>
					<td nowrap="nowrap" class="alignCenter"><%=uF.showData((String) alReportInner.get(6), "")%></td>
				
					<% for (int j = 7; j < alReportInner.size(); j++) {%>
						<td class="alignCenter"><%=(String) alReportInner.get(j)%></td>
					<% } %>
					
				</tr>
				<% }%>
				</tbody>
			</table>
		</div>
		<!-- /.box-body -->
	</div>


