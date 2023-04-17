<%@page import="java.sql.Time"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 

<script type="text/javascript">
	$(document).ready(function(){
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
	});
	
    hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
    hs.outlineType = 'rounded-white';
    hs.wrapperClassName = 'draggable-header';
    
    
    function show_employees() {
    	dojo.event.topic.publish("show_employees");
    } 
    
    function viewReason(mode, emp_id, service_id, _date) {
    	/* var dialogEdit = '#viewReason';
    	document.getElementById("updateSettingDiv").innerHTML = '';
    	document.getElementById("viewReason").innerHTML = ''; */
    	
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('View Reason');
	   	$.ajax({
			url : "GetExceptionReason.action?mode="+mode+"&emp_id="+emp_id+"&service_id="+service_id+"&_date="+_date,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    function submitForm(type){
    	var org = document.getElementById("f_org").value;
    	var paycycle = document.getElementById("paycycle").value;
    	var location = document.getElementById("location").value;
    	var level = document.getElementById("level").value;
    	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&location='+location+'&strSelectedEmpId='+strSelectedEmpId+'&level='+level+'&paycycle='+paycycle;
    	}
    	//alert("service ===>> " + service);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'ClockEntries.action?f_org='+org+paramValues,
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	$("#divResult").html(result);
       		}
    	});
    }
    
    
   /*  function formSubmit(type){
    	if(type == '1'){
    		if(document.getElementById("paycycle")){
    			document.getElementById("paycycle").selectedIndex = "0";
    		}
    		if(document.getElementById("location")){
    			document.getElementById("location").selectedIndex = "0";
    		}
    		if(document.getElementById("level")){
    			document.getElementById("level").selectedIndex = "0";
    		}
    		if(document.getElementById("strSelectedEmpId")){
    			document.getElementById("strSelectedEmpId").selectedIndex = "0";
    		}
    	}
    	document.getElementById("frm_roster_actual1").submit();
    } */
    
    
    /* var dialogEdit = '#updateSettingDiv'; */
    function updateClockEntries(DATE,EID,SID,AS,AE,divid, e,strOTMinuteStatus) { 
    	/* document.getElementById("updateSettingDiv").innerHTML = '';
    	document.getElementById("viewReason").innerHTML = ''; */
    	
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Update Clock Entry'); 
	   	$.ajax({  
			url : 'AddClockEntries.action?DATE='+DATE+'&EID='+EID+'&SID='+SID+'&AS='+AS+'&AE='+AE+'&divid='+divid+'&E='+e+'&otMinuteStatus='+strOTMinuteStatus+'&timeApprovalType=individualCE',//Created By Dattatray Date:01-11-21
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    
    function updateBreakPolicy(DATE,EID,SID,AS,AE,divid) { 
    	
    	var dialogEdit = '#changeBreakPolicyDiv';
    	var dialogEdit = '.modal-body';
	   	 $(dialogEdit).empty();
	   	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	   	 $("#modalInfo").show();
	   	 $(".modal-title").html('Change Break Policy');
	   	$.ajax({  
			url : 'ChangeBreakPolicyType.action?strDate='+DATE+'&empid='+EID+'&serviceId='+SID+'&strAS='+AS+'&strAE='+AE+'&divid='+divid,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
    }
    
    
    function updateSttings(DATE,EID,SID,AS,AE,divid,startTime,endTime,strE,e,strApStatusTmp,strOTMinuteStatus){
    	if(startTime==""){
    		alert('Please enter the valid start time');
    	}else if(endTime==""){
    		alert('Please enter the valid end time');
    	}else{
    		var strServiceId=SID;
    		var strEmpId=EID;
    		var strDate=DATE;
    		var strStatus='';
    		
    		var action='AddClockEntries.action?strDate='+DATE+'&strEmpId='+EID+'&EID='+EID+'&strServiceId='+SID+'&strStatus=&strE='+strE+'&divid='+divid+'&strStartTime='+startTime+'&strEndTime='+endTime+'&E='+e+'&strOTMinuteStatus='+strOTMinuteStatus+'&strApStatusTmp='+strApStatusTmp;
    		//alert("action ===>> " + action);
    		getContent(divid, action);
    		$("#modalInfo").hide();
    		//$(dialogEdit).dialog('close');
    	}
    	
    }
    
    
</script>
<%!UtilityFunctions uF = new UtilityFunctions();
    double dblPayAmount = 0.0;
    double dblTotalPayAmount = 0.0;
    
    String showData(String strData, String strVal) {  
    	if (strData == null){
    		return strVal;
    	}else{
    		return strData;
    	}
    }
    
    String showDataAdd(String strData) {
    	if (strData == null) {
    		return "0";
    	} else {
    		dblPayAmount += Double.parseDouble(strData);
    		dblTotalPayAmount += Double.parseDouble(strData);
    		return strData;
    	}
    }
    
    
    
    %>
<%
    dblPayAmount = 0.0;
    dblTotalPayAmount = 0.0;

    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
    String strEmpID = (String) request.getParameter("EMPID");
    String strReqEmpID = (String) request.getParameter("strSelectedEmpId");
    
    //System.out.println("strReqEmpID ===>> " + strReqEmpID);
    
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    
    String strD1 = (String) request.getParameter("D1");
    String strD2 = (String) request.getParameter("D2");
    String strType = (String) request.getParameter("T");
    String strPC = (String) request.getParameter("PC");
    
    List alInOut = (List) request.getAttribute("alInOut");
    List alDate = (List) request.getAttribute("alDate");
    List alDay = (List) request.getAttribute("alDay");
    
    
    Map hmHours = (HashMap) request.getAttribute("hmHours");
    Map hmHoursActual = (HashMap) request.getAttribute("hmHoursActual");
    Map hmStart = (HashMap) request.getAttribute("hmStartClockEntries");
    Map hmEnd = (HashMap) request.getAttribute("hmEndClockEntries");
    
    Map hmActualStart = (HashMap) request.getAttribute("hmActualStartClockEntries");
    Map hmActualEnd = (HashMap) request.getAttribute("hmActualEndClockEntries");
    
    Map hmRosterStart = (HashMap) request.getAttribute("hmRosterStart");
    Map hmRosterEnd = (HashMap) request.getAttribute("hmRosterEnd");
    
    Map hmDailyRate = (HashMap) request.getAttribute("hmDailyRate");
    Map hmHoursRates = (HashMap) request.getAttribute("hmHoursRates");
    Map hmServicesWorkedFor = (HashMap) request.getAttribute("hmServicesWorkedFor");
    Map hmDateServices = (HashMap) request.getAttribute("hmDateServices_TS");
    
    Map hmExceptions = (Map) request.getAttribute("hmExceptions");
    
    
    if(hmDateServices==null)hmDateServices = new HashMap();
    
    //	out.println("<br/>hmRosterStart="+hmRosterStart);
    //	out.println("<br/>hmRosterEnd="+hmRosterEnd);
    //	out.println("<br/>hmStart="+hmStart);
    //	out.println("<br/>hmEnd="+hmEnd);
    //	out.println("<br/>hmDateServices="+hmDateServices);
    //	out.println("<br/>hmHoursActual="+hmHoursActual);	
    
    String TOTALW1 = (String) request.getAttribute("TOTALW1");
    String TOTALW2 = (String) request.getAttribute("TOTALW2");
    String DEDUCTION = (String) request.getAttribute("DEDUCTION");
    String PAYW1 = (String) request.getAttribute("PAYTOTALW1");
    String PAYW2 = (String) request.getAttribute("PAYTOTALW2");
    
    String _TOTALRosterW1 = (String) request.getAttribute("_TOTALRosterW1");
    String _TOTALRosterW2 = (String) request.getAttribute("_TOTALRosterW2");
    String _ALLOWANCE = (String) request.getAttribute("ALLOWANCE");
    
    String strPayMode = (String) request.getAttribute("strPayMode");
    String strFIXED = (String) request.getAttribute("FIXED");
    
    Map<String, Set<String>> hmWeekEndList = (Map<String, Set<String>>) request.getAttribute("hmWeekEndList");
    if(hmWeekEndList == null) hmWeekEndList = new HashMap<String, Set<String>>();
    String strWLocationId = (String)request.getAttribute("strWLocationId");
    String strLevelId = (String)request.getAttribute("strLevelId");
    Set<String> weeklyOffSet= (Set<String>)hmWeekEndList.get(strWLocationId);
    if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
    
    List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");;
    if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
    
    Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");;
    if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
    
    Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strReqEmpID);
    if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
    
    Map hmWLocationHolidaysName = (Map) request.getAttribute("hmWLocationHolidaysName");
    Map hmHolidaysName = (Map)hmWLocationHolidaysName.get(strWLocationId);
    if(hmHolidaysName==null) hmHolidaysName = new HashMap();
    
    Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
    Map hmHolidayDates = (Map) request.getAttribute("hmHolidayDates");
    Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");
    Map hmServices = (Map) request.getAttribute("hmServices");
    
    Map hmLeavesMap = (Map) request.getAttribute("hmLeaves");
    if(hmLeavesMap==null)hmLeavesMap=new HashMap();
    
    Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
    if(hmLeavesColour==null)hmLeavesColour=new HashMap();
    
    
    
    
    List _alHolidays = (List) request.getAttribute("_alHolidays");
    
    String strEmpName = (String) request.getAttribute("EMP_NAME");
    Map hmRosterHours = (Map) request.getAttribute("hmRosterHours");
    
    if (strEmpName == null) {
    	strEmpName = "";
    }
    
    if (hmHours == null) {
    	hmHours = new HashMap();
    }
    
    if (hmStart == null) {
    	hmStart = new HashMap();
    }
    
    if (hmEnd == null) {
    	hmEnd = new HashMap();
    }
    if (hmDailyRate == null) {
    	hmDailyRate = new HashMap();
    }
    if (hmHoursRates == null) {
    	hmHoursRates = new HashMap();
    }
    
    if (hmHoursRates == null) {
    	hmHoursRates = new HashMap();
    }
    
    if (hmEarlyLateReporting == null) {
    	hmEarlyLateReporting = new HashMap();
    }
    if (hmRosterHours == null) {
    	hmRosterHours = new HashMap();
    }
    if (_hmHolidaysColour == null) {
    	_hmHolidaysColour = new HashMap();
    }
    if (hmLeavesColour == null) {
    	hmLeavesColour = new HashMap();
    }
    if (hmExceptions == null) {
    	hmExceptions = new HashMap();
    }
    
    if (hmActualStart == null) {
    	hmActualStart = new HashMap();
    }
    if (hmActualEnd == null) {
    	hmActualEnd = new HashMap();
    }
    
    //code for filters =>
    
    String paycycle = (String) request.getParameter("paycycle");
    
    String[] strPayCycleDates = null;
    
    if (paycycle != null) {
    
    	strPayCycleDates = paycycle.split("-");
    	strD1 = strPayCycleDates[0];
    	strD2 = strPayCycleDates[1];
    
    }else {
    	 
    	strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
    	strD1 = strPayCycleDates[0];
    	strD2 = strPayCycleDates[1];
    	
    }
    
    //String PAY = (String)request.getParameter("PAY");
    
    Map<String, String> hmBreakPolicy =(Map<String, String>)request.getAttribute("hmBreakPolicy");
    if(hmBreakPolicy==null) hmBreakPolicy = new HashMap<String, String>();
    Boolean flagBreak=(Boolean)request.getAttribute("flagBreak");
    
    String strEmpServiceId = (String) request.getAttribute("strEmpServiceId");
    int nEmpServiceId = uF.parseToInt(strEmpServiceId); 
    
    List<String> alSalPaidEmpList = (List<String>)request.getAttribute("alSalPaidEmpList");
	if(alSalPaidEmpList == null) alSalPaidEmpList = new ArrayList<String>();
	List<String> alApproveClockEntrieEmp = (List<String>)request.getAttribute("alApproveClockEntrieEmp");
	if(alApproveClockEntrieEmp == null) alApproveClockEntrieEmp = new ArrayList<String>();
	
	String pageFrom = (String) request.getAttribute("pageFrom");
	
	Map<String, String> hmShiftBreak = (Map<String, String>) request.getAttribute("hmShiftBreak");
	if(hmShiftBreak == null) hmShiftBreak = new HashMap<String, String>();
	
	Map<String, String> hmRosterShiftId = (Map<String, String>) request.getAttribute("hmRosterShiftId");
	if(hmRosterShiftId == null) hmRosterShiftId = new HashMap<String, String>();
	
	String strDefaultLunchDeduction = (String) request.getAttribute("strDefaultLunchDeduction");
	
	
	Map<String,Map<String,String>> hmOvertimeType=(Map<String,Map<String,String>>)request.getAttribute("hmOvertimeType");
	if(hmOvertimeType==null)hmOvertimeType=new HashMap<String,Map<String,String>>();
	//System.out.println("hmOvertimeType ===>> " + hmOvertimeType);
	
	
	String locationstarttime=(String)request.getAttribute("locationstarttime");
	String locationendtime=(String)request.getAttribute("locationendtime");
	
	Map<String,String> hmActualOT=(Map<String,String>)request.getAttribute("hmActualOT");
	if(hmActualOT==null) hmActualOT=new HashMap<String, String>();
	

	Map<String, String> hmApproveOT=(Map<String, String>)request.getAttribute("hmApproveOT");
	Map<String, String> hmCheckPayroll =(Map<String, String>)request.getAttribute("hmCheckPayroll");
	
	String userlocation=(String)request.getAttribute("userlocation");
	
	Map<String, List<Map<String,String>>> hmOvertimeMinuteSlab = (Map<String, List<Map<String,String>>>)request.getAttribute("hmOvertimeMinuteSlab");
	if(hmOvertimeMinuteSlab == null) hmOvertimeMinuteSlab = new HashMap<String, List<Map<String,String>>>();
	
	Map<String,String> hmCalculateOT =(Map<String,String>)request.getAttribute("hmCalculateOT");
	if(hmCalculateOT == null) hmCalculateOT = new HashMap<String, String>();
	
	List<String> alApproveOTMinuteEmp = (List<String>)request.getAttribute("alApproveOTMinuteEmp");
	if(alApproveOTMinuteEmp == null) alApproveOTMinuteEmp = new ArrayList<String>();
	
	//===start parvez date: 14-10-2021===
	String employmentEndDate=(String)request.getAttribute("employmentEndDate");
	//System.out.println("CE2.jsp/399--employmentEndDate="+employmentEndDate);
	//===end parvez date: 14-10-2021===
	
  %>
<%-- <% String strText = "";
    if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.MANAGER))){
    	strText = "Please <a href=\"PayCycleList.action?T="+strType+"\">click here</a> to go back to the paycycles list or <a href=\"EmployeeReportPayCycle.action?T="+strType+"&PC="+strPC+"&D1="+strD1+"&D2="+strD2+"\">click here</a> to go back to the employee list"; 
    }else{
    	strText = "<a href=\"PayCycleList.action?T="+strType+"\">Please click here to go back to the paycycles list</a>";
    }
    %> --%>
<%
    dblPayAmount = 0.0d;
    %>
<%String strTitle =  ((strUserType != null && strUserType.equalsIgnoreCase("EMPLOYEE") ? "My Clock Entries"  : "Clock Entries")); %>	   

	<%-- <div>
		<% if(pageFrom != null && pageFrom.equals("THREESTEP")) { %>
			<span style="float: right; margin-right: 50px;"><b>Step 1</b> &nbsp; <a href="ApprovePay.action?pageFrom=<%=pageFrom %>"><input type="button" class="input_button" value="<%="Next >" %>" style="margin: 0px;"/></a></span>
		<% } %>
	</div> --%>
                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
					    <div class="pagetitle" style="margin:0px;">
					        <span>
					        <% if(strUserType != null && strUserType.equalsIgnoreCase("EMPLOYEE")){
					            %>    	
					        <%="Pay cycle "+uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) %> 
					        <%   	  
					            }else if(strUserType != null && strEmpName!=null && strEmpName.length()>0){
					            %>
					        <%= strEmpName + " for pay cycle "+uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"-"+uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())%>
					        <%
					            }
					            %>
					        </span>
					    </div>
					    <!-- id="selectLevel" -->
					    <div class="box box-default">  <!-- collapsed-box -->
						<%-- <div class="box-header with-border">
						    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
						    <div class="box-tools pull-right">
						        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
						    </div>
						</div> --%>
						<div class="box-body" style="padding: 5px; overflow-y: auto;">
							<s:form theme="simple"  action="ClockEntries" name="frm_roster_actual" id="frm_roster_actual1" cssClass="formcss" >
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
										<i class="fa fa-filter" aria-hidden="true"></i>
									</div>
									<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
										<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Organization</p>
											<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Paycycle</p>
											<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" list="payCycleList" onchange="submitForm('2');"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Location</p>
											<s:select name="location" id="location" theme="simple" listKey="wLocationId" listValue="wLocationName" headerKey="0" headerValue="Select Location" list="workLocationList" onchange="submitForm('2');"/>
										</div>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Level</p>
										<%-- 	<s:select theme="simple" name="level" id="level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" 
							                    onchange="getContent('myDiv', 'GetEmployeeList.action?fromPage=CKE&f_org='+document.frm_roster_actual.f_org.options[document.frm_roster_actual.f_org.selectedIndex].value+'&paycycle='+document.frm_roster_actual.paycycle.options[document.frm_roster_actual.paycycle.selectedIndex].value+'&level='+document.frm_roster_actual.level.options[document.frm_roster_actual.level.selectedIndex].value+'&strMul=N'+'&location='+document.frm_roster_actual.location.options[document.frm_roster_actual.location.selectedIndex].value);"
							                    list="levelList" key="" required="true"/> --%>
							                    <s:select theme="simple" name="level" id="level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" 
							                    onchange="submitForm('2');"
							                    list="levelList" key="" required="true"/>
										</div>
										<% } else { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Paycycle</p>
											<s:select theme="simple" name="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" list="payCycleList" onchange="submitForm('2');"/>
										</div>
										<% } %>
										<% if(strUserType!=null && !strUserType.equals(IConstants.EMPLOYEE)) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Employee</p>
											<span id="myDiv">
												<s:select theme="simple" label="Select Single Employee" name="strSelectedEmpId" id="strSelectedEmpId" listKey="employeeId"  listValue="employeeName" headerKey="" headerValue="Select Employee" list="empNamesList" key="" onchange="submitForm('2');"  />
											</span>
										</div>
										<% } %>
									</div>
								</div>
							</s:form>
						</div>
					</div>
					    
					    <div class="clr margintop20"></div>

					    <% 
					        String strWeekOffcolor=null; 
					        if(hmDateServices.size()!=0){ %> 
					    <table class="table table-bordered" width="100%">
					        <%
					            StringBuilder str=new StringBuilder();
					            double dblTotalActualHrs = 0;
					            double dblTotalRosterHrs = 0;
					            double dblTotalVarianceHrs = 0;
					            double dblTotalOTHrs = 0;
					            
					            int k=0;
					            	for (int i = 0; i < alDate.size(); i++) {
					            		
					            		
					            		List alDateServices = (List)hmDateServices.get((String) alDate.get(i));
					            		if(alDateServices==null){ 
					            			alDateServices=new ArrayList();
					            			alDateServices.add("-1");
					            		}
					            
					            int ii=0; 
					            for (ii = 0; ii < alDateServices.size(); ii++) {
					            	
					            	if(uF.parseToInt((String)alDateServices.get(ii))==0){
					            		continue; 
					            	}
					            	/* if(uF.parseToInt((String)alDateServices.get(ii))==-1){
					            		alDateServices.set(ii,""+nEmpServiceId);
					            	} */ 
					            	
					            	if(str.length()==0){
					            		str.append("1");
					            	}else{
					            		str=new StringBuilder();;
					            	}
					            	
					            %>
					        <%	
					            double dblHrsAtten = uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
					            double dblHrsAttenActual = uF.parseToDouble((String) hmHoursActual.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
					            double dblHrsRoster = uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
					            
					            if(k==0){
					        %>
					        <tr>
					            <th class="reportHeading alignCenter">Date</th>
								<th class="reportHeading alignCenter">Cost-center</th>
								<th class="reportHeading alignCenter">Roster/Standard Start Time<br/>(HH:mm)</th>
								<th class="reportHeading alignCenter">Approved [Actual] Start Time<br/>(HH:mm)</th>
								<th class="reportHeading alignCenter">Roster/Standard End Time<br/>(HH:mm)</t>
								<th class="reportHeading alignCenter">Approved [Actual] End Time<br/>(HH:mm)</th>
								<th class="reportHeading alignCenter">Roster Summary<br/>(HH:mm)</th> 
								<th class="reportHeading alignCenter">Day Summary<br/>(HH:mm)</th>
								<%if(uF.parseToBoolean(CF.getIsBreakPolicy()) && flagBreak){ %>
									<th class="reportHeading alignCenter">Break Policy</th>
								<% } %>
								<%if(CF.getIsShowTimeVariance()){ %>
									<th class="reportHeading alignCenter">Variance<br/>(HH:mm)</th>
								<% } %>
								<th class="reportHeading alignCenter">OT<br/>(HH:mm)</th>
					        </tr>
					        <%
					            }
					            k++;
					            
					            String strBgColor = (String) _hmHolidaysColour.get(i + "");
					            strBgColor = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocationId);
					            //System.out.println("strBgColor ===>> " + strBgColor +" -- hmHolidayDates ===>> " + hmHolidayDates);
					            if(strBgColor==null){
									//String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), "EEEE");
									String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
									if(strDay!=null)strDay=strDay.toUpperCase();
										//strBgColor = (String) hmWeekEndList.get(strDay+"_"+strWLocationId);
										/* for(String strDate:weeklyOffSet){
											if(strDay!=null && strDay.equals(strDate)){
												strBgColor = IConstants.WEEKLYOFF_COLOR;
												strWeekOffcolor = IConstants.WEEKLYOFF_COLOR;
											}
										} */
										
										//System.out.println("alEmpCheckRosterWeektype ===>> " + alEmpCheckRosterWeektype+ " -- strReqEmpID ===>> " + strReqEmpID);
										//System.out.println("rosterWeeklyOffSet ===>> "+ rosterWeeklyOffSet +" -- weeklyOffSet ===>> " + weeklyOffSet + " -- strDay ===>> " + strDay);
										if(alEmpCheckRosterWeektype.contains(strReqEmpID)){
											if(rosterWeeklyOffSet.contains(strDay)){
												strBgColor = IConstants.WEEKLYOFF_COLOR;
												strWeekOffcolor = IConstants.WEEKLYOFF_COLOR;
											}
										}else if(weeklyOffSet.contains(strDay)){
											strBgColor = IConstants.WEEKLYOFF_COLOR;
											strWeekOffcolor = IConstants.WEEKLYOFF_COLOR;
										}
									}
					            
					            
					            
					      //===start parvez date: 05-08-2022===  
					    	  
					            /* if(strBgColor==null){
					            	strBgColor = (String) hmLeavesColour.get((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)));
					            } */
					      
					            String strTravel = null;
								if(strBgColor==null){
									
								//System.out.println(hmLeavesColour);
									String tempLeaveTypeCode = hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L") ? (String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L") : (String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T");
									if(tempLeaveTypeCode!=null && tempLeaveTypeCode.contains("(HD)")){
										int index = tempLeaveTypeCode.indexOf("(");
										tempLeaveTypeCode = tempLeaveTypeCode.substring(0,index);
									}
									//strBgColor = (String) hmLeavesColour.get(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L") ? (String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L") : (String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"));
									strBgColor = (String) hmLeavesColour.get(tempLeaveTypeCode);
									strTravel = (String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T");
								}
						//===end parvez date: 05-08-2022===		
					            
					            //out.println("<br/>"+hmLeavesColour);
					            //out.println("<br/>"+(String) alDate.get(i));
					            //out.println("<br/>"+(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)));
					            //out.println("<br/>"+(String) hmLeavesColour.get((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT))));
					            //out.println("<br/>"+strBgColor);
					            %>
					        <tr>
								<td class="alignLeft" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%=(String) alDay.get(i)%>, <%=(String) alDate.get(i)%>
								</td>
								<td class="alignLeft" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%=(((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null)?(String) hmServices.get((String)alDateServices.get(ii)):"-")%>
								</td>
									
								<td class="alignCenter" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%> --%>
							<!-- ===start parvez date: 05-08-2022=== -->	
									<%if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")){ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : "")+(" ["+showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%>
									<%} else{ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%>
									<%} %>	
							<!-- ===end parvez date: 05-07-2022=== -->		
								</td>
								<td class="alignCenter" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")%> --%>
									
									<%if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")){ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : "")+(" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]") %>
									<%} else{ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")%>
									<%} %>
									
									<%if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_IN"))==-1){ %>
									<a href="javascript:void(0);" title="Exception Denied" onclick="viewReason('IN', '<%=request.getParameter("strSelectedEmpId") %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="leftearly">&nbsp;</div></a>
									<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_IN"))==1){ %>
									<a href="javascript:void(0);" title="Exception Approved" onclick="viewReason('IN', '<%=request.getParameter("strSelectedEmpId") %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="worklate">&nbsp;</div></a>
									<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_IN"))==-2){ %>
									<a href="javascript:void(0);" title="Exception Pending for approval" onclick="viewReason('IN', '<%=request.getParameter("strSelectedEmpId") %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="wentontime">&nbsp;</div></a>
									<%} %>
									
								</td> 
								<td class="alignCenter" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%> --%>
									<%if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")){ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : "")+(" ["+showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")) %>
									<%} else{ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")) %>
									<%} %>
								</td>
								<td class="alignCenter" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")%> --%>
									
									<%if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")){ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))? (((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : ""))+(" ["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]") %>
									<%} else{ %>
										<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))? ((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")%>
									<%} %>
									
									<%if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_OUT"))==-1){ %>
									<a href="javascript:void(0);" title="Exception Denied" onclick="viewReason('OUT', '<%=request.getParameter("strSelectedEmpId") %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="leftearly">&nbsp;</div></a>
									<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_OUT"))==1){ %>
									<a href="javascript:void(0);" title="Exception Approved" onclick="viewReason('OUT', '<%=request.getParameter("strSelectedEmpId") %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="worklate">&nbsp;</div></a>
									<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_OUT"))==-2){ %>
									<a href="javascript:void(0);" title="Exception Pending for approval" onclick="viewReason('OUT', '<%=request.getParameter("strSelectedEmpId") %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="wentontime">&nbsp;</div></a>
									<%} %>
									
									
								</td>
								
								<td class="alignRight padRight20" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%=uF.showTime(showData((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "0"))%>
									<%
										dblTotalRosterHrs+=uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
										dblTotalRosterHrs = dblTotalRosterHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalRosterHrs) : 0.0d;
									%>					
								</td>
								
								<td class="alignRight padRight20" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%=uF.showTime(uF.formatIntoTwoDecimalWithOutComma(dblHrsAttenActual))%>
									<%
										dblTotalActualHrs+=dblHrsAttenActual;
										dblTotalActualHrs = dblTotalActualHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalActualHrs) : 0.0d;
									%>
									
									<%if((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null){ %>
										<%if(alSalPaidEmpList.contains(strReqEmpID)){ %>
											<div id="myDiv_<%=i%>" style="float:right">
												<a href="javascript:void(0)" onclick="alert('Payroll has been processed for this date.')">
													<i class="fa fa-trash" aria-hidden="true"></i>
												</a>
											</div>
										<%} else if(alApproveClockEntrieEmp.contains(strReqEmpID)){ %>
											<div id="myDiv_<%=i%>" style="float:right">
												<a href="javascript:void(0)" onclick="alert('Clock entries has been approved for this dates.')">
													<i class="fa fa-trash" aria-hidden="true"></i>
												</a>
											</div>
										<%} else { 
											String strOTMinuteStatus = "false";
											if(alApproveOTMinuteEmp.contains((String) alDate.get(i))){
												strOTMinuteStatus = "true";
											}
										%>	
											<div id="myDiv_<%=i%>" style="float:right">
											
											<!-- Created By Dattatray Date:08-12-21 -->
											<%if(strOTMinuteStatus.equals("true")){%>
											<a href="javascript:void(0)" onclick="alert('Unable to delete clock entries because OT already approved.')">
													<i class="fa fa-trash" aria-hidden="true"></i>
												</a>
											<%}else{ %>
											<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to delete this attendance?')?getContent('myDiv_<%=i%>', 'AddClockEntries.action?strDelete=D&DATE=<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>&EID=<%=strReqEmpID%>&SID=<%=(String)alDateServices.get(ii)%>&AS=<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>&AE=<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>&strOTMinuteStatus=<%=strOTMinuteStatus %>'):'')">
													<i class="fa fa-trash" aria-hidden="true"></i>
												</a>
											<%} %>
												
											</div>
										<%} %>
									<% } %>
									<%
									if(uF.parseToInt((String)alDateServices.get(ii))!=0 && uF.parseToInt((String)alDateServices.get(ii))!=-1){
										if(!uF.parseToBoolean(CF.getIsExceptionAutoApprove())) { %>
											<%if(alSalPaidEmpList.contains(strReqEmpID)) { %>
												<a href="javascript:void(0)" onclick="alert('Payroll has been processed for this date.')"> 
													<span class="time_edit_setting"></span>
												</a> 
											<% } else if(alApproveClockEntrieEmp.contains(strReqEmpID)) { %>
												<a href="javascript:void(0)" onclick="alert('Clock entries has been approved for this dates.')"> 
													<span class="time_edit_setting"></span>
												</a> 
											<% } else { 
												String strOTMinuteStatus = "false";
												if(alApproveOTMinuteEmp.contains((String) alDate.get(i))){
													strOTMinuteStatus = "true";
												}
											%>
												<%-- <a href="<%=request.getContextPath()%>/AddClockEntries.action?level=<%=(String)request.getAttribute("level") %>&location=<%=(String)request.getAttribute("location") %>&f_org=<%=(String)request.getAttribute("f_org") %>&paycycle=<%=(String)request.getAttribute("paycycle") %>&DATE=<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>&EID=<%=strReqEmpID%>&SID=<%=(String)alDateServices.get(ii)%>&AS=<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>&AE=<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>" onclick="return hs.htmlExpand(this, {objectType: 'ajax' , width:200});"> 
													<span class="time_edit_setting"></span>
												</a> --%>
												
												<!-- ===start parvez date: 14-10-2021=== -->
												<%
												//String endDate = uF.dateDifference((String) alDate.get(i),IConstants.DATE_FORMAT,employmentEndDate,IConstants.DBDATE,CF.getStrTimeZone());
												//System.out.println("CE2.jsp/692--employmentEndDate="+employmentEndDate);
												if(alDate.get(i)!=null && employmentEndDate!=null && !employmentEndDate.equalsIgnoreCase("Null") && !employmentEndDate.isEmpty()){
													//System.out.println("CE2.jsp/692--if");
													java.util.Date strEmpEndDate = uF.getDateFormatUtil(employmentEndDate, IConstants.DBDATE);
													java.util.Date stralDate = uF.getDateFormatUtil(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE), IConstants.DBDATE);
												
												//if(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE).equals(employmentEndDate)){
												if(strEmpEndDate.after(stralDate) || strEmpEndDate.equals(stralDate)){
													//System.out.println("CE2.jsp/692--true");	
													//System.out.println("CE2.jsp/692--strEmpEndDate="+strEmpEndDate);
													//System.out.println("CE2.jsp/692--stralDate="+stralDate);
												 %>
													<a href="javascript:void(0)" onclick="updateClockEntries('<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>','<%=strReqEmpID%>','<%=(String)alDateServices.get(ii)%>','<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>','<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>','settingDiv_<%=i%>','E','<%=strOTMinuteStatus %>');"> 
														<span class="time_edit_setting"></span>
													</a>
												<% } %>
												<% } else{ %>
													<a href="javascript:void(0)" onclick="updateClockEntries('<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>','<%=strReqEmpID%>','<%=(String)alDateServices.get(ii)%>','<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>','<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>','settingDiv_<%=i%>','E','<%=strOTMinuteStatus %>');"> 
														<span class="time_edit_setting"></span>
													</a>
												<% } %>
												
												<!-- ===end parvez date: 14-10-2021=== -->
												
												<%-- <a href="javascript:void(0)" onclick="updateClockEntries('<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>','<%=strReqEmpID%>','<%=(String)alDateServices.get(ii)%>','<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>','<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>','settingDiv_<%=i%>','E','<%=strOTMinuteStatus %>');"> 
													<span class="time_edit_setting"></span>
												</a> --%>
												
											<% } %>
										<% } else { %>
											<%if(alSalPaidEmpList.contains(strReqEmpID)) { %>
												<a href="javascript:void(0)" onclick="alert('Payroll has been processed for this date.')"> 
													<span class="time_edit_setting"></span>
												</a> 
											<% } else if(alApproveClockEntrieEmp.contains(strReqEmpID)) { %>
												<a href="javascript:void(0)" onclick="alert('Clock entries has been approved for this dates.')"> 
													<span class="time_edit_setting"></span>
												</a> 
											<% } else {
												
											//System.out.println("alApproveOTMinuteEmp : "+alApproveOTMinuteEmp);
												String strOTMinuteStatus = "false";
												if(alApproveOTMinuteEmp.contains((String) alDate.get(i))){
													strOTMinuteStatus = "true";
												}
												
											%>
											<!-- Created By Dattatray Date:08-12-21 -->
											<% if(strOTMinuteStatus.equals("true")){ %>
											<a href="javascript:void(0)" onclick="alert('Unable to update clock entries because OT already approved.')"> 
													<span class="time_edit_setting"></span>
												</a>
											<%}else{ %>
											<a href="javascript:void(0)" onclick="updateClockEntries('<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>','<%=strReqEmpID%>','<%=(String)alDateServices.get(ii)%>','<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>','<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>','settingDiv_<%=i%>','E','<%=strOTMinuteStatus %>');"> 
													<span class="time_edit_setting"></span>
												</a>
											<%} %>
												
											<% } %>
									<%	} 
									} %>
									<br/>
									<div id="settingDiv_<%=i%>"></div>
								   
								</td>
								<%
								String checkStartTime = ((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"));
								String checkEndTime = ((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"));
								
								if(uF.parseToBoolean(CF.getIsBreakPolicy()) && flagBreak){ %>
								<td class="alignRight padRight20" <%="style=\'background-color:" + strBgColor + "\'"%>>
								<%if(!checkStartTime.equals("-") && !checkEndTime.equals("-")){ %>
									<div style="float: right;">
										<div id="breakPolicyDiv_<%=i%>" style="float: left;"><%=uF.showData(hmBreakPolicy.get((String) alDate.get(i)),"") %></div>
										<div style="float: left;">
											<a href="javascript:void(0)" onclick="updateBreakPolicy('<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>','<%=strReqEmpID%>','<%=(String)alDateServices.get(ii)%>','<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>','<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>','breakPolicyDiv_<%=i%>');"> 
												<div class="time_edit_setting"></div>
											</a>
										</div>
									</div>
								<% } %>	
								</td>
								<% } %>
								<%if(CF.getIsShowTimeVariance()){ %>
									<td class="alignRight padRight20" <%="style=\'background-color:" + strBgColor + "\'"%>>
										<%
											String strShiftId = hmRosterShiftId.get((String)alDate.get(i)+"_"+(String)alDateServices.get(ii));
											String strLunchBreak = strDefaultLunchDeduction;
											if(strShiftId != null && !strShiftId.trim().equals("") && !strShiftId.trim().equalsIgnoreCase("NULL") && hmShiftBreak.containsKey(strShiftId)){
												strLunchBreak = hmShiftBreak.get(strShiftId);	
											}
											double dblRosterHRS = dblHrsRoster;
											if(dblHrsRoster > uF.parseToDouble(strLunchBreak)){
												dblRosterHRS = dblHrsRoster - uF.parseToDouble(strLunchBreak); 
											}
											
											double dblAttenActualHRS = dblHrsAttenActual;
											if(dblHrsAttenActual > uF.parseToDouble(strLunchBreak)){
												dblAttenActualHRS = dblHrsAttenActual - uF.parseToDouble(strLunchBreak);
											}
											
											//String strVariance = uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dblHrsRoster),uF.formatIntoTwoDecimal(dblHrsAttenActual));
											String strVariance = uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dblRosterHRS),uF.formatIntoTwoDecimal(dblAttenActualHRS));
											dblTotalVarianceHrs+= uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(strVariance)));
											dblTotalVarianceHrs = uF.convertHoursMinsInDouble(dblTotalVarianceHrs);
										%>
										<%=uF.showTime(strVariance) %>
									</td>
								<%} %>
								
								<td class="alignRight padRight20" <%="style=\'background-color:" + strBgColor + "\'"%>>
									<%
									//System.out.println("otHrs==> 1 ");
									if(((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null) && ((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null)){
									    String otHrs="0.00";
									    //System.out.println("0.00 otHrs ===>> " + otHrs);
									    if(hmCalculateOT != null && hmCalculateOT.containsKey(strReqEmpID+"_"+uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT))){
									    	otHrs = uF.showData(hmCalculateOT.get(strReqEmpID+"_"+uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT)),"");
									    	//System.out.println("otHrs ==> "+otHrs);
									    } else {
									    	//System.out.println("else ====>>> ");
										    Map<String,String> hmOvertime=null;
										    String day=uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT);
										    if(hmHolidayDates!=null && hmHolidayDates.containsKey((String)alDate.get(i)+"_"+userlocation)){
										    	hmOvertime=hmOvertimeType.get("PH");
										    }else if(hmWeekEndList!=null && hmWeekEndList.containsKey(day+"_"+userlocation)){
										    	hmOvertime=hmOvertimeType.get("BH");
										    }else{
										    	hmOvertime=hmOvertimeType.get("EH");
										    }
										    
										    if(hmOvertime==null) hmOvertime=new HashMap<String,String>();
					
										   // System.out.println("JSP --- STANDARD_WKG_HRS ====>>> " + hmOvertime.get("STANDARD_WKG_HRS"));
										    if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("RH")){
										    	//System.out.println("====>>> 1 ");
										    	/* if(hmOvertime.get("CALCULATION_BASIS")!=null && hmOvertime.get("CALCULATION_BASIS").equals("M")){
													Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time rosterEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
																	
													long milliseconds1 = entryTime.getTime();
													long milliseconds2 = rosterEndTime.getTime();
													long milliseconds3 = endTime.getTime();
														
													if(milliseconds3>=milliseconds2){
														double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
														double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
														double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
														double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
														ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
														double dblOTInMinute = uF.convertHoursIntoMinutes1(ottime);										
														List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmOvertime.get("OVERTIME_ID"));
														if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
														int nRoundOffMinute = 0;
														int nAlOtMinuteSize = alOtMinute !=null ? alOtMinute.size() : 0;
														for(int x = 0; x < nAlOtMinuteSize; x++){
															Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
															if(uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblOTInMinute) 
																	&& ((int) dblOTInMinute) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))){
																nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
																break;
															}
														}
														if(nRoundOffMinute > 0){
															double dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
															String strTotal = ""+dblHour;
															otHrs=""+uF.parseToDouble(strTotal);
														} else {
															otHrs = ""+0.0d;
														}
													}
													
												} else { */
											    	Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time rosterEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
																	
													long milliseconds1 = entryTime.getTime();
													long milliseconds2 = rosterEndTime.getTime();
													long milliseconds3 = endTime.getTime();
														
													if(milliseconds3>=milliseconds2){
														double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
														double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
														double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
														double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
														ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
														if(ottime>=bufferTime){
															double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
															double otTime=(ottime-bufferTime);
															if(otTime>=minOT){
																double dblOTInMinute = uF.convertHoursIntoMinutes1(otTime);
																int nRoundOffPolicy = uF.parseToInt(hmOvertime.get("ROUND_OFF_OVERTIME"));
																double dblCalTime = otTime;
																if(nRoundOffPolicy > 0){
																	double dblCalRoundOff = dblOTInMinute / nRoundOffPolicy;
																	String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblCalRoundOff);
																	//System.out.println((String)alDate.get(i)+"--1--dblOTInMinute=====>"+dblOTInMinute+"--nRoundOffPolicy==>"+nRoundOffPolicy+"--dblCalRoundOff=====>"+dblCalRoundOff+"--strTotal==>"+strTotal);
																	
																	if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
																		String str11 = strTotal.replace(".", ":");
																		String[] tempTotal = str11.split(":");
																		double dblHr = uF.parseToDouble(tempTotal[1]);
																		if(dblHr > 0){
																			double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
																			double dblHour = uF.convertMinutesIntoHours(dblMain);
																			strTotal = ""+dblHour;
																			otHrs=strTotal;
																		} else {
																			double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
																			double dblHour = uF.convertMinutesIntoHours(dblMain);
																			strTotal = ""+dblHour;
																			otHrs=strTotal;
																		}
																	} else {
																		otHrs=""+otTime;		
																	}
																} else {
																	otHrs=""+otTime;
																}										
															}
															/* System.out.println((String)alDate.get(i)+"--1--otHrs=====>"+otHrs+"--dbl==>"+dbl
																	+"--entryTime=====>"+entryTime+"--rosterEndTime==>"+rosterEndTime+"--endTime==>"+endTime
																	+"--milliseconds1=====>"+milliseconds1+"--milliseconds2==>"+milliseconds2+"--milliseconds3==>"+milliseconds3
																	+"--actualTime==>"+actualTime+"--bufferTime==>"+bufferTime+"--actualTime==>"+
																	ottime+"--minOT==>"+minOT+"--ottime==>"+ottime+"--otTime==>"+otTime); */
														}
													}
										    	//}
											}else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("SWH")){
												//System.out.println("====>>> 2 ");
												/* if(hmOvertime.get("CALCULATION_BASIS")!=null && hmOvertime.get("CALCULATION_BASIS").equals("M")){
													Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time wlocationEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+locationendtime,CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													
													long milliseconds1 = entryTime.getTime();
													long milliseconds2 = wlocationEndTime.getTime();
													long milliseconds3 = endTime.getTime();
														
													if(milliseconds3>=milliseconds2){
														double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
														dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
														double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
														actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
														double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
														double ottime=actualTime-dbl;
														double dblOTInMinute = uF.convertHoursIntoMinutes1(ottime);			
														
														List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmOvertime.get("OVERTIME_ID"));
														if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
														int nRoundOffMinute = 0;
														int nAlOtMinuteSize = alOtMinute !=null ? alOtMinute.size() : 0;
														for(int x = 0; x < nAlOtMinuteSize; x++){
															Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
															if(uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblOTInMinute) 
																	&& ((int) dblOTInMinute) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))){
																nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
																break;
															}
														}
														
														if(nRoundOffMinute > 0){
															double dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
															String strTotal = ""+dblHour;
															otHrs =""+uF.parseToDouble(strTotal);
														} else {
															otHrs = ""+0.0d;
														}
													}
												} else { */
													Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time wlocationEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+locationendtime,CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
													
													long milliseconds1 = entryTime.getTime();
													long milliseconds2 = wlocationEndTime.getTime();
													long milliseconds3 = endTime.getTime();
													/* if(((String)alDate.get(i)).equals("24-Feb-2021")) {
														System.out.println("milliseconds3 =====>> " + milliseconds3 + " --- milliseconds2 =====>> " + milliseconds2);
													} */
													if(milliseconds3>=milliseconds2) {
														double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
														dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
														double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
														actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
														double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
														double ottime=actualTime-dbl;
														//double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
														//ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
														/* if(((String)alDate.get(i)).equals("24-Feb-2021")) {
															System.out.println("ottime =====>> " + ottime + " --- bufferTime =====>> " + bufferTime);
														} */
														if(ottime>=bufferTime) {
															//otHrs=""+(ottime-bufferTime);
															double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
															//double otTime=(ottime-bufferTime);
															double otTime = (ottime-bufferTime) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(bufferTime),uF.formatIntoTwoDecimal(ottime))): 0.0d;
															otTime = otTime > 0.0d ? uF.convertHoursMinsInDouble(otTime) : 0.0d;
															/* if(((String)alDate.get(i)).equals("24-Feb-2021")) {
																System.out.println("otTime =====>> " + otTime + " --- minOT =====>> " + minOT);
															} */
															if(otTime>=minOT) {
																double dblOTInMinute = uF.convertHoursIntoMinutes1(otTime);
																int nRoundOffPolicy = uF.parseToInt(hmOvertime.get("ROUND_OFF_OVERTIME"));
																double dblCalTime = otTime;
																/* if(((String)alDate.get(i)).equals("24-Feb-2021")) {
																	System.out.println("nRoundOffPolicy =====>> " + nRoundOffPolicy);
																} */
																if(nRoundOffPolicy > 0) {
																	double dblCalRoundOff = dblOTInMinute / nRoundOffPolicy;
																	String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblCalRoundOff);
																	//System.out.println((String)alDate.get(i)+"--1--dblOTInMinute=====>"+dblOTInMinute+"--nRoundOffPolicy==>"+nRoundOffPolicy+"--dblCalRoundOff=====>"+dblCalRoundOff+"--strTotal==>"+strTotal);
																	
																	if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
																		String str11 = strTotal.replace(".", ":");
																		String[] tempTotal = str11.split(":");
																		double dblHr = uF.parseToDouble(tempTotal[1]);
																		if(dblHr > 0) {
																			double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
																			double dblHour = uF.convertMinutesIntoHours(dblMain);
																			strTotal = ""+dblHour;
																			otHrs=strTotal;
																		} else {
																			double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
																			double dblHour = uF.convertMinutesIntoHours(dblMain);
																			strTotal = ""+dblHour;
																			otHrs=strTotal;
																		}
																	} else {
																		otHrs=""+otTime;		
																	}
																} else {
																	otHrs=""+otTime;
																}	
															}
															/* if(((String)alDate.get(i)).equals("24-Feb-2021")) {
																System.out.println("jsp ------------------- "+(String)alDate.get(i)+"--2--otHrs=====>"+otHrs);
															} */
														}
													}
												//}
											}else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("F")){
												//System.out.println("====>>> 3 ");
												/* if(hmOvertime.get("CALCULATION_BASIS")!=null && hmOvertime.get("CALCULATION_BASIS").equals("M")){
													double ottime=uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
													ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
													double dblOTInMinute = uF.convertHoursIntoMinutes1(ottime);										
													List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmOvertime.get("OVERTIME_ID"));
													if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
													int nRoundOffMinute = 0;
													int nAlOtMinuteSize = alOtMinute !=null ? alOtMinute.size() : 0;
													for(int x = 0; x < nAlOtMinuteSize; x++){
														Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
														if(uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblOTInMinute) 
																&& ((int) dblOTInMinute) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))){
															nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
															break;
														}
													}
													
													if(nRoundOffMinute > 0){
														double dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
														String strTotal = ""+dblHour;
														otHrs = ""+uF.parseToDouble(strTotal);
													} else {
														otHrs = ""+0.0d;
													}							
												} else {	 */
													double bufferTime=uF.parseToDouble(hmOvertime.get("FIXED_STWKG_HRS"))+uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
													double ottime=uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
													ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
													if(ottime>=bufferTime){
														double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
														double otTime=(ottime-bufferTime);
														//double otTime = (ottime-bufferTime) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(bufferTime),uF.formatIntoTwoDecimal(ottime))): 0.0d;
														//otTime = otTime > 0.0d ? uF.convertHoursMinsInDouble(otTime) : 0.0d;
														if(otTime>=minOT){
															double dblOTInMinute = uF.convertHoursIntoMinutes1(otTime);
															int nRoundOffPolicy = uF.parseToInt(hmOvertime.get("ROUND_OFF_OVERTIME"));
															double dblCalTime = otTime;
															if(nRoundOffPolicy > 0){
																double dblCalRoundOff = dblOTInMinute / nRoundOffPolicy;
																String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblCalRoundOff);
																//System.out.println((String)alDate.get(i)+"--1--dblOTInMinute=====>"+dblOTInMinute+"--nRoundOffPolicy==>"+nRoundOffPolicy+"--dblCalRoundOff=====>"+dblCalRoundOff+"--strTotal==>"+strTotal);
																
																if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
																	String str11 = strTotal.replace(".", ":");
																	String[] tempTotal = str11.split(":");
																	double dblHr = uF.parseToDouble(tempTotal[1]);
																	if(dblHr > 0){
																		double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
																		double dblHour = uF.convertMinutesIntoHours(dblMain);
																		strTotal = ""+dblHour;
																		otHrs=strTotal;
																	} else {
																		double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
																		double dblHour = uF.convertMinutesIntoHours(dblMain);
																		strTotal = ""+dblHour;
																		otHrs=strTotal;
																	}
																} else {
																	otHrs=""+otTime;		
																}
															} else {
																otHrs=""+otTime;
															}	
														}
														//System.out.println((String)alDate.get(i)+"--3--otHrs=====>"+otHrs);
													}	
												//}
											}
									    }
											if(hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"))!=null && !hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId")).equals("0.00")){
												otHrs=(String)hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"));
												//System.out.println((String)alDate.get(i)+"--4--otHrs=====>"+otHrs);
											}else if(hmActualOT.get((String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i))!=null){
												otHrs=hmActualOT.get((String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i));
												//System.out.println((String)alDate.get(i)+"--5--otHrs=====>"+otHrs);
											}
											
										%>
										<%-- <input id="ot_<%=(String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i)%>" type="text" name="ot_<%=(String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i)%>" value="<%=(dblHrsAtten - dblHrsRoster)>0?uF.formatIntoTwoDecimal(dblHrsAtten - dblHrsRoster):"0" %>" style="width:50px"/> --%>
										<%if(hmCheckPayroll!=null && hmCheckPayroll.containsKey((String)request.getAttribute("strSelectedEmpId"))){ 
											//System.out.println((String)alDate.get(i)+"--6--otHrs=====>"+otHrs);
										%>
											<%=uF.showTime(uF.showData(otHrs,"0.00")) %>
										<%} else if(hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"))!=null && !hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId")).equals("0.00")){
											otHrs=(String)hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"));
											//System.out.println((String)alDate.get(i)+"--7--otHrs=====>"+otHrs);
										%>
											<%=uF.showTime(uF.showData(otHrs,"0.00")) %>
										<%}else{ 
											//System.out.println((String)alDate.get(i)+"--8--otHrs=====>"+otHrs);
										%>
											<%=uF.showTime(uF.formatIntoTwoDecimal(uF.parseToDouble(otHrs))) %>
										<%} %>
										<%
										
										double dblOT = uF.parseToDouble(otHrs) > 0.0d ? uF.parseToDouble(otHrs): 0.0d;
										dblTotalOTHrs += dblOT;
										dblTotalOTHrs = dblTotalOTHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalOTHrs) : 0.0d;
										 %>
									<%
									
								} %>
									
								</td>
								
							</tr>
					        <%
					            }
					            }
					            
					            %>
					        <tr>
								<td colspan="6" class="alignRight padRight20"><strong>Total</strong></td>
								<td class="alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalRosterHrs))%></strong></td>
								<td class="alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalActualHrs))%></strong></td>
								<%if(CF.getIsShowTimeVariance()){ %>
									<td class="alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalVarianceHrs))%></strong></td>
								<%} %>
								<td class="alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalOTHrs))%></strong></td>
							</tr>
					    </table>
					    <%}else{ %>
					    <div class="filter" style="clear: both;">
					        <div class="msg nodata">
					            <span>
					            No Clock Entries present.
					            </span>
					        </div>
					    </div>
					    <%}%>
					    <div class="custom-legends">
					    	 <%if(strWeekOffcolor!=null){ %>
							    <div class="custom-legend" style="border-color: <%=strWeekOffcolor%>">
								    <div class="legend-info">Weekly Off</div>
								  </div>
						    <%}%>
						   <%
					        Set set = hmHolidayDates.keySet();
					        Iterator it = set.iterator();
					        while(it.hasNext()){
					        	String str = (String)it.next();
					        	if(str!=null && str.indexOf("_"+strWLocationId)>0){
					        	%>
					    	<div class="custom-legend" style="border-color: <%=hmHolidayDates.get(str)%>">
						    	<div class="legend-info"><%=uF.showData((String)hmHolidaysName.get(uF.getDateFormat(str, CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)), "")%></div>
						  	</div>
					    <%
					        }
					        }
					        %>
						  <br/>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info">
							    <div class="leftearly" style="float:left;width:25px;">&nbsp;</div>
						        Exception Denied
						    </div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info">
						    	<div class="wentontime" style="float:left;width:25px;">&nbsp;</div>
					        	Exception Pending for Approval
						    </div>
						  </div>
						  <div class="custom-legend no-borderleft-for-legend">
						    <div class="legend-info">
						    	<div class="worklate" style="float:left;width:25px;">&nbsp;</div>
					        	Exception Approved
						    </div>
						  </div>
						</div>
                </div>
                <!-- /.box-body -->


<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>