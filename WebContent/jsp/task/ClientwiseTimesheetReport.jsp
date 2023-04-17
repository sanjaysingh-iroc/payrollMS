<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">

$(function() {
	$('#lt1').DataTable({
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
	
    $("#strStartDate").datepicker({format: 'dd/mm/yyyy'});
    $("#strEndDate").datepicker({format: 'dd/mm/yyyy'}); 
});


function submitForm(type){
	document.frmClientwiseTimesheetReport.exportType.value='';
	var data = "";
	if(type == '1') {
		var f_org = document.getElementById("f_org").value;
		data = 'f_org='+f_org;
	} else if(type == '2') {
		data = $("#frmClientwiseTimesheetReport").serialize();
	}
	$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ClientwiseTimesheetReport.action',
		data: data,
		success: function(result){
        	$("#actionResult").html(result);
        	$("#f_client").multiselect().multiselectfilter();
        	$("#f_project").multiselect().multiselectfilter();
        	$("#f_strWLocation").multiselect().multiselectfilter();
        	$("#f_department").multiselect().multiselectfilter();
        	$("#f_service").multiselect().multiselectfilter();
        	$("#f_project_service").multiselect().multiselectfilter();
   		}
	});
}

function getProjectsByClient() {
	var xmlhttp = GetXmlHttpObject();
	if (xmlhttp == null) {
		alert("Browser does not support HTTP Request");
		return;
	} else {
		var strClient = getSelectedValue("f_client");
		
		var xhr = $.ajax({
			url : 'GetProjects.action?strClient='+strClient,
			cache : false,
			success : function(data) {
				document.getElementById('projectDiv').innerHTML = data;
				$("#f_project").multiselect().multiselectfilter();
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
</script>


 <%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>

<script type="text/javascript">
$(function(){
	$("#f_client").multiselect().multiselectfilter();
	$("#f_project").multiselect().multiselectfilter();
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_project_service").multiselect().multiselectfilter();
});     
</script>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
		
		/* List<List<DataStyle>> reportListExport = (List<List<DataStyle>>)request.getAttribute("reportListExport");
		session.setAttribute("reportListExport", reportListExport); */
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
                    <s:form name="frmClientwiseTimesheetReport" id="frmClientwiseTimesheetReport" action="ClientwiseTimesheetReport" theme="simple">
                        <s:hidden name="exportType"></s:hidden>
                        <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                                	<s:select theme="simple" name="f_org" id="f_org" headerKey="" headerValue="All Organizations" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
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
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_project_service" id="f_project_service" list="projectServiceList" listKey="serviceId" listValue="serviceName" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Client</p>
									<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" onchange="getProjectsByClient();"/>
			             		</div>
			             		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Projects</p>
									<div id="projectDiv">
										<s:select name="f_project" id="f_project" listKey="id" listValue="name" list="projectList" key="" multiple="true" />
									</div>	
								</div>
								
							</div>
						</div>
						
						<div class="row row_without_margin" style="margin-top: 10px;">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
					      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" >
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
        
        
	<display:table name="alOuter" cellspacing="1" class="table table-bordered" id="lt1">
	<%-- <display:table name="alOuter" cellspacing="1" class="tb_style" export="true" pagesize="50" id="lt1" requestURI="EmployeeCostReport.action" width="100%"> --%>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Client Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Project Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="No. of Days"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="No. of Hrs"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Billable No. of Hrs"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leaves"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
		<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Holidays"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
		 <!-- sort="true" -->
	</display:table>
	
</div>
