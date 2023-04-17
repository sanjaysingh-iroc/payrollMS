<%@page import="java.util.HashSet"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.task.ViewTaskIdealTime"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.io.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@taglib uri="/struts-tags" prefix="s"%>
<div id="divResult">
<style>.timeline>li>.timeline-item>.timeline-header {font-size: 14px;}</style>
<script type="text/javascript">    
    function viewSnapShot(empID, screenShotId) {
    	var dialogEdit = '.modal-body';
		 $(dialogEdit).empty();
		 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		 $("#modalInfo").show();
		 $(".modal-title").html('Screen Shots');
		 if($(window).width() >= 900){
			 $(".modal-dialog").width(900);
		 }
		 $.ajax({
			url : 'ViewSnapShot1.action?empID='+empID+'&screenShotId='+screenShotId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    function changeView(action){
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'GET',
    		url: action, 
    		success: function(result) {
            	$("#divResult").html(result);
       		}
    	});
    }
    
    function submitForm() {
    	var org = document.getElementById("f_org").value;
    	var location = document.getElementById("f_strWLocation").value;
    	var department = document.getElementById("f_department").value;
    	var strEmpId = document.getElementById("strEmpId").value;
    	var level = document.getElementById("f_level").value;
    	var f_start = document.getElementById("f_start").value;
    	var f_end = document.getElementById("f_end").value;
    	//alert("service ===>> " + service);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'DesktopActivityReport.action?f_org='+org+'&f_strWLocation='+location+'&f_department='+department+'&strEmpId='+strEmpId
    			+'&f_level='+level+"&f_start="+f_start+'&f_end='+f_end, 
    		data: $("#"+this.id).serialize(),
    		success: function(result) {
            	//console.log(result);
            	$("#divResult").html(result);
       		}
    	});
    }
    
    
    $(function() {
    	$("body").on('click','#closeButton',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
        });
    	$("body").on('click','.close',function(){
    		$(".modal-dialog").removeAttr('style');
    		$(".modal-body").height(400);
    		$("#modalInfo").hide();
    	});
        
        $("#f_start").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#f_end').datepicker('setStartDate', minDate);
        });
        
        $("#f_end").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
                var minDate = new Date(selected.date.valueOf());
                $('#f_start').datepicker('setEndDate', minDate);
        });
    });
    
</script>

<% String proType = (String)request.getAttribute("proType"); %>

