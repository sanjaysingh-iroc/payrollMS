<%@page import="java.util.HashMap"%>
<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.performance.FillAttribute"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script src="scripts/charts/highcharts.js" type="text/javascript"></script>

<style>
.factsheet-link a{
color: #fff;
/* top: 60px; */
}

</style>

<script type="text/javascript">
    function show_employees() {
    	dojo.event.topic.publish("show_employees");
    }
     
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
    	}else{
    		document.getElementById("attriblistdiv").style.display="none";
    		//document.getElementById("attribParam").checked = false;
    	}
    }
     $(document).ready(function() {
    	 showattribList(<%=request.getAttribute("AT")!=null ? true : false %>);
    	});
     
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
		
		var dataType = document.getElementById("dataType").value;
		
		var filterParam = "";
		if(document.getElementById("filterParam")){
			filterParam = getCheckedValue("filterParam");
		}
		
		var dateParam = $("input[name=dateParam]:checked").val();
		/* alert("dateParam ===>> " + dateParam); */
		
		var attribParam = "";
		if(document.getElementById("attribParam")){
			attribParam = getCheckedValue("attribParam");
		}
		
		var paramValues="&f_strWLocation="+f_strWLocation+"&f_department="+f_department+"&f_service="+f_service+"&strEmpIds1="+strEmpId
		+"&period="+period+"&strStartDate="+strStartDate+"&strEndDate="+strEndDate+"&dataType="+dataType+"&dateParam="+dateParam
		+"&attribParam="+attribParam+"&filterParam1="+filterParam; 
		 
		//alert("paramValues==>"+paramValues);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url:'EmployeeComparison.action?f_org='+f_org+paramValues,
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
 		for (var i=0, n=checkboxes.length;i<n;i++) {
 		    if (checkboxes[i].checked) {
 		    	exportchoice += ","+checkboxes[i].value;
 		    }
 		}
 		if (exportchoice) exportchoice = exportchoice.substring(1);

 		return exportchoice;
 	}
 	
 	
 	/* function getCheckedRadioValue(id) {
 		var radioButton = document.getElementsByName(id);
 		var exportchoice = "";
 		alert("radioButton ===>> " + radioButton);
 		for (var i=0; i<radioButton.length; i++) {
 			alert("radioButton[i].checked ===>> " + radioButton[i].checked);
 		    if (radioButton[i].checked) {
 		    	exportchoice =radioButton[i].value;
 		    	alert("radioButton[i].value ===>> " + radioButton[i].value);
 		    }
 		}
 		return exportchoice;
 	} */
 	
 	
 	function getEmpComparisonData(dataType) {
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url:'EmployeeComparison.action?dataType='+dataType,
			data: $("#"+this.id).serialize(),
			success: function(result){
				$("#divResult").html(result);
	   		}
		});
	}
</script>


<%
    List alEmpCompareData = (List)request.getAttribute("alEmpCompareData");
    List alEmployeeNames = (List)request.getAttribute("alEmployeeNames");
    List alGrossComp = (List)request.getAttribute("alGrossComp");
    List alLoggedHours = (List)request.getAttribute("alLoggedHours");
    List alProjectHours = (List)request.getAttribute("alProjectHours");
    
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
      
    List<List<String>> attriblist = (List<List<String>>) request.getAttribute("attriblist");
    UtilityFunctions uF=new UtilityFunctions();
    List<String> checkAttribute = (List<String>)request.getAttribute("checkAttribute");
    if(checkAttribute==null) checkAttribute=new ArrayList();
    
    List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
    if(elementouterList == null) elementouterList = new ArrayList<List<String>>();
    
    Map<String,List<List<String>>> hmElementAttribute=(Map<String,List<List<String>>>)request.getAttribute("hmElementAttribute");
    if(hmElementAttribute == null) hmElementAttribute = new HashMap<String,List<List<String>>>();
    
    String[] arrEnabledModules = (String[])request.getAttribute("arrEnabledModules");
    
    String dataType = (String) request.getAttribute("dataType"); 
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
    
    %>
