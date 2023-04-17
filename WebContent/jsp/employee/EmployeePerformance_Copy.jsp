<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script src="scripts/charts/justgage/raphael-2.1.4.min.js" type="text/javascript"></script>
<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>


<script type="text/javascript">


$(function() {
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
    
    $("#strEmpId").multiselect().multiselectfilter();
});

function showattribList(check) {
	if (check==true) {
		document.getElementById("attriblistdiv").style.display="block";
		//document.getElementById("attribParam").checked = true;
		//$(".checkBoxClass").attr('checked', this.checked);
	} else {
		document.getElementById("attriblistdiv").style.display="none";
		//document.getElementById("attribParam").checked = false;
	}
}
 
$(document).ready(function() {
	 showattribList(<%=request.getAttribute("AT")!=null ? true : false %>);
});
 
function getEmpPerformanceData(dataType) {
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url:'EmployeePerformance.action?dataType='+dataType,
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divResult").html(result);
   		}
	});
}

	function submitForm() {
		var f_org ="";
		var f_strWLocation ="";
		var f_department ="";
		var f_service ="";
		var strEmpId = "";
		
		if(document.getElementById("f_org")){
			f_org=document.getElementById("f_org").value;
		}
		
		if(document.getElementById("f_strWLocation")){
			f_strWLocation = document.getElementById("f_strWLocation").value;
		}
		
		if(document.getElementById("f_department")){
			f_department = document.getElementById("f_department").value;
		}
		
		if(document.getElementById("f_service")){
			f_service = document.getElementById("f_service").value;
		}
		
		if(document.getElementById("strEmpId")){
			strEmpId = getSelectedValue("strEmpId");
		}
				
		var dataType = document.getElementById("dataType").value;
		
		var period = "";
		if(document.getElementById("period")){
			period = document.getElementById("period").value;
		}
		
		var strStartDate = "";
		if(document.getElementById("strStartDate")){
			strStartDate = document.getElementById("strStartDate").value;
		}
		
		var strEndDate = "";
		if(document.getElementById("strEndDate")){
			strEndDate = document.getElementById("strEndDate").value;
		}
		
		var filterParam = "";
		if(document.getElementById("filterParam")){
			filterParam = getCheckedValue("filterParam");
		}
		
		var dateParam = $("input[name=dateParam]:checked").val();
		/* if(document.getElementById("dateParam")){
			dateParam = document.getElementById("dateParam").value;
		} */
		
		var attribParam = "";
		if(document.getElementById("attribParam")){
			attribParam = getCheckedValue("attribParam");//document.getElementById("attribParam").value;
		}
		
		
		var paramValues="&f_strWLocation="+f_strWLocation+"&f_department="+f_department+"&f_service="+f_service+"&strEmpIds1="+strEmpId
		+"&period="+period+"&strStartDate="+strStartDate+"&strEndDate="+strEndDate+"&dataType="+dataType+"&dateParam="+dateParam
		+"&attribParam="+attribParam+"&filterParam1="+filterParam; 
		 
		//alert("paramValues==>"+paramValues);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url:'EmployeePerformance.action?f_org='+f_org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
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
 	
 	function getCheckedValue(id) {


 		var checkboxes = document.getElementsByName(id);
 		var exportchoice = "";
 		for (var i=0, n=checkboxes.length;i<n;i++) 
 		{
 		    if (checkboxes[i].checked) 
 		    {
 		    	exportchoice += ","+checkboxes[i].value;
 		    }
 		}
 		if (exportchoice) exportchoice = exportchoice.substring(1);

 		return exportchoice;
 	}
</script>


<style>
.emp_perfmnc1{
border: solid 1px #ccc;
padding: 10px;
border-radius: 5px;
-moz-box-shadow: 0px 2px 4px #D2D2D2;
box-shadow: 0px 2px 4px #D2D2D2;
margin-top: 10px;
}
.info_row{
border-bottom: solid 1px #e4e4e4;
padding: 5px;
color: #666666;
background-color: rgb(243, 243, 243);
}
</style>

	<%
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String dataType = (String) request.getAttribute("dataType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		
		UtilityFunctions uF=new UtilityFunctions();
		List<String> checkAttribute = (List<String>)request.getAttribute("checkAttribute");
		if(checkAttribute==null) checkAttribute=new ArrayList();
		//System.out.println("checkAttribute ===>> " + checkAttribute);
		List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
		if(elementouterList == null) elementouterList = new ArrayList<List<String>>();
		
		Map<String,List<List<String>>> hmElementAttribute = (Map<String,List<List<String>>>)request.getAttribute("hmElementAttribute");
		if(hmElementAttribute == null) hmElementAttribute = new HashMap<String,List<List<String>>>();
	
		String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
	%>
	
<%-- <section class="content"> --%>
	<% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
		<div style="width: 100%;">
			<ul class="nav nav-pills">
				<li class="<%=(dataType == null || dataType.equals("MYTEAM")) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(dataType == null || dataType.equals("MYTEAM")) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="javascript:void(0)" onclick="getEmpPerformanceData('MYTEAM')" style="padding: 4px 15px !important; border-radius: 10px 10px 0px 0px;">My Team</a>
				</li>
				<li class="<%=(dataType == null || dataType.equals(strBaseUserType)) ? "active" : "" %>" style="border-radius: 10px 10px 0px 0px; <%=(dataType == null || dataType.equals(strBaseUserType)) ? "" : "background-color: rgb(226, 226, 226)" %>;">
					<a href="javascript:void(0)" onclick="getEmpPerformanceData('<%=strBaseUserType %>')" style="padding: 4px 15px !important;border-radius: 10px 10px 0px 0px;"><%=strBaseUserType %></a>
				</li>
			</ul>
		</div>
	<% } %>
	
	<%-- <div class="row jscroll">
		<section class="col-lg-12 connectedSortable">
			<div class="box box-primary"> --%>
				<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
					<s:form name="frmEmployeePerformance" action="EmployeePerformance" theme="simple">
						<s:hidden name="dataType" id="dataType" />
						<div class="box box-default collapsed-box">
							<div class="box-header with-border">
							    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
							    <div class="box-tools pull-right">
							        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							    </div>
							</div>
							<div class="box-body" style="padding: 5px; overflow-y: auto;">
								
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									 <% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER)) || (dataType != null && dataType.equals(strBaseUserType))) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
											<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" headerKey="" headerValue="All Organisations" 
													cssClass="inline" onchange="submitForm();" list="organisationList" key="" />
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Location</p>
											<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" 
								    				list="wLocationList" key="" onchange="getContent('myDiv', 'GetLiveEmployeeList.action?f_strWLocation='+document.frmEmployeePerformance.f_strWLocation.options[document.frmEmployeePerformance.f_strWLocation.selectedIndex].value+'&f_department='+document.frmEmployeePerformance.f_department.options[document.frmEmployeePerformance.f_department.selectedIndex].value+'&f_service='+document.frmEmployeePerformance.f_service.options[document.frmEmployeePerformance.f_service.selectedIndex].value+'&multiple=multiple')" />										
								    	</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Department</p>
											<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments" 
								    				onchange="getContent('myDiv', 'GetLiveEmployeeList.action?f_strWLocation='+document.frmEmployeePerformance.f_strWLocation.options[document.frmEmployeePerformance.f_strWLocation.selectedIndex].value+'&f_department='+document.frmEmployeePerformance.f_department.options[document.frmEmployeePerformance.f_department.selectedIndex].value+'&f_service='+document.frmEmployeePerformance.f_service.options[document.frmEmployeePerformance.f_service.selectedIndex].value+'&multiple=multiple')"></s:select>										
								    	</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Service</p>
											<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" headerKey="0" headerValue="All SBUs" 
								    				onchange="getContent('myDiv', 'GetLiveEmployeeList.action?f_strWLocation='+document.frmEmployeePerformance.f_strWLocation.options[document.frmEmployeePerformance.f_strWLocation.selectedIndex].value+'&f_department='+document.frmEmployeePerformance.f_department.options[document.frmEmployeePerformance.f_department.selectedIndex].value+'&f_service='+document.frmEmployeePerformance.f_service.options[document.frmEmployeePerformance.f_service.selectedIndex].value+'&multiple=multiple')"></s:select>										
								    	</div>
								    	<% } %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px; margin-bottom: 8px;">Employee</p>
											<div id="myDiv">
												<s:select theme="simple" name="strEmpId"  id="strEmpId" listKey="employeeId" multiple="true" 
													size="6" listValue="employeeName" headerKey="0" list="employeeList" key="" required="true" />
											</div>
										</div>
									</div>
								</div><br>
								
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<div class="col-lg-12 col-md-12 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Filter</p>
											<s:radio name="dateParam" list="#{'1':''}" />
											<s:select theme="simple" label="Select Pay Cycle" name="period" id="period" listKey="periodId"
												listValue="periodName" headerKey="0" list="periodList" key="" required="true" cssStyle="width: 130px !important;"/>
							                &nbsp;&nbsp;
							                <s:radio name="dateParam" list="#{'2':''}" />
							                <s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 90px !important;"/>
							                <s:textfield name="strEndDate"  id="strEndDate" cssStyle="width: 90px !important;"/>
										</div>
									</div>
								</div>
								<div class="content1">									
									<div class="row row_without_margin" style="padding-top: 10px;">
										<div class="col-lg-1 col-md-2 autoWidth" style="padding-right: 0px;">
											<p style="float: left;">
								             <input type="checkbox" name="filterParam" id="filterParam" value="LH" <%=request.getAttribute("LH") !=null ? "checked" : ""%>/> Logged Hours &nbsp;
								             <% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
									             <input type="checkbox" name="filterParam" value="REVIEW" <%=request.getAttribute("REVIEW") !=null ? "checked" : ""%>/> Review &nbsp;
									             <input type="checkbox" name="filterParam" id="filterParam" value="GOAL_KRA_TARGET" <%=request.getAttribute("GOAL_KRA_TARGET") !=null ? "checked" : ""%>/> Goals/ BSC/ KRAs/ Targets &nbsp;
									             <%-- <input type="checkbox" name="filterParam" value="GOAL" <%=request.getAttribute("GOAL") !=null ? "checked" : ""%>/>Goals &nbsp;
									             <input type="checkbox" name="filterParam" value="KRA" <%=request.getAttribute("KRA") !=null ? "checked" : ""%>/>KRAs &nbsp;
									             <input type="checkbox" name="filterParam" value="TARGET" <%=request.getAttribute("TARGET") !=null ? "checked" : ""%>/>Target &nbsp; --%>
									             <input type="checkbox" name="filterParam" value="AT" <%=request.getAttribute("AT") !=null ? "checked" : ""%> onclick="showattribList(this.checked);"/> Attribute &nbsp;
								             <% } %>
							                </p>
										</div>
										
										<div class="col-lg-1 col-md-2 autoWidth" style="padding-right: 0px;">
											<input type="button" class="btn btn-primary" value="Search" name="search" align="center" onclick="submitForm();" />
										</div>
									</div>
									
									<div class="row row_without_margin" style="padding-top: 10px;">
										<div id="attriblistdiv" style=" display: none;">
											<table class="table table_no_border" style="margin-left: 5px; width: auto;">
												<%
												//List<List<String>> attributeouterList = (List<List<String>>)request.getAttribute("attributeouterList");
												for(int i=0; elementouterList!=null && i<elementouterList.size(); i++) {
													List<String> innerList = elementouterList.get(i);
												%>
													<tr>
														<td><strong><%=innerList.get(1)%></strong></td>
													</tr>
													<%
														int count=0;
														List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
														for(int j=0; attributeouterList1 != null && j<attributeouterList1.size(); j++) {
															List<String> attributeList1 = attributeouterList1.get(j);
															if(attributeList1 == null) attributeList1 = new ArrayList<String>();
															//System.out.println("checkAttribute ===>> " + checkAttribute+ " -- attributeList1 ===>> " + attributeList1);
															if(count==0) {  
													%>
													<tr>
									                 <% }
									                 count++;
									                 %>
														<td nowrap="nowrap"><input type="checkbox" name="attribParam" id="attribParam" value="<%=attributeList1.get(0) %>" <%if(checkAttribute.contains(attributeList1.get(0).trim())){ %>checked<%} %>/> <%=attributeList1.get(1) %> </td>                 
									                <%if(count==5) { count=0; %>
									                </tr>
									                <% } } %>
												<% } %>
											</table>
										</div>
									</div>
								</div>
							</div>
						</div>
					</s:form> 
							
					<div class="row" style="margin-right: 0px;margin-left: 0px;margin-top: 30px;clear:both;">
						<%
						//Map hmProKPI = (Map)request.getAttribute("hmProKPI");
						Map hmEmpName = (Map)request.getAttribute("hmEmpName");
						Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap");
						Map hmKPI = (Map)request.getAttribute("hmKPI");
						if(hmKPI == null)hmKPI = new HashMap();
						
						Map<String, String> hmKPIData = (Map<String, String>)request.getAttribute("hmKPIData");
						if(hmKPIData == null) hmKPIData = new HashMap<String, String>();
						
						/* Map<String, String> hmAnalysisSummaryMap = (Map<String, String>) request.getAttribute("hmAnalysisSummaryMap");
						if(hmAnalysisSummaryMap == null) hmAnalysisSummaryMap = new HashMap<String, String>(); */
						
						Set set = hmKPI.keySet();
						Iterator it = set.iterator();
						int i=0; 
						while(it.hasNext()) {
							String strEmpId = (String)it.next();
							i++;
							AngularMeter semiWorkedAbsent = (AngularMeter)hmKPI.get(strEmpId);
							
						%>
								
							<div class="emp_perfmnc1 col-lg-3 col-md-4 col-sm-6">
			                  <div class="kpi_view1">
			                      <div id="guage<%=strEmpId %>" class="gauge"></div>
			                      <script>
									//document.addEventListener("DOMContentLoaded", function(event) {
									    var g1 = new JustGage({
									        id: "guage<%=strEmpId %>",
									     title: "",
									     label: "Performance",
									     value: <%=uF.parseToDouble(hmKPIData.get(strEmpId))%>,
									     min: 0,
									     max: 100,
									        decimals: 0,
									        gaugeWidthScale: 0.6,
									        levelColors: [
						                      "#FF0000",
						                      "#FFFF00",
						                      "#008000"
						                    ]
									    });
									//});
								</script>
			                  </div>
			                  
			                  <div class="emp_info" style="text-align: center;">
			                    <div class="info_row"><span>Name:</span><strong> <%= (String)hmEmpName.get(strEmpId)%></strong></div>
			                    <div class="info_row"><span>Designation:</span> <strong><%= uF.showData((String)hmEmpDesigMap.get(strEmpId), "-")%></strong></div>
			                  </div>
							</div>
						<% }
						if(hmKPI.size()==0) {
						%>
							<div class="nodata msg"><span>You have not selected any employee from the list.</span></div>
						<% } %>
						</div>
	                </div>
	      <%--        </div>
	       </section>
	    </div>
</section> --%>
	


