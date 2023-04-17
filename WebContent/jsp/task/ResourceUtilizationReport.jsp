<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<% String strUserType =(String) session.getAttribute(IConstants.BASEUSERTYPE); %>

<script type="text/javascript" charset="utf-8">
function generateReportInExcel() {
	window.location="ExportExcelReport.action?type=type";
}

$(function() {
	$('#lt1').DataTable({ 
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
});


function submitForm(type){
	document.frmResourceUtilizationReport.exportType.value='';
	var data = "";
	if(type == '1') {
		var f_org = document.getElementById("f_org").value;
		data = 'f_org='+f_org;
	} else if(type == '2') {
		data = $("#frmResourceUtilizationReport").serialize();
	} else if(type == '3') {
		data = $("#frmResourceUtilizationReport").serialize();
	}
	var divResult = 'actionResult';
	$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ResourceUtilizationReport.action',
		data: data,
		success: function(result){
        	$("#"+divResult).html(result);
        	$("#f_strWLocation").multiselect().multiselectfilter();
        	$("#f_department").multiselect().multiselectfilter();
        	$("#f_service").multiselect().multiselectfilter();
        	$("#f_level").multiselect().multiselectfilter();
        //===start parvez date: 04-01-2022=== 	
        	$("#pro_id").multiselect().multiselectfilter();
        	$("#client").multiselect().multiselectfilter();
        //===end parvez date: 04-01-2022=== 
   		}
	});
}


$(function() {
/* ===start parvez date: 04-01-2022=== */
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
/* ===end parvez date: 04-01-2022=== */   
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
//===start parvez date: 04-01-2022=== 	
	$("#pro_id").multiselect().multiselectfilter();
	$("#client").multiselect().multiselectfilter();
//===end parvez date: 04-01-2022=== 
});

</script>



<%
	UtilityFunctions uF = new UtilityFunctions();
	List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
	//List<List<DataStyle>> reportListExport = (List<List<DataStyle>>)request.getAttribute("reportListExport");
	//session.setAttribute("reportListExport", reportListExport);
	
	//System.out.println("RU.jsp/70--alOuter="+alOuter);
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
                    <s:form name="frmResourceUtilizationReport" id="frmResourceUtilizationReport" action="ResourceUtilizationReport" method="post" theme="simple">
                        <s:hidden name="exportType"></s:hidden>
                        <s:hidden name="reportType" id="reportType"></s:hidden>
                        <input type="hidden" name="strUserType" id="strUserType" value="<%=strUserType %>" />
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
                                	<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
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
							<!-- ===start parvez date: 04-03-2022=== -->		
									<s:select name="f_level" id="f_level" list="levelList" listKey="levelId" listValue="levelCodeName" key="" multiple="true"/>
							<!-- ===end parvez date: 04-01-2022=== -->
								</div>
								
						<!-- ===start parvez date: 04-01-2022=== -->
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project</p>
									<s:select name="pro_id" id="pro_id" listKey="projectID" listValue="projectName" list="projectdetailslist" key="" multiple="true" />
					      		</div>
					      		
					      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Client</p>
									<s:select label="Select Client" name="client" id="client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
								</div>
						<!-- ===end parvez date: 04-01-2022=== -->		
							</div>
						</div>
						
						<%-- <div class="row row_without_margin" style="margin-top: 10px;">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
					      		<div id="paycycleDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycycle</p>
									<s:select name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerValue="Select Paycycle" list="paycycleList" />
					      		</div>
					      		
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div> --%>
						
				<!-- ===start parvez date: 04-01-2022=== -->		
						<div class="row row_without_margin" style="margin-top: 10px;">
							
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">From Date</p>
								<s:textfield name="strStartDate" id="strStartDate" cssStyle="width:100px !important;" readonly="true"></s:textfield>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-right: 5px;">To Date</p>
					    		<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
							</div>
							
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
					      		<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Project</p>
									<s:select name="pro_id" id="pro_id" listKey="projectID" listValue="projectName" list="projectdetailslist" key="" multiple="true" />
					      		</div>
					      		
					      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Client</p>
									<s:select label="Select Client" name="client" id="client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
								</div> --%>
					      		
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
				<!-- ===end parvez date: 04-01-2022=== -->
                    </s:form>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
     

		<display:table name="alOuter" cellspacing="1" class="table table-bordered" id="lt1">
	<!-- ===start parvez date: 04-01-2022=== -->
			<display:column style="text-align:left; width: 25%;" valign="top" title="Client Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
			<display:column style="text-align:left; width: 25%;" valign="top" title="Project Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
	<!-- ===end parvez date: 04-01-2022=== -->
			<display:column style="text-align:left; width: 12%;" valign="top" title="Resource Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
			<display:column style="text-align:left; width: 25%;" valign="top" title="Resource Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<display:column style="text-align:left;" valign="top" title="Department"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Designation"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
			<display:column style="text-align:left;" valign="top" title="Worked Days on Projects"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Worked Days as a Desk Job"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Timesheet Status"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
				
		</display:table>

	</div>