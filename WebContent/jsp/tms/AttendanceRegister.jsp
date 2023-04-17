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
		
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		$("#f_emptype").multiselect().multiselectfilter();
	});
	
    /* function generateTimesheetExcel() {
   		var paycycle = document.frm_Attendance.paycycle.value;
   		var wLocation=document.frm_Attendance.strWLocation.value;
   		var f_department=document.frm_Attendance.department.value;
   		var f_service=document.frm_Attendance.service.value;
   		var url='TimeSheetHoursExcel.action?paycycle='+paycycle+'&wLocation='+wLocation+'&f_department='+f_department+'&f_service='+f_service;
   		
   		window.location = url;
   	} */
    
    function generateReportPdf(){
    	window.location = "ExportPdfReport.action";
    }
    
    function generateReportExcel(){
    	window.location = "ExportExcelReport.action";
    }
    
    function submitForm(type){
    	document.frm_Attendance.exportType.value='';
    	var org = "";
    	var location = "";
    	var department = "";
    	var service = "";
    	var level = "";
    	if(document.getElementById("f_org")) {
	    	var org = document.getElementById("f_org").value;
	    	var location = getSelectedValue("f_strWLocation");
	    	var department = getSelectedValue("f_department");
	    	var service = getSelectedValue("f_service");
	    	var level = getSelectedValue("f_level");
    	}
    	var paycycle = document.getElementById("paycycle").value;
    	/* var strYear = document.getElementById("strYear").value;
    	var strMonth = document.getElementById("strMonth").value; */
    	var strEmpType = getSelectedValue("f_emptype");
    	
    	var paramValues = "";
    	//alert("asdf ........");
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
				+'&strLevel='+level+"&paycycle="+paycycle+'&strEmpType='+strEmpType; //+'&strMonth='+strMonth
    	}
    	
    	var action = 'AttendanceRegister.action?f_org='+org+paramValues;
    	//alert("action ===>> " + action);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: action, 
    		data: $("#"+this.id).serialize(),
    		success: function(result) {
    			//alert("result ===>> " + result);
            	//console.log(result);
            	$("#divResult").html(result);
       		}
    	});
    	/* document.frm_Attendance.submit(); */
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
	Map hmLeaveCnt = (Map)request.getAttribute("hmLeaveCnt");
	if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
		strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
		strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	}
	
	List alLegends = (List)request.getAttribute("alLegends");
	
	Map<String,String> hmLeaveCodeMap = (Map<String,String>) request.getAttribute("hmLeaveCodeMap");
	if(hmLeaveCodeMap == null) hmLeaveCodeMap = new HashMap<String,String>();

    %>
    
