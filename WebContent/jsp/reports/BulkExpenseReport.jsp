<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

 <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
 
<script type="text/javascript" charset="utf-8">

/* $(document).ready(function() {
	 
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	}); 
	
}); */

function submitForm(type) {
	var org = document.getElementById("f_org").value;
	var paycycle = document.getElementById("paycycle").value;
	var f_strWLocation = document.getElementById("f_strWLocation").value;
	var f_department = document.getElementById("f_department").value;
	var f_level = document.getElementById("f_level").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_level='+f_level+'&paycycle='+paycycle;
	}
	
	$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'BulkExpenseReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#actionResult").html(result);
   		}
	});
}


function exportXLS(){
	window.location="BulkExpenseReport.action?download=true";
 
}

</script>



<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Client Conveyance Report" name="title"/>
</jsp:include> --%>

<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmEmpNameMap = (Map<String, String>) request.getAttribute("hmEmpNameMap");
	List<String> clientList = (List<String>) request.getAttribute("clientList");
	List<String> empList = (List<String>) request.getAttribute("empList");
	Map<String, String> hmClient = (Map<String, String>) request.getAttribute("hmClient");
	Map<String, String> hmAmount = (Map<String, String>) request.getAttribute("hmAmount");
	Map<String, String> hmBillable = (Map<String, String>) request.getAttribute("hmBillable");
	Map<String, String> hmClientAmount = (Map<String, String>) request.getAttribute("hmClientAmount");

	Map<String, Map<String, String>> hmConveyanceAmount = (Map<String, Map<String, String>>) request.getAttribute("hmConveyanceAmount");
	Map<String, String> hmDept = (Map<String, String>) request.getAttribute("hmDept");
	Map<String, String> hmEmpAmount = (Map<String, String>) request.getAttribute("hmEmpAmount"); 
%>
<section class="content">
    <div class="row jscroll">

<section class="col-lg-12 connectedSortable">
<div class="box box-primary">
        		<div class="box-header with-border">
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<%-- <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;"><%=(String) request.getAttribute("selectedFilter")%></h3>
				<div class="box-tools pull-right"><button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; display: none;">
				<s:form theme="simple" method="post" name="BulkExpenseReport" action="BulkExpenseReport">
					<s:hidden name="exportType"></s:hidden>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline" style="padding-right: 0px;">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 inline" style="padding-left: 0px;">
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Paycycle</p>
								<s:select theme="simple" label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" onchange="submitForm('2');" list="payCycleList" key="" />
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" onchange="submitForm('2');"/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div> --%>
		
		<div class="col-md-2 pull-right">
			<a onclick="exportXLS();" href="javascript:void(0)" style="float: right;"><i class="fa fa-file-excel-o"></i></a>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12">
			
			<table class="table table-bordered" id="lt1">
			<tr>
			 <th></th>
            <th></th>
			<th class="alignCenter" nowrap colspan="4">Travel</th>
			<th class="alignCenter" nowrap colspan="2">Local</th>
            <th></th>
            <th></th>
            <th></th>
            <th></th>
			<th class="alignCenter" nowrap colspan="2">Other Expenses</th>
			</tr>
				<tr>
					<th class="alignCenter" nowrap>Date</th>
					<th class="alignCenter" nowrap>Purpose</th>
					<th class="alignCenter" nowrap>Flight</th>
					<th class="alignCenter" nowrap>Train</th>
					<th class="alignCenter" nowrap>Car</th>
					<th class="alignCenter" nowrap>Bus</th>
					<th class="alignCenter" nowrap>Amount</th>
					<th class="alignCenter" nowrap>Details</th>
					<th class="alignCenter" nowrap>Mobile Bill</th>
					<th class="alignCenter" nowrap>Internet Charges</th>
					<th class="alignCenter" nowrap>Food Expenses</th>
					<th class="alignCenter" nowrap>Lodging Expenses</th>
					<th class="alignCenter" nowrap>Amount</th>
					<th class="alignCenter" nowrap>Details</th>
					<th class="alignCenter" nowrap>Total Amount</th>
				</tr>

				<tr>
					<td class="alignLeft" nowrap>12/01/18</td>
					<td class="alignLeft" nowrap>test</td>
					<td class="alignLeft" nowrap>1200</td>
					<td class="alignCenter" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap>150</td>
					<td class="alignRight" nowrap>Snacks</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>1000</td>
					<td class="alignRight" nowrap>3000</td>
					<td class="alignRight" nowrap>4000</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>travel</td>
				</tr>
				<tr>
					<td class="alignLeft" nowrap>10/01/18</td>
					<td class="alignLeft" nowrap>test</td>
					<td class="alignLeft" nowrap>1200</td>
					<td class="alignCenter" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap>150</td>
					<td class="alignRight" nowrap>Snacks</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>1000</td>
					<td class="alignRight" nowrap>3000</td>
					<td class="alignRight" nowrap>4000</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>travel</td>
				</tr>
				<tr>
					<td class="alignLeft" nowrap>25/02/18</td>
					<td class="alignLeft" nowrap>test</td>
					<td class="alignLeft" nowrap>1200</td>
					<td class="alignCenter" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap>150</td>
					<td class="alignRight" nowrap>Snacks</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>1000</td>
					<td class="alignRight" nowrap>3000</td>
					<td class="alignRight" nowrap>4000</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>travel</td>
				</tr>
				<tr>
					<td class="alignLeft" nowrap>26/02/18</td>
					<td class="alignLeft" nowrap>test</td>
					<td class="alignLeft" nowrap>1200</td>
					<td class="alignCenter" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap>100</td>
					<td class="alignRight" nowrap>Snacks</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>2000</td>
					<td class="alignRight" nowrap>1000</td>
					<td class="alignRight" nowrap>900</td>
					<td class="alignRight" nowrap>400</td>
					<td class="alignRight" nowrap>travel</td>
				</tr>
				<tr>
					<td class="alignLeft" nowrap>28/01/18</td>
					<td class="alignLeft" nowrap>test</td>
					<td class="alignLeft" nowrap>1200</td>
					<td class="alignCenter" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap>50</td>
					<td class="alignRight" nowrap>Snacks</td>
					<td class="alignRight" nowrap>200</td>
					<td class="alignRight" nowrap>700</td>
					<td class="alignRight" nowrap>2000</td>
					<td class="alignRight" nowrap>1000</td>
					<td class="alignRight" nowrap>300</td>
					<td class="alignRight" nowrap>travel</td>
				</tr>
				<tr>
					<td class="alignLeft" nowrap>23/09/17</td>
					<td class="alignLeft" nowrap>test</td>
					<td class="alignLeft" nowrap>1400</td>
					<td class="alignCenter" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap></td>
					<td class="alignRight" nowrap>100</td>
					<td class="alignRight" nowrap>Snacks</td>
					<td class="alignRight" nowrap>500</td>
					<td class="alignRight" nowrap>100</td>
					<td class="alignRight" nowrap>2000</td>
					<td class="alignRight" nowrap>5000</td>
					<td class="alignRight" nowrap>800</td>
					<td class="alignRight" nowrap>travel</td>
				</tr>
				 <tr><td colspan="15">Total Payment/Refund</td></tr>
			</table>
		</div>
	</div>
</div></div>
</section>
</div></section>