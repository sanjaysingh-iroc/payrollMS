<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">

<script>
	$(document).ready(function() {
		$('#lt').DataTable({
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
		
		$( "#strStartDate").datepicker({format: 'dd/mm/yyyy'});
	    $( "#strEndDate").datepicker({format: 'dd/mm/yyyy'});
		
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		$("#f_emptype").multiselect().multiselectfilter();
	});
	
    /* function generateTimesheetExcel() {
   		var month=document.frm_Attendance.strMonth.value;
   		var year=document.frm_Attendance.strYear.value;
   		var wLocation=document.frm_Attendance.strWLocation.value;
   		var f_department=document.frm_Attendance.department.value;
   		var f_service=document.frm_Attendance.service.value;
   		var url='TimeSheetHoursExcel.action?year='+year+'&month='+month+'&wLocation='+wLocation+'&f_department='+f_department+'&f_service='+f_service;
   		
   		window.location = url;
   	} */
    
    function generateReportPdf(){
    	window.location = "ExportPdfReport.action";
    }
    
    function generateReportExcel() {
    	window.location = "ExportExcelReport.action";
    }
    
    function submitForm(type) {
    	var org = document.getElementById("f_org").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	var level = getSelectedValue("f_level");
    	var exceptionStatus = document.getElementById("exceptionStatus").value;
    	var strStartDate = document.getElementById("strStartDate").value;
    	var strEndDate = document.getElementById("strEndDate").value;
    	
    	var strEmpType = getSelectedValue("f_emptype");
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+"&exceptionStatus="+exceptionStatus
    			+'&strEmpType='+strEmpType+"&strStartDate="+strStartDate+"&strEndDate="+strEndDate;
    	}
    	var action = 'HalfDayFullDayExceptionReport.action?f_org='+org+paramValues;
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action, 
    		data: $("#"+this.id).serialize(),
    		success: function(result) {
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

<%

	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	
	String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
	String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
	String  strYear = (String)request.getAttribute("strYear");
	String  strMonth = (String)request.getAttribute("strMonth");
	
	List alDates = (List)request.getAttribute("alDates"); 
	List alEmployees = (List)request.getAttribute("alEmployees");
	Map hmEmpAttendance = (Map)request.getAttribute("hmEmpAttendance");
	Map hmEmpServiceWorkedFor = (Map)request.getAttribute("hmEmpServiceWorkedFor");
	Map hmEmpWlocation = (Map)request.getAttribute("hmEmpWlocation");
	Map hmWeekEnds = (Map)request.getAttribute("hmWeekEnds");
	Map hmEmpName = (Map)request.getAttribute("hmEmpName");
	Map hmServiceMap = (Map)request.getAttribute("hmServiceMap");
	Map hmLeaveCnt = (Map)request.getAttribute("hmLeaveCnt");
	if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
	
	List alLegends = (List)request.getAttribute("alLegends");
	
	Map<String,String> hmLeaveCodeMap = (Map<String,String>) request.getAttribute("hmLeaveCodeMap");
	if(hmLeaveCodeMap == null) hmLeaveCodeMap = new HashMap<String,String>();

    %>
    
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
   		<div class="desgn" style="background:#f5f5f5; color:#232323;">
   			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-bottom: 10px;">
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
					<s:form name="frm_HalfDayFullDayExceptionReport" id="frm_HalfDayFullDayExceptionReport" action="HalfDayFullDayExceptionReport" theme="simple">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<% if(strUserType != null && !strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && !strBaseUserType.equals(IConstants.HOD)) { %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
		
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">SBU</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level"listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
									</div>
								<%} %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_emptype" id="f_emptype" listKey="empTypeId" cssStyle="float:left;margin-right: 10px;width:200px;" 
										listValue="empTypeName" multiple="true" list="empTypeList" key="" />
								</div>
								<div class="col-lg-1 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Status</p>
									<s:select theme="simple" name="exceptionStatus" id="exceptionStatus" list="#{'':'All', '1':'Approved', '0':'Pending', '-1':'Denied'}" cssStyle="width: 120px !important;" onchange="submitForm('2');"/>
								</div>
							</div>
						</div>
 						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date</p>
									<s:textfield name="strStartDate" id="strStartDate" cssStyle="width:100px !important;" readonly="true"></s:textfield>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-right: 5px;">To Date</p>
				    				<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
							
							<%-- <div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycycle</p>
								     <s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key=""/>
								 </div>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>	
								</div>
							</div> --%>
						</div>
					</s:form>
				</div>
			</div>
          <!-- /.box-body -->
		</div>
	</div>
	<br/>
		<!-- <div class="col-md-2" style="margin: 0px 0px -15px 0px; text-align: right; float: right;">
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>               
		</div> -->

		<div class="scroll">
			<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt">
				<display:setProperty name="export.csv" value="true"/>
				<display:setProperty name="export.excel" value="true"/>
				<display:setProperty name="export.xml" value="true"/>
				<display:column style="align:left;" nowrap="nowrap" title="Sr.No" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column> 
				<display:column style="align:left;" nowrap="nowrap" title="Employee code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Organization" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Work Location" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="SBU" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>	
				<display:column style="align:left;" nowrap="nowrap" title="Employee Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Exception Date" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Exception Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Worked Hours" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Status" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Approve By" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(12)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Approved Date" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(13)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Approval Reason" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(14)%></display:column>	
			</display:table>
      
		</div> 
	</div>
	<!-- /.box-body -->
	
</div>