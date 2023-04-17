<%@page import="com.konnect.jpms.select.FillLevel"%>
<%@page import="com.konnect.jpms.select.FillDepartment"%>
<%@page import="com.konnect.jpms.select.FillWLocation"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

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
    
//    $("#strEmpId").multiselect().multiselectfilter();
});

$(document).ready(function(){
	callTeamKPIMeasures();
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
		/* var f_strWLocation ="";
		var f_department ="";
		var f_service ="";
		var strEmpId = "";
		 */
		if(document.getElementById("f_org")){
			f_org=document.getElementById("f_org").value;
		}
		
		/* if(document.getElementById("f_strWLocation")){
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
		} */
				
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
		/* var paramValues="&f_strWLocation="+f_strWLocation+"&f_department="+f_department+"&f_service="+f_service+"&strEmpIds1="+strEmpId
		+"&period="+period+"&strStartDate="+strStartDate+"&strEndDate="+strEndDate+"&dataType="+dataType+"&dateParam="+dateParam
		+"&attribParam="+attribParam+"&filterParam1="+filterParam; */
		var paramValues="&period="+period+"&strStartDate="+strStartDate+"&strEndDate="+strEndDate+"&dataType="+dataType+"&dateParam="+dateParam
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
 	
 	
 	function callTeamKPIMeasures() {
		var wLocParam = new Array();
		var deptParam = new Array();
		var levelParam = new Array();
		var checkedParam = new Array();
		var fltrParam = new Array();
		//alert("checkedParam ===>> " +checkedParam);
		var datedParam = new Array();
		if(document.getElementById("checkLocation")) {
			$("input:checkbox[name=checkLocation]:checked").each(function() {
				wLocParam.push($(this).val());
			});
		}
		$("input:checkbox[name=checkDepart]:checked").each(function() {
			deptParam.push($(this).val());
		});
		$("input:checkbox[name=checkLevel]:checked").each(function() {
			levelParam.push($(this).val());
		});
		$("input:checkbox[name=checkParam]:checked").each(function() {
			checkedParam.push($(this).val());
		});
		$("input:radio[name=dateParam]:checked").each(function() {
			datedParam.push($(this).val());
		});
		//var dateParam = $("input[name=dateParam]:checked").val();
		$("input:checkbox[name=filterParam]:checked").each(function() {
			fltrParam.push($(this).val());
		});
		//var dateParam = document.getElementById("dateParam").value;
		var period = document.getElementById("period").value;
		var strStartDate = document.getElementById("strStartDate").value;
		var strEndDate = document.getElementById("strEndDate").value;
		var dataType = document.getElementById("dataType").value;
		var f_org = "";
		if(document.getElementById("f_org")) {
			f_org = document.getElementById("f_org").value;
		}
		//alert(" period ===> " +period + " strStartDate ===> " +strStartDate+ " strEndDate ===> " +strEndDate);
		$("#teamKPIs").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		var action = 'EmployeePerformanceKPI.action?checkParam=' + checkedParam + '&dateParam=' + datedParam + '&wLocParam=' + wLocParam 
			+ '&deptParam=' + deptParam + '&levelParam=' + levelParam + '&filterParam1='+fltrParam + '&f_org=' + f_org
			+'&period=' + period + '&strStartDate=' + strStartDate + '&strEndDate=' + strEndDate + '&dataType=' + dataType;
		//alert("action ===> " + action);
		getContent('teamKPIs', action);
	}
 	
 	
</script>


<style>
.emp_perfmnc1 {
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
		List<FillWLocation> wLocationList = (List<FillWLocation>) request.getAttribute("wLocationList");
		List<FillDepartment> departmentList = (List<FillDepartment>) request.getAttribute("departmentList");
		List<FillLevel> levelList = (List<FillLevel>) request.getAttribute("levelList");
		
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
	
				<div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
					<s:form name="frmEmployeePerformance" action="EmployeePerformance" theme="simple">
						<s:hidden name="dataType" id="dataType" />
						<div class="box box-default collapsed-box">
							<div class="box-header with-border">
							    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
							    <div class="box-tools pull-right">
							        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
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
										<% } %>
										<div class="col-lg-9 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px; margin-bottom: 5px;">Period</p>
											<s:radio name="dateParam" list="#{'1':''}" />
											<s:select theme="simple" label="Select Pay Cycle" name="period" id="period" listKey="periodId"
												listValue="periodName" headerKey="0" list="periodList" key="" required="true" cssStyle="width: 130px !important;"/>
							                &nbsp;&nbsp;
							                <s:radio name="dateParam" list="#{'2':''}" />
							                <s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 90px !important;"/>
							                <s:textfield name="strEndDate"  id="strEndDate" cssStyle="width: 90px !important;"/>
										</div>
										<div class="col-lg-1 col-md-2 autoWidth" style="padding-right: 0px;">
											<p style="padding-left: 5px;">&nbsp;</p>
											<input type="button" class="btn btn-primary" value="Search" name="search" onclick="submitForm();" />
										</div>
									</div>
								</div>
								
							</div>
						</div>
					
	                
	                
	                <div id="MainDiv" class="row" style="margin-left: 0px;margin-right: 0px;"> 
						<%-- <s:form theme="simple" action="TeamPerformance" method="POST" id="frmPerformance" name="frmPerformance"> --%>
						<div class="col-lg-3 col-md-3 col-sm-12" style="padding: 2px; overflow-y: auto; max-height: 500px;">
							<div style="margin-top: 10px; margin-left: 4px; border: 2px solid #F3F0F0;padding-top: 10px;padding-bottom: 10px;padding-left: 5px;padding-right: 5px;">
								<% if((strUserType!=null && !strUserType.equals(IConstants.MANAGER)) || (dataType != null && dataType.equals(strBaseUserType))) { %>
								<div class="box box-primary" style="border-top-color:#cda55f;/* #EEEEEE;*/">
              
					                <div class="box-header with-border">
					                    <h3 class="box-title">Work Location</h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto;">
					                    <div class="content1" id="idlocation">
											<table>
												<% for (int i = 0; wLocationList != null && !wLocationList.isEmpty() && i < wLocationList.size(); i++) { %>
												<tr>
													<td class="textblue"><input type="checkbox" value="<%=(String) ((FillWLocation) wLocationList.get(i)).getwLocationId()%>" name="checkLocation" id="checkLocation" checked="checked" onclick="callTeamKPIMeasures();" /></td>
													<td><%=(String) ((FillWLocation) wLocationList.get(i)).getwLocationName()%></td>
												</tr>
												<% } %>
											</table>
										</div>
					                </div>
					                <!-- /.box-body -->
					            </div>
								<% } %>
								<div class="box box-primary" style="border-top-color:	#cda55f;/* #EEEEEE;*/">
              
					                <div class="box-header with-border">
					                    <h3 class="box-title">Department</h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto;">
					                    <div class="content1" id="iddepart">
											<table>
												<% for (int i = 0; departmentList != null && !departmentList.isEmpty() && i < departmentList.size(); i++) { %>
												<tr>
													<td class="textblue"><input type="checkbox" value="<%=(String) ((FillDepartment) departmentList.get(i)).getDeptId()%>" name="checkDepart" checked="checked" onclick="callTeamKPIMeasures();" /></td>
													<td><%=(String) ((FillDepartment) departmentList.get(i)).getDeptName()%></td>
												</tr>
												<% } %>
											</table>
										</div>
					                </div>
					                <!-- /.box-body -->
					            </div>
								<div class="box box-primary" style="border-top-color: #cda55f;/* #EEEEEE;*/">
              
					                <div class="box-header with-border">
					                    <h3 class="box-title">Levels</h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto;">
					                    <div class="content1" id="idlevels">
											<table>
												<% for (int i = 0; levelList != null && !levelList.isEmpty() && i < levelList.size(); i++) { %>
												<tr>
													<td class="textblue"><input type="checkbox" value="<%=(String) ((FillLevel) levelList.get(i)).getLevelId()%>" name="checkLevel" checked="checked" onclick="callTeamKPIMeasures();" />
													</td>
													<td><%=(String) ((FillLevel) levelList.get(i)).getLevelCodeName()%></td>
												</tr>
												<% } %>
											</table>
										</div>
					                </div>
					                <!-- /.box-body -->
					            </div>
					            <div class="box box-primary" style="border-top-color:#cda55f;/* #EEEEEE;*/">
              
					                <div class="box-header with-border">
					                    <h3 class="box-title">Measure</h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto;">
					                    <div class="content1" id="idMeasures">
											<table>
							                  	<tr>
								                  	<td class="textblue"><input type="checkbox" value="LH" name="filterParam" checked="checked" onclick="callTeamKPIMeasures();"/></td>
								                  	<td>Logged Hours</td> 
							                  	</tr>
							                  	<tr>
								                  	<td class="textblue"><input type="checkbox" value="REVIEW" name="filterParam" checked="checked" onclick="callTeamKPIMeasures();"/></td>
								                  	<td>Review</td> 
							                  	</tr>
							                  	<tr>
								                  	<td class="textblue"><input type="checkbox" value="GOAL_KRA_TARGET" name="filterParam" checked="checked" onclick="callTeamKPIMeasures();"/></td>
								                  	<td>Goals/ BSC/ KRAs/ Targets</td> 
							                  	</tr>
							                  	<tr>
								                  	<td class="textblue"><input type="checkbox" value="AT" name="filterParam" checked="checked" onclick="callTeamKPIMeasures();"/></td>
								                  	<td>Attribute</td> 
							                  	</tr>
											</table>
										</div>
					                </div>
					                <!-- /.box-body -->
					            </div>
								<div class="box box-primary" style="border-top-color: #cda55f;/* #EEEEEE;*/">
              
					                <div class="box-header with-border">
					                    <h3 class="box-title">Parameters</h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto;">
					                    <div class="content1" id="idparameters">
					
											<ul style="padding-left: 0px;">
												<%
													//List<List<String>>  attributeouterList=(List<List<String>>  )request.getAttribute("attributeouterList");
													for (int i = 0; elementouterList != null && !elementouterList.isEmpty() && i < elementouterList.size(); i++) {
														List<String> innerList = elementouterList.get(i);
												%>
												<li><strong><%=innerList.get(1)%></strong>
													<ul>
														<%
															int count = 0;
																List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
																for (int j = 0; attributeouterList1 != null && !attributeouterList1.isEmpty() && j < attributeouterList1.size(); j++) {
																	List<String> attributeList1 = attributeouterList1.get(j);
														%>
														<li><input type="checkbox" value="<%=attributeList1.get(0)%>" name="checkParam" checked="checked" onclick="callTeamKPIMeasures();" /> <%=attributeList1.get(1) %></li>
					
														<% } %>
													</ul></li>
												<% } %>
											</ul>
										</div>
					                </div>
					                <!-- /.box-body -->
					            </div>

							</div>
			
						</div>
						
						<div id="teamKPIs" class="col-lg-9 col-md-9 col-sm-12">
							
						</div>
					</div>
	                
	               </s:form> 
				</div> 
	                
	                
	      <%--        </div>
	       </section>
	    </div>
</section> --%>
	


