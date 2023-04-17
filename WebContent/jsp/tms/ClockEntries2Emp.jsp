<%@page import="java.sql.Time"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" charset="utf-8">

<%	UtilityFunctions uF = new UtilityFunctions(); 
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	
	double dblTotalOTHrs1=uF.parseToDouble((String)request.getAttribute("dblTotalOTHrs1"));
	double dblTotalActualHrs1= uF.parseToDouble((String)request.getAttribute("dblTotalActualHrs"));
	double dblTotalRosterHrsexcludingWeekOf=uF.parseToDouble((String)request.getAttribute("dblTotalRosterHrsexcludingWeekOf"));

	double needToattenHrs=dblTotalRosterHrsexcludingWeekOf-dblTotalActualHrs1;

	if((dblTotalRosterHrsexcludingWeekOf-dblTotalActualHrs1)<0){
		needToattenHrs=0;
	}
%>

var ITDeclaration=[];

ITDeclaration.push({"name": "Actual worked Hrs", "count":'<%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalActualHrs1)%>' ,"color":"#53ce40" });
ITDeclaration.push({"name": "Absent Hrs", "count":'<%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),needToattenHrs)%>' ,"color":"#ffa500" }); 

$(document).ready(function(){
var chart = AmCharts.makeChart("containerForchart1", {
  "type": "pie",
  "startDuration": 0,
   "theme": "",
  "addClassNames": true,
  "legend":{
   	"position":"right",
    "marginRight":100,
    "autoMargins":false
  },
  "labelsEnabled": false,
  "innerRadius": "",
  "defs": {
    "filter": [{
      "id": "shadow",
      "width": "250%",
      "height": "250%",
      "feOffset": {
        "result": "offOut",
        "in": "SourceAlpha",
        "dx": 0,
        "dy": 0
      },
      "feGaussianBlur": {
        "result": "blurOut",
        "in": "offOut",
        "stdDeviation": 5
      },
      "feBlend": {
        "in": "SourceGraphic",
        "in2": "blurOut",
        "mode": "normal"
      }
    }]
  },
  "dataProvider": ITDeclaration,
  "valueField": "count",
  "titleField": "name",
  "colorField": "color",
  "export": {
    "enabled": true
  }
});
});
</script>


<script type="text/javascript" charset="utf-8">
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

	function viewReason(mode, emp_id, service_id, _date) {
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
		var divResult = 'divResult';
		var paycycle = document.getElementById("paycycle").value;
		//alert("service ===>> " + service);
		$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ClockEntries.action?paycycle='+paycycle,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#"+divResult).html(result);
	   		}
		});
	}
	
</script>

