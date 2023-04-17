<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>


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
$(function() {
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();

	$('#lt1').DataTable({
		dom: 'lBfrtip',
       /*  buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ] */
        buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Expense Journal Entry Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Expense Journal Entry Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Expense Journal Entry Report'
		            },
		            {
		                extend: 'print',
		                title: 'Expense Journal Entry Report'
		            }
		        ]
	});
	
});

function submitForm(type){
	document.frm_ExpenseJournalEntryReport.exportType.value='';
	var org = document.getElementById("f_org").value;
	/* var f_type = document.getElementById("f_type").value; */
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
		url: 'ExpenseJournalEntryReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	console.log(result);
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

function generateReportExcel(){
	window.location = "ExportExcelReport.action";
}

</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE); 
	
	List<List<DataStyle>> reportListExport = (List<List<DataStyle>>)request.getAttribute("reportListExport");
	if(reportListExport == null) reportListExport = new ArrayList<List<DataStyle>>();
	session.setAttribute("reportListExport",reportListExport);
%>


<!-- Custom form for adding new records -->

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title" />
</jsp:include>
 --%>

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
				<s:form name="frm_ExpenseJournalEntryReport" action="ExpenseJournalEntryReport" theme="simple" method="post">
					<s:hidden name="exportType"></s:hidden>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
						
							<%-- <div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Loan Status</p>
								<s:select name="f_type" id="f_type" onchange="submitForm('2');" list="#{'0':'All', '1':'Completed','2':'InComplete'}" key="" />
							</div> --%>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
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
						</div>
					</div>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-calendar"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" list="paycycleList" key="" onchange="submitForm('2');" />
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

		<div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
			<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
			
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			
		</div>
			
	
		<display:table name="reportList" class="table table-hover table-bordered" id="lt1">
			    <%-- <display:setProperty name="export.excel.filename" value="ExpenseJournalEntryReport.xls" />
				<display:setProperty name="export.xml.filename" value="ExpenseJournalEntryReport.xml" />
				<display:setProperty name="export.csv.filename" value="ExpenseJournalEntryReport.csv" /> --%>
			      
			    <display:column style="text-align:center;" nowrap="nowrap" title="Sr No." sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
			    <display:column style="text-align:center;" nowrap="nowrap" title="Date of Submission" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			    <display:column style="text-align:center;" nowrap="nowrap" title="Employee Code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Pan No" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Period of Expenses" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Nature of Expenses" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
				<display:column style="text-align:right;" nowrap="nowrap" title="Amount" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
				<display:column style="text-align:left;" nowrap="nowrap" title="Remarks" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
		</display:table>
	</div>
</div>
