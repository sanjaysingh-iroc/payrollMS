<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script>
    function divScroll() {
        var pvt = document.getElementById('pivot');
        var sdates = document.getElementById('scrolldates');
    	var semp = document.getElementById('scrollemp');
        semp.scrollTop = pvt.scrollTop;
    	sdates.scrollLeft = pvt.scrollLeft;
    }
    
    
    function displayBlock(id){
    document.getElementById(id).style.display= 'block';
    }
    
    function hideBlock(id){
    document.getElementById(id).style.display= 'none';
    }
    
    function submitForm(type){
    	var calendarYear = document.getElementById("calendarYear").value;
    	var strMonth = document.getElementById("strMonth").value;
    	var org = "";
    	var f_strWLocation = "";
    	var f_department = "";
    	var f_service = "";
    	var f_level = "";
    	if(document.getElementById("f_org")) {
    		org = document.getElementById("f_org").value;
    	}
    	if(document.getElementById("f_strWLocation")) {
    		f_strWLocation = document.getElementById("f_strWLocation").value;
    	}
    	if(document.getElementById("f_department")) {
    		f_department = document.getElementById("f_department").value;
    	}
    	if(document.getElementById("f_service")) {
    		f_service = document.getElementById("f_service").value;
    	}
    	if(document.getElementById("f_level")) {
    		f_level = document.getElementById("f_level").value;
    	}
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_service='+f_service+'&f_level='+f_level+'&calendarYear='+calendarYear
    			+'&strMonth='+strMonth;
    	}
    	//alert("1 ===>> ");
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'RosterReport.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#divResult").html(result);
       		}
    	});
    }
    
   function generateReportExcel(){
   		document.frm_roster_actual.exportType.value='excel';
    	document.frm_roster_actual.submit();
   }
    
    
</script>

