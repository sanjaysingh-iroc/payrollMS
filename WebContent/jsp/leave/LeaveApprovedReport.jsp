<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<div id="divResult">

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	$('#lt').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	$("#f_wLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
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
});

function generateReportExcel(){
	window.location = "ExportExcelReport.action";
}

function submitForm(type){
	document.frm.exportType.value='';
	var org = document.getElementById("f_org").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	var location = getSelectedValue("f_wLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LeaveApprovedReport.action?f_org='+org+paramValues, 
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

<%String strTitle = (String)request.getAttribute(IConstants.TITLE); %>

 <%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>

<!-- <div class="leftbox reportWidth"> -->

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="desgn" style="margin-bottom: 5px;color:#232323;">
            <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
                <div class="box-header with-border">
                    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
                    <div class="box-tools pull-right">
                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                    </div>
                </div>
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                    <s:form name="frm" action="LeaveApprovedReport" theme="simple">
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
                                	<s:select name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key=""  multiple="true"/>
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
                                	<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
							</div>
						</div><br>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date</p>
									<s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 95px" cssClass="form-control autoWidth inline"></s:textfield>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">To Date</p>
									<s:textfield name="strEndDate"  id="strEndDate" cssStyle="width: 95px;" cssClass="form-control autoWidth inline"></s:textfield>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
                    </s:form>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
		
		<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;/* float: right; */"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div> -->
		
<div class="col-md-2 pull-right">
					
<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

</div>


		<div class="scroll">
			 <display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt"> <!-- requestURI="LeaveApprovedReport.action" -->
			   <%-- <display:setProperty name="export.excel.filename" value="LeaveApprovedReport.xls" />
				<display:setProperty name="export.xml.filename" value="LeaveApprovedReport.xml" />
				<display:setProperty name="export.csv.filename" value="LeaveApprovedReport.csv" /> --%>
				
			    <display:column style="align:left;" nowrap="nowrap" title="Employee Id" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Designation" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(13)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(14)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Date of Joining" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(15)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Leave Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Apply Date" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="From" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="To" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column style="align:center;" nowrap="nowrap" title="No.of Days" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Emp Reason" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Approve Date" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(16)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Approve By" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(17)%></display:column>
			</display:table>
		</div>
		
	</div>	
<!-- </div> -->

</div>