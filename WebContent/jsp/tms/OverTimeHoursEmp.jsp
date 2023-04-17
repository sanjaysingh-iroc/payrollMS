<%@page import="java.sql.Time"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">

function checkUncheckValue() { 
	var allOt=document.getElementById("allOt");		
	var strOt = document.getElementsByName('ot');

	if(allOt.checked==true){
		 for(var i=0;i<strOt.length;i++){
			 strOt[i].checked = true;			  
		 }
	}else{		
		 for(var i=0;i<strOt.length;i++){
			 strOt[i].checked = false;			 
		 }		 
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

%>

<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
	String strEmpID = (String) request.getParameter("EMPID");
	String strReqEmpID = (String) request.getParameter("EMPID");
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
	
	session.setAttribute("allDate",alDate);
	
	List alDay = (List) request.getAttribute("alDay");
	
	Map hmHours = (HashMap) request.getAttribute("hmHours");
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
	
	Map<String,Map<String,String>> hmOvertimeType=(Map<String,Map<String,String>>)request.getAttribute("hmOvertimeType");
	if(hmOvertimeType==null)hmOvertimeType=new HashMap<String,Map<String,String>>();
	
	String locationstarttime=(String)request.getAttribute("locationstarttime");
	String locationendtime=(String)request.getAttribute("locationendtime");
	
	Map<String,String> hmActualOT=(Map<String,String>)request.getAttribute("hmActualOT");
	if(hmActualOT==null) hmActualOT=new HashMap<String, String>();
	

	Map<String, String> hmApproveOT=(Map<String, String>)request.getAttribute("hmApproveOT");
	Map<String, String> hmCheckPayroll =(Map<String, String>)request.getAttribute("hmCheckPayroll");
	
	String userlocation=(String)request.getAttribute("userlocation");
	
%>



<%
	dblPayAmount = 0.0d;
%>


<%String strTitle = ((strUserType != null && (strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) || strUserType.equalsIgnoreCase(IConstants.ARTICLE) || strUserType.equalsIgnoreCase(IConstants.CONSULTANT)) ? "My Clock Entries for pay cycle "+uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) : "Clock Entries of "+(String) hmEmpData.get("NAME") + " for pay cycle "+uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"-"+uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()))); %>	   
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Over Time Request" name="title"/>
</jsp:include>		
 


    <div class="leftbox reportWidth">
    
    
    <div class="filter_div">
	   <s:form theme="simple" method="post" name="frm_roster_actual" action="OverTimeHoursEmp">
		
			<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId"
					listValue="paycycleName" headerKey="0" cssStyle="width:300px"
					onchange="document.frm_roster_actual.submit();"
					list="payCycleList" key="" />
			<%-- 
			<%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) && !strUserType.equalsIgnoreCase(IConstants.ARTICLE) && !strUserType.equalsIgnoreCase(IConstants.CONSULTANT)){ %>
			
			<s:select label="Select Employee" name="strEmpId" listKey="employeeId"
					listValue="employeeName" headerKey="0"
					onchange="document.frm_roster_actual.submit();"
					list="empList" key="" />
			<%} %> --%>
			
		</s:form>
	</div>

	<s:form theme="simple" method="post" name="frm_actual_overtime" action="OverTimeHoursEmp">
	<s:hidden name="strSelectedEmpId"></s:hidden>
	<s:hidden name="paycycle"></s:hidden>
		<table cellpadding="2" cellspacing="1" align="left" width="100%">
		
		<% if(hmDateServices.size()!=0){ %>
		
			<%
			StringBuilder str=new StringBuilder();
			double dblTotalActualHrs = 0;
			double dblTotalRosterHrs = 0;
			double dblTotalVarianceHrs = 0;
			double dblTotalOTHrs = 0;
			
				for (int i = 0; i < alDate.size(); i++) {
					
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
			
				
				if(i==0 && ii==0){
					%>
					<tr>
						<td width="15%" class="reportHeading alignCenter">Date</td>
						<td width="10%" class="reportHeading alignCenter">SBU/Cost-center</td>
						<td width="10%" class="reportHeading alignCenter">Roster/Standard Start Time<br/>(HH:mm)</td>
						<td width="10%" class="reportHeading alignCenter">Actual Start Time<br/>(HH:mm)</td>
						<td width="10%" class="reportHeading alignCenter">Roster/Standard End Time<br/>(HH:mm)</td>
						<td width="10%" class="reportHeading alignCenter">Actual End Time<br/>(HH:mm)</td>
						<td width="10%" class="reportHeading alignCenter">Roster Sumary<br/>(hrs)</td> 
						<td width="10%" class="reportHeading alignCenter">Day Sumary<br/>(hrs)</td>
						<!-- <td width="10%" class="reportHeading alignCenter">Variance<br/>(hrs)</td> -->
						<td width="5%" class="reportHeading alignCenter">OT<br/>(hrs)
						<br/><input onclick="checkUncheckValue();" type="checkbox" name="allOt" id="allOt" checked></td>
					</tr>
					<%
				}
				
				String strBgColor = (String) _hmHolidaysColour.get(i + "");
				
				
				if(strBgColor==null){
//				String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), "EEEE");
					String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
					if(strDay!=null)strDay=strDay.toUpperCase();
						//strBgColor = (String) hmWeekEndList.get(strDay+"_"+strWLocationId);
					if(alEmpCheckRosterWeektype.contains(strReqEmpID)){
						if(rosterWeeklyOffSet.contains(strDay)){
							strBgColor = IConstants.WEEKLYOFF_COLOR;
						}
					}else if(weeklyOffSet.contains(strDay)){
						strBgColor = IConstants.WEEKLYOFF_COLOR;
					}
				}
				if(strBgColor==null){
					strBgColor = (String) hmLeavesColour.get((String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)));
				}
			%>
			
			<tr>
				<td class="reportLabel<%=str.toString() %> alignLeft"
					style="background-color:<%=strBgColor%>" >
					<%=(String) alDay.get(i)%>, <%=(String) alDate.get(i)%>
				</td>
				<td class="reportLabel<%=str.toString() %> alignLeft"
					style="background-color:<%=strBgColor%>" >
					<%=(((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null)?(String) hmServices.get((String)alDateServices.get(ii)):"-")%>
				</td>
					
				<td class="reportLabel<%=str.toString() %> alignCenter"
					style="background-color:<%=strBgColor%>" >
					<%=showData((String) hmRosterStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")%>
				</td>
				<td class="reportLabel<%=str.toString() %> alignCenter"
					style="background-color:<%=strBgColor%>" >
					<%=showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")%>
				</td> 
				<td class="reportLabel<%=str.toString() %> alignCenter"
					style="background-color:<%=strBgColor%>" >
					<%=showData((String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")%>
				</td>
				<td class="reportLabel<%=str.toString() %> alignCenter"
					style="background-color:<%=strBgColor%>" >
					<%=showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")%>
				</td>
				
				<td class="reportLabel<%=str.toString() %> alignRight padRight20"
					style="background-color:<%=strBgColor%>" >
					<%=showData((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "0")%>
					<%dblHrsRoster = uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))); %>
					<%dblTotalRosterHrs+=dblHrsRoster; %>
				</td>
				
				<td class="reportLabel<%=str.toString() %> alignRight padRight20"
					style="background-color:<%=strBgColor%>" >
					<%=showData((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "0")%>
					<%dblHrsAtten = uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))); %>
					<%dblTotalActualHrs+=dblHrsAtten; %>
				</td>
				
				<%-- <td class="reportLabel<%=str.toString() %> alignRight padRight20"
					style="background-color:<%=strBgColor%>" >
					<%=uF.formatIntoOneDecimal(dblHrsAtten - dblHrsRoster) %>
					<%dblTotalVarianceHrs+=(dblHrsAtten - dblHrsRoster); %>
				</td> --%>
				
				<td class="reportLabel<%=str.toString() %> alignLeft "
					style="background-color:<%=strBgColor%>" >
					<%-- <%=(dblHrsAtten - dblHrsRoster)>0?uF.formatIntoTwoDecimal(dblHrsAtten - dblHrsRoster):"0" %> --%>					
					<%
					if(((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null) && ((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null)){
					    String otHrs="0.00";
					    Map<String,String> hmOvertime=null;
					    String day=uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),"dd-MMM-yyyy"),IConstants.DBDATE,IConstants.DATE_FORMAT);
					    //System.out.println("day=====>"+day);
					    if(hmHolidayDates!=null && hmHolidayDates.containsKey((String)alDate.get(i)+"_"+userlocation)){
					    	//System.out.println("IF=====>");
					    	hmOvertime=hmOvertimeType.get("PH");
					    }else if(hmWeekEndList!=null && hmWeekEndList.containsKey(day+"_"+userlocation)){
					    	//System.out.println("IF else=====>");
					    	hmOvertime=hmOvertimeType.get("BH");
					    }else{
					    	//System.out.println("else=====>");
					    	hmOvertime=hmOvertimeType.get("EH");
					    }
					    
					    if(hmOvertime==null) hmOvertime=new HashMap<String,String>();
					    //System.out.println("hmOvertime=====>"+hmOvertime);
						if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("RH")){
							Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),"dd-MMM-yyyy "+IConstants.DBTIME);
							Time rosterEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),"dd-MMM-yyyy "+IConstants.DBTIME);
							Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),"dd-MMM-yyyy "+IConstants.DBTIME);
											
							long milliseconds1 = entryTime.getTime();
							long milliseconds2 = rosterEndTime.getTime();
							long milliseconds3 = endTime.getTime();
								
							if(milliseconds3>=milliseconds2){
								double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
								//dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
								double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
								//actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
								double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
								//double ottime=actualTime-dbl;
								double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
								ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
								if(ottime>=bufferTime){
									double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
									double otTime=(ottime-bufferTime);
									//double otTime = (ottime-bufferTime) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(bufferTime),uF.formatIntoTwoDecimal(ottime))): 0.0d;
									//otTime = otTime > 0.0d ? uF.convertHoursMinsInDouble(otTime) : 0.0d;
									if(otTime>=minOT){
										otHrs=""+otTime;
									}
									System.out.println((String)alDate.get(i)+"--1--otHrs=====>"+otHrs+"--dbl==>"+dbl
											+"--entryTime=====>"+entryTime+"--rosterEndTime==>"+rosterEndTime+"--endTime==>"+endTime
											+"--milliseconds1=====>"+milliseconds1+"--milliseconds2==>"+milliseconds2+"--milliseconds3==>"+milliseconds3
											+"--actualTime==>"+actualTime+"--bufferTime==>"+bufferTime+"--actualTime==>"+
											ottime+"--minOT==>"+minOT+"--ottime==>"+ottime+"--otTime==>"+otTime);
								}
							}
						}else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("SWH")){
							Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),"dd-MMM-yyyy "+IConstants.DBTIME);
							Time wlocationEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+locationendtime,"dd-MMM-yyyy "+IConstants.DBTIME);
							Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),"dd-MMM-yyyy "+IConstants.DBTIME);
							
							long milliseconds1 = entryTime.getTime();
							long milliseconds2 = wlocationEndTime.getTime();
							long milliseconds3 = endTime.getTime();
								
							if(milliseconds3>=milliseconds2){
								double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
								dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
								double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
								actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
								double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
								//double ottime=actualTime-dbl;
								double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
								ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
								if(ottime>=bufferTime){
									//otHrs=""+(ottime-bufferTime);
									double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
									//double otTime=(ottime-bufferTime);
									double otTime = (ottime-bufferTime) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(bufferTime),uF.formatIntoTwoDecimal(ottime))): 0.0d;
									otTime = otTime > 0.0d ? uF.convertHoursMinsInDouble(otTime) : 0.0d;
									if(otTime>=minOT){
										otHrs=""+otTime;
									}
									System.out.println((String)alDate.get(i)+"--2--otHrs=====>"+otHrs);
								}
							}
						}else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("F")){
								double bufferTime=uF.parseToDouble(hmOvertime.get("FIXED_STWKG_HRS"))+uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
								double ottime=uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
								ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
								if(ottime>=bufferTime){
									double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
									//double otTime=(ottime-bufferTime);
									double otTime = (ottime-bufferTime) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(bufferTime),uF.formatIntoTwoDecimal(ottime))): 0.0d;
									otTime = otTime > 0.0d ? uF.convertHoursMinsInDouble(otTime) : 0.0d;
									if(otTime>=minOT){
										otHrs=""+otTime;
									}
									System.out.println((String)alDate.get(i)+"--3--otHrs=====>"+otHrs);
								}							
						}
						if(hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"))!=null && !hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId")).equals("0.00")){
							otHrs=(String)hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"));
							System.out.println((String)alDate.get(i)+"--4--otHrs=====>"+otHrs);
						}else if(hmActualOT.get((String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i))!=null){
							otHrs=hmActualOT.get((String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i));
							System.out.println((String)alDate.get(i)+"--5--otHrs=====>"+otHrs);
						}
						
					%>
					<%-- <input id="ot_<%=(String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i)%>" type="text" name="ot_<%=(String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i)%>" value="<%=(dblHrsAtten - dblHrsRoster)>0?uF.formatIntoTwoDecimal(dblHrsAtten - dblHrsRoster):"0" %>" style="width:50px"/> --%>
					<%if(hmCheckPayroll!=null && hmCheckPayroll.containsKey((String)request.getAttribute("strSelectedEmpId"))){ 
						System.out.println((String)alDate.get(i)+"--6--otHrs=====>"+otHrs);
					%>
						<%=uF.showData(otHrs,"0.00") %>
					<%}if(hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"))!=null && !hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId")).equals("0.00")){
						otHrs=(String)hmApproveOT.get((String)alDate.get(i)+"_"+(String)request.getAttribute("strSelectedEmpId"));
						System.out.println((String)alDate.get(i)+"--7--otHrs=====>"+otHrs);
					%>
						<%=uF.showData(otHrs,"0.00") %>
					<%}else{ 
						System.out.println((String)alDate.get(i)+"--8--otHrs=====>"+otHrs);
					%>
					<input id="ot_<%=(String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i)%>" type="text" name="ot_<%=(String)request.getAttribute("strSelectedEmpId")+"_"+(String)alDate.get(i)%>" value="<%=uF.formatIntoTwoDecimal(uF.parseToDouble(otHrs)) %>" style="width:31px"/>
					<input type="checkbox" name="ot" value="<%=(String)alDate.get(i) %>" checked/>
					<%} %>
					<%dblTotalOTHrs+=(((dblHrsAtten - dblHrsRoster)>0)?(dblHrsAtten - dblHrsRoster):0); %>
					<%} %> 
				</td>
				
			</tr>
			<%
				}
			}
			%>
		<%-- <tr>
			<td colspan="6" class="reportLabel alignRight padRight20"><strong>Total</strong></td>
			<td class="reportLabel alignRight padRight20"><strong><%=uF.formatIntoOneDecimal(dblTotalRosterHrs)%></strong></td>
			<td class="reportLabel alignRight padRight20"><strong><%=uF.roundOffInTimeInHoursMins(dblTotalActualHrs)%></strong></td>
			<td class="reportLabel alignRight padRight20"><strong><%=uF.roundOffInTimeInHoursMins(dblTotalVarianceHrs)%></strong></td>
			<td class="reportLabel alignRight padRight20"><strong><%=uF.roundOffInTimeInHoursMins(dblTotalOTHrs)%></strong></td>
		</tr> --%>
		<tr>
			<td colspan="10" class="reportLabel alignRight padRight20">
				<s:submit cssClass="input_button" value="Submit" name="submit"></s:submit>
			</td>			
		</tr>
		
		
			<%}else{ %>
			<tr>
				<td class="msg nodata"><span>No Clock Entries present.</span></td>
			</tr>
			<%}%>
		
		</table>
		
		</s:form>
	<%-- 	
