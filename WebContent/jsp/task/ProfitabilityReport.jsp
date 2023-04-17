<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
			
			
function generateSalaryExcel(){
	window.location="ExportExcelReport.action"; 
}

$(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
    $( "#strStartDate" ).datepicker({format: 'dd/mm/yyyy'});
    $( "#strEndDate" ).datepicker({format: 'dd/mm/yyyy'});
});


function submitForm(type) {
	var data = "";
	if(type == '1') {
		var f_org = document.getElementById("f_org").value;
		data = 'f_org='+f_org;
	} else if(type == '2') {
		data = $("#frmProfitabilityReport").serialize();
	}
	$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ProfitabilityReport.action',
		data: data,
		success: function(result){
        	$("#actionResult").html(result);
   		}
	});
}
</script>


	<%
		UtilityFunctions uF = new UtilityFunctions();
		List alOuter = (List)request.getAttribute("alOuter");
		List<List<DataStyle>> reportListExport =(List<List<DataStyle>>)request.getAttribute("reportListExport");
		session.setAttribute("reportListExport",reportListExport);
		//System.out.println("reportListExport ====>>> " + reportListExport);
	%>

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
                    <s:form action="ProfitabilityReport" name="frmProfitabilityReport" id="frmProfitabilityReport" theme="simple" method="post">
                        <s:hidden name="exportType"></s:hidden>
                        <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                                	<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
                                	<s:select name="strWLocation" listKey="wLocationId" headerKey="" headerValue="All Location" listValue="wLocationName" onchange="submitForm('2');" list="wlocationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
                               		<s:select name="strDept" listKey="deptId" headerKey="" headerValue="All Department" listValue="deptName" onchange="submitForm('2');" list="departmentList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
                                	<s:select name="strService" listKey="serviceId" headerKey="" headerValue="All Service" listValue="serviceName" onchange="submitForm('2');" list="serviceList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Client</p>
                                	<s:select name="strClient" listKey="clientId" headerKey="" headerValue="All Client" listValue="clientName" onchange="submitForm('2');" list="clientList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project Owner</p>
									<s:select name="strOwner" listKey="employeeId" headerKey="" headerValue="All Owner" listValue="employeeName" onchange="submitForm('2');" list="ownerList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Report Type</p>
									<s:select name="reportType" list="#{'1':'ClientWise', '2':'DivisionWise','3':'ProjectWise'}" onchange="submitForm('2');"/>
			             		</div>
							</div>
						</div>
						
						<div class="row row_without_margin" style="margin-top: 10px;">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								
								<div id="fromToDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<!-- <p style="padding-left: 5px;">&nbsp;</p> -->
									<input type="text" name="strStartDate" id="strStartDate" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
									<input type="text" name="strEndDate" id="strEndDate" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
					      		</div>
					      		
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<!-- <p style="padding-left: 5px;">&nbsp;</p> -->
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
                    </s:form>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
        
        <div class="col-md-2 pull-right">
			<a onclick="generateSalaryExcel();" href="javascript:void(0)" style="float: right;"><i class="fa fa-file-excel-o"></i></a>
		</div>
        
		<%
			String reportType=(String)request.getAttribute("reportType");
			String reportName="Project Name";
			if(reportType!=null && reportType.equals("1")) {
				reportName="Client Name";
			} else if(reportType!=null && reportType.equals("2")) {
				reportName="Division Name";
			} else {  
				reportName="Project Name";
			}
		%>

		<display:table name="alOuter" cellspacing="1" class="table table-bordered" id="lt1">	
			<display:column nowrap="nowrap" title="<%=reportName %>"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<display:column nowrap="nowrap" title="Actual Amount" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>	
			<display:column nowrap="nowrap" title="Billed Amount" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
			<display:column nowrap="nowrap" title="Amount Received" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
			<display:column title="Profitability (w.r.t. Actual)" styleClass="alignRight padRight20"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
			<display:column title="Profitability (w.r.t. Billing)" align="center"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
			
		</display:table>
	</div>