<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Desktop Activity Report" name="title"/>
    </jsp:include> --%>

                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                      <div style="margin-bottom: 15px">
                          <a class="<%=((proType == null || proType.trim().equals("") || proType.trim().equals("null") || proType.trim().equalsIgnoreCase("L")) ? "current" : "next") %>" href="javascript:void(0)" onclick="changeView('DesktopActivityReport.action?proType=L')" title="List View"><i class="fa fa-bars" aria-hidden="true"></i></a> | 
                          <a class="<%=((proType != null && proType.equalsIgnoreCase("GV")) ? "current" : "next") %>" href="javascript:void(0)" onclick="changeView('DesktopActivityReport.action?proType=GV')" title="Grid View"><i class="fa fa-th" aria-hidden="true"></i></a> 
                      </div>
                      <s:form name="desktopActivityReportForm" id="desktopActivityReportForm" theme="simple" action="DesktopActivityReport" method="POST">
                          <div class="desgn" style="width: 100%; color:#232323;">
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
                                    <s:hidden name="proType" id="proType" />
                                    <div class="row row_without_margin">
										<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
											<i class="fa fa-filter"></i>
										</div> 
										<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
												<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" headerKey="" headerValue="All Organisations" list="organisationList" key="" onchange="submitForm();"/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Location</p>
												<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.desktopActivityReportForm.f_level.options[document.desktopActivityReportForm.f_level.selectedIndex].value+'&f_department='+document.desktopActivityReportForm.f_department.options[document.desktopActivityReportForm.f_department.selectedIndex].value+'&f_strWLocation='+document.desktopActivityReportForm.f_strWLocation.options[document.desktopActivityReportForm.f_strWLocation.selectedIndex].value)" list="wLocationList" key=""/>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Department</p>
												<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.desktopActivityReportForm.f_level.options[document.desktopActivityReportForm.f_level.selectedIndex].value+'&f_department='+document.desktopActivityReportForm.f_department.options[document.desktopActivityReportForm.f_department.selectedIndex].value+'&f_strWLocation='+document.desktopActivityReportForm.f_strWLocation.options[document.desktopActivityReportForm.f_strWLocation.selectedIndex].value)" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Level</p>
												<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" onchange="getContent('empDiv', 'GetLiveEmployeeList.action?f_level='+document.desktopActivityReportForm.f_level.options[document.desktopActivityReportForm.f_level.selectedIndex].value+'&f_department='+document.desktopActivityReportForm.f_department.options[document.desktopActivityReportForm.f_department.selectedIndex].value+'&f_strWLocation='+document.desktopActivityReportForm.f_strWLocation.options[document.desktopActivityReportForm.f_strWLocation.selectedIndex].value)" list="levelList" key="" required="true" />
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">Employee</p>
												<s:select name="strEmpId" id="strEmpId" listKey="employeeId" listValue="employeeCode" headerKey="" headerValue="All Employees" list="empList" key="" required="true"/>
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
												<s:textfield name="f_start" id="f_start"></s:textfield>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">To Date</p>
												<s:textfield name="f_end" id="f_end" cssClass="form-control autoWidth inline"></s:textfield>
											</div>
											<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
												<p style="padding-left: 5px;">&nbsp;</p>
												<input type="button" name="submit" value="Search" class="btn btn-primary" style="margin:0px" onclick="submitForm();"/>
											</div>
										</div>
									</div>		
                                </div>
			                </div>
			                <!-- /.box-body -->
			            </div>
                          </div>
                          <div style="width: 100%;">
                              <div style="width: 50px; display: inline;">Sort By: </div>
                              <div style="display: inline;">
                                  <s:select theme="simple" name="sortBy" id="sortBy" cssClass="form-control autoWidth inline" list="#{'1':'Time', '2':'Employee'}" onchange="document.desktopActivityReportForm.submit();"/>
                              </div>
                          </div>
                      </s:form>
                        <% 
                            String strTotalHrsAndMins = (String)request.getAttribute("strTotalHrsAndMins"); 
                            String strWorkHrsAndMins = (String)request.getAttribute("strWorkHrsAndMins");
                            String strIdleHrsAndMins = (String)request.getAttribute("strIdleHrsAndMins");
                        %>
                        <div class="row row_without_margin">
                        	<div class="col-lg-6 col-md-6 col-sm-12 col_no_padding">
                        		<ul class="site-stats-new paddingleft0">
									<li class="bg_lh"><i class="fa fa-calendar-o" aria-hidden="true"></i><strong><%=strTotalHrsAndMins %></strong> <small>Total Hrs</small></li>
									<li class="bg_lh"><i class="fa fa-leanpub" aria-hidden="true"></i><strong><%=strWorkHrsAndMins %></strong> <small>Working Hrs</small></li>
									<li class="bg_lh"><i class="fa fa-briefcase" aria-hidden="true"></i><strong><%=strIdleHrsAndMins %></strong> <small>Idle Hrs</small></li>
								</ul>
                        	</div>
                        </div>
                        
                        <div class="clr" style="padding-top: 10px;"></div>
                        <%if(proType == null || proType.trim().equals("") || proType.trim().equals("null") || proType.trim().equalsIgnoreCase("L")){ %> 
                        <%
                            UtilityFunctions uF = new UtilityFunctions();
                            CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
                            String sortBY = (String) request.getAttribute("sortBy");
                            
                            if(uF.parseToInt(sortBY) <= 1) {
                                  
                            Map<String, Map<String, List<List<String>>>> hmDatewiseHours = (Map<String, Map<String, List<List<String>>>>)request.getAttribute("hmDatewiseHours");
                            if(hmDatewiseHours == null) hmDatewiseHours = new HashMap<String, Map<String, List<List<String>>>>();
                            
                            Iterator<String> itDate = hmDatewiseHours.keySet().iterator();
                            while(itDate.hasNext()) {
                            	String strDate = itDate.next();
                            	Map<String, List<List<String>>> hmHourwiseData = hmDatewiseHours.get(strDate);
                            	if(hmHourwiseData == null) hmHourwiseData = new HashMap<String, List<List<String>>>();
                        %>
                        <div class="box box-default collapsed-box" style="margin-top: 10px;border-top: 2px solid #d2d6de;">
			                <div class="box-header with-border" style="padding:6px;">
			                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=uF.getDateFormat(strDate, IConstants.DBDATE, IConstants.DATE_FORMAT_STR) %></h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
			                    <%
                            Iterator<String> itDateHrs = hmHourwiseData.keySet().iterator();
                            while(itDateHrs.hasNext()) {
                            	String strDateHrs = itDateHrs.next();
                            	List<List<String>> alOuterData = hmHourwiseData.get(strDateHrs);
                            	if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
                            	
                            	int linehght = 23;
                            	int totLinehght = 0;
                            	totLinehght = totLinehght + (linehght * alOuterData.size());
                            	if(totLinehght == 0) {
                            		totLinehght = 23;
                            	}
                            	
                            	%>
                            	<ul class="timeline">
						            <li class="time-label">
						                  <span class="bg-yellow">
						                    <%=uF.getHourInAMorPM(uF.parseToInt(strDateHrs)) %>
						                  </span>
						            </li>
						            <% for(int i=0; !alOuterData.isEmpty() && i<alOuterData.size(); i++) {
		                                    List<String> innerList = alOuterData.get(i);%>
						            <li>
						              <i class="fa fa-clock-o bg-blue"></i>
						              <div class="timeline-item">
						                <span class="time"><i class="fa fa-clock-o"></i><%=innerList.get(1) %></span>
						                <p class="timeline-header">
		                                    <span><%=innerList.get(5) %></span>
		                                    <span><%=innerList.get(2) %></span>
		                                    <span><%=innerList.get(3) %></span>
		                                    <span><%=innerList.get(6) %></span>
		                                    <% if(uF.parseToInt(innerList.get(8)) == 1) { %>
		                                    <a href="javascript:void(0)" onclick="viewSnapShot(<%=innerList.get(7) %>, <%=innerList.get(4) %>)"><img src="images1/icons/popup_arrow.gif" title="Snapshots" height="10px"/></a>
		                                    <% } %>
		                                    <%	String div1=null,div2=null,div3=null,div4=null,div5=null,div6=null,div7=null,div8=null,div9=null,div10=null; 
		                                        if(uF.parseToInt(innerList.get(10)) == 0){
		                                        	 div1="lightgray";
		                                        	 div2="lightgray";
		                                        	 div3="lightgray";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else{ 
		                                        double percentage=uF.parseToDouble(innerList.get(11));
		                                        if(percentage>=0.0 && percentage<=10.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgray";
		                                        	 div3="lightgray";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>10.0 && percentage<=20.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgray";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>20.0 && percentage<=30.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>30.0 && percentage<=40.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>40.0 && percentage<=50.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>50.0 && percentage<=60.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>60.0 && percentage<=70.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>70.0 && percentage<=80.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgreen";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>80.0 && percentage<=90.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgreen";
		                                        	 div9="lightgreen";
		                                        	 div10="lightgray";
		                                        }else if(percentage>90.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgreen";
		                                        	 div9="lightgreen";
		                                        	 div10="lightgreen";
		                                        }
		                                        
		                                        } %>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div1 %>;display: inline-block;margin-left: 10px;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div2 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div3 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div4 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div5 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div7 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div8 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div9 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div10 %>;display: inline-block;"></span>
			                                    <br><span><%=innerList.get(9) %></span> 
		                                </p>
						              </div>
						            </li>
						            <% } %>
						          </ul>
		                        <% } %>
			                </div>
			                <!-- /.box-body -->
			            </div>
                        <% } %>
                        <% if(hmDatewiseHours == null || hmDatewiseHours.isEmpty() || hmDatewiseHours.size() == 0) { %>
                        <div class="msg nodata" style="clear: both;"><span>No data available.</span></div>
                        <% } %>
                        <% } else { %>
                        <%
                            Map<String, Map<String, Map<String, List<List<String>>>>> hmDatewiseHours = (Map<String, Map<String, Map<String, List<List<String>>>>>)request.getAttribute("hmDatewiseHours");
                            if(hmDatewiseHours == null) hmDatewiseHours = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                            
                            Iterator<String> itDate = hmDatewiseHours.keySet().iterator();
                            while(itDate.hasNext()) {
                            	String strDate = itDate.next();
                            	Map<String, Map<String, List<List<String>>>> hmHourwiseData = hmDatewiseHours.get(strDate);
                            	if(hmHourwiseData == null) hmHourwiseData = new HashMap<String, Map<String, List<List<String>>>>();
                            	%>
                            	<div class="box box-default collapsed-box" style="margin-top: 10px;border-top: 2px solid #d2d6de;">
					                <div class="box-header with-border" style="padding:6px;">
					                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=uF.getDateFormat(strDate, IConstants.DBDATE, IConstants.DATE_FORMAT_STR) %></h3>
					                    <div class="box-tools pull-right">
					                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					                    </div>
					                </div>
					                <!-- /.box-header -->
					                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					                   	
					                    <%
                            Iterator<String> itDateHrs = hmHourwiseData.keySet().iterator();
                            while(itDateHrs.hasNext()) {
                            	String strDateHrs = itDateHrs.next();
                            	Map<String, List<List<String>>> hmEmpwiseData = hmHourwiseData.get(strDateHrs);
                            	if(hmEmpwiseData == null) hmEmpwiseData = new HashMap<String, List<List<String>>>();
                            	
                            	int linehght = 23;
                            	int totLinehght = 0;
                            	Iterator<String> itDateHrsEmpCnt = hmEmpwiseData.keySet().iterator();
                            	while(itDateHrsEmpCnt.hasNext()) {
                            		String strEmpId = itDateHrsEmpCnt.next();
                            		List<List<String>> alOuterData = hmEmpwiseData.get(strEmpId);
                            		if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
                            		totLinehght = totLinehght + (linehght * alOuterData.size());
                            	}
                            	if(totLinehght == 0) {
                            		totLinehght = 23;
                            	}
                            	
                            	%>
                            	<ul class="timeline">
							            <li class="time-label">
							                  <span class="bg-yellow">
							                    <%=uF.getHourInAMorPM(uF.parseToInt(strDateHrs)) %>
							                  </span>
							            </li>
							            <%
		                                    Iterator<String> itDateHrsEmp = hmEmpwiseData.keySet().iterator();
		                                    while(itDateHrsEmp.hasNext()) {
		                                    	String strEmpId = itDateHrsEmp.next();
		                                    	List<List<String>> alOuterData = hmEmpwiseData.get(strEmpId);
		                                    	if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
		                                    	
		                                    	for(int i=0; !alOuterData.isEmpty() && i<alOuterData.size(); i++) {
		                                    		List<String> innerList = alOuterData.get(i);
		                                    %>
							            <li>
							              <i class="fa fa-clock-o bg-blue"></i>
							              <div class="timeline-item">
							                <span class="time"><i class="fa fa-clock-o"></i> <%=innerList.get(1) %></span>
							                <p class="timeline-header">
							                	<span><%=innerList.get(5) %></span>
			                                    <span><%=innerList.get(2) %></span>
			                                    <span><%=innerList.get(3) %></span>
			                                    <span><%=innerList.get(6) %></span>
			                                    <% if(uF.parseToInt(innerList.get(8)) == 1) { %>
			                                    <a href="javascript:void(0)" onclick="viewSnapShot(<%=innerList.get(7) %>, <%=innerList.get(4) %>)"><img src="images1/icons/popup_arrow.gif" title="Snapshots" height="10px"/></a>
			                                    <% } %>
			                                    <%	String div1=null,div2=null,div3=null,div4=null,div5=null,div6=null,div7=null,div8=null,div9=null,div10=null; 
		                                        if(uF.parseToInt(innerList.get(10)) == 0){
		                                        	 div1="lightgray";
		                                        	 div2="lightgray";
		                                        	 div3="lightgray";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else{ 
		                                        double percentage=uF.parseToDouble(innerList.get(11));
		                                        if(percentage>=0.0 && percentage<=10.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgray";
		                                        	 div3="lightgray";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>10.0 && percentage<=20.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgray";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>20.0 && percentage<=30.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgray";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>30.0 && percentage<=40.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgray";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>40.0 && percentage<=50.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgray";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>50.0 && percentage<=60.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgray";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>60.0 && percentage<=70.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgray";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>70.0 && percentage<=80.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgreen";
		                                        	 div9="lightgray";
		                                        	 div10="lightgray";
		                                        }else if(percentage>80.0 && percentage<=90.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgreen";
		                                        	 div9="lightgreen";
		                                        	 div10="lightgray";
		                                        }else if(percentage>90.0){
		                                        	 div1="lightgreen";
		                                        	 div2="lightgreen";
		                                        	 div3="lightgreen";
		                                        	 div4="lightgreen";
		                                        	 div5="lightgreen";
		                                        	 div6="lightgreen";
		                                        	 div7="lightgreen";
		                                        	 div8="lightgreen";
		                                        	 div9="lightgreen";
		                                        	 div10="lightgreen";
		                                        }
		                                        
		                                        } %>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div1 %>;display: inline-block;margin-left: 10px;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div2 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div3 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div4 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div5 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div7 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div8 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div9 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div10 %>;display: inline-block;"></span>
			                                    <br><span><%=innerList.get(9) %></span> 
							                </p>
							              </div>
							            </li>
							            <%	
		                                    }
		                                    }
		                                    %>
							          </ul>
		                        <% } %>
					                </div>
					                <!-- /.box-body -->
					            </div>
                        
                        <% } %>
                        <% if(hmDatewiseHours == null || hmDatewiseHours.isEmpty() || hmDatewiseHours.size() == 0) { %>
                        <div class="msg nodata" style="clear: both;"><span>No data available.</span></div>
                        <% } %>
                        <% } %>
                        <%} else if(proType != null && proType.trim().equalsIgnoreCase("GV")){  %>
                        <%
                            UtilityFunctions uF = new UtilityFunctions();
                            CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
                            String sortBY = (String) request.getAttribute("sortBy");
                            
                            if(uF.parseToInt(sortBY) <= 1) {
                            
                                   
                            	Map<String, Map<String, List<List<String>>>> hmDatewiseHours = (Map<String, Map<String, List<List<String>>>>)request.getAttribute("hmDatewiseHours");
                            	if(hmDatewiseHours == null) hmDatewiseHours = new HashMap<String, Map<String, List<List<String>>>>();
                            	
                            	Iterator<String> itDate = hmDatewiseHours.keySet().iterator();
                            	while(itDate.hasNext()) {
                            		String strDate = itDate.next();
                            		Map<String, List<List<String>>> hmHourwiseData = hmDatewiseHours.get(strDate);
                            		if(hmHourwiseData == null) hmHourwiseData = new HashMap<String, List<List<String>>>();
                            		%>
                          			<div class="box box-default collapsed-box" style="margin-top: 10px;border-top: 2px solid #d2d6de;">
						                <div class="box-header with-border" style="padding:6px;">
						                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=uF.getDateFormat(strDate, IConstants.DBDATE, IConstants.DATE_FORMAT_STR) %></h3>
						                    <div class="box-tools pull-right">
						                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						                    </div>
						                </div>
						                <!-- /.box-header -->
						                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
						                    <%
                            Iterator<String> itDateHrs = hmHourwiseData.keySet().iterator();
                            while(itDateHrs.hasNext()) {
                            	String strDateHrs = itDateHrs.next();
                            	List<List<String>> alOuterData = hmHourwiseData.get(strDateHrs);
                            	if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
                            	
                            	int linehght = 127;
                            	int totLinehght = 0;
                            	/* totLinehght = (linehght *); */
                            	double size =(double)alOuterData.size()/5;
                            	long iPart; double fPart;
                            	iPart = (long) size;
                            	fPart = size - iPart;
                            	if(fPart>.0){
                            		  size=iPart+1;
                            	}else{
                            		  size=iPart;
                                }
                            	totLinehght =(int)size*linehght;
                            	if(totLinehght == 0) {
                            		totLinehght = 127;
                            	}
                            	%>
                            	<ul class="timeline">
						            <li class="time-label">
						                  <span class="bg-yellow">
						                    <%=uF.getHourInAMorPM(uF.parseToInt(strDateHrs)) %>
						                  </span>
						            </li>
						            <% for(int i=0; !alOuterData.isEmpty() && i<alOuterData.size(); i++) {
                                    List<String> innerList = alOuterData.get(i);
                                    %>
						            <li>
						              <i class="fa fa-clock-o bg-blue"></i>
						              <div class="timeline-item">
						                <span class="time"><i class="fa fa-clock-o"></i> 12:05</span>
						                <p class="timeline-header">
						                	<% if(uF.parseToInt(innerList.get(8)) == 1) {
		                                        if(innerList.get(12)!=null && !innerList.get(12).equals("null")){ %>
		                                    <a href="javascript:void(0)" onclick="viewSnapShot(<%=innerList.get(7) %>, <%=innerList.get(4) %>)"><img src="<%=innerList.get(12)%>" title="Snapshots" height="86px" width="153px" style="margin-top: 3px;margin-left:7px;"/></a>
		                                    <%}else{ %>		
		                                    <img src="images1/placeholder-640x480.png" title="Snapshots" height="86px" width="153px"/>
		                                    <%}%>
		                                    <% }else{ %>
		                                    <img src="images1/placeholder-640x480.png" title="Snapshots" height="86px" width="153px"/>
		                                    <%} %>
		                                    <%	String div1=null,div2=null,div3=null,div4=null,div5=null,div6=null,div7=null,div8=null,div9=null,div10=null; 
                                        if(uF.parseToInt(innerList.get(10)) == 0){
                                        	 div1="lightgray";
                                        	 div2="lightgray";
                                        	 div3="lightgray";
                                        	 div4="lightgray";
                                        	 div5="lightgray";
                                        	 div6="lightgray";
                                        	 div7="lightgray";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else{ 
                                        double percentage=uF.parseToDouble(innerList.get(11));
                                        if(percentage>=0.0 && percentage<=10.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgray";
                                        	 div3="lightgray";
                                        	 div4="lightgray";
                                        	 div5="lightgray";
                                        	 div6="lightgray";
                                        	 div7="lightgray";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>10.0 && percentage<=20.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgray";
                                        	 div4="lightgray";
                                        	 div5="lightgray";
                                        	 div6="lightgray";
                                        	 div7="lightgray";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>20.0 && percentage<=30.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgray";
                                        	 div5="lightgray";
                                        	 div6="lightgray";
                                        	 div7="lightgray";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>30.0 && percentage<=40.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgreen";
                                        	 div5="lightgray";
                                        	 div6="lightgray";
                                        	 div7="lightgray";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>40.0 && percentage<=50.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgreen";
                                        	 div5="lightgreen";
                                        	 div6="lightgray";
                                        	 div7="lightgray";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>50.0 && percentage<=60.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgreen";
                                        	 div5="lightgreen";
                                        	 div6="lightgreen";
                                        	 div7="lightgray";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>60.0 && percentage<=70.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgreen";
                                        	 div5="lightgreen";
                                        	 div6="lightgreen";
                                        	 div7="lightgreen";
                                        	 div8="lightgray";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>70.0 && percentage<=80.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgreen";
                                        	 div5="lightgreen";
                                        	 div6="lightgreen";
                                        	 div7="lightgreen";
                                        	 div8="lightgreen";
                                        	 div9="lightgray";
                                        	 div10="lightgray";
                                        }else if(percentage>80.0 && percentage<=90.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgreen";
                                        	 div5="lightgreen";
                                        	 div6="lightgreen";
                                        	 div7="lightgreen";
                                        	 div8="lightgreen";
                                        	 div9="lightgreen";
                                        	 div10="lightgray";
                                        }else if(percentage>90.0){
                                        	 div1="lightgreen";
                                        	 div2="lightgreen";
                                        	 div3="lightgreen";
                                        	 div4="lightgreen";
                                        	 div5="lightgreen";
                                        	 div6="lightgreen";
                                        	 div7="lightgreen";
                                        	 div8="lightgreen";
                                        	 div9="lightgreen";
                                        	 div10="lightgreen";
                                        }
                                        
                                        } %>
                                        		<span style="width: 8px; height: 14px; background-color:  <%=div1 %>;display: inline-block;margin-left: 10px;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div2 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div3 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div4 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div5 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div6 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div7 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div8 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div9 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div10 %>;display: inline-block;"></span>
			                                    <br><span><%=innerList.get(6) %></span> 
						                </p>
						              </div>
						            </li>
						            <%} %>
				         		 </ul>
                        <% } %>
						                </div>
						                <!-- /.box-body -->
						            </div>
                        <% } %>
                        <% if(hmDatewiseHours == null || hmDatewiseHours.isEmpty() || hmDatewiseHours.size() == 0) { %>
                        <div class="msg nodata" style="clear: both;"><span>No data available.</span></div>
                        <% } %>
                        <% 
                            }else{ %>
                        <%
                            Map<String, Map<String, Map<String, List<List<String>>>>> hmDatewiseHours = (Map<String, Map<String, Map<String, List<List<String>>>>>)request.getAttribute("hmDatewiseHours");
                            if(hmDatewiseHours == null) hmDatewiseHours = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                            
                            Iterator<String> itDate = hmDatewiseHours.keySet().iterator();
                            while(itDate.hasNext()) {
                            	String strDate = itDate.next();
                            	Map<String, Map<String, List<List<String>>>> hmHourwiseData = hmDatewiseHours.get(strDate);
                            	if(hmHourwiseData == null) hmHourwiseData = new HashMap<String, Map<String, List<List<String>>>>();
                            	%>
                        <div class="box box-default collapsed-box" style="margin-top: 10px;border-top: 2px solid #d2d6de;">
			                
			                <div class="box-header with-border" style="padding:6px;">
			                    <h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=uF.getDateFormat(strDate, IConstants.DBDATE, IConstants.DATE_FORMAT_STR) %></h3>
			                    <div class="box-tools pull-right">
			                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                    </div>
			                </div>
			                <!-- /.box-header -->
			                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
			                    <%
                            Iterator<String> itDateHrs = hmHourwiseData.keySet().iterator();
                            while(itDateHrs.hasNext()) {
                            	String strDateHrs = itDateHrs.next();
                            	Map<String, List<List<String>>> hmEmpwiseData = hmHourwiseData.get(strDateHrs);
                            	if(hmEmpwiseData == null) hmEmpwiseData = new HashMap<String, List<List<String>>>();
                            	
                            	int linehght = 127;
                            	int totLinehght = 0;
                            	Iterator<String> itDateHrsEmpCnt = hmEmpwiseData.keySet().iterator();
                            	while(itDateHrsEmpCnt.hasNext()) {
                            		String strEmpId = itDateHrsEmpCnt.next();
                            		List<List<String>> alOuterData = hmEmpwiseData.get(strEmpId);
                            		if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
                            		double size =(double)alOuterData.size()/5;
                            		long iPart;double fPart;
                            		iPart = (long) size;
                            		fPart = size - iPart;
                            		if(fPart>.0){
                            			 size=iPart+1;
                            		}else{
                            			  size=iPart;
                            		}
                            	    totLinehght =(int)size*linehght;
                            	  System.out.println(" totLinehght=="+totLinehght);
                            	}
                            	if(totLinehght == 0) {
                            		totLinehght = 127;
                            		
                            	}
                            	
                            	%>
                            	<ul class="timeline">
						            <li class="time-label">
					                  <span class="bg-yellow">
					                    <%=uF.getHourInAMorPM(uF.parseToInt(strDateHrs)) %>
					                  </span>
						            </li>
						            <%
                                    Iterator<String> itDateHrsEmp = hmEmpwiseData.keySet().iterator();
                                    while(itDateHrsEmp.hasNext()) {
                                    	String strEmpId = itDateHrsEmp.next();
                                    	List<List<String>> alOuterData = hmEmpwiseData.get(strEmpId);
                                    	if(alOuterData == null) alOuterData = new ArrayList<List<String>>();
                                    	int counter=0;
                                    	
                                    	for(int i=0; !alOuterData.isEmpty() && i<alOuterData.size(); i++) {
                                    		List<String> innerList = alOuterData.get(i);
                                    		
                                    		counter++;
                                    		//System.out.println(" Counter on jsp=="+counter);
                                    %>
                                    	<li>
						              <i class="fa fa-clock-o bg-blue"></i>
						              <div class="timeline-item">
						                <span class="time"><i class="fa fa-clock-o"></i> 12:05</span>
						                <p class="timeline-header">
						                	<% if(uF.parseToInt(innerList.get(8)) == 1) {
                                            if(innerList.get(12)!=null && !innerList.get(12).equals("null")){ %>
                                        <a href="javascript:void(0)" onclick="viewSnapShot(<%=innerList.get(7) %>, <%=innerList.get(4) %>)"><img src="<%=innerList.get(12)%>" title="Snapshots" height="86px" width="153px"/></a>
                                        <%}else{ %>		
                                        <img src="images1/placeholder-640x480.png" title="Snapshots" height="86px" width="153px"/>
                                        <%}%>
                                        <% }else{ %>
                                        <img src="images1/placeholder-640x480.png" title="Snapshots" height="86px" width="153px"/>
                                        <%} %>
                                        <%	String div1=null,div2=null,div3=null,div4=null,div5=null,div6=null,div7=null,div8=null,div9=null,div10=null; 
                                            if(uF.parseToInt(innerList.get(10)) == 0){
                                            	 div1="lightgray";
                                            	 div2="lightgray";
                                            	 div3="lightgray";
                                            	 div4="lightgray";
                                            	 div5="lightgray";
                                            	 div6="lightgray";
                                            	 div7="lightgray";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else{ 
                                            double percentage=uF.parseToDouble(innerList.get(11));
                                            if(percentage>=0.0 && percentage<=10.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgray";
                                            	 div3="lightgray";
                                            	 div4="lightgray";
                                            	 div5="lightgray";
                                            	 div6="lightgray";
                                            	 div7="lightgray";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>10.0 && percentage<=20.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgray";
                                            	 div4="lightgray";
                                            	 div5="lightgray";
                                            	 div6="lightgray";
                                            	 div7="lightgray";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>20.0 && percentage<=30.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgray";
                                            	 div5="lightgray";
                                            	 div6="lightgray";
                                            	 div7="lightgray";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>30.0 && percentage<=40.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgreen";
                                            	 div5="lightgray";
                                            	 div6="lightgray";
                                            	 div7="lightgray";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>40.0 && percentage<=50.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgreen";
                                            	 div5="lightgreen";
                                            	 div6="lightgray";
                                            	 div7="lightgray";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>50.0 && percentage<=60.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgreen";
                                            	 div5="lightgreen";
                                            	 div6="lightgreen";
                                            	 div7="lightgray";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>60.0 && percentage<=70.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgreen";
                                            	 div5="lightgreen";
                                            	 div6="lightgreen";
                                            	 div7="lightgreen";
                                            	 div8="lightgray";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>70.0 && percentage<=80.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgreen";
                                            	 div5="lightgreen";
                                            	 div6="lightgreen";
                                            	 div7="lightgreen";
                                            	 div8="lightgreen";
                                            	 div9="lightgray";
                                            	 div10="lightgray";
                                            }else if(percentage>80.0 && percentage<=90.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgreen";
                                            	 div5="lightgreen";
                                            	 div6="lightgreen";
                                            	 div7="lightgreen";
                                            	 div8="lightgreen";
                                            	 div9="lightgreen";
                                            	 div10="lightgray";
                                            }else if(percentage>90.0){
                                            	 div1="lightgreen";
                                            	 div2="lightgreen";
                                            	 div3="lightgreen";
                                            	 div4="lightgreen";
                                            	 div5="lightgreen";
                                            	 div6="lightgreen";
                                            	 div7="lightgreen";
                                            	 div8="lightgreen";
                                            	 div9="lightgreen";
                                            	 div10="lightgreen";
                                            }
                                            
                                            } %>
                                            <span style="width: 8px; height: 14px; background-color:  <%=div1 %>;display: inline-block;margin-left: 10px;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div2 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div3 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div4 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div5 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div6 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div7 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div8 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div9 %>;display: inline-block;"></span>
		                                        <span style="width: 8px; height: 14px; background-color:  <%=div10 %>;display: inline-block;"></span>
			                                    <br><span><%=innerList.get(6) %></span> 
						                </p>
						              </div>
						            </li>
                                    <% }
                                    	}%>
					          </ul>
                        <% } %>
			                </div>
			                <!-- /.box-body -->
			            </div>
                        
                        <% } %>
                        <% if(hmDatewiseHours == null || hmDatewiseHours.isEmpty() || hmDatewiseHours.size() == 0) { %>
                        <div class="msg nodata" style="clear: both;"><span>No data available.</span></div>
                        <% } %>
                        <% } %>
                        <%} %>
                </div>
                <!-- /.box-body -->

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">Candidate Information</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>		
</div>