<style>
    .reportHeading {
	    background-color: #d8d8d8;
	    font-family: verdana,arial,helvetica,sans-serif;
	    font-size: 10px;
	    font-weight: bold;
    }
    
    img {
    	border:none;
    }
    
    .prevlink a {
    	float:right;
    }
    
    .day {
	    text-align:center;
	    width:104px;
	    float:left;
	    border:#fff solid 1px;
    }
    
    .date {
	    text-align:center;
	    width:104px;
	    float:left;
	    border:#fff solid 1px;
    }
    
    .inout {
	    width:50px ; float:left;
	    border:#fff solid 0px;
	    _height:21px;
	    background:#efefef;
    }
    
    .empname {
    	border:#fff solid 1px;
	    font-size:12px;
	    background:#d8d8d8;
	    float:left;
	    overflow:hidden;
	    width:100%;
	    padding:0px 3px 0px 3px;
	    /* line-height:19px; */
	    height:auto;
    }
    
    .block { 
	    float:left;
	    position:absolute; 
	    /* background-color:#abc; */ 
	    /*left:180px;*/
	    top:0px;
	    width:1459px; 
	    height:auto;
	    margin:0px; 
	    z-index:10;
	    overflow:hidden;
    }
    
    .block2 { 
	    float:left;
	    background-color:#fff; 
	    left:0px;
	    top:0px;
	    width:100%; 
	    height:auto;
	    margin:0px; 
	    z-index:100;
	    border-top:#fff solid 1px; 
	    overflow:hidden;
    }
    
    .block_dates { 
    float:left;
    position:absolute; 
    background-color:#fff; 
    left:0px;
    top:0px;
    width:1460px; 
    height:auto;
    margin:0px; 
    z-index:10;
    border:solid 0px #f00;
    }
    
    .next { width:32px; float:right; border:#666666 solid 1px;display:block;}
    .posfix { left:20%; top:35% ; width:630px; }
    .posfix h2 { color:#fff;margin:5px 0px 10px 0px}
    #mask { width:100%; border:#fff solid 1px; height:600px; overflow:hidden; position:relative; float:left;}
    #pivot {width:84%;  border:solid 0px #ff0; position:absolute; height:595px;overflow:scroll;float:left;margin:0px 0px 0px 15%;left:0px; }
    .weekly_width{   width: 730px; }
    .biweekly_width{  width: 1563px; }
    .monthly_width{  width: 3250px; }
    .fortnightly_width{  width: 1560px; }
    
</style>

<%
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    
    
    List alDay = (List) request.getAttribute("alDay");
    List alDate = (List) request.getAttribute("alDate");
    List alEmpId = (List) request.getAttribute("alEmpId");
    Map hmList = (Map) request.getAttribute("hmList");
    String paycycleDuration = (String)request.getAttribute("paycycleDuration");
    
    Map hmRosterServiceId = (HashMap) request.getAttribute("hmRosterServiceId");
    Map hmServicesWorkrdFor = (Map) request.getAttribute("hmServicesWorkrdFor");
    Map hmServices = (Map) request.getAttribute("hmServices");
    
    
    
    Map hmHolidayDates = (Map) request.getAttribute("hmHolidayDates");
    Map hmHolidays = (Map) request.getAttribute("hmHolidays");
    Map hmWLocation = (Map) request.getAttribute("hmWLocation");
    Map<String, Set<String>> hmWeekEnds = (Map<String, Set<String>>) request.getAttribute("hmWeekEnds");
    if(hmWeekEnds==null) hmWeekEnds = new HashMap<String, Set<String>>();
    
    Map<String, String> hmEmpLevelMap = (Map<String, String>) request.getAttribute("hmEmpLevelMap");
    if(hmEmpLevelMap==null) hmEmpLevelMap = new HashMap<String, String>();
    
    Map hmLeavesMap = (Map) request.getAttribute("hmLeavesMap");
    Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
    Map hmLeavesName = (Map) request.getAttribute("hmLeavesName");
    if(hmLeavesName==null) hmLeavesName = new HashMap();
    //	out.println("<br/>hmServices="+hmServices);
    //	out.println("<br/>hmServicesWorkrdFor="+hmServicesWorkrdFor);
    
    Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");	
    List _alHolidays = (List) request.getAttribute("_alHolidays");
    
    String strReqAlphaValue = (String)request.getParameter("alphaValue");
    if(strReqAlphaValue==null){
    	strReqAlphaValue="";
    }
    
    String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
    if(strAction!=null){
    	strAction = strAction.replace(request.getContextPath()+"/","");
    }
    
    
    //out.println("<br/>hmHolidays===>"+hmHolidays);
    //out.println("<br/>hmHolidayDates===>"+hmHolidayDates);
    //out.println("<br/>hmWLocation===>"+hmWLocation);
    //out.println("<br/>hmWeekEnds===>"+hmWeekEnds);
    
    String f_org = (String) request.getAttribute("f_org");
    String f_strWLocation = (String) request.getAttribute("f_strWLocation");
    String f_department = (String) request.getAttribute("f_department");
    String f_service = (String) request.getAttribute("f_service");
    String f_level = (String) request.getAttribute("f_level");
    String strMonth = (String) request.getAttribute("strMonth");
    String strYear = (String) request.getAttribute("strYear");
    String calendarYear = (String) request.getAttribute("calendarYear");
    
    Map<String, Set<String>> hmWeekEndList = (Map<String, Set<String>>) request.getAttribute("hmWeekEndList");
    if(hmWeekEndList == null) hmWeekEndList = new HashMap<String, Set<String>>();
    List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");;
    if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
    Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");;
    if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
    
    %>
    
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Staff Roster" name="title"/>
    </jsp:include> --%>
    
	<%StringBuilder sb = new StringBuilder(); %>
        <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
        <div class="box box-default"> <!--  collapsed-box -->
			<%-- <div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div> --%>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form theme="simple" action="RosterReport" method="post" name="frm_roster_actual">
					<s:hidden name="currUserType" id="currUserType" />
					<s:hidden name="exportType"></s:hidden>
					<% if (strUserType != null && !strUserType.equalsIgnoreCase(IConstants.MANAGER)) { %>
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" headerKey="" headerValue="All Locations" onchange="submitForm('2');" list="wLocationList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments" onchange="submitForm('2');"></s:select>
							</div>

							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Service</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" headerKey="0" headerValue="All Services"  onchange="submitForm('2');"></s:select>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" onchange="submitForm('2')" list="levelList"/>
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
								<p style="padding-left: 5px;">Calendar</p>
								<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0"  list="calendarYearList" key="" onchange="submitForm('2');"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Month</p>
								<s:select theme="simple" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="0" onchange="submitForm('2');" list="monthList"/>
							</div>
							<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
							
							<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
							
						</div>
					</div>
				</s:form>
			</div>
		</div>
        
        <%
            List<String> alLegends = new ArrayList<String>();
            if(hmList.size()!=0) {
        %>
        <div class="clr"></div>
        <div class="roster_holder" style="border:solid 0px #ccc; margin:0px auto; width:100%">
        <div style="width:100%;  float:left; border:#ff0 solid 0px; height:65px;margin-top: 30px;">
	        <div class="prev" style="width:15%; border:solid #fff 0px; height:70px;float:left; padding:0px">
	            <div class="prevlink" style="float:right"> </div>
	        </div>
        <div class="mask_dates" id="scrolldates" style="width:83%; border:#00f solid 0px;position:relative; float:left; overflow:hidden; height:65px">
        <% if(paycycleDuration.equalsIgnoreCase("W")) {%>
        <div class="block_dates weekly_width" style="height:45px;">
        <% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
        <div class="block_dates biweekly_width" style="height:45px;">
            <% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
            <div class="block_dates fortnightly_width" style="height:45px;">
                <% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
                <div class="block_dates monthly_width" style="height:45px;">
                    <%} %>
                    <!-- <div class="block_dates" style="width:1462px; height:45px;"> -->
                    <div>
                        <div class="row_day">
                            <% for (int i = 0; alDay!=null && i < alDay.size(); i++) { %>
                            <div class="day reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) alDay.get(i) == null) ? "" : (String) alDay.get(i))%> </div>
                            <% } %>
                        </div>
                        <div style="clear:both"></div>
                        <div class="row_date">
                            <% for (int i = 0; alDate!=null && i < alDate.size(); i++) { %>
                            <div class="date reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) alDate.get(i) == null) ? "" : (String) alDate.get(i))%>
                            </div>
                            <% } %>
                        </div>
                        <div style="clear:both"></div>
                        <div class="row_inout">
                            <% for (int i = 0; alDate != null && i < alDate.size(); i++) { %>
                            <div style="width: 104px; float: left; background: #ccc; border: 1px solid rgb(255, 255, 255);">
                                <div class="inout reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>>IN</div>
                                <div class="inout reportHeading" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>>OUT</div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
            <div class="next" style="width:32px; float:right; border:#fff solid 1px;">   
            </div>
            <!-- <a  href="#" id="reset" onclick="resetall()">reset</a>-->
        </div>
        <div class="clr" style="clear:both"></div>
        <div style="border:#fff solid 0px; width:100%; float:left ; height:auto">
        <div id="mask" >
            <div id="scrollemp" style="border:0px solid #f0f;width:15%;height:580px; overflow:hidden;float:left;"  >
                <div class="block2" >
                    <!-- Employee names -->      
                    <%
                    //System.out.println("alEmpId ===>> " + alEmpId);
                        for (int i = 0; alEmpId!=null && i < alEmpId.size(); i++) {
                        	// String strCol = ((i % 2 == 0) ? "1" : "");
                        	String strCol = ((i % 2 == 0) ? "#f9f9f9" : "#efefef");
                        	List alServices = (List) hmList.get((String) alEmpId.get(i));
                        	//System.out.println("alEmpId.get(i) ===>> " + alEmpId.get(i));
                        	//out.println((String) alEmpId.get(i)+" alServices===>"+alServices+" hmList==>"+hmList);
                        	
                        	Map hm2 = (Map) hmServicesWorkrdFor.get((String) alEmpId.get(i));
                        %>
                    <div class="empname" >
                        <% 
                            for(int ii=0; alServices!=null && ii<alServices.size(); ii++){
                            	String strServiceId = (String)alServices.get(ii);
                            	if(uF.parseToInt(strServiceId) == 0){
                    				continue;
                    			}
                            	
                            	Map hm = (Map) hmList.get((String) alEmpId.get(i)+"_"+strServiceId);
                            	if(hm==null){
                            		hm = new HashMap();
                            	}
                            	if(ii==0) {
                        %>
                        <div class="alignLeft rosterEmpName"><%=(String) hm.get("EMPNAME")%></div> <!-- style="float:left;height: 20px;overflow: hidden;width: 75%;" -->
                        <!-- <div style="float:right; width:25%;overflow: hidden; height:auto"> -->
                        <% } %>
                            <div style="height:21px; overflow:hidden;"> <!-- class="alignLeft rosterEmpSBU" --> <!-- style="height:21px; overflow:hidden;" -->
                                <%=uF.showData((String)hmServices.get(strServiceId), "")%>
                            </div>
                            <%-- <% if(ii<alServices.size() -1 ){%>
                            <div style="border:1px solid #fff;"></div>
                            <%} %> --%>
                           <%--  <% if(ii==alServices.size() -1 ){%>
                        </div> --%>
                        <%/*  } */
                            }
                        %>
                    </div>
                    <%}%>   
                </div>
            </div>
            <div id="pivot"  onscroll="divScroll();">
                <% if(paycycleDuration.equalsIgnoreCase("W")) {%>
                <div class="block weekly_width" id="sos">
                    <% }else if(paycycleDuration.equalsIgnoreCase("BW")) {%>
                    <div class="block biweekly_width" id="sos">
                        <% }else if(paycycleDuration.equalsIgnoreCase("F")) {%>
                        <div class="block fortnightly_width" id="sos">
                            <% }else if(paycycleDuration.equalsIgnoreCase("M")) {%>
                            <div class="block monthly_width" id="sos" style="width:3210px">
                                <%} %>
                                <!-- <div class="block" id="sos"> -->
                                <div >
                                    <!-- hrizontal colck entries row thas is to be repeated -->
                                    <%
                                        for (int i = 0; alEmpId!=null && i < alEmpId.size(); i++) {
                                        	// String strCol = ((i % 2 == 0) ? "1" : "");
                                        	String strCol = ((i % 2 == 0) ? "#f9f9f9" : "#efefef");
                                        	List alServices = (List) hmList.get((String) alEmpId.get(i));
                                        	Map hm2 = (Map) hmServicesWorkrdFor.get((String) alEmpId.get(i));
                                        
                                        	String strLocationId  = (String)hmWLocation.get((String)alEmpId.get(i));
                                        	
                                        	
                                        	Map hmLeaves = (Map)hmLeavesMap.get((String) alEmpId.get(i));
                                        	if(hmLeaves==null)hmLeaves=new HashMap();
                                        	
                                        	String level=hmEmpLevelMap.get((String)alEmpId.get(i));
                                        	
                                        	
                                        	Set<String> weeklyOffSet= (Set<String>)hmWeekEndList.get(strLocationId);
                                        	if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
                                        	
                                        	Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get((String)alEmpId.get(i));
                                        	if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
                                        	
                                        %>
                                    <%
                                        for(int ii=0; alServices!=null && ii<alServices.size(); ii++){
                                        	String strServiceId = (String)alServices.get(ii);
                                        	if(uF.parseToInt(strServiceId) == 0){
                                				continue;
                                			}
                                        	
                                        	Map hm = (Map) hmList.get((String) alEmpId.get(i)+"_"+strServiceId);
                                        	
                                        //	out.println("<br>hm"+hm);
                                        	
                                        	if(hm==null){
                                        		hm = new HashMap();
                                        	}
                                        %>
                                    <% if(paycycleDuration.equalsIgnoreCase("W")) { %>
                                    <div style="height:30px; float:left; border:solid 1px #fff;" class="weekly_width" >
                                        <% } else if(paycycleDuration.equalsIgnoreCase("BW")) { %>
                                        <div style="height:30px; float:left; border:solid 1px #fff;" class="biweekly_width" >
                                            <% } else if(paycycleDuration.equalsIgnoreCase("F")) { %>
                                            <div style="height:30px; float:left; border:solid 1px #fff;" class="fortnightly_width" >
                                                <% } else if(paycycleDuration.equalsIgnoreCase("M")) { %>
                                                <div style="height:30px; float:left; border:solid 1px #fff;" class="monthly_width" style="width:3025px">
                                                    <% } %>
                                                    <%
                                                        for (int k = 0; alDate != null && k < alDate.size(); k++) {
                                                        
                                                        	String strWeekDay = uF.getDateFormat((String)alDate.get(k),  CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                                        	if(strWeekDay!=null) {
                                                        		strWeekDay = strWeekDay.toUpperCase();
                                                        	}
                                                        	String strWeekOff = null;
                                                        	String strColour = (String)hmHolidayDates.get((String)alDate.get(k)+"_"+strLocationId);
                                                        	if(strColour==null) {
                                                        		String strDay = uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                                        		if(strDay!=null)strDay=strDay.toUpperCase();
                                                        		if(alEmpCheckRosterWeektype.contains((String)alEmpId.get(i))) {
                                                        			if(rosterWeeklyOffSet.contains(strDay)) {
                                                        				strColour = IConstants.WEEKLYOFF_COLOR;
                                                        				strWeekOff = "W/Off";
                                                        			}
                                                        		} /* else if(weeklyOffSet.contains(strDay)){
                                                        			strColour = IConstants.WEEKLYOFF_COLOR;
                                                        		} */
                                                        	}
                                                        	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                                        	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String)alDate.get(k), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                                        	if(strLeave != null) {
                                                        		strColour = (String)hmLeavesColour.get(strLeave);
                                                        	}
                                                        	
                                                        	
                                                        	String strDate = (String) alDate.get(k);
                                                        	String strCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", IConstants.DBDATE, CF.getStrReportDateFormat());
                                                        %>
                                                    <%if(strLeave != null) { %>
                                                    <div style="width: 104px; float: left; background: #fff; <%= (strDate!=null && strDate.equalsIgnoreCase(strCurrentDate)?"border-right: 1px solid blue;border-left: 1px solid blue;":"border: 1px solid rgb(255, 255, 255)")%>;">
                                                        <div class="inout" style="width:102px;cursor:pointer; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center"><%=strLeave %></div>
                                                    </div>
                                                    <% } else if(strWeekOff != null) { %>
                                                    <div style="width: 104px; float: left; background: #fff; <%= (strDate!=null && strDate.equalsIgnoreCase(strCurrentDate)?"border-right: 1px solid blue;border-left: 1px solid blue;":"border: 1px solid rgb(255, 255, 255)")%>;">
                                                        <div class="inout" style="width:102px;cursor:pointer; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center"><%=strWeekOff %></div>
                                                    </div>
                                                    <% } else { %>
                                                    <div style="width: 104px; float: left; background: #fff; <%= (strDate!=null && strDate.equalsIgnoreCase(strCurrentDate)?"border-right: 1px solid blue;border-left: 1px solid blue;":"border: 1px solid rgb(255, 255, 255)")%>;">
                                                        <%-- <div class="inout" style="cursor:pointer;width:50px; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center"><%=((hm.containsKey((String) alDate.get(k) + "FROM")) ? "<a href=\"javascript:void(0)\" onclick=\"displayBlock('popup_name"+(String) hm.get((String) alDate.get(k)+"_"+strServiceId+"ROSTER_ID")+"');\"  class=\"poplight\">"+ uF.showData( (String) hm.get((String) alDate.get(k) + "FROM"),"-")+"</a>" : "<a href=\"javascript:void(0)\" onclick=\"displayBlock('popup_name"+(String) alEmpId.get(i)+uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"');\" rel=\"popup_name"+(String) alEmpId.get(i)+uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"\" class=\"poplight\"> - </a>") %></div>
                                                            <div class="inout" style="cursor:pointer;width:50px; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center"><%=((hm.containsKey((String) alDate.get(k) + "TO")) ? "<a href=\"javascript:void(0)\" onclick=\"displayBlock('popup_name"+(String) hm.get((String) alDate.get(k)+"_"+strServiceId+"ROSTER_ID")+"');\" class=\"poplight\">"+uF.showData( (String) hm.get((String) alDate.get(k) + "TO"),"-")+"</a>" : "<a href=\"javascript:void(0)\" onclick=\"displayBlock('popup_name"+(String) alEmpId.get(i)+uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"');\" rel=\"popup_name"+(String) alEmpId.get(i)+uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"\" class=\"poplight\"> - </a>") %></div> --%>
                                                        <div class="inout" style="cursor:pointer;width:50px; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center"><%=((hm.containsKey((String) alDate.get(k) + "FROM")) ? ""+ uF.showData( (String) hm.get((String) alDate.get(k) + "FROM"),"-")+"" : "-") %></div>
                                                        <div class="inout" style="cursor:pointer;width:50px; <%=((strColour!=null) ? "background-color:" + strColour + ";line-height:21px;" : "background:"+strCol+";line-height:21px")%>" align="center"><%=((hm.containsKey((String) alDate.get(k) + "TO")) ? ""+uF.showData( (String) hm.get((String) alDate.get(k) + "TO"),"-")+"" : "-") %></div>
                                                    </div>
                                                    <%} %>
                                                    
                                                    <%
													sb.append("<div id=\"popup_name"+(String) alEmpId.get(i)+ uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"\" class=\"popup_block posfix\">"+
														"<a href=\"javascript:void(0)\" onclick=\"hideBlock('popup_name"+(String) alEmpId.get(i)+ uF.getDateFormat((String) alDate.get(k),CF.getStrReportDateFormat(),"ddMMyy")+"');\" class=\"close\"><img src=\""+request.getContextPath()+"/images/close_pop.png\" class=\"btn_close\" title=\"Close Window\" alt=\"Close\" /></a>"+
														"<h2>You have chosen to add roster for "+(String) hm.get("EMPNAME")+" on "+  (String) alDate.get(k) +"</h2>"+
														"<form name=\"frmAddRoster\" action=\"AddRosterReport.action\">"+
														"<input type=\"hidden\" name=\"rosterDate\" value=\""+ (String) alDate.get(k) +"\">"+
														"<input type=\"hidden\" name=\"empId\" value=\""+(String) alEmpId.get(i) +"\">"+
														"<input type=\"hidden\" name=\"f_org\" value=\""+ f_org +"\">"+
														"<input type=\"hidden\" name=\"f_strWLocation\" value=\""+ f_strWLocation +"\">"+
														"<input type=\"hidden\" name=\"f_department\" value=\""+ f_department +"\">"+
														"<input type=\"hidden\" name=\"f_service\" value=\""+ f_service +"\">"+
														"<input type=\"hidden\" name=\"f_level\" value=\""+ f_level +"\">"+
														"<input type=\"hidden\" name=\"strMonth\" value=\""+ strMonth +"\">"+
														"<input type=\"hidden\" name=\"calendarYear\" value=\""+ calendarYear +"\">"+
														"<table align=\"center\">"+
														"<tr>"+
															/* "<td class=\"reportLabel\" nowrap=\"nowrap\">Select cost centre</td>"+ */
															"<td>&nbsp;</td>"+
														"<td>"+
															/* "<select name=\"service\">"+
																request.getAttribute("CC")+
															"</select>"+ */
														"<input type=\"hidden\" name=\"service\" value=\""+strServiceId+"\" /> </td>"+
														"</tr>"+
														
														"<tr>"+
															"<td class=\"reportLabel\">Start Time</td>"+
															"<td><input type=\"text\" name=\"_from\" value=\""+uF.getDateFormat("00:00","HH:mm",CF.getStrReportTimeFormat())+"\" style=\"width:100px\"></td>"+
															
														"</tr>"+
														
														"<tr>"+
															"<td class=\"reportLabel\">End Time</td>"+
															"<td><input type=\"text\" name=\"_to\" value=\""+uF.getDateFormat("00:00","HH:mm",CF.getStrReportTimeFormat())+"\" style=\"width:100px\"></td>"+			
														"</tr>"+
														"<tr>"+
															"<td colspan=\"2\" align=\"center\"><input type=\"submit\" class=\"input_button\" value=\"Add Roster\" /></td>"+
														"</tr>"+
														
														"</table>"+
														"</form>"+
														"<p style=\"color: white; font-size: 10px\">Please enter start time and end time in "+ CF.getStrReportTimeFormat() +" format. ie. for 03:00AM enter " + uF.getDateFormat("03:00AM", "hh:mma", CF.getStrReportTimeFormat()) +" and 03:00PM enter "+ uF.getDateFormat("03:00PM", "hh:mma", CF.getStrReportTimeFormat()) +"</p>"+
													"</div>");
													%>	
		
                                                    <% } %>
                                                </div>
                                                <div class="clr" style="clear:both"></div>
                                                <% } %>
                                                <% }
                                                    if(alEmpId!=null && alEmpId.size()==0){
                                                %>
                                                <div class="msg nodata"><span>No employees found for the current selection.</span></div>
                                                <% } %>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <%
                                if (request.getAttribute("empRosterDetails") != null) {
                                	out.println(request.getAttribute("empRosterDetails"));
                                }
                                out.print(sb.toString());
                                } else {
                            %>
                            You have no roster allocated for the current Pay Cycle, change 'Pay Cycle' to view other roster's
                            <% } %>
                        </div>
                        <div class="paddingtop20">&nbsp;</div>
                        <div class="custom-legends">
							<%
								Set set = hmLeavesColour.keySet();
								Iterator it = set.iterator();
								while (it.hasNext()) {
									String strLeave = (String) it.next();
							%>
								 <div class="custom-legend" style="border-color:<%=(String) hmLeavesColour.get(strLeave)%>">
								    <div class="legend-info"><%=(String) hmLeavesName.get(strLeave)%>[<%=strLeave%>]</div>
								  </div>
							<% } %>
						</div>
                </div>
                <!-- /.box-body -->