<%!

	double dblPayAmount = 0.0;
	double dblTotalPayAmount = 0.0;

	String showData(String strData, String strVal) {
		if (strData == null)
			return strVal;
		else
			return strData;
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
int weekoffHolidayCount=0;
int weekoffCount=0;
double ActualAtenWorkHrs=0;

	dblPayAmount = 0.0;
	dblTotalPayAmount = 0.0;

	//CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
	String strEmpID = (String) request.getParameter("EMPID");
	//String strReqEmpID = (String) request.getParameter("EMPID");
	String strReqEmpID = (String) session.getAttribute(IConstants.EMPID);
	if (strEmpID != null) {
		strEmpID = "&EMPID=" + strEmpID;
	} else {
		strEmpID = "";
	}

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

	Map hmRosterStart = (HashMap) request.getAttribute("hmRosterStart");
	Map hmRosterEnd = (HashMap) request.getAttribute("hmRosterEnd");
	Map hmRosterHours = (HashMap) request.getAttribute("hmRosterHours");

	Map hmDailyRate = (HashMap) request.getAttribute("hmDailyRate");
	Map hmHoursRates = (HashMap) request.getAttribute("hmHoursRates");
	Map hmServicesWorkedFor = (HashMap) request.getAttribute("hmServicesWorkedFor");
	Map hmDateServices = (HashMap) request.getAttribute("hmDateServices_TS");
	
	if(hmDateServices==null)hmDateServices = new HashMap();
	
//	out.println("<br/>hmRosterStart="+hmRosterStart);
//	out.println("<br/>hmRosterEnd="+hmRosterEnd);
//	out.println("<br/>hmStart="+hmStart);
//	out.println("<br/>hmEnd="+hmEnd);
//	out.println("<br/>hmDateServices="+hmDateServices);
//	out.println("<br/>hmRosterHoursEmp="+hmRosterHours);	
	
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

	//Map hmWeekEndList = (HashMap) request.getAttribute("hmWeekEndList");
	//String strWLocationId = (String)request.getAttribute("strWLocationId");
	
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
	
	Map hmHolidayDates = (Map) request.getAttribute("hmHolidayDates");
	
	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
	Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");
	Map hmServices = (Map) request.getAttribute("hmServices");
	
	Map hmLeavesMap = (Map) request.getAttribute("hmLeaves");
	if(hmLeavesMap==null)hmLeavesMap=new HashMap();

	Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
	if(hmLeavesColour==null)hmLeavesColour=new HashMap();


	List _alHolidays = (List) request.getAttribute("_alHolidays");

	Map hmEmpData = (Map) request.getAttribute("hmEmpData");
	
	Map hmActualStart = (HashMap) request.getAttribute("hmActualStartClockEntries");
	Map hmActualEnd = (HashMap) request.getAttribute("hmActualEndClockEntries");
	Map hmExceptions = (Map) request.getAttribute("hmExceptions");
	

	if (hmEmpData == null) {
		hmEmpData = new HashMap();
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
	if (hmEmpData == null) {
		hmEmpData = new HashMap();
	}
	if (hmRosterHours == null) {
		hmRosterHours = new HashMap();
	}
	if (_hmHolidaysColour == null) {
		_hmHolidaysColour = new HashMap();
	}
	
	if (hmActualStart == null) {
		hmActualStart = new HashMap();
	}
	if (hmActualEnd == null) {
		hmActualEnd = new HashMap();
	}
	
	if (hmExceptions == null) {
		hmExceptions = new HashMap();
	}
	
	String paycycle = (String) request.getAttribute("paycycle");
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
	
	Map<String, String> hmShiftBreak = (Map<String, String>) request.getAttribute("hmShiftBreak");
	if(hmShiftBreak == null) hmShiftBreak = new HashMap<String, String>();
	
	Map<String, String> hmRosterShiftId = (Map<String, String>) request.getAttribute("hmRosterShiftId");
	if(hmRosterShiftId == null) hmRosterShiftId = new HashMap<String, String>();
	
	String strDefaultLunchDeduction = (String) request.getAttribute("strDefaultLunchDeduction");
		
	Map<String,Map<String,String>> hmOvertimeType=(Map<String,Map<String,String>>)request.getAttribute("hmOvertimeType");
	if(hmOvertimeType==null)hmOvertimeType=new HashMap<String,Map<String,String>>();
	
	String locationstarttime=(String)request.getAttribute("locationstarttime");
	String locationendtime=(String)request.getAttribute("locationendtime");
	
	Map<String,String> hmActualOT=(Map<String,String>)request.getAttribute("hmActualOT");
	if(hmActualOT==null) hmActualOT=new HashMap<String, String>();
	

	Map<String, String> hmApproveOT=(Map<String, String>)request.getAttribute("hmApproveOT");
	Map<String, String> hmCheckPayroll =(Map<String, String>)request.getAttribute("hmCheckPayroll");
	
	String userlocation=(String)request.getAttribute("userlocation");

	dblPayAmount = 0.0d;
	
	Map<String, List<Map<String,String>>> hmOvertimeMinuteSlab = (Map<String, List<Map<String,String>>>)request.getAttribute("hmOvertimeMinuteSlab");
	if(hmOvertimeMinuteSlab == null) hmOvertimeMinuteSlab = new HashMap<String, List<Map<String,String>>>();
	
	Map<String,String> hmCalculateOT =(Map<String,String>)request.getAttribute("hmCalculateOT");
	if(hmCalculateOT == null) hmCalculateOT = new HashMap<String, String>();
	
	List<String>alweekoffDayList=new ArrayList<String>();
%>


<%String strTitle = ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.ARTICLE) || strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) ? "My Clock Entries for pay cycle "+uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) : "Clock Entries of "+(String) hmEmpData.get("NAME") + " for pay cycle "+uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"-"+uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()))); %>	   

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle%>" name="title"/>
</jsp:include> --%> 		
 
             <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
				<div class="box box-default">  <!-- collapsed-box -->
					<%-- <div class="box-header with-border">
					    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
					    <div class="box-tools pull-right">
					        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					    </div>
					</div> --%>
					<div class="box-body" style="padding: 5px; overflow-y: auto;">
						<s:form theme="simple" method="post" name="frm_roster_actual" action="ClockEntries">
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0" onchange="submitForm('2');" list="payCycleList" key=""/>
									</div>
									<% if(strUserType!=null &&!strUserType.equals(IConstants.EMPLOYEE)) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Employee</p>
											<s:select label="Select Employee" name="strEmpId" listKey="employeeId" listValue="employeeName" headerKey="0" onchange="submitForm('2');" list="empList" key="" />
										</div>
									<% } %>
								</div>
							</div>
						</s:form>
					</div>
				</div>
		
				<section  class="col-lg-8 col-md-8 connectedSortable paddingright5">
					<div class="box box-info">
			            <div class="box-header with-border">
			                <h3 class="box-title"></h3>
			                <div class="box-tools pull-right">
			                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                </div>
			            </div>
			            <!-- /.box-header -->
			            <div class="box-body" style="padding: 5px; height: 230px; overflow-y: auto;">
							<div id="containerForchart1" style="height: 220px; width:100%;">&nbsp;</div>
			            </div>
			            <!-- /.box-body -->
			        </div>
				</section>
				
				<section  class="col-lg-4 col-md-4 connectedSortable paddingright5">
					<div class="box box-info">
			            <div class="box-header with-border">
			                <h3 class="box-title"></h3>
			                <div class="box-tools pull-right">
			                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
			                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			                </div>
			            </div>
			            <!-- /.box-header -->
			            <div class="box-body" style="padding: 5px; max-height: 230px; min-height: 230px; overflow-y: auto;">
			                <ul class="site-stats-new paddingtop0" style="margin-bottom: 0px;">
								<li class="bg_lh" style="cursor: unset;"><strong><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalActualHrs1) %></strong> <small>Actual Worked Hrs</small></li>
								<li class="bg_lh" style="cursor: unset;"><strong><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalOTHrs1) %></strong> <small>Overtime Hrs</small></li>
							</ul>
							
							<ul class="site-stats-new paddingtop0">
								<li class="bg_lh" style="cursor: unset;"><strong><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalRosterHrsexcludingWeekOf) %></strong> <small>Roster Hrs</small></li>
							</ul>
			                <%-- <div class="content1 " >
                  				<div class="bg_lh site-stats" style="width:100%; float:left;height: 200px">
                  					 <h5 class="box-title" style="padding-top: 50px">Roster Hrs: <strong><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalRosterHrsexcludingWeekOf)%></strong></h5>
									 <h5 class="box-title">Actual worked Hrs: <strong><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalActualHrs1)%></strong></h5>
									 <h5 class="box-title">Overtime Hrs: <strong><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalOTHrs1)%></strong></h5>
								</div>
							</div> --%>
						</div>
			            <!-- /.box-body -->
			       </div>
				</section>
				
				<div class="col-lg-12 col-md-12 col-sm-12">
					<%
					String strWeekOffcolor=null;
					
					if(hmDateServices.size()!=0) { %>
						<table cellpadding="2" cellspacing="1" align="left" width="100%" class="table table-bordered table-hover" style="table-layout: fixed;">
						<%
						StringBuilder str=new StringBuilder();
						double dblTotalActualHrs = 0;
						double dblTotalRosterHrs = 0;
						double dblTotalVarianceHrs = 0;
						double dblTotalOTHrs = 0;
						int k=0;
							
						for(int i = 0; i < alDate.size(); i++) {
								List alDateServices = (List)hmDateServices.get((String) alDate.get(i));
								if(alDateServices==null){alDateServices=new ArrayList();alDateServices.add("-1");}
					//			for (int ii = 0; ii < ((alDateServices.size()==0)?1:alDateServices.size()); ii++) {
						for (int ii = 0; ii < alDateServices.size(); ii++) {
								
							if(str.length()==0){
								str.append("1");
							}else{
								str=new StringBuilder();;
							}
									
							double dblHrsAtten = uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
							double dblHrsRoster = uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
							
							double dblHrsAttenActual = uF.parseToDouble((String) hmHoursActual.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
							
							//if(i==0 && ii==0){
							if(k==0){	
								%>
								<tr>
									<th class="reportHeading alignCenter">Date</th>
									<th class="reportHeading alignCenter">SBU/Cost-center</th>
									<th class="reportHeading alignCenter">Roster/Standard Start Time<br/>(HH:mm)</th>
									<th class="reportHeading alignCenter">Approved [Actual] Start Time<br/>(HH:mm)</th>
									<th class="reportHeading alignCenter">Roster/Standard End Time<br/>(HH:mm)</th>
									<th class="reportHeading alignCenter">Approved [Actual] End Time<br/>(HH:mm)</th>
									<th class="reportHeading alignCenter">Roster Summary<br/>(HH:mm)</th> 
									<th class="reportHeading alignCenter">Day Summary<br/>(HH:mm)</th>
									<%if(CF.getIsShowTimeVariance()){ %>
										<th class="reportHeading alignCenter">Variance<br/>(HH:mm)</th>
									<%} %>
									<th class="reportHeading alignCenter">OT<br/>(HH:mm)</th> 
								</tr>
								<%
							}
							k++;
							
							String strBgColor = (String) _hmHolidaysColour.get(i + "");
							strBgColor = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocationId);				
							if(strBgColor==null){
								String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
								if(strDay!=null)strDay=strDay.toUpperCase();
									if(alEmpCheckRosterWeektype.contains(strReqEmpID)){
										if(rosterWeeklyOffSet.contains(strDay)){
											alweekoffDayList.add(strDay);
											strBgColor = IConstants.WEEKLYOFF_COLOR;
											strWeekOffcolor = IConstants.WEEKLYOFF_COLOR;
										}
									}else if(weeklyOffSet.contains(strDay)){
										alweekoffDayList.add(strDay);
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
							
							
						%>
						
						<tr>
							<td class="alignLeft"style="background-color:<%=strBgColor%>" >
								<%=(String) alDay.get(i)%>, <%=(String) alDate.get(i)%>
							</td>
							<td class="alignLeft" style="background-color:<%=strBgColor%>" >
								<%=(((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null) ? uF.showData((String) hmServices.get((String)alDateServices.get(ii)),"-"):"-")%>
							</td>
					<!-- ===start parvez date: 05-08-2022=== -->			
							<td class="alignCenter" style="background-color:<%=strBgColor%>" >
								<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%> --%>
								
								<% if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")) { %>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : "")+(" ["+showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")):showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")) %>
								<% } else { %>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))? ((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")) : showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))  %>
								<% } %>
							</td>
							<td class="alignCenter" style="background-color:<%=strBgColor%>" >
								<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")%> --%>
								
								<%if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")){%>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : "")+(" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")):showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]") %>
								<%} else{ %>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")):showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+" ["+showData((String) hmActualStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]") %>
								<%}%>
								
								<%if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_IN"))==-1){ %>
								<a href="javascript:void(0);" title="Exception Denied" onclick="viewReason('IN', '<%=strReqEmpID %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="leftearly">&nbsp;</div></a>
								<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_IN"))==1){ %>
								<a href="javascript:void(0);" title="Exception Approved" onclick="viewReason('IN', '<%=strReqEmpID %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="worklate">&nbsp;</div></a>
								<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_IN"))==-2){ %>
								<a href="javascript:void(0);" title="Exception Pending for approval" onclick="viewReason('IN', '<%=strReqEmpID %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="wentontime">&nbsp;</div></a>
								<%} %>
							</td>  
							<td class="alignCenter" style="background-color:<%=strBgColor%>" >
								<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%> --%>
								<%if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")){ %>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : "")+(" ["+showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")):showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%>
								<%} else{ %>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")):showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")) %>
								<%} %>
							</td>
							<td class="alignCenter" style="background-color:<%=strBgColor%>" >
								<%-- <%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")%> --%>
								
								<%if(hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")){ %>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_L")+ (strTravel!=null ? " / "+strTravel : "")+(" ["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")):showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]") %>
								<%} else{ %>
									<%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T"))?((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_T")+(" ["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")):showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"["+showData((String) hmActualEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")+"]")  %>
								<%} %>
								
								<%if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_OUT"))==-1){ %>
								<a href="javascript:void(0);" title="Exception Denied" onclick="viewReason('OUT', '<%=strReqEmpID %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="leftearly">&nbsp;</div></a>
								<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_OUT"))==1){ %>
								<a href="javascript:void(0);" title="Exception Approved" onclick="viewReason('OUT', '<%=strReqEmpID %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="worklate">&nbsp;</div></a>
								<%} else if(uF.parseToInt((String)hmExceptions.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)+"_OUT"))==-2){ %>
								<a href="javascript:void(0);" title="Exception Pending for approval" onclick="viewReason('OUT', '<%=strReqEmpID %>', '<%=(String)alDateServices.get(ii)%>', '<%=(String) alDate.get(i)%>')"><div class="wentontime">&nbsp;</div></a>
								<%} %>
							</td>
					<!-- ===start parvez date: 05-08-2022=== -->		
							
							<td class="alignRight padRight20" style="background-color:<%=strBgColor%>" >
								<%=uF.showTime(showData((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "0"))%>
								
								<% ActualAtenWorkHrs=uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
								
									dblTotalRosterHrs+=uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
									dblTotalRosterHrs = dblTotalRosterHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalRosterHrs) : 0.0d;
									
								%>
							</td>
							
							<td class="alignRight padRight20" style="background-color:<%=strBgColor%>" >
								<%=uF.showTime(uF.formatIntoTwoDecimalWithOutComma(dblHrsAttenActual))%>
							
								<%
									dblTotalActualHrs+=dblHrsAttenActual; 
									dblTotalActualHrs = dblTotalActualHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalActualHrs) : 0.0d;
							
								%>
							</td>
							
							<%if(CF.getIsShowTimeVariance()){ %>
								<td class="alignRight padRight20" style="background-color:<%=strBgColor%>" >
									<%
										/* String strVariance = uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dblHrsRoster),uF.formatIntoTwoDecimal(dblHrsAttenActual));
										dblTotalVarianceHrs+= uF.parseToDouble(uF.formatIntoTwoDecimal(uF.parseToDouble(strVariance)));
										dblTotalVarianceHrs = uF.convertHoursMinsInDouble(dblTotalVarianceHrs); */
										
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
									<%=uF.showTime(strVariance)%>
								</td>
							<%} %>
							<td class="alignRight padRight20" style="background-color:<%=strBgColor%>" >
								
								<%
								if(((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null) && ((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null)){
								    String otHrs="0.00";
								    if(hmCalculateOT != null && hmCalculateOT.containsKey(strReqEmpID+"_"+uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT))){
								    	otHrs = uF.showData(hmCalculateOT.get(strReqEmpID+"_"+uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT)),"");
								    	//System.out.println("otHrs==>"+otHrs);
									} else {
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
				
									    if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("RH")){
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
													
												if(milliseconds3>=milliseconds2){
													double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
													dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
													double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
													actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
													double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
													double ottime=actualTime-dbl;
													//double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
													//ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
													if(ottime>=bufferTime){
														//otHrs=""+(ottime-bufferTime);
														double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
														//double otTime=(ottime-bufferTime);
														double otTime = (ottime-bufferTime) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(bufferTime),uF.formatIntoTwoDecimal(ottime))): 0.0d;
														otTime = otTime > 0.0d ? uF.convertHoursMinsInDouble(otTime) : 0.0d;
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
														//System.out.println((String)alDate.get(i)+"--2--otHrs=====>"+otHrs);
													}
												}
											//}
										}else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("F")){
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
											} else { */	
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
								
								//System.out.println("dblTotalOTHrs=="+dblTotalOTHrs);
								%>
								<%} %>
							</td>
							
						</tr>
				<%
					}
				}
				%>
				
				<tr>
					<td colspan="6" class="reportLabel alignRight padRight20"><strong>Total</strong></td>
					<td class="reportLabel alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalRosterHrs))%></strong></td>
					<td class="reportLabel alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalActualHrs))%></strong></td>
					<%if(CF.getIsShowTimeVariance()){ %>
						<td class="reportLabel alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalVarianceHrs))%></strong></td>
					<%} %>
					<td class="reportLabel alignRight padRight20"><strong><%=uF.showTime(uF.convertInHoursMins(dblTotalOTHrs))%></strong></td>
				
				</tr>
			
			</table>
		
				<% //System.out.println("ActualAtenWorkHrs"+ActualAtenWorkHrs);
					//weekoffHolidayCount=weekoffCount+holidayCount;
				%>
		
		
			<div class="paddingtop20">&nbsp;</div>
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
					String str1 = (String)it.next();
					if(str1!=null && str1.indexOf("_"+strWLocationId)>0){
		        	%>
		    	<div class="custom-legend" style="border-color: <%=hmHolidayDates.get(str1)%>">
			    	<div class="legend-info"><%=uF.showData((String)hmHolidaysName.get(uF.getDateFormat(str1, CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)), "")%></div>
			  	</div>
		    <%
		        }
		        }
		        %>
			</div>
			<% } else { %>
				<div class="filter"><div class="msg nodata"><span>No clock entries available.</span></div></div>
			<% } %>
			</div>
		</div>

	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
  
 	