<%-- <section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable"> --%>
        	<% if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) { %>
            <div class="box box-none nav-tabs-custom">
	            <ul class="nav nav-tabs">
					<li class="<%=(dataType == null || dataType.equals("MYTEAM")) ? "active" : "" %>"><a href="javascript:void(0)" onclick="getEmpComparisonData('MYTEAM')" data-toggle="tab">My Team</a></li>
					<li class="<%=(dataType == null || dataType.equals(strBaseUserType)) ? "active" : "" %>"><a href="javascript:void(0)" onclick="getEmpComparisonData('<%=strBaseUserType %>')" data-toggle="tab"><%=strBaseUserType %></a></li>
				</ul>
			<% }else{ %>
				<div class="box box-none">
			<% } %>
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height: 600px;">
                    <div class="leftbox reportWidth">
                        <s:form name="frmEmployeeComparison" id="frmEmployeeComparison" action="EmployeeComparison" theme="simple">
                            <% 	int attriblistcnt=0;
								String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
							%>
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
	                                            		onchange="submitForm();" list="organisationList" key="" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" list="wLocationList" key=""
	                                            		onchange="getContent('myDiv', 'GetLiveEmployeeList.action?f_strWLocation='+document.frmEmployeeComparison.f_strWLocation.options[document.frmEmployeeComparison.f_strWLocation.selectedIndex].value+'&f_department='+document.frmEmployeeComparison.f_department.options[document.frmEmployeeComparison.f_department.selectedIndex].value+'&f_service='+document.frmEmployeeComparison.f_service.options[document.frmEmployeeComparison.f_service.selectedIndex].value+'&multiple=multiple')" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Department</p>
												<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments"
	                                            		onchange="getContent('myDiv', 'GetLiveEmployeeList.action?f_strWLocation='+document.frmEmployeeComparison.f_strWLocation.options[document.frmEmployeeComparison.f_strWLocation.selectedIndex].value+'&f_department='+document.frmEmployeeComparison.f_department.options[document.frmEmployeeComparison.f_department.selectedIndex].value+'&f_service='+document.frmEmployeeComparison.f_service.options[document.frmEmployeeComparison.f_service.selectedIndex].value+'&multiple=multiple')"></s:select>
											</div>
			
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Service</p>
												<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" headerKey="0" headerValue="All SBUs"
	                                            		onchange="getContent('myDiv', 'GetLiveEmployeeList.action?f_strWLocation='+document.frmEmployeeComparison.f_strWLocation.options[document.frmEmployeeComparison.f_strWLocation.selectedIndex].value+'&f_department='+document.frmEmployeeComparison.f_department.options[document.frmEmployeeComparison.f_department.selectedIndex].value+'&f_service='+document.frmEmployeeComparison.f_service.options[document.frmEmployeeComparison.f_service.selectedIndex].value+'&multiple=multiple')"></s:select>
											</div>
											<% } %>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Employee</p>
												<div id="myDiv">
													<s:select theme="simple" name="strEmpId" id="strEmpId" listKey="employeeId" multiple="true"
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
											<% String dateParam = (String)request.getAttribute("dateParam"); %>
												<p style="padding-left: 5px;">Filter</p>
												<input type="radio" id="dateParam" name="dateParam" value="1" <% if(dateParam != null && dateParam.equals("1")) { %> checked="checked" <% } %>>
												<%-- <s:radio name="dateParam" id="dateParam" list="#{'1':''}" /> --%>
												<s:select theme="simple" label="Select Pay Cycle" name="period" id="period" listKey="periodId" listValue="periodName" 
													headerKey="0" list="periodList" key="" required="true" cssStyle="width: 130px !important;" />
								                &nbsp;&nbsp;
								                <input type="radio" id="dateParam" name="dateParam" value="2" <% if(dateParam != null && dateParam.equals("2")) { %> checked="checked" <% } %>>
								                <%-- <s:radio name="dateParam" id="dateParam" list="#{'2':''}" /> --%>
								                <s:textfield name="strStartDate" id="strStartDate" cssStyle="width: 90px !important;"/>
								                <s:textfield name="strEndDate"  id="strEndDate" cssStyle="width: 90px !important;"/>
											</div>
										</div>
									</div><br>
										<div class="row row_without_margin">
                                        	<div class="col-lg-5 col-md-5 col-sm-5 autoWidth">
                                        		<div class="row row_without_margin">
	                                        		<div class="col-lg-5 col-md-5 col-sm-5 autoWidth">
	                                        			<input type="checkbox" name="filterParam" id="filterParam" value="SKILL" <%=request.getAttribute("SKILL")!=null ? "checked" : ""%>/> Skills &nbsp;
											             <% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
												             <input type="checkbox" name="filterParam" id="filterParam" value="REVIEW" <%=request.getAttribute("REVIEW") !=null ? "checked" : ""%>/> Review &nbsp;
												             <input type="checkbox" name="filterParam" id="filterParam" value="GOAL_KRA_TARGET" <%=request.getAttribute("GOAL_KRA_TARGET") !=null ? "checked" : ""%>/> Goals/ BSC/ KRAs/ Targets &nbsp;
												             <%-- <input type="checkbox" name="filterParam" id="filterParam" value="GOAL" <%=request.getAttribute("GOAL") !=null ? "checked" : ""%>/>Goals &nbsp;
												             <input type="checkbox" name="filterParam" id="filterParam" value="KRA" <%=request.getAttribute("KRA") !=null ? "checked" : ""%>/>KRAs &nbsp;
												             <input type="checkbox" name="filterParam" id="filterParam" value="TARGET" <%=request.getAttribute("TARGET") !=null ? "checked" : ""%>/>Target &nbsp; --%>
												             <input type="checkbox" name="filterParam" id="filterParam" value="AT" <%=request.getAttribute("AT") !=null ? "checked" : ""%> onclick="showattribList(this.checked);"/> Attribute &nbsp;
											             <% } %>
	                                        		</div>
	                                        		<div class="col-lg-2 col-md-2 col-sm-2 hidden-xs autoWidth">
	                                        			<img src="images1/vert_line_img.png" height="70"> 
	                                        		</div>
	                                        		<div class="col-lg-5 col-md-5 col-sm-5 autoWidth">
	                                        			<input type="checkbox" name="filterParam" id="filterParam" value="GC" <%=request.getAttribute("GC")!=null ? "checked" : ""%> /> Gross Compensation <br /> 
	                                        			<input type="checkbox" name="filterParam" id="filterParam" value="LH" <%=request.getAttribute("LH")!=null ? "checked" : ""%> /> Logged Hours <br />
	                                        		</div>
	                                        	</div>
                                        	</div>
                                        	<div class="col-lg-5 col-md-5 col-sm-5 autoWidth">
                                        		<div id="attriblistdiv" style="display: none;">
													<table class="table table_no_border" style="margin-left: 5px; width: auto;">
														<%
														List<List<String>> attributeouterList = (List<List<String>>)request.getAttribute("attributeouterList");
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
																	if(count==0) {  
															%>
															<tr>
											                 <% } 
											                 count++;
											                 %>
																<td nowrap="nowrap"><input type="checkbox" id="attribParam" name="attribParam" value="<%=attributeList1.get(0) %>" <%if(checkAttribute.contains(attributeList1.get(0))){ %>checked<%} %>/> <%=attributeList1.get(1) %> </td>                 
											                <%if(count==5) { count=0; %>
											                </tr>
											                <% } } %>
														<% } %>
													</table>
												</div>
                                        	</div>
                                       	</div><br>
                                       	<div class="row row_without_margin">
	                                       	<div class="col-lg-2 col-md-2 col-sm-12 autoWidth">
		                                    	<input type ="button" value="Submit" class="btn btn-primary" onclick="submitForm()"/>
		                                    </div>
	                                    </div>
									</div>
								</div>
							</div>
                        </s:form>
                        
                        <div class="clr margintop20"></div>
                        <% if(alEmpCompareData!=null && alEmpCompareData.size()!=0) { 
                            for(int i=0; i<alEmpCompareData.size(); i++) {	
                            //if(i%4==0 || i==0) {
                            %>
                            
                                <%	
                                    int cnt=0;
	                                List<String> alInner = (List<String>)alEmpCompareData.get(i); 
	                        		//List<String> empPerformanceAttribWiseList =(List<String>) request.getAttribute("empPerformanceAttribWiseList");
	                        		Map<String, String> hmEmpPerformanceAvg = (Map<String, String>) request.getAttribute("hmEmpPerformanceAvg");
	                        		if(hmEmpPerformanceAvg == null) hmEmpPerformanceAvg = new HashMap<String, String>();
	                        		
	                        		Map<String, String> hmEmpListOverallAvg = (Map<String, String>) request.getAttribute("hmEmpListOverallAvg");
	                        		if(hmEmpListOverallAvg == null) hmEmpListOverallAvg = new HashMap<String, String>();
	                        		
	                        		Map<String, String> hmEmpSkillAvg = (Map<String, String>) request.getAttribute("hmEmpSkillAvg");
	                        		if(hmEmpSkillAvg == null) hmEmpSkillAvg = new HashMap<String, String>();
	                        		
	                        		Map<String, String> hmEmpReviewAvg = (Map<String, String>) request.getAttribute("hmEmpReviewAvg");
	                        		if(hmEmpReviewAvg == null) hmEmpReviewAvg = new HashMap<String, String>();
	                        		
	                        		Map<String, String> hmEmpKRAAvg = (Map<String, String>) request.getAttribute("hmEmpKRAAvg");
	                        		if(hmEmpKRAAvg == null) hmEmpKRAAvg = new HashMap<String, String>();
	                        		
	                        		Map<String, String> hmEmpGoalsAvg = (Map<String, String>) request.getAttribute("hmEmpGoalsAvg");
	                        		if(hmEmpGoalsAvg == null) hmEmpGoalsAvg = new HashMap<String, String>();
	                        		
	                        		Map<String, String> hmEmpTargetAvg = (Map<String, String>) request.getAttribute("hmEmpTargetAvg");
	                        		if(hmEmpTargetAvg == null) hmEmpTargetAvg = new HashMap<String, String>();
	                        		
	                        		Map<String, String> hmEmpGoalsKRATargetsAvg = (Map<String, String>) request.getAttribute("hmEmpGoalsKRATargetsAvg");
	                        		if(hmEmpGoalsKRATargetsAvg == null) hmEmpGoalsKRATargetsAvg = new HashMap<String, String>();
	                        		
								%>
								
								<div class="col-lg-3 col-md-4 col-sm-6">
								<div class="box box-widget widget-user-2">
						            <div class="widget-user-header" style="max-height: 110px; min-height: 105px;">
						            	<div class="factsheet-link pull-right" style="margin-right: 15px;"><a class="factsheet" href="MyProfile.action?empId=<%=alInner.get(10) %>"> </a></div>
						              	<div class="widget-user-image">
						              	<%if(docRetriveLocation==null) { %>
											<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + alInner.get(0)%>">
						              	<%} else { %>
						              		<img class="img-circle lazy" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+alInner.get(10)+"/"+IConstants.I_100x100+"/"+alInner.get(0)%>">
						              	<%} %>
							            </div>
							              <h3 class="widget-user-username" title="<%=alInner.get(1)%>"><%=alInner.get(1)%></h3>
							              <h3 class="widget-user-username" title="<%=alInner.get(2)%>"><%=alInner.get(2)%></h3>
							              <h5 class="widget-user-desc" title="<%=alInner.get(3)%>"><%=alInner.get(3)%></h5>
							              <h5 class="widget-user-desc" title="<%=alInner.get(5)%>[<%=alInner.get(6)%>]"><%=alInner.get(5)%>[<%=alInner.get(6)%>]</h5>
						            </div>
						            
						            <div class="box-footer no-padding">
						              <ul class="nav nav-stacked" style="min-height: 165px;"> <!-- overflow-y: auto; -->
						                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Date of Joining: <span class="pull-right badge bg-blue"><%=alInner.get(7)%></span></a></li>
						                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Reporting Manager: <span class="pull-right"><%=uF.showData(alInner.get(4), "NA") %></span></a></li>
						                <%if(request.getAttribute("GC")!=null) {%>
		                                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Gross Compensation:<span class="pull-right"><%=uF.showData(alInner.get(8), "0")%></span></a></li>
		                                <%} %>
						                <%if(request.getAttribute("LH")!=null) { %>
		                                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Logged Hours:<span class="pull-right"><%=uF.showData(alInner.get(9), "0")%></span></a></li>
		                                <%} %>
		                                <%if(request.getAttribute("SKILL")!=null) { %>
		                                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Skills:
		                                <span class="pull-right">
		                                	<%if(hmEmpSkillAvg == null || hmEmpSkillAvg.isEmpty() || hmEmpSkillAvg.get(alInner.get(10)+"_AVG")==null || uF.parseToDouble(hmEmpSkillAvg.get(alInner.get(10)+"_AVG")) == 0) { %>
		                                    <%="NA" %>
		                                    <% } else { %>
		                                    <span id="starSkill<%=alInner.get(10)%>"></span>
		                                    <script type="text/javascript">
		                                        $(function() {
		                                        	$('#starSkill<%=alInner.get(10)%>').raty({
		                                        		readOnly: true,
		                                        		start: <%=uF.showData(hmEmpSkillAvg.get(alInner.get(10)+"_AVG"),"0.00")%>,
		                                        		half: true,
		                                        		targetType: 'number'
		                                        	});
		                                        	});
		                                    </script>
		                                    <% } %>
		                                </span></a></li>
		                                <%} %>
						                <%if(request.getAttribute("REVIEW")!=null) { %>
						                	<li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Reviews:
						                	<span class="pull-right">
						                		<%if(hmEmpReviewAvg == null && hmEmpReviewAvg.isEmpty() && hmEmpReviewAvg.get(alInner.get(10))==null) { %>
													<%="NA" %>
												<% } else { %>
												<span id="starREVIEW<%=alInner.get(10)%>"></span> 
													<script type="text/javascript">
												        $(function() {
												        	$('#starREVIEW<%=alInner.get(10)%>').raty({
												        		readOnly: true,
												        		start: <%=uF.showData(hmEmpReviewAvg.get(alInner.get(10)),"0.00")%>,
												        		half: true,
												        		targetType: 'number'
												        	});
												        	});
												        </script>
												  <% } %> 
						                	</span></a></li>     
											<% } %>
											
											
											<%if(request.getAttribute("GOAL_KRA_TARGET")!=null) { %>
											<li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Goals/ BSC/ KRAs/ Targets:<span class="pull-right">
												<%if(hmEmpGoalsKRATargetsAvg == null || hmEmpGoalsKRATargetsAvg.isEmpty() || hmEmpGoalsKRATargetsAvg.get(alInner.get(10))==null) { %>
													<%="NA" %>
												<% } else { %>	
												<span id="starGoals<%=alInner.get(10)%>"></span> 
													<script type="text/javascript">
												        $(function() {
												        	$('#starGoals<%=alInner.get(10)%>').raty({
												        		readOnly: true,
												        		start: <%=uF.showData(hmEmpGoalsKRATargetsAvg.get(alInner.get(10)),"0.00")%>,
												        		half: true,
												        		targetType: 'number'
												        	});
												        	});
												        </script>
												  <% } %> 
											</span></a></li>     
											<%} %>
											
											<%-- <%if(request.getAttribute("GOAL")!=null) { %>
											<li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Goals:<span class="pull-right">
												<%if(hmEmpGoalsAvg == null || hmEmpGoalsAvg.isEmpty() || hmEmpGoalsAvg.get(alInner.get(10))==null) { %>
													<%="NA" %>
												<% } else { %>	
												<span id="starGoals<%=alInner.get(10)%>"></span> 
													<script type="text/javascript">
												        $(function() {
												        	$('#starGoals<%=alInner.get(10)%>').raty({
												        		readOnly: true,
												        		start: <%=uF.showData(hmEmpGoalsAvg.get(alInner.get(10)),"0.00")%>,
												        		half: true,
												        		targetType: 'number'
												        	});
												        	});
												        </script>
												  <% } %> 
											</span></a></li>     
											<%} %>
											<%if(request.getAttribute("KRA")!=null) { %>
											<li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">KRAs: <span class="pull-right">
											<%if(hmEmpKRAAvg == null && hmEmpKRAAvg.isEmpty() && hmEmpKRAAvg.get(alInner.get(10))==null) { %>
													<%="NA" %>
											<% } else { %>
											<span id="starKRA<%=alInner.get(10)%>"></span> 
												<script type="text/javascript">
											        $(function() {
											        	$('#starKRA<%=alInner.get(10)%>').raty({
											        		readOnly: true,
											        		start: <%=uF.showData(hmEmpKRAAvg.get(alInner.get(10)),"0.00")%>,
											        		half: true,
											        		targetType: 'number'
											        	});
											        	});
											        </script>
											  <% } %> 
											</span></a></li>
											<% } %>
											<%if(request.getAttribute("TARGET")!=null) { %>
											<li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Targets:<span class="pull-right">
												<%if(hmEmpTargetAvg == null || hmEmpTargetAvg.isEmpty() || hmEmpTargetAvg.get(alInner.get(10))==null) { %>
													<%="NA" %>
												<% } else { %>	
												<span id="starTARGET<%=alInner.get(10)%>"></span> 
													<script type="text/javascript">
												        $(function() {
												        	$('#starTARGET<%=alInner.get(10)%>').raty({
												        		readOnly: true,
												        		start: <%=uF.showData(hmEmpTargetAvg.get(alInner.get(10)),"0.00")%>,
												        		half: true,
												        		targetType: 'number'
												        	});
												        	});
												        </script>
												  <% } %> 
											</span></a></li>     
											<% } %> --%>
			                                <%if(request.getAttribute("AT")!=null) { %>
			                                <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;">Attributes: <span class="pull-right">
			                                <%if(hmEmpListOverallAvg == null || hmEmpListOverallAvg.isEmpty() || hmEmpListOverallAvg.get(alInner.get(10)) == null) { %>
			                                    <%="NA" %>
			                                    <% } else { %>
			                                    <span id="starATOverall<%=alInner.get(10)%>"></span>
			                                    <script type="text/javascript">
			                                        $(function() {
			                                        	$('#starATOverall<%=alInner.get(10)%>').raty({
			                                        		readOnly: true,
			                                        		start: <%=uF.showData(hmEmpListOverallAvg.get(alInner.get(10)), "0.00")%>,
			                                        		half: true,
			                                        		targetType: 'number'
			                                        	});
			                                        	});
			                                    </script>
			                                    <%} %>
			                                </span></a></li>
			                                <%
			                                    for(int ii=0; attriblist != null && !attriblist.isEmpty() && ii< attriblist.size();ii++) { 
			                                    	List<String> innList=attriblist.get(ii);
			                                    %>
			                                    <li style="float: left; width: 100%;"><a href="javascript:void(0)" style="padding: 5px;"><%=innList.get(1) %>:<span class="pull-right">
			                                    <%if(hmEmpPerformanceAvg == null || hmEmpPerformanceAvg.isEmpty() || hmEmpPerformanceAvg.get(alInner.get(10)+"_"+innList.get(0))==null){ %>
			                                    <%="NA" %>
			                                    <%}else{ %>
			                                    <span id="starPrimary<%=alInner.get(10)+innList.get(0)%>"></span>
			                                    <script type="text/javascript">
			                                        $(function() {
			                                        	$('#starPrimary<%=alInner.get(10)+innList.get(0)%>').raty({
			                                        		readOnly: true,
			                                        		start: <%=uF.showData(hmEmpPerformanceAvg.get(alInner.get(10)+"_"+innList.get(0)), "0.00")%>,
			                                        		half: true,
			                                        		targetType: 'number'
			                                        	});
			                                        });
			                                    </script>
			                                    <% } %>
			                                    </span></a></li>
			                                
	                                    <% }
	                                    } %>
						              </ul>
						              </div>
					            </div>
					          </div>
							
                            <% } %>
                        </div>
                        <div class="clr"></div>
                        <div id="container" style="width: 100%; height: 500px"></div>
                        <script type="text/javascript">
                          $(function() {
                            	var chartSkill;
                            	chartSkill = new Highcharts.Chart({
                            		chart : {
                            			renderTo : 'container',
                            			defaultSeriesType : 'column',
                            			plotBorderWidth : 1
                            		},
                            		credits : {
                            			enabled : false
                            		},
                            		title : {
                            			text : 'Employee Capital Comparison'
                            		},
                            		xAxis : {
                            			categories : <%=alEmployeeNames%>
		                            },
                            		yAxis : {
                            			lineWidth : 2, //y axis itself
                            			title : {
                            			text : ''
                            			}
                            		},
                            		credits : {
                            			enabled : false
                            		},
                            		title : {
                            			text : '',
                            			floating : true
                            		},
                            		plotOptions : {
                            			bar : {
                            				pointPadding : 0.2,
                            				borderWidth : 0
                            			}
                            		},
                            		series : [{
                            			name : 'Gross Compensation',
                            			data : <%=alGrossComp %>
                            		}, {
                            			name : 'Logged Efforts',
                            			data : <%=alLoggedHours %>
                            		}]
                            	});
                            });
                        </script>
                        <%} else{%>
                        <div class="nodata msg">
                            <span>You have not selected any employee from the list.</span>
                        </div>
                        <%}%>
                    </div>
            </div>
        <%-- </section>
    </div>
</section> --%>