<%if(hmWeekEndList!=null && hmWeekEndList.containsKey(IConstants.SUNDAY+"_"+strWLocationId)){ %>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmWeekEndList.get(IConstants.SUNDAY+"_"+strWLocationId)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<%}else if(hmWeekEndList!=null && hmWeekEndList.containsKey(IConstants.MONDAY+"_"+strWLocationId)){ %>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmWeekEndList.get(IConstants.MONDAY+"_"+strWLocationId)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<%}else if(hmWeekEndList!=null && hmWeekEndList.containsKey(IConstants.TUESDAY+"_"+strWLocationId)){ %>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmWeekEndList.get(IConstants.TUESDAY+"_"+strWLocationId)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<%}else if(hmWeekEndList!=null && hmWeekEndList.containsKey(IConstants.WEDNESDAY+"_"+strWLocationId)){ %>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmWeekEndList.get(IConstants.WEDNESDAY+"_"+strWLocationId)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<%}else if(hmWeekEndList!=null && hmWeekEndList.containsKey(IConstants.THURSDAY+"_"+strWLocationId)){ %>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmWeekEndList.get(IConstants.THURSDAY+"_"+strWLocationId)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<%}else if(hmWeekEndList!=null && hmWeekEndList.containsKey(IConstants.FRIDAY+"_"+strWLocationId)){ %>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmWeekEndList.get(IConstants.FRIDAY+"_"+strWLocationId)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<%}else if(hmWeekEndList!=null && hmWeekEndList.containsKey(IConstants.SATURDAY+"_"+strWLocationId)){ %>
<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmWeekEndList.get(IConstants.SATURDAY+"_"+strWLocationId)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div>Weekly Off</div>
<%}%>



<%

Set set = hmHolidayDates.keySet();
Iterator it = set.iterator();
while(it.hasNext()){
	String str = (String)it.next();
	%>
	<div style="margin-top:10px;float:left;width:100%">    <div style="background-color:<%=hmHolidayDates.get(str)%>;width:20px;float:left;text-align:center;margin-right:5px;">&nbsp;</div><%=uF.showData((String)hmHolidaysName.get(uF.getDateFormat(str, CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)), "")%></div>
	<%
}
%>
	 --%>	
		
 	</div>    

