<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		
	});
	
	
	function submitForm(type){
		var data = "";
		if(type == '1') {
			var f_org = document.getElementById("f_org").value;
			data = '&f_org='+f_org;
		} else if(type == '2') {
			data = $("#frmEmployeeSummaryReport").serialize();
		}
		$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'EmployeeSummaryReport.action',
			data: data,
			success: function(result){
	        	$("#actionResult").html(result);
	        	$("#f_strWLocation").multiselect().multiselectfilter();
	        	$("#f_department").multiselect().multiselectfilter();
	        	$("#f_service").multiselect().multiselectfilter();
	        	$("#f_level").multiselect().multiselectfilter();
	   		}
		});
	}

</script>

<script type="text/javascript">
	$(function(){
		
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
	    
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
	});    
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	
	List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList"); 
	List<List<DataStyle>> reportListExport = (List<List<DataStyle>>)request.getAttribute("reportListExport");
	session.setAttribute("reportListExport",reportListExport);
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
                    <s:form name="frmEmployeeSummaryReport" id="frmEmployeeSummaryReport" action="EmployeeSummaryReport" theme="simple">
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
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" multiple="true"/>
								</div>
								
							</div>
						</div>
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
        
        <display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt1">
        	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
        	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Designation"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Total Expected Hours"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Total Actual Hours"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Project Hours"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Office Hours"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Idle Hours"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Leave Hours"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Project Cost"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Office Cost"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="idle Cost"><%=((java.util.List) pageContext.getAttribute("lt1")).get(11)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Billing"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Total Cost"><%=((java.util.List) pageContext.getAttribute("lt1")).get(12)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Contribution"><%=((java.util.List) pageContext.getAttribute("lt1")).get(13)%></display:column>
        </display:table>
        
    </div>