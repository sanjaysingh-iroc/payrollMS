<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

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
 
	/* $('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});  */
	
	 $('#lt1').DataTable({
	        dom: 'lBfrtip',
	        buttons: [
	            'copy',
	            {
	                extend: 'csv',
	                title: 'Client Conveyance Report'
	            },
	            {
	                extend: 'excel',
	                title: 'Client Conveyance Report'
	            },
	            {
	                extend: 'pdf',
	                title: 'Client Conveyance Report'
	            },
	            {
	                extend: 'print',
	                title: 'Client Conveyance Report'
	            }
	        ]
	    });
	    
	 	$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		
});


function submitForm(type) {
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var level = getSelectedValue("f_level");
	var paycycle = document.getElementById("paycycle").value;
	/* var f_strWLocation = document.getElementById("f_strWLocation").value;
	var f_department = document.getElementById("f_department").value;
	var f_level = document.getElementById("f_level").value; */
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strLevel='+level+'&paycycle='+paycycle;
	}
	
	$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ClientConveyanceReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#actionResult").html(result);
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


function exportXLS(){
  var paycycle=document.getElementById("paycycle").value;
  var f_strWLocation=document.frm_ClientConveyanceReport.f_strWLocation.value;
	var f_department=document.frm_ClientConveyanceReport.f_department.value;
	var f_level=document.frm_ClientConveyanceReport.f_level.value;
	var f_org=document.frm_ClientConveyanceReport.f_org.value;
	
	var url="ClientConveyanceReport.action?paycycle="+paycycle+"&type=excel";
	url+="&f_strWLocation="+f_strWLocation+"&f_department="+f_department;
	url+="&f_level="+f_level+"&f_org="+f_org;
	
	window.location=url;
  //window.location="ClientConveyanceReport.action?paycycle="+paycycle+"&type=excel";
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
				<s:form theme="simple" method="post" name="frm_ClientConveyanceReport" action="ClientConveyanceReport">
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
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" /> <!-- onchange="submitForm('2');" -->
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true" /> <!-- onchange="submitForm('2');" -->
							</div>
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>  <!-- onchange="submitForm('2');" -->
							</div>
							
							<div class="col-lg-1 col-md-6 col-sm-12 autoWidth inline paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');" />
							</div>
							
							
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>
		
		<div class="col-md-2 pull-right">
			<a onclick="exportXLS();" href="javascript:void(0)" style="float: right;"><i class="fa fa-file-excel-o"></i></a>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12">
			<% if (empList != null && empList.size() > 0) { %>
			<table class="table table-bordered" id="lt1">
				<thead>
					<tr>
						<th class="alignCenter" nowrap>Sr.No.</th>
						<th class="alignCenter" nowrap>Client</th>
						<th class="alignCenter" nowrap>Department</th>
						<th class="alignCenter" nowrap>Chargeable Y/N</th>
						<th class="alignCenter" nowrap>Total</th>
						<% for (int i = 0; empList != null && i < empList.size(); i++) { %>
						<th class="alignCenter" nowrap><%=hmEmpNameMap.get(empList.get(i).trim())%></th>
						<% } %>
					</tr>
				</thead>
				<tbody>
					<%
						int i = 0;
						Iterator<String> it = hmConveyanceAmount.keySet().iterator();
						double clientTotalAmount = 0;
						while (it.hasNext()) {
							String key = it.next();
							Map<String, String> hmInner = hmConveyanceAmount.get(key);
							i++;
							clientTotalAmount += uF.parseToDouble(hmClientAmount.get(hmInner.get("CLIENT_ID") + "_"+ hmInner.get("DEPART_ID") + "_"+ hmInner.get("IS_BILLABLE")));
					%>
					<tr>
						<td class="alignLeft" nowrap><%=i%></td>
						<td class="alignLeft" nowrap><%=hmClient.get(hmInner.get("CLIENT_ID"))%></td>
						<td class="alignLeft" nowrap><%=hmDept.get(hmInner.get("DEPART_ID"))%></td>
						<td class="alignCenter" nowrap><%=hmInner.get("IS_BILLABLE")%></td>
						<td class="alignRight" nowrap><%=uF.showData(hmClientAmount.get(hmInner.get("CLIENT_ID") + "_" + hmInner.get("DEPART_ID") + "_" + hmInner.get("IS_BILLABLE")), "0.00")%></td>
						<% for (int j = 0; empList != null && j < empList.size(); j++) { %>
							<td class="alignRight" nowrap><%=uF.showData(hmAmount.get(hmInner.get("CLIENT_ID") + "_" + hmInner.get("DEPART_ID") + "_" + hmInner.get("IS_BILLABLE") + "_" + empList.get(j)), "")%></td>
						<% } %>
					</tr>
					<% } %>
				
					<tr>
						<td class="alignLeft" nowrap>&nbsp;</td>
						<td class="alignLeft" nowrap>&nbsp;</td>
						<td class="alignLeft" nowrap>&nbsp;</td>
						<td class="alignCenter" nowrap><strong>Total</strong>
						</td>
						<td class="alignRight" nowrap><strong><%=uF.formatIntoTwoDecimalWithOutComma(clientTotalAmount)%></strong>
						</td>
						<% for (int j = 0; empList != null && j < empList.size(); j++) { %>
						<td class="alignRight" nowrap><strong><%=uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmEmpAmount.get(empList.get(j))))%></strong>
						</td>
						<% } %>
					</tr>
				</tbody>	
			</table>
			<% } else { %>
			<div class="nodata msg">No Data Found</div>
			<% } %>
		</div>
	</div>
