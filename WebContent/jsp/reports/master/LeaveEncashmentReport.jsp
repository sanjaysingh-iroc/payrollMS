<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">

	$(function() {
		$('#lt1').DataTable({
			dom: 'lBfrtip',
	        buttons: [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
		});
		
	});
	
	
	$(document).ready(function() {
		$('#lt').DataTable({
			aLengthMenu: [
				  			[25, 50, 100, 200, -1],
				  			[25, 50, 100, 200, "All"]
				  		],
			iDisplayLength: -1,
			dom: 'lBfrtip',
			"ordering": false,
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
	
	function submitForm(type){
		document.frm.exportType.value='';
		var org = document.getElementById("f_org").value;
		var calendarYear = document.getElementById("calendarYear").value;
		var location = getSelectedValue("f_wLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
			+'&strLevel='+level+'&calendarYear='+calendarYear;
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'LeaveEncashmentReport.action?f_org='+org+paramValues, 
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	//console.log(result);
	        	$("#divResult").html(result);
	   		}
		});
	}
</script>

<%
	List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList");
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
                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
                	<s:form name="frm" action="LeaveEncashmentReport" theme="simple" method="post">
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
                                	<s:select name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
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
									<p style="padding-left: 5px;">Calendar Year</p>
									<s:select label="Select Calendar Year" name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" 
										headerKey="0" onchange="submitForm('2');" list="calendarYearList" key=""/>
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
		</div>
		
		<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt1">
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
        	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Leave Type"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
        	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Balance"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
        	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Amount"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
		</display:table>
		
	</div>

</div>