<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
	$("#f_strWLocation").multiselect().multiselectfilter();
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
	var leaveStatus = document.getElementById("leaveStatus").value;
	var org = document.getElementById("f_org").value;
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = '&leaveStatus='+leaveStatus;
	if(type == '2') {
		paramValues += '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'LeaveCancelledReport.action?f_org='+org+paramValues, 
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
                    <div class="content1">
                        <s:form name="frm" action="LeaveCancelledReport" theme="simple">
                            <div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Status</p>
                                    	<s:select theme="simple" name="leaveStatus" id="leaveStatus" cssStyle="width:92px;" list="#{'0':'All','1':'Cancelled', '2':'Pull Out','3':'Denied'}" onchange="submitForm('2');"/>
									</div>
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
										<s:textfield name="strStartDate" id="strStartDate"></s:textfield>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">To Date</p>
										<s:textfield name="strEndDate"  id="strEndDate"></s:textfield>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
									</div>
								</div>
							</div>
                        </s:form>
                    </div>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
		
		<!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div> -->
		
<div class="col-md-2 pull-right">
					
<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

</div>
	
		 <div class="scroll">
			 <table class="table table-bordered" id="lt">
		    	<thead>
			    	<tr>
			    		<th class="alignCenter" nowrap="nowrap">Leave Status</th>
			    		<th class="alignCenter" nowrap="nowrap">Employee Code</th>
						<th class="alignCenter" nowrap="nowrap">Employee Name</th>
						<th class="alignCenter" nowrap="nowrap">Designation</th>
						<th class="alignCenter" nowrap="nowrap">Department</th>
						<th class="alignCenter" nowrap="nowrap">Date of Joining</th>
						<th class="alignCenter" nowrap="nowrap">Date of Confirmation</th>
						<th class="alignCenter" nowrap="nowrap">Apply Date</th>
						<th class="alignCenter" nowrap="nowrap">From</th>
						<th class="alignCenter" nowrap="nowrap">To</th>
						<th class="alignCenter" nowrap="nowrap">No.of Days</th>
						<th class="alignCenter" nowrap="nowrap">Leave Type</th>
						<th class="alignCenter" nowrap="nowrap">Emp Reason</th>
						<th class="alignCenter" nowrap="nowrap">Cancel By</th>
						<th class="alignCenter" nowrap="nowrap">Cancel Date</th>
						<th class="alignCenter" nowrap="nowrap">Reason Remark</th>
			    	</tr>
		    	<thead>
		    	<tbody>
		    		<%
		    		List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList");;
		    		if(reportList == null) reportList = new ArrayList<List<String>>();

		    		int nReportSize = reportList!=null ? reportList.size() : 0;
		    		for(int i=0; i < nReportSize; i++){
		    			List<String> alInner = (List<String>) reportList.get(i);
		    		%>
	    				<tr>
	    					<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(0)%></td>
						    <td class="alignLeft;" nowrap="nowrap"><%=alInner.get(1)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(2)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(3)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(4)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(5)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(6)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(7)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(8)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(9)%></td>
							<td class="alignCenter;" nowrap="nowrap"><%=alInner.get(10)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(11)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(12)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(13)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(14)%></td>
							<td class="alignLeft;" nowrap="nowrap"><%=alInner.get(15)%></td>
			    		</tr>
			    	<%
		    			}
		    		%>
		    	</tbody>
		    </table>	
		</div>
	</div>
</div>
