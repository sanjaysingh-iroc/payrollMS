<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<div id="divResult">
<script type="text/javascript">
	$(document).ready(function() {
		$('#lt1').DataTable({
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
		$("#f_strWLocation").multiselect().multiselectfilter(); 
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter();
	});
    function submitForm(type){
    	document.frm_LeaveFactoryRegister.exportType.value='';
    	var org = document.getElementById("f_org").value;
    	var strEmpId = document.getElementById("strEmpId").value;
    	var typeOfLeave = document.getElementById("typeOfLeave").value;
    	var calendarYear = document.getElementById("calendarYear").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	var level = getSelectedValue("f_level");
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
    		+'&strLevel='+level+'&strEmpId='+strEmpId+'&calendarYear='+calendarYear;
    	} else if(type == '3') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
    		+'&strLevel='+level+'&strEmpId='+strEmpId+'&calendarYear='+calendarYear+'&typeOfLeave='+typeOfLeave;
    	}
    	//alert("service ===>> " + service);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'LeaveFactoryRegister.action?f_org='+org+paramValues, 
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

<%
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
%>

<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Yearly Leave Card" name="title"/>
    </jsp:include> --%>
    
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                        <div class="desgn" style="margin-bottom: 5px; color:#232323;">
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
                                        <s:form name="frm_LeaveFactoryRegister" action="LeaveFactoryRegister" theme="simple">
                                            <s:hidden name="exportType"></s:hidden>
                                            <div class="row row_without_margin">
												<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
													<i class="fa fa-filter"></i>
												</div>
												<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                                                    <s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px; margin-bottom: 8px;">Employee</p>
                                                    <s:select name="strEmpId" id="strEmpId" listKey="employeeId" headerValue="Select Employee"listValue="employeeCode" headerKey="0" onchange="submitForm('2');" list="empList" key=""/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px; margin-bottom: 8px;">Leave</p>
                                                    	<s:select theme="simple" cssClass="validateRequired" name="typeOfLeave" id="typeOfLeave" listKey="leaveTypeId" listValue="leavetypeName" list="leaveTypeList" key="" onchange="submitForm('3');"/>
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
														<p style="padding-left: 5px; margin-bottom: 8px;">Level</p>
                                                    <s:select theme="simple" name="f_level" id="f_level"listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
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
														<s:select label="Select Calendar Year" name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0" onchange="submitForm('3');" list="calendarYearList" key=""/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">&nbsp;</p>
														<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('3');"/>
													</div>
												</div>
											</div>
                                        </s:form>
                                </div>
                                <!-- /.box-body -->
                            </div>
                        </div>
                        
                        <table id="lt1" class="table table-bordered" style="width: 100%; margin-top: 30px; clear: both;">
                        	<thead>
                        		<tr>
	                        		<th style="text-align: left;">Year</th>
	                        		<th style="text-align: left;">Month</th>
	                        		<th style="text-align: left;">NO. of days work performed</th>
	                        		<th style="text-align: left;">No of days of  lay-off</th>
	                        		<th style="text-align: left;">No. of days of maternity leave with wages</th>
	                        		<th style="text-align: left;">No. of days leave with wages enjoyed</th>
	                        		<th style="text-align: left;">Total</th>
	                        		<th style="text-align: left;">Balance of leaves with wages from preceding year</th>
	                        		<th style="text-align: left;">Leave with wages earned</th>
	                        		<th style="text-align: left;">Total</th>
	                        		<th style="text-align: left;">Whether leave with wages refused</th>
	                        		<th style="text-align: left;">Whether leave with wages not desired during the next calender year</th>
	                        		<th style="text-align: left;">From</th>
	                        		<th style="text-align: left;">To</th>
	                        		<th style="text-align: left;">Balance to credit</th>
	                        		<th style="text-align: left;">Normal rate of wages</th>
	                        		<th style="text-align: left;">Cash equivalent or advantage accuring throgh concessional sale of foodgrains or other articles</th>
	                        		<th style="text-align: left;">Rate of wages for leave with wages period</th>
                        		</tr>
                        	</thead>
                        	<tbody>
                        		<%
                        		List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
                        		if(reportList == null)reportList = new ArrayList<List<String>>();	
                        		for(List<String> innerList : reportList){
                        		%>
	                        		<tr>
		                        		<td style="text-align: center;"><%=innerList.get(0) %></td>
		                        		<td style="text-align: left;"><%=innerList.get(1) %></td>	
		                        		<td style="text-align: right;"><%=innerList.get(2) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(3) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(4) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(5) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(6) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(7) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(8) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(9) %></td>
		                        		<td style="text-align: left;"><%=innerList.get(10) %></td>
		                        		<td style="text-align: left;"><%=innerList.get(11) %></td>
		                        		<td style="text-align: center;"><%=innerList.get(12) %></td>
		                        		<td style="text-align: center;"><%=innerList.get(13) %></td>
		                        		<td style="text-align: right;"><%=innerList.get(14) %></td>
		                        		<td style="text-align: left;"><%=innerList.get(15) %></td>
		                        		<td style="text-align: left;"><%=innerList.get(16) %></td>
		                        		<td style="text-align: left;"><%=innerList.get(17) %></td>
	                        		</tr>
	                        	<%} %>
                        	</tbody>
                        </table>
                </div>
           
</div>
