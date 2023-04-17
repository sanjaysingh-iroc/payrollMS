<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Time"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%-- <script src='scripts/calender/jquery.min.js'></script> --%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script>  --%>

<script src="scripts/charts/highcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />

<style> 
	.listMenu1 .icon .fa{
		font-size: 45px;
		vertical-align: top;
		margin-top: 20px;
	}
</style>

<script type="text/javascript" charset="utf-8">

function submitForm(type){
	
	if(type=='<%=IConstants.MANAGER%>'){	
		var strSearch = document.getElementById("strSearch").value;
		var D2 = document.getElementById("D2").value;
		var D1 = document.getElementById("D1").value;
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AttendanceReport.action?strSearch='+strSearch+'&D2='+D2+'&D1='+D1,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}else {
		
		var org = "";
		var location = "";
		var department = "";
		var service = "";
		var strSearch = document.getElementById("strSearch").value;
		if(document.getElementById("f_org")) {
			org = document.getElementById("f_org").value;
		}
		if(document.getElementById("f_strWLocation")) {
			location = getSelectedValue("f_strWLocation");
		}
		if(document.getElementById("f_department")) {
			department = getSelectedValue("f_department");
		}
		if(document.getElementById("f_service")) {
			service = getSelectedValue("f_service");
		}
		var D2 = document.getElementById("D2").value;
		var D1 = document.getElementById("D1").value;
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&D2='+D2+'&D1='+D1;
		}
		if(type == '3') {
			var D2 = document.getElementById("D2").value;
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&D2='+D2;
		}
		if(type == '4') {
			var D1 = document.getElementById("D1").value;
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&D1='+D1;
		}
		if(strSearch!=''){
			 var D1 = document.getElementById("D1").value;
			 var D2 = document.getElementById("D2").value;
			 paramValues = paramValues+'&strSearch='+strSearch+'&D1='+D1+'&D2='+D2;
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AttendanceReport.action?f_org='+org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
}

function loadMore(type,proPage, minLimit) {
	if(type=='<%=IConstants.MANAGER%>'){	
		var strSearch = document.getElementById("strSearch").value;
		var D2 = document.getElementById("D2").value;
		var D1 = document.getElementById("D1").value;
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AttendanceReport.action?strSearch='+strSearch+'&proPage='+proPage+'&minLimit='+minLimit+'&D2='+D2+'&D1='+D1,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}else {
		
		var org = "";
		var location = "";
		var department = "";
		var service = "";
		var strSearch = document.getElementById("strSearch").value;
		if(document.getElementById("f_org")) {
			org = document.getElementById("f_org").value;
		}
		if(document.getElementById("f_strWLocation")) {
			location = getSelectedValue("f_strWLocation");
		}
		if(document.getElementById("f_department")) {
			department = getSelectedValue("f_department");
		}
		if(document.getElementById("f_service")) {
			service = getSelectedValue("f_service");
		}
		var D2 = document.getElementById("D2").value;
		var D1 = document.getElementById("D1").value;
		var paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&D2='+D2+'&D1='+D1;
		
		
		if(strSearch!=''){
			 var D1 = document.getElementById("D1").value;
			 var D2 = document.getElementById("D2").value;
			 paramValues = paramValues+'&strSearch='+strSearch+'&D1='+D1+'&D2='+D2;
		}
	
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'AttendanceReport.action?f_org='+org+'&proPage='+proPage+'&minLimit='+minLimit+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
	
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

	$(function() {
		$("#D2").datepicker({format : 'dd/mm/yyyy', autoclose: true});
	});
 
	function generateReportPdf() {
		alert("pdf generation");
	}

	function generateReportExcel() {
		alert("Excel  generation");
	}
	
	function submitForwardForm(type,date,usertype){ 
			if(usertype=='<%=IConstants.MANAGER%>'){	
				var D2 = document.getElementById("D2").value;
				var D1 = document.getElementById("D1").value;
				var paramValues = "";
				if(type==1){
					document.getElementById("D2").value = date;
					document.getElementById("D1").value = '';
					var D2 = document.getElementById("D2").value;
					var D1 = document.getElementById("D1").value;
				    paramValues = '&D2='+D2+'&D1='+D1;
				}else if(type==2){
					document.getElementById("D2").value = '';
					document.getElementById("D1").value = date;
					var D2 = document.getElementById("D2").value;
					var D1 = document.getElementById("D1").value;
				    paramValues = '&D2='+D2+'&D1='+D1;
				}
				//alert("service ===>> " + service);
				$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$.ajax({
					type : 'POST',
					url: 'AttendanceReport.action?f_org='+org+paramValues,
					data: $("#"+this.id).serialize(),
					success: function(result){
						$("#divResult").html(result);
			   		}
				});
		}else{
			
			var org = org = document.getElementById("f_org").value;
			var location = location = getSelectedValue("f_strWLocation");
			var department = department = getSelectedValue("f_department");
			var service = service = getSelectedValue("f_service");
			var D2 = document.getElementById("D2").value;
			var D1 = document.getElementById("D1").value;
			var paramValues = "";
			if(type==1){
				document.getElementById("D2").value = date;
				document.getElementById("D1").value = '';
				var D2 = document.getElementById("D2").value;
				var D1 = document.getElementById("D1").value;
			    paramValues = '&D2='+D2+'&D1='+D1;
			}else if(type==2){
				document.getElementById("D2").value = '';
				document.getElementById("D1").value = date;
				var D2 = document.getElementById("D2").value;
				var D1 = document.getElementById("D1").value;
			    paramValues = '&D2='+D2+'&D1='+D1;
			}
			//alert("service ===>> " + service);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'AttendanceReport.action?f_org='+org+paramValues,
				data: $("#"+this.id).serialize(),
				success: function(result){
					$("#divResult").html(result);
		   		}
			});
		}
		
		
		  //displayStartDate D1 
		/* if(type == 1){
			document.frmAttendance.displayStartDate.value = date;
			document.frmAttendance.D1.value = '';
		} else if(type == 2){
			document.frmAttendance.displayStartDate.value = '';
			document.frmAttendance.D1.value = date;
		}
		document.frmAttendance.proPage.value = '';
		document.frmAttendance.minLimit.value = '';
		document.frmAttendance.submit(); */
	  } 
	
</script>


<%-- <script type="text/javascript">
	$(function() {
		$("#wLocation").multiselect().multiselectfilter();
		$("#department").multiselect().multiselectfilter();
		$("#service").multiselect().multiselectfilter();
	});
</script> --%>

<%
	UtilityFunctions uF = new UtilityFunctions();
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
	
	Map hmServicesMap = (Map) request.getAttribute("hmServicesMap");
	Map hmEmployeeService = (Map) request.getAttribute("hmEmployeeService");
	Map hmExistingEmpNameMap = (Map) request.getAttribute("hmExistingEmpNameMap");
	if(hmExistingEmpNameMap == null) hmExistingEmpNameMap = new HashMap();
	Map hmEmployeeRosterHours = (Map) request.getAttribute("hmEmployeeRosterHours");
	Map hmEmployeeActualHours = (Map) request.getAttribute("hmEmployeeActualHours");
	Map hmLeaveDatesMap = (Map) request.getAttribute("hmLeaveDatesMap");
	Map hmServicesCount = (Map) request.getAttribute("hmServicesCount");
	
	Map<String, Set<String>> hmWLocationHolidaysWeekEndDates = (Map<String, Set<String>>) request.getAttribute("hmWLocationHolidaysWeekEndDates");
	if(hmWLocationHolidaysWeekEndDates == null) hmWLocationHolidaysWeekEndDates = new HashMap<String, Set<String>>();
	
	List<String> alEmpCheckRosterWeektype = (List<String>) request.getAttribute("alEmpCheckRosterWeektype");
	if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
	Map<String, Set<String>> hmRosterWeekEndDates =(Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");
	if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
	
	Map hmWLocationHolidaysColour = (Map) request.getAttribute("hmWLocationHolidaysColour");
	Map hmWLocationHolidaysName = (Map) request.getAttribute("hmWLocationHolidaysName");
	//Map hmWLocationHolidaysWeekEnd = (Map) request.getAttribute("hmWLocationHolidaysWeekEnd");
	Map hmEmpWLocation = (Map) request.getAttribute("hmEmpWLocation");
	Map hmLeaveTypes = (Map) request.getAttribute("hmLeaveTypes");
	if(hmLeaveTypes == null) hmLeaveTypes = new HashMap();
	Map hmWeekEndMap = (Map) request.getAttribute("hmWeekEndMap");
	Map hmBreakPolicy = (Map) request.getAttribute("hmBreakPolicy");
	
	List alDates = (List) request.getAttribute("alDates");
	if(alDates == null) alDates = new ArrayList();
	
	String pageCount = (String)request.getAttribute("pageCount");
	
	String sbData = (String) request.getAttribute("sbData");
	String strSearch = (String) request.getAttribute("strSearch");

%>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Realtime Attendance" name="title"/>
</jsp:include> --%>

		<div class="box-body" style="padding: 5px; /*overflow-y: auto;*/">
			<s:form action="AttendanceReport" name="frmAttendance" theme="simple">
				<div class="box box-default"> <!--  collapsed-box -->
					<%-- <div class="box-header with-border">
					    <h4 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h4>
					    <div class="box-tools pull-right">
					        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					    </div>
					</div> --%>
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
						<% if (strUserType != null && !strUserType.equals(IConstants.MANAGER)) { %>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
									<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1')" list="organisationList" key="" />
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
									<s:select name="f_strWLocation" id="f_strWLocation" list="wLocationList" listKey="wLocationId" listValue="wLocationName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
								</div>
							</div>
						</div><br>
						<% } %>		
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;"><sup>*</sup>Select Last Date of 15 Days</p>
									<s:textfield name="D2" id="D2"></s:textfield>
									<s:hidden name="D1" id="D1"></s:hidden>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
									<%-- <s:submit value="Search" cssClass="btn btn-primary"/> --%>
								</div>
							</div>
						</div>									
						</div>
					</div>


					<div class="col-lg-6 col-md-6 col-sm-12 no-padding">
						<script type="text/javascript">
							$(document).ready(function() {
								
								chartAttendance = new Highcharts.Chart({
							   		
							      chart: {
							         renderTo: 'container_Attendance',
							        	type: 'column'
							      },
							      title: {
							         text: 'Counter'
							      },
							      xAxis: {
							         categories: [<%=request.getAttribute("sbDatesAttendanceDate")%>],
							         labels: {
							             rotation: -45,
							             align: 'right',
							             style: {
							                 font: 'normal 10px Verdana, sans-serif'
							             }
							          },
							         title: {
								            text: 'Date'
								         }
							      },
							      credits: {
							       	enabled: false
							   	  },
							      yAxis: {
							         min: 0,
							         title: {
							            text: 'Attendance'
							         }
							      },
							      plotOptions: {
							         column: {
							            pointPadding: 0.2,
							            borderWidth: 0
							         }
							      },
							     series: [<%=request.getAttribute("sbDatesAttendance")%>]
							   });
								
							});
						</script>
						
						<div class="box box-danger">
			                <div class="box-header with-border">
			                    <h3 class="box-title">Attendance Summary</h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto;">
			                    <div class="content1">
									<div id="container_Attendance" style="height: 300px; width:95%; margin:10px 0 0 0px;"></div>
								</div>
			                </div>
			                <!-- /.box-body -->
			            </div>
			            
					
						<script type="text/javascript">
							$(document).ready(function() {
								
								chartAttendance = new Highcharts.Chart({
							   		
							      chart: {
							         renderTo: 'container_leavestatus',
							        	type: 'column'
							      },
							      title: {
							         text: 'Counter'
							      },
							      xAxis: {
							         categories: [<%=request.getAttribute("sbDatesLeavesDate")%>],
							         labels: {
							             rotation: -45,
							             align: 'right',
							             style: {
							                 font: 'normal 10px Verdana, sans-serif'
							             }
							          },
							         title: {
								            text: 'Date'
								         }
							      },
							      credits: {
							       	enabled: false
							   	  },
							      yAxis: {
							         min: 0,
							         title: {
							            text: 'Leaves'
							         }
							      },
							      plotOptions: {
							         column: {
							            pointPadding: 0.2,
							            borderWidth: 0
							         }
							      },
							     series: [<%=request.getAttribute("sbDatesLeaves")%>]
							   });
								
							});
						</script>
						
						<div class="box box-success">
			                <div class="box-header with-border">
			                    <h3 class="box-title">Leave Summary</h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto;">
			                    <div class="content1">
									<div id="container_leavestatus" style="height: 300px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
								</div>
			                </div>
			                <!-- /.box-body -->
			            </div>
					</div>
					
					
					<% 	List<List<String>> alLeaveData = (List<List<String>>) request.getAttribute("alLeaveData"); 
						List<List<String>> alAttendanceData = (List<List<String>>) request.getAttribute("alAttendanceData");
					%>
					
					<div class="col-lg-6 col-md-6 col-sm-12">
						<div style="width: 100%; overflow-x: auto;">
							<% if(alLeaveData != null && alLeaveData.size()>0) { 
								for(int i=0; i<alLeaveData.size(); i++) {
									List<String> innerList = alLeaveData.get(i);
									String strBgClass = "bg-gray";
									if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
										strBgClass = "bg-red";
									} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) > uF.parseToInt(innerList.get(3))) {
										strBgClass = "bg-yellow";
									} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == uF.parseToInt(innerList.get(2))) {
										strBgClass = "bg-green";
									}
							 %>
								<div class="col-lg-4 col-xs-12 col-sm-12 paddingright0">
								<!-- small box -->
									<div class="small-box <%=strBgClass %>">
										<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
											<i class="fa fa-umbrella" aria-hidden="true"></i>
										</div>
										<div class="inner" style="padding: 0px 10px; text-align: right;">
											<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
											<div style="margin-top: -5px;">Applied</div>
										</div>
										<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
											<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
											<div style="margin-top: -5px;">Approved/Denied</div>
										</div>
										<a href="javascript:void(0);" style="font-size: 12px;" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>  <%-- onclick="Mngerapprovefun('<%=innerList.get(0) %>')" --%>
									</div>
								</div>
								<% } %>
							<% } %>
						</div>
						
						<div style="width: 100%; overflow-x: auto;">
							<% if(alAttendanceData != null && alAttendanceData.size()>0) { 
								for(int i=0; i<alAttendanceData.size(); i++) {
									List<String> innerList = alAttendanceData.get(i);
									String strBgClass = "bg-gray";
									if(uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) == 0) {
										strBgClass = "bg-red";
									} else if(uF.parseToInt(innerList.get(3)) > 0 && uF.parseToInt(innerList.get(2)) > 0) {
										strBgClass = "bg-yellow";
									} else if(uF.parseToInt(innerList.get(2)) > 0 && uF.parseToInt(innerList.get(3)) == 0) {
										strBgClass = "bg-green";
									}
							%>
								<div class="col-lg-4 col-xs-12 col-sm-12 paddingright0">
								<!-- small box -->
									<div class="small-box <%=strBgClass %>">
										<div style="float: left;font-size: 45px;color: rgba(0,0,0,0.15);">
											<i class="fa fa-clock-o" aria-hidden="true"></i>
										</div>
										<div class="inner" style="padding: 0px 10px; text-align: right;">
											<h3 style="margin: 0px; font-size: 24px;"><%=innerList.get(2) %></h3>
											<div style="margin-top: -5px;">Approved</div>
										</div>
										<div class="inner" style="padding: 0px 10px 2px; text-align: right;">
											<h4 style="margin: 0px;"><%=innerList.get(3) %></h4>
											<div style="margin-top: -5px;">Waiting</div>
										</div>
										<a href="javascript:void(0);" style="font-size: 12px;" class="small-box-footer"><%=innerList.get(1) %><i class="fa fa-arrow-circle-right"></i></a>  <%-- onclick="Mngerapprovefun('<%=innerList.get(0) %>')" --%>
									</div>
								</div>
								<% } %>
							<% } %>
						</div>
						
					</div>
					
					<div class="col-lg-12 col-md-12 col-sm-12">
						<%if(alDates !=null && alDates.size() > 0){ %>
							<div class="col-lg-1 col-md-1 col-sm-12 no-padding">
								<div style="float:left"><a href="javascript:void(0);" onclick="submitForwardForm(1,'<%=alDates.get(0)%>','<%=strUserType%>')"><i class="fa fa-backward"></i></a></div>
							</div>
						<% } %>
						<div class="col-lg-10 col-md-10 col-sm-12 no-padding" style="text-align: center;">
							<input type="text" id="strSearch" class="form-control" name="strSearch" placeholder="Search" value="<%=uF.showData(strSearch, "") %>"/>
							<input type="button" value="Search" class="btn btn-primary" onclick="submitForm('<%=strUserType%>');">
						</div>
						
						<%if(alDates !=null && alDates.size() > 0){ %>  			
							<div class="col-lg-1 col-md-1 col-sm-12 no-padding">
								<div style="float:right"><a href="javascript:void(0);" onclick="submitForwardForm(2,'<%=alDates.get(alDates.size()-1)%>','<%=strUserType%>')"><i class="fa fa-forward"></i></a></div>
							</div>
						<% } %>
					</div>
					
				    <script type="text/javascript">
						$("#strSearch").autocomplete({
							source: [ <%=uF.showData(sbData, "") %> ]
						});
					</script>
					
				</s:form>
			
				<div class="attendance">
					<table cellpadding="2" cellspacing="0" width="100%" align="left" >
						<tr><td colspan="18"><h3 style="margin-top: 0px;">Hours & Status</h3></td></tr>
						<tr class="darktable"> 
							<td colspan="2">Employee Name</td>
							<% for (int i = 0; i < alDates.size(); i++) { %>
								<td style="width:75px"><%=uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, "dd MMM")%></td>
							<% } %>
							<td align="right">Hours</td>
						</tr>
					
						<%
						List alBreaks = new ArrayList();
						Map hmBreaks = new HashMap();
						
							List alHolidayList = new ArrayList();
						
							Map hmCount = new HashMap();
							Map hmCountHrs = new HashMap();
							Map hmLeavesCount = new HashMap();
							int nEmpCount = 0;
							Set set = hmExistingEmpNameMap.keySet();
							Iterator it = set.iterator();
							while (it.hasNext()) {
								String strEmpId = (String) it.next();
								Map hmActual = (Map) hmEmployeeActualHours.get(strEmpId);
								Map hmRoster = (Map) hmEmployeeRosterHours.get(strEmpId);
								Map hmEmployeeServiceTemp =  (Map) hmEmployeeService.get(strEmpId);
					
								if (hmActual == null) {
									hmActual = new HashMap();
								}
								if (hmRoster == null) {
									hmRoster = new HashMap();
								}
								if (hmEmployeeServiceTemp == null) {
									hmEmployeeServiceTemp = new HashMap();
								}
								Map hmLeaveDates = (Map) hmLeaveDatesMap.get(strEmpId);
								if (hmLeaveDates == null) {
									hmLeaveDates = new HashMap();
								}
								
								
						%>
					
						
							
							
							<%		boolean isService = false;	
									boolean isLeave = false;
									int nRowspan = 1;
									double dblCountRowWise = 0;
									//String strServiceid = (String)hmEmployeeServiceTemp.get((String) alDates.get(i));
									List alServiceCount = (List)hmServicesCount.get(strEmpId);
									if(alServiceCount==null) alServiceCount = new ArrayList();
									for(int s=0; s<alServiceCount.size(); s++){
										String strServiceId = (String)alServiceCount.get(s);
										isService = true;
										
										
							%>
										<tr class="lighttable">	
										<%if(s==0){ %>		
										<td style="width:20%" valign="top" nowrap="nowrap" rowspan="<%=alServiceCount.size()%>"><%=(String) hmExistingEmpNameMap.get(strEmpId)%><%nEmpCount++;%></td>
										<%} %>
										<td style="width:5%" nowrap="nowrap" ><%= uF.showData((String) hmServicesMap.get(strServiceId), "-")%></td> 
										<%
											for (int i = 0; i < alDates.size(); i++) {
												
										%>
							
											<td style="width:5%">
												<%
													String strWH = (String) hmActual.get((String) alDates.get(i) + "_WH_"+strServiceId);
													String strIN = (String) hmActual.get((String) alDates.get(i) + "_IN_"+strServiceId);
							
													double dblEI = uF.parseToDouble((String) hmActual.get((String) alDates.get(i) + "_EARLY_IN_"+strServiceId));
													double dblLI = uF.parseToDouble((String) hmActual.get((String) alDates.get(i) + "_LATE_IN_"+strServiceId));
													double dblOTI = uF.parseToDouble((String) hmActual.get((String) alDates.get(i) + "_ONTIME_IN_"+strServiceId));
							
													double dblEO = uF.parseToDouble((String) hmActual.get((String) alDates.get(i) + "_EARLY_OUT_"+strServiceId));
													double dblLO = uF.parseToDouble((String) hmActual.get((String) alDates.get(i) + "_LATE_OUT_"+strServiceId));
													double dblOTO = uF.parseToDouble((String) hmActual.get((String) alDates.get(i) + "_ONTIME_OUT_"+strServiceId));
													
													Date empEndTime = uF.getTimeFormat((String)hmRoster.get((String) alDates.get(i)+"_E"), IConstants.DBTIME);
													Date currentTime = uF.getDateFormatUtil(uF.getCurrentTime(CF.getStrTimeZone())+"", IConstants.DBTIME);
													if(empEndTime==null){
														empEndTime = currentTime;
													}
													
													Calendar c1 = Calendar.getInstance();
													Calendar c2 = Calendar.getInstance();
													c1.setTime(empEndTime);
													c2.setTime(currentTime);
													
													String strTitleIN = uF.showData((String)hmBreakPolicy.get(strEmpId+"_"+(String) alDates.get(i)+"_IN"), "") ;
													String strTitleOUT = uF.showData((String)hmBreakPolicy.get(strEmpId+"_"+(String) alDates.get(i)+"_OUT"), "") ;
													
													
													
													if (dblEI < 0) {
														//out.println("EI");
														out.println("<div class=\"cameearly\">&nbsp;</div>");
														Map hm = (Map) hmCount.get((String) alDates.get(i));
														if (hm == null)
															hm = new HashMap();
														int nCount = uF.parseToInt((String) hm.get("nCountEI")) + 1;
														hm.put("nCountEI", nCount + "");
														hmCount.put((String) alDates.get(i), hm);
													} else if (dblLI > 0) {
														//out.println("LI");
														out.println("<div class=\"camelate\" title=\""+strTitleIN+"\">&nbsp;</div>");
														if(!alBreaks.contains(strTitleIN) && strTitleIN.length()>0){
															alBreaks.add(strTitleIN);
														}
														int nBreakCount = uF.parseToInt((String)hmBreaks.get(strTitleIN+"_"+(String) alDates.get(i)));
														hmBreaks.put(strTitleIN+"_"+(String) alDates.get(i), (nBreakCount+1)+"");
														
														Map hm = (Map) hmCount.get((String) alDates.get(i));
														if (hm == null)
															hm = new HashMap();
														int nCount = uF.parseToInt((String) hm.get("nCountLI")) + 1;
														hm.put("nCountLI", nCount + "");
														hmCount.put((String) alDates.get(i), hm);
													} else if (dblOTI == 0 && strIN != null) {
														//out.println("OTI");
														out.println("<div class=\"cameontime\">&nbsp;</div>");
														Map hm = (Map) hmCount.get((String) alDates.get(i));
														if (hm == null)
															hm = new HashMap();
														int nCount = uF.parseToInt((String) hm.get("nCountOTI")) + 1;
														hm.put("nCountOTI", nCount + "");
														hmCount.put((String) alDates.get(i), hm);
													}
					
					
					
													String strWLocationId = (String)hmEmpWLocation.get(strEmpId);
													
													Set<String> weeklyOffSet= hmWLocationHolidaysWeekEndDates.get(strWLocationId);
													if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
													
													Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
													if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
													
													String strColor ="";
													if(weeklyOffSet.contains(alDates.get(i)) || rosterWeeklyOffSet.contains(alDates.get(i))){
														strColor = IConstants.WEEKLYOFF_COLOR;
													}
													
													String strDay = uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, CF.getStrReportDayFormat());
													if(strDay!=null)strDay = strDay.toUpperCase();
													Map hmHolidaysName = (Map)hmWLocationHolidaysName.get(strWLocationId);
													if(hmHolidaysName==null) hmHolidaysName = new HashMap();
													
													//Map hmHolidaysWeekEnd = (Map)hmWLocationHolidaysWeekEnd.get(strWLocationId);
													//if(hmHolidaysWeekEnd==null) hmHolidaysWeekEnd = new HashMap();
													
													Map hmHolidaysColour = (Map)hmWLocationHolidaysColour.get(strWLocationId);
													if(hmHolidaysColour==null) hmHolidaysColour = new HashMap();
													
													
					
													if (strWH != null) {
														out.println("<div class=\"data\">"+strWH+"</div>");
							
														double dbl = uF.parseToDouble((String) hmCountHrs.get((String) alDates.get(i)));
														dblCountRowWise += uF.parseToDouble(strWH); 
														dbl += uF.parseToDouble(strWH);
														hmCountHrs.put((String) alDates.get(i), dbl + "");
							
													} else if (strIN != null) {
														long lINTime = uF.getDateFormat(strIN, IConstants.DBTIMESTAMP).getTime();
														long lCurrTime = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + " " + uF.getCurrentTime(CF.getStrTimeZone()), IConstants.DBTIMESTAMP).getTime();
														String strTime = uF.getTimeDiffInHoursMins(lINTime, lCurrTime);
														boolean isNotClockOff = false;
														if(uF.parseToDouble(strTime)>=24){
															//strTime = "24.0";												
															Time dayLastTime = uF.getTimeFormat(uF.getDateFormat(strIN, IConstants.DBTIMESTAMP,IConstants.DBDATE)+"23:59", IConstants.DBDATE+IConstants.DBTIME);
															long dayLastTimeMiliSecond = dayLastTime.getTime();
															strTime = uF.getTimeDiffInHoursMins(lINTime, dayLastTimeMiliSecond);
															isNotClockOff = true;
														}
							
														double dbl = uF.parseToDouble((String) hmCountHrs.get((String) alDates.get(i)));
														dblCountRowWise += uF.parseToDouble(strTime);
														dbl += uF.parseToDouble(strTime);
														hmCountHrs.put((String) alDates.get(i), uF.formatIntoOneDecimalWithOutComma(dbl) + ""); 
							
														out.println("<div class=\"data\">"+strTime+"</div>");
														//if(uF.parseToDouble(strTime)>=24){
														if(isNotClockOff){	
															out.println("<div  title=\"Did not clock off\" class=\"qstn_atdnce\">&nbsp;</div>");
														}
														
													} else if (hmLeaveDates.containsKey((String) alDates.get(i))) {
														out.println("<div class=\"data\" style=\"width:100%;background:"+hmLeaveTypes.get(hmLeaveDates.get((String) alDates.get(i)))+"\" >"+hmLeaveDates.get((String) alDates.get(i))+"</div>");
														
														//if(!isLeave){
															isLeave = true;
															String strLeaveType = (String) hmLeaveDates.get((String) alDates.get(i));
															Map hm = (Map) hmLeavesCount.get(strLeaveType);
															if (hm == null)
																hm = new HashMap();
															int nCount = uF.parseToInt((String) hm.get((String) alDates.get(i)));
															nCount++;
															hm.put((String) alDates.get(i), nCount + "");
															hmLeavesCount.put(strLeaveType, hm);
													//	}
							
													} else if (hmRoster.containsKey((String) alDates.get(i))) {
														
														
														
														if (i == 0) { // Todays condition
																/* out.println("<div class=\"data\">"+" - "+"</div>"); */
																
																
																if(c2.before(c1)){
																	out.println("<div class=\"data\">YTC</div>");
																	Map hm = (Map) hmCount.get((String) alDates.get(i));
																	if (hm == null)
																		hm = new HashMap();
																	int nCount = uF.parseToInt((String) hm.get("nCountYTC")) + 1;
																	hm.put("nCountYTC", nCount + "");
																	hmCount.put((String) alDates.get(i), hm);
																}else{
																	out.println("<div class=\"data\">"+"<font color=\"red\">AB</font>"+"</div>");
																	
																	Map hm = (Map) hmCount.get((String) alDates.get(i));
																	if (hm == null)
																		hm = new HashMap();
																	int nCount = uF.parseToInt((String) hm.get("nCountAB")) + 1;
								//									System.out.println("Date"+(String) alDates.get(i)+"Count"+nCount);
																	hm.put("nCountAB", nCount + "");
																	hmCount.put((String) alDates.get(i), hm);
																}
																
																
																
														}else if(hmHolidaysName.containsKey((String) alDates.get(i))){
																/* out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+hmHolidaysColour.get((String) alDates.get(i))+"; text-align: center;\">"+uF.showData((String)hmHolidaysName.get((String) alDates.get(i)), "")+"</div>"); */
																out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+hmHolidaysColour.get((String) alDates.get(i))+"; text-align: center;\">H</div>");
																
																
																if(!alHolidayList.contains((String)hmHolidaysName.get((String) alDates.get(i)))){
																	alHolidayList.add((String)hmHolidaysColour.get((String) alDates.get(i)));
																	alHolidayList.add((String)hmHolidaysName.get((String) alDates.get(i)));
																}
																
														}else if(alEmpCheckRosterWeektype.contains(strEmpId)){
															if(rosterWeeklyOffSet.contains(alDates.get(i))){
																out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+strColor+"; text-align: center;\">"+uF.showData("W/O", "")+"</div>");
															} else {
																Map hm = (Map) hmCount.get((String) alDates.get(i));
																if (hm == null)
																	hm = new HashMap();
																int nCount = uF.parseToInt((String) hm.get("nCountAB")) + 1;
//																System.out.println("Date"+(String) alDates.get(i)+"Count"+nCount);
																hm.put("nCountAB", nCount + "");
																hmCount.put((String) alDates.get(i), hm);
																out.println("<div class=\"data\">"+"<font color=\"red\">AB</font>"+"</div>");
															}
														}else if(weeklyOffSet.contains(alDates.get(i))){
																out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+strColor+"; text-align: center;\">"+uF.showData("W/O", "")+"</div>");
														}else {
																Map hm = (Map) hmCount.get((String) alDates.get(i));
																if (hm == null)
																	hm = new HashMap();
																int nCount = uF.parseToInt((String) hm.get("nCountAB")) + 1;
	//															System.out.println("Date"+(String) alDates.get(i)+"Count"+nCount);
																hm.put("nCountAB", nCount + "");
																hmCount.put((String) alDates.get(i), hm);
																out.println("<div class=\"data\">"+"<font color=\"red\">AB</font>"+"</div>");
																
														}
														
													} else {
														if(hmHolidaysName.containsKey((String) alDates.get(i))){
															/* out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+hmHolidaysColour.get((String) alDates.get(i))+"; text-align: center;\">"+uF.showData((String)hmHolidaysName.get((String) alDates.get(i)), "")+"</div>"); */
															out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+hmHolidaysColour.get((String) alDates.get(i))+"; text-align: center;\">H</div>");
														} else if(alEmpCheckRosterWeektype.contains(strEmpId)){
															if(rosterWeeklyOffSet.contains(alDates.get(i))){
																out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+strColor+"; text-align: center;\">"+uF.showData("W/O", "")+"</div>");
															} else {
																out.println("<div class=\"data\">"+"<font color=\"red\">AB</font>"+"</div>");
																Map hm = (Map) hmCount.get((String) alDates.get(i));
																if (hm == null)
																	hm = new HashMap();
																int nCount = uF.parseToInt((String) hm.get("nCountAB")) + 1;
							//									System.out.println("Date"+(String) alDates.get(i)+"Count"+nCount);
																hm.put("nCountAB", nCount + "");
																hmCount.put((String) alDates.get(i), hm);
															}
														}else if(weeklyOffSet.contains(alDates.get(i))){
															out.println("<div class=\"blueColor\" style=\"width: 100%; height: 100%; background-color: "+strColor+"; text-align: center;\">"+uF.showData("W/O", "")+"</div>");
														}else {
															out.println("<div class=\"data\">"+"<font color=\"red\">AB</font>"+"</div>");
															Map hm = (Map) hmCount.get((String) alDates.get(i));
															if (hm == null)
																hm = new HashMap();
															int nCount = uF.parseToInt((String) hm.get("nCountAB")) + 1;
							//								System.out.println("Date"+(String) alDates.get(i)+"Count"+nCount);
															hm.put("nCountAB", nCount + "");
															hmCount.put((String) alDates.get(i), hm);
														}
													}
					
													if (dblEO < 0) {
														//out.println("EO");
														out.println("<div class=\"leftearly\" title=\""+strTitleOUT+"\">&nbsp;</div>");
														if(!alBreaks.contains(strTitleOUT) && strTitleOUT.length()>0){
															alBreaks.add(strTitleOUT);
														}
														int nBreakCount = uF.parseToInt((String)hmBreaks.get(strTitleOUT+"_"+(String) alDates.get(i)));
														hmBreaks.put(strTitleOUT+"_"+(String) alDates.get(i), (nBreakCount+1)+"");
														
														
														Map hm = (Map) hmCount.get((String) alDates.get(i));
														if (hm == null)
															hm = new HashMap();
														int nCount = uF.parseToInt((String) hm.get("nCountEO")) + 1;
														hm.put("nCountEO", nCount + "");
														hmCount.put((String) alDates.get(i), hm);
													} else if (dblLO > 0) {
														//out.println("LO");
														out.println("<div class=\"worklate\">&nbsp;</div>");
														Map hm = (Map) hmCount.get((String) alDates.get(i));
														if (hm == null)
															hm = new HashMap();
														int nCount = uF.parseToInt((String) hm.get("nCountLO")) + 1;
														hm.put("nCountLO", nCount + "");
														hmCount.put((String) alDates.get(i), hm);
													} else if (dblOTO == 0 && strWH != null) {
														//out.println("OTO");
														out.println("<div class=\"wentontime\">&nbsp;</div>");
														Map hm = (Map) hmCount.get((String) alDates.get(i));
														if (hm == null)
															hm = new HashMap();
														int nCount = uF.parseToInt((String) hm.get("nCountOTO")) + 1;
														hm.put("nCountOTO", nCount + "");
														hmCount.put((String) alDates.get(i), hm);
													}
												%>
										</td>
							
							
										<%
											}	
										%>
							
										<%
										if(isService){
										%>
										<td style="width:5%" align="right"><%= uF.formatIntoTwoDecimal(dblCountRowWise) %></td>
										<%} %>
									</tr>
								<%			
									}
								%>
							
							<%
								}
							%>
					
					
						<tr class="table_result">
							<td><b>Total Employees: <%=nEmpCount%></b></td>
							<td style="width:40px;">Hours</td>
							<%
								double dblTotalCount = 0;
								for (int i = 0; i < alDates.size(); i++) {
									dblTotalCount += uF.parseToDouble((String) hmCountHrs.get((String) alDates.get(i)));
									String strCount = (String) hmCountHrs.get((String) alDates.get(i));
							%>
							<td><%=uF.formatIntoTwoDecimal(uF.parseToDouble(strCount))%></td>
							<%
								}
							%>
							<td align="right"><%=uF.formatIntoTwoDecimal(dblTotalCount) %></td>
						</tr>
					
					
					<tr>
						<td colspan=18>&nbsp;</td>
					</tr>
					
					<tr>
						<td colspan=18><h3>Summary</h3></td>
					</tr>
						<tr class="darktable">
					
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
							%>
							<td><%=uF.getDateFormat((String) alDates.get(i), IConstants.DATE_FORMAT, "dd MMM")%></td>
							<%
								}
							%>
						</tr>
					
					
						<tr>
					
							<td>Came in early</td>
							<td ><div class="cameearly">&nbsp;</div></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountEI"));
							%>
							<td><%=nCount%></td>
							<%
								}
							%>
					
						</tr>
					
						<tr>
					
							<td>Went out early</td>
							<td><div class="leftearly">&nbsp;</div></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountEO"));
							%>
							<td><%=nCount%></td>
							<%
								}
							%>
					
						</tr>
					
					
						<tr>
					
							<td>Came on time</td>
							<td><div class="cameontime">&nbsp;</div></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountOTI"));
							%>
							<td><%=nCount%></td>
							<%
								}
							%>
					
						</tr>
					
					
						<tr>
					
							<td>Went on time</td>
							<td><div class="wentontime">&nbsp;</div></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountOTO"));
							%>
							<td><%=nCount%></td>
							<%
								}
							%>
					
						</tr>
					
					
					
						<tr>
					
							<td>Came late</td>
							<td ><div class="camelate">&nbsp;</div></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountLI"));
							%>
							<td><%=nCount%></td>
							<%
								}
							%>
					
						</tr>
					
					
						<tr>
					
							<td>Worked late</td>
							<td><div class="worklate">&nbsp;</div></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountLO"));
							%>
							<td><%=nCount%></td>
							<%
								}
							%>
					
						</tr>
					 <tr>
					
							<td>Absent</td>
							<td><font color="red">AB</font></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountAB"));
	//								System.out.println("Total Date"+(String) alDates.get(i)+"Total Count"+nCount);
							%>
							<td><%=nCount%></td>
							<%
								}
							%>
					
						</tr>
						
						<tr>
					
							<td>Yet to come</td>
							<td><font>YTC</font></td>
							<%
								for (int i=0; i<alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									int nCount = uF.parseToInt((String) hm.get("nCountYTC"));
							%>
							<td><%=nCount%></td>
							<% } %>
					
						</tr>
						
						<tr>
					
							<td colspan="2"><strong>Total Present</strong></td>
							
							<%
								for (int i = 0; i < alDates.size(); i++) {
									Map hm = (Map) hmCount.get((String) alDates.get(i));
									if (hm == null)
										hm = new HashMap();
									
									int nTotalPresent = 0;
									
									if(uF.parseToInt((String) hm.get("nCountEI"))>0){
										nTotalPresent+=uF.parseToInt((String) hm.get("nCountEI"));
									}
									if(uF.parseToInt((String) hm.get("nCountLI"))>0){
										nTotalPresent+=uF.parseToInt((String) hm.get("nCountLI"));
									}
									if(uF.parseToInt((String) hm.get("nCountOTI"))>0){
										nTotalPresent+=uF.parseToInt((String) hm.get("nCountOTI"));
									}
									
							%>
							<td><%=nTotalPresent%></td>
							<% } %>
					
						</tr>
					
					
					<%for(int k=0; k<alBreaks.size(); k++){%>
						 <tr>
							<td><%=alBreaks.get(k)%></td>
							<td><font color="red"><%=(((alBreaks.get(k)!=null && alBreaks.get(k).toString().indexOf("IN")>=0))?"<div class=\"camelate\">&nbsp;</div>":"<div class=\"leftearly\">&nbsp;</div>") %></font></td>
							<%
								for (int i = 0; i < alDates.size(); i++) {
									int nCount = uF.parseToInt((String)hmBreaks.get((String)alBreaks.get(k)+"_"+(String)alDates.get(i)));
							%>
							<td><%=nCount%></td>
							<% } %>
					
						</tr>
					<%} %>
					
					<% if(hmLeaveTypes!=null && hmLeaveTypes.size()>0) { %>
					<tr>
						<td colspan=18>&nbsp;</td>
					</tr>
					
					<tr>
						<td colspan=18><h3>Leave Status</h3></td>
					</tr>
					<%} %>
						<%
							Set set1 = hmLeaveTypes.keySet();
							Iterator it1 = set1.iterator();
							while (it1.hasNext()) {
								String strLeaveType = (String) it1.next();
								Map hm = (Map) hmLeavesCount.get(strLeaveType);
								if(hm==null)hm=new HashMap();
						%>
							<tr>
								<td style="background:<%=hmLeaveTypes.get(strLeaveType)%>"><%=strLeaveType%></td>
								<td>&nbsp;</td>
								<% for (int i = 0; i < alDates.size(); i++) { %>
								<td><%=uF.showData((String) hm.get((String) alDates.get(i)), "0")%></td>
								<% } %>
							</tr>
							<% } %>
							<tr>
								<td colspan="18">
								<%
								for(int i=0; i<alHolidayList.size();) {
									if(i==0) {
								%>
										<h3>Public Holidays</h3>
										<% } %>
										<div style="margin-bottom:10px"> <div style="float:left;background-color:<%=alHolidayList.get(i++) %>;width:20px;text-align:center;margin-right:10px"> H </div><%=alHolidayList.get(i++) %></div>
									<% } %>
								</td>
							</tr>
						</table>
					</div>
					
					<div style="text-align: center; float: left; width: 100%;">
						<%
						int intPageCnt = uF.parseToInt(pageCount);
							int pageCnt = 0;
							int minLimit = 0;
							
							for(int i=1; i<=intPageCnt; i++) { 
								minLimit = pageCnt * 10;
								pageCnt++;
						%>
						<% if(i ==1) {
							String strPgCnt = (String)request.getAttribute("proPage");
							String strMinLimit = (String)request.getAttribute("minLimit");
							if(uF.parseToInt(strPgCnt) > 1) {
								 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
								 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
							}
							if(strMinLimit == null) {
								strMinLimit = "0";
							}
							if(strPgCnt == null) {
								strPgCnt = "1";
							}
						%>
							<span style="color: lightgray;">
							<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
								<a href="javascript:void(0);" onclick="loadMore('<%=strUserType%>','<%=strPgCnt %>','<%=strMinLimit %>');">
								<%="< Prev" %></a>
							<% } else { %>
								<b><%="< Prev" %></b>
							<% } %>
							</span>
							<span><a href="javascript:void(0);" onclick="loadMore('<%=strUserType%>','<%=pageCnt %>','<%=minLimit %>');"
							<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
							style="color: black;"<% } %>><%=pageCnt %></a></span>
							
							<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
								<b>...</b>
							<% } %>
						
						<% } %>
						
						<% if(i > 1 && i < intPageCnt) { %>
							<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
								<span><a href="javascript:void(0);" onclick="loadMore('<%=strUserType%>','<%=pageCnt %>','<%=minLimit %>');"
								<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
								style="color: black;"<% } %>><%=pageCnt %></a></span>
							<% } %>
						<% } %>
						
						<% if(i == intPageCnt && intPageCnt > 1) {
							String strPgCnt = (String)request.getAttribute("proPage");
							String strMinLimit = (String)request.getAttribute("minLimit");
							 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
							 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
							 if(strMinLimit == null) {
								strMinLimit = "0";
							}
							if(strPgCnt == null) {
								strPgCnt = "1";
							}
							%>
							<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intPageCnt) { %>
								<b>...</b>
							<% } %>
						
							<span><a href="javascript:void(0);" onclick="loadMore('<%=strUserType%>','<%=pageCnt %>','<%=minLimit %>');"
							<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
							style="color: black;"<% } %>><%=pageCnt %></a></span>
							<span style="color: lightgray;">
							<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
								<a href="javascript:void(0);" onclick="loadMore('<%=strUserType%>','<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
							<% } else { %>
								<b><%="Next >" %></b>
							<% } %>
							</span>
						<% } %>
						<%} %>
					</div>
		
			</div>

	<script>
	$(function(){
		/* $("#lt").DataTable({
			"order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
			'dom': 'lBfrtip',
	        'buttons': [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
	  	}); */
	
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
	});
	
	</script>