<!-- Custom form for adding new records -->
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Attendance Muster" name="title"/>
    </jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
   		<div class="desgn" style="background:#f5f5f5; color:#232323;">
   			<div class="box box-primary" style="border-top-color: #EEEEEE; margin-bottom: 10px;"> <!--  collapsed-box -->
         		<%-- <div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div> --%>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto;">  <!-- display:none; -->
				<div class="content1">
					<s:form name="frm_Attendance" id="frm_Attendance" action="AttendanceRegister" theme="simple">
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
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
									</div>
								<%} %>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Employee Type</p>
									<s:select theme="simple" name="f_emptype" id="f_emptype" listKey="empTypeId" cssStyle="float:left;margin-right: 10px;width:200px;" 
										listValue="empTypeName" multiple="true" list="empTypeList" key="" />
								</div>
							</div>
						</div>
 						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycycle</p>
									<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="" headerValue="Select Paycycle" list="paycycleList" key=""/>
								</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key="" />
								</div>
	
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Year</p>
									<s:select theme="simple" name="strYear" id="strYear" listKey="yearsID" listValue="yearsName" list="yearList" key="" />	
								</div> --%>
								
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>	
								</div>
							</div>
						</div>
					</s:form>
				</div>
			</div>
          <!-- /.box-body -->
		</div>
	</div>
	<br/>
		<div class="col-md-2" style="margin: 0px 0px -15px 0px; text-align: right; float: right;">
			<!-- <a onclick="generateReportPdf();" href="javascript:void(0)" style="background-image: url('images1/file-pdf.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
			
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>               
			
		</div>

		<div class="scroll">
			<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt">
				<display:setProperty name="export.excel.filename" value="AttendanceRegister.xls" />
				<display:setProperty name="export.xml.filename" value="AttendanceRegister.xml" />
				<display:setProperty name="export.csv.filename" value="AttendanceRegister.csv" />
				<display:setProperty name="export.csv" value="true"/>
				<display:setProperty name="export.excel" value="true"/>
				<display:setProperty name="export.xml" value="true"/>
				<display:column style="align:left;" nowrap="nowrap" title="Sr.No" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column> 
				<display:column style="align:left;" nowrap="nowrap" title="Employee code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Organization" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Work Location" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
				<display:column style="align:center;" nowrap="nowrap" title="Gender" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Designation" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>	
				<%-- <display:column style="align:left;" nowrap="nowrap" title="Employee Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column> --%>	
					<%
						for (int ii=0; ii<alDates.size(); ii++){
							int count = 9+ii;
							String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
							%>
							<display:column media="html" title="<%= strDate%>" > <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
							<%
						}  
						int totalDaysCnt = 9+alDates.size();
						int absentDaysCnt = 10+alDates.size();
						%>
						<display:column style="align:left;" nowrap="nowrap" title="Total Days" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(totalDaysCnt)%></display:column>
						<display:column style="align:left;" nowrap="nowrap" title="Absent" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(absentDaysCnt)%></display:column>
						<%
						Iterator<String> it11= hmLeaveCodeMap.keySet().iterator();
						int i1=0;
						 while(it11.hasNext()){
						    	String strId = it11.next();
						    	String strValue = hmLeaveCodeMap.get(strId);
						    	int count = (11+alDates.size())+i1;
						    	i1++;
						%>
							<display:column media="html" nowrap="nowrap"  title="<%= strValue%>" > <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
						<%}%>
						
			
			</display:table>
      
			<%-- <display:table name="reportListPrint" cellspacing="1" class="table table-border" style="display:none" pagesize="0" id="lt1" width="100%">
				<display:setProperty name="export.excel.filename" value="AttendanceRegister.xls" />
				<display:setProperty name="export.xml.filename" value="AttendanceRegister.xml" />
				<display:setProperty name="export.csv.filename" value="AttendanceRegister.csv" />
				<display:column style="align:left;" nowrap="nowrap" title="Employee code" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Employee Name" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Organization" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Work Location" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="Department" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
				<display:column style="align:center;" nowrap="nowrap" title="Gender" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
				<display:column style="align:left;" nowrap="nowrap" title="SBU" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
			<%
				for (int ii=0; ii<alDates.size(); ii++){
					int count = 7+ii;
					String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
			%>
				<display:column title="<%= strDate%>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
			<% }  
				int totalDaysCnt = 7+alDates.size();
			%>
				<display:column style="align:left;" nowrap="nowrap" title="Total Days" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(totalDaysCnt)%></display:column>

			<%
				Iterator<String> it11= hmLeaveCodeMap.keySet().iterator();
				int i1=0;
 				while(it11.hasNext()){
					String strId = it11.next();
					String strValue = hmLeaveCodeMap.get(strId);
					int count = (8+alDates.size())+i1;
					i1++;
			%>
				<display:column nowrap="nowrap"  title="<%= strValue%>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
			<%}%>
			</display:table> --%>
		</div> 
		<div class="custom-legends">
			<%for(int i=0; i<alLegends.size(); i++){ %>
					<%=alLegends.get(i) %>
			<%} %>
		  
		</div>
	</div>
	<!-- /.box-body -->
	
</div>