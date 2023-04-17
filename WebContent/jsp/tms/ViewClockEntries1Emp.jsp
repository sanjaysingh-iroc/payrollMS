<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<style>
#reasonSubmit {
	margin-top:10px;
}
</style>

<div id="divResult">

<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/highslide/highslide-with-html.js"> </script>
<link type="text/css" rel="stylesheet" href="<%=request.getContextPath()%>/css/highslide/highslide.css" />
<script type="text/javascript">
hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
	hs.outlineType = 'rounded-white';
	hs.wrapperClassName = 'draggable-header';

	$(function() {
		$("#selectDate").datepicker({
			format : 'dd/mm/yyyy'
		});
	});
	
	function submitForm(type){
		var form_data = $("#frm_roster_actual").serialize();
		
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'UpdateClockEntries.action',
			data: form_data,
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
	
	
	function addReason(inOutStatus, DATE, SID, EID) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Enter Reason');
		$("#modalInfo").show();
		$.ajax({
			url : 'AddReason.action?inOutStatus='+inOutStatus+'&strDate='+DATE+'&strServiceId='+SID+'&strEmpId='+EID,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	//===start parvez on 26-07-2021=== 
	function getAlert(limit) {
		alert("Exception apply limit is "+limit+". Your are applying more that limit.");
	}
	//===end parvez on 26-07-2021 
	
	
</script>

<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);

	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);

	//String DATE = request.getParameter("DATE");
	//String EMPID = request.getParameter("EMPID");
	//String date = request.getParameter("strDATE");
	//String strDate = request.getParameter("strDate");
	//String strPC = request.getParameter("PC");

	/* String strD1 = request.getParameter("D1");
	String strD2 = request.getParameter("D2"); */

	//String paycycle = (String) request.getAttribute("paycycle");
	
	Map hmEmpData = (Map) request.getAttribute("hmEmpData");
	Map hmRoster = (Map) request.getAttribute("hmRoster");

	/* String[] strPayCycleDates = null;

	if (paycycle != null && !paycycle.equalsIgnoreCase("NULL")) {

		strPayCycleDates = paycycle.split("-");
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
		
	} else {
		strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, (String)session.getAttribute(IConstants.ORGID), request);
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];

	} */

	String strType = (String) request.getParameter("T");
	String PAY = (String) request.getParameter("PAY");

	UtilityFunctions uF = new UtilityFunctions();

	/* if (strDate != null) {
		date = strDate;
	} else if (DATE != null) {
		date = DATE;
	} else if (date == null || (date != null && date.equalsIgnoreCase(""))) {
		date = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", IConstants.DBDATE, CF.getStrReportDateFormat());
	} */

	Map<String, String> hmEmpLevelMap = (Map<String, String>) request.getAttribute("hmEmpLevelMap");
	if (hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
	Map<String, Set<String>> hmWeekEndHalfDates = (Map<String, Set<String>>) request.getAttribute("hmWeekEndHalfDates");
	if (hmWeekEndHalfDates == null) hmWeekEndHalfDates = new HashMap<String, Set<String>>();
	Map<String, Set<String>> hmWeekEndDates = (Map<String, Set<String>>) request.getAttribute("hmWeekEndDates");
	if (hmWeekEndDates == null) hmWeekEndDates = new HashMap<String, Set<String>>();
	Map<String, String> hmEmpWlocation = (Map<String, String>) request.getAttribute("hmEmpWlocation");
	if (hmEmpWlocation == null) hmEmpWlocation = new HashMap<String, String>();
	List<String> alEmpCheckRosterWeektype = (List<String>) request.getAttribute("alEmpCheckRosterWeektype");
	if (alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
	Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>) request.getAttribute("hmRosterWeekEndDates");
	if (hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();

	Map<String, String> hmHolidays = (Map<String, String>) request.getAttribute("hmHolidays");
	if (hmHolidays == null) hmHolidays = new HashMap<String, String>();
	Map<String, String> hmHolidayDates = (Map<String, String>) request.getAttribute("hmHolidayDates");
	if (hmHolidayDates == null) hmHolidayDates = new HashMap<String, String>();
	
	List<String> alSalPaidEmpList = (List<String>) request.getAttribute("alSalPaidEmpList");
	if (alSalPaidEmpList == null) alSalPaidEmpList = new ArrayList<String>();

	String strTitle = "My Clock on/off exceptions";
	
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");

	String appliedExceptionCount = (String)request.getAttribute("appliedExceptionCount");
	
	/* String strTitle1 = "My exceptions for "+ uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())
			+ ((strD2 != null) ? " to " + uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) : ""); */
%>

			<%-- <div class="box-header with-border">
				<h3 class="box-title"><%=strTitle1%></h3>
			</div> --%>
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
						<s:form theme="simple" method="post" name="frm_roster_actual" id="frm_roster_actual" action="UpdateClockEntries">
							<div class="content1">
								<div class="row row_without_margin">
									<div class="col-lg-1 col-md-1 autoWidth" style="padding-right: 0px;">
										<i class="fa fa-filter"></i>
									</div>
									<div class="col-lg-1 col-md-2 autoWidth" style="padding-right: 0px;">
										<p style="padding-left: 5px;">Paycycle</p>
										<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" headerKey="0"
											onchange="submitForm('1');" list="payCycleList" key=""/>
									</div>
								</div>
							</div>
						</s:form>
					</div>
				</div>
					
				<%
					List alEmp = (List) request.getAttribute("TIMESHEET_EMP");

					if (alEmp == null) {
						alEmp = new ArrayList();
					}
					Map hmTime = (Map) request.getAttribute("TIMESHEET_");
					Map hmEmpName = (Map) request.getAttribute("TIMESHEET_EMPNAME");
					Map hmServicesMap = (Map) request.getAttribute("TIMESHEET_SERVICENAME");
					Map hmExceptionReason = (Map) request.getAttribute("hmExceptionReason");
					//System.out.println("hmExceptionReason : "+hmExceptionReason);
					List alDt = (List) request.getAttribute("TIMESHEET_DATE");
					List alService = (List) request.getAttribute("TIMESHEET_SERVICE");
					
					//System.out.println("alService : "+alService);

					//System.out.println("VCE1Emp.jsp/190--TIMESHEET_DATE : "+alDt);
					//out.println("<br/>alDt===>"+alDt);
					//out.println(alDt);
					//	out.println(alService);

					int rowCount = 0;
					String strColour = "";
					boolean isRowCountFirst = false;
					int i = 0;
					for (i = 0; i < alEmp.size(); i++) {
						Map hmExceptionInner = (Map) hmExceptionReason.get((String) alEmp.get(i));
						if (hmExceptionInner == null)
							hmExceptionInner = new HashMap();

						String strWLocationId = hmEmpWlocation.get((String) alEmp.get(i));
						Set<String> weeklyOffSet = hmWeekEndDates.get(strWLocationId);
						if (weeklyOffSet == null)
							weeklyOffSet = new HashSet<String>();

						Set<String> halfDayWeeklyOffSet = hmWeekEndHalfDates.get(strWLocationId);
						if (halfDayWeeklyOffSet == null)
							halfDayWeeklyOffSet = new HashSet<String>();

						Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get((String) alEmp.get(i));
						if (rosterWeeklyOffSet == null)
							rosterWeeklyOffSet = new HashSet<String>();

						//for(int k=0; k<alDt.size(); k++){
						for (int k = alDt.size() - 1; k >= 0; k--) {
							for (int j = 0; j < alService.size(); j++) {

								Map hmAS = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_AS");
								Map hmAE = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_AE");
								Map hmRS = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_RS");
								Map hmRE = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_RE");
								Map hmReason_OUT = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_OUT_REASON");
								Map hmApprove_OUT = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_OUT_APPROVE");
								Map hmReason_IN = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_IN_REASON");
								Map hmApprove_IN = (Map) hmTime.get(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) + (String) alService.get(j) + "_IN_APPROVE");

								if (hmAS == null) {
									hmAS = new HashMap();
								}
								if (hmAE == null) {
									hmAE = new HashMap();
								}
								if (hmRS == null) {
									hmRS = new HashMap();
								}
								if (hmRE == null) {
									hmRE = new HashMap();
								}
								if (hmReason_IN == null) {
									hmReason_IN = new HashMap();
								}
								if (hmApprove_IN == null) {
									hmApprove_IN = new HashMap();
								}
								if (hmReason_OUT == null) {
									hmReason_OUT = new HashMap();
								}
								if (hmApprove_OUT == null) {
									hmApprove_OUT = new HashMap();
								}
								isRowCountFirst = false;
								
								
								//System.out.println("VCE1Emp.jsp/255--Date="+alDt.get(k)+"---OUT_STATUS="+hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT"+"_STATUS"));
								//System.out.println("VCE1Emp.jsp/256--Date="+alDt.get(k)+"---IN_OUT_STATUS="+hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+"_STATUS"));
					%>
	
					<%
						boolean isInOut = false;
						String strTemp = (String) hmRS.get((String) alEmp.get(i));
						if (strTemp != null) {
							rowCount++;
							isRowCountFirst = true;
	
							if (rowCount % 2 == 0) {
								strColour = "1";
							} else {
								strColour = "";
							}
					%>

				<%
					if (((String) hmAS.get((String) alEmp.get(i))) != null && ((String) hmAE.get((String) alEmp.get(i))) != null && ((String) hmAS.get((String) alEmp.get(i))).length() == 0
						&& ((String) hmAE.get((String) alEmp.get(i))).length() == 0 && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+ "_STATUS"))) == 0) {
						if (hmHolidayDates.containsKey(uF.getDateFormat(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT), IConstants.DATE_FORMAT, CF.getStrReportDateFormat()) + "_" + strWLocationId)) {
							continue;
						} else if (alEmpCheckRosterWeektype.contains((String) alEmp.get(i))) {
							if (rosterWeeklyOffSet.contains(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT))) {
								continue;
							}
						} else if (weeklyOffSet.contains(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT))) {
							continue;
						} else if (halfDayWeeklyOffSet.contains(uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, IConstants.DATE_FORMAT))) {
							continue;
						}
				%>
				<div class="exceptions">
					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT")) != null) { %>
					<!-- <img src="images1/icons/pending.png" title="Waiting for approval" /> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval"></i>
					<% } else { %>
					<!-- <img src="images1/icons/pullout.png"
						title="Waiting for your reasons" /> -->
						<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900"></i>
					<% } %>

					I neither clocked on nor clocked off on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat()) %>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) %>
					for
					<%=(String) hmServicesMap.get((String) alService.get(j)) %>
					because ....
					<%
						isInOut = true;
						if (((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT")) != null) {
					%>

					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">View Reason</a>
					<div class="highslide-maincontent"><h4>Given Reason</h4>
					<!-- Started By Dattatray Date:07-10-21-->
					In Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT"+"_IN_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
					Out Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT"+"_OUT_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
					Reason : <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT") %>
					<!-- Ended By Dattatray Date:07-10-21-->
					</div>

					<% } else { %>
					<span id="myDiv1_<%=i%>_<%=j%>_<%=k%>">
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
						<font size="1"><i>(Payroll has been processed for this date.)</i></font>
					<% } else { %>
					<!-- ===start parvez on 26-07-2021=== -->
						<% 
						if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT)) && hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS") != null) { 
							if(uF.parseToInt(appliedExceptionCount) < uF.parseToInt(hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS").get(0))){%>
								<a href="javascript:void(0);" onclick="addReason('IN_OUT','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
							<%} else { %>
								<a href="javascript:void(0);" onclick="getAlert(<%=uF.parseToInt(hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS").get(0))%>);">Enter Reason</a>
							<%} %>
						<%} else { %>
							<a href="javascript:void(0);" onclick="addReason('IN_OUT','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
						<%} %>
					<!-- ===end parvez on 26-07-2021=== -->
					<% } %>
					</span>
					<% } %>
				</div>
				<% } else if (((String) hmAS.get((String) alEmp.get(i))) != null && ((String) hmAS.get((String) alEmp.get(i))).length() > 0
						&& uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+ "_STATUS"))) == 0) {
				%>
				<div class="exceptions">
					<% if ((String) hmReason_IN.get((String) alEmp.get(i)) != null) { %>
						<!-- <img src="images1/icons/pending.png" title="Waiting for approval" /> -->
						<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval"></i>
					<% } %>

					I clocked on for <%=(String) hmServicesMap.get((String) alService.get(j))%> at <%=(String) hmAS.get((String) alEmp.get(i))%>hrs on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>
					and my start time was
					<%=(String) hmRS.get((String) alEmp.get(i))%>hrs
					<% if ((String) hmReason_IN.get((String) alEmp.get(i)) != null) { %>

					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason given...</a>
					<div class="highslide-maincontent"> <h3>Given Reason</h3>
						<%=(String) hmReason_IN.get((String) alEmp.get(i))%>
					</div>
					<% } %>
				</div>
				 <!-- Created By Dattatray Date:18-Oct-21 -->
		
	<!-- ===start parvez date: 02-12-2021=== -->
				<%-- <% } else if (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+ "_STATUS"))) == 0 && (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+"_STATUS"))) != -1)
						&& (String) hmAS.get((String) alEmp.get(i)) != null && ((String) hmAS.get((String) alEmp.get(i))).length() == 0) {
				%> --%>
		
				<% } else if (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+ "_STATUS"))) <= 0 && (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+"_STATUS"))) != -1)
						&& (String) hmAS.get((String) alEmp.get(i)) != null && ((String) hmAS.get((String) alEmp.get(i))).length() == 0) {
					
				%>
		<!-- ===end parvez date: 02-12-2021=== -->

				<div class="exceptions">
					<%-- <% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT_STATUS")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+ "_OUT_STATUS"))) == -1) { %>
					<!-- <img src="images1/icons/pending.png" title="Waiting for approval" /> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#E61626" title="Waiting for approval" ></i>
					<% }%> --%>
<!-- ===start parvez date: 02-12-2021=== -->
					<%-- <% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN")) != null) { %> --%>
					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_STATUS"))) != -1) { %>
<!-- ===end parvez date: 02-12-2021=== -->
					<!-- <img src="images1/icons/pending.png" title="Waiting for approval" /> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval" ></i>
			<!-- ===start parvez date: 02-12-2021=== -->
					<%} else if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+"_STATUS")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_STATUS"))) == -1) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#E61626" title="Denied approval" ></i>
			<!-- ===end parvez date: 02-12-2021=== -->
					<% } else { %>
					<!-- <img src="images1/icons/pullout.png" title="Waiting for your reasons" /> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for your reasons"></i>
					<% } %>
					I did not clock on for
					<%=(String) hmServicesMap.get((String) alService.get(j))%>
					on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>,
					however, my start time was
					<%=(String) hmRS.get((String) alEmp.get(i))%>hrs

					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN")) != null) { %>

					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">View Reason</a>
					<div class="highslide-maincontent"> <h3>Given Reason</h3>
						<%-- <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN") %> --%>
					
			<!-- ===start parvez date: 02-12-2021=== -->
						In Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+"_IN_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
						<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+"_STATUS")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_STATUS"))) == -1) { %>
						Self Reason : <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN") %><br>
						Manager Reason : <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+"_MANAGER_REASON") %>
						</div>
						<a href="javascript:void(0);" onclick="addReason('IN','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
						<%} else{ %>
							Reason : <%=((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN"))%>
							
							<!-- Ended By Dattatray Date:07-10-21-->
				
						</div>
						<!-- created by parvez -->
						
						<%} %>
						<!-- </div> -->
			<!-- ===end parvez date: 02-12-2021=== -->
					<% } else { %>
					<span id="myDiv2_<%=i%>_<%=j%>_<%=k%>">
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
						<font size="1"><i>(Payroll has been processed for this date.)</i></font>
					<% } else { %>
						<!-- ===start parvez on 26-07-2021=== -->
					<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT)) && hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS") != null) {
						if((uF.parseToInt(appliedExceptionCount) < uF.parseToInt(hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS").get(0)))){%>
							<a href="javascript:void(0);" onclick="addReason('IN','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
						<%} else{ %>
							<a href="javascript:void(0);" onclick="getAlert('<%= hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS").get(0) %>');">Enter Reason</a>
						<%} %>
					<%} else{ %>
						<a href="javascript:void(0);" onclick="addReason('IN','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
					<%} %>
					<!-- ===end parvez on 26-07-2021=== -->
					<% } %>
					</span>
					<% } %>
				</div>
				<% } %>
				<% }
					strTemp = (String) hmAE.get((String) alEmp.get(i));
					if (strTemp != null) {
						if (!isRowCountFirst) {
							rowCount++;
							if (rowCount % 2 == 0) {
								strColour = "1";
							} else {
								strColour = "";
							}
						}
				%>

				<% if ((String) hmAE.get((String) alEmp.get(i)) != null && ((String) hmAE.get((String) alEmp.get(i))).length() > 0) { %>
				<div class="exceptions">
				
					<% if ((String) hmReason_OUT.get((String) alEmp.get(i)) != null) { %>
					<!-- <img src="images1/icons/pending.png" title="Waiting for approval" /> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval"></i>
					<% } %>

					I clocked off for
					<%=(String) hmServicesMap.get((String) alService.get(j))%>
					at
					<%=(String) hmAE.get((String) alEmp.get(i))%>hrs on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>
					and my end time was
					<%=(String) hmRE.get((String) alEmp.get(i))%>hrs
					<% if ((String) hmReason_OUT.get((String) alEmp.get(i)) != null) { %>

					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">Reason Given...</a>
					<div class="highslide-maincontent"> <h3>Given Reason</h3>
						<%=(String) hmReason_OUT.get((String) alEmp.get(i))%>
					</div>
				</div>
				<%
					}
					 //Created By Dattatray Date:18-Oct-21
			//===start parvez date: 02-12-2021===
					//} else if (!isInOut && (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT"+"_STATUS"))) == 0) && (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+"_STATUS"))) != -1)) {
					} else if (!isInOut && (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT"+"_STATUS"))) <= 0) && (uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+"_STATUS"))) != -1)) {
					//System.out.println("VCE1Emp.jsp/454--Date="+alDt.get(k)+"--OUT_STATUS="+hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT"+"_STATUS"));
					
			//===end parvez date: 02-12-2021===
				%>
				<div class="exceptions">
					<% 
					//System.out.println("hmExceptionInner ===>> "+ hmExceptionInner);
					//System.out.println("alService.get(j) ===>> "+ alService.get(j));
					//System.out.println("Date ===>>>"+alDt.get(k)+"---"+hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+"_STATUS"));
					if (((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT_STATUS"))) != -1) { %>
					<!-- <img src="images1/icons/pending.png" title="Waiting for approval" /> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#b71cc5" title="Waiting for approval"></i>
			<!-- ===start parvez date: 27-06-2022=== -->
					<%-- <%} else if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN"+"_STATUS")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_STATUS"))) == -1) { %> --%>
					<%} else if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT"+"_STATUS")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT_STATUS"))) == -1) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#E61626" title="Denied approval" ></i>
					
					<% } else { %>
			<!-- ===end parvez date: 27-06-2022=== -->
					<!-- <img src="images1/icons/pullout.png" title="Waiting for your reasons" /> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#ea9900" title="Waiting for your reasons" ></i>
					<% } %>

					I did not clock off for <%=(String) hmServicesMap.get((String) alService.get(j))%>
					on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat())%>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat())%>,
					however, my end time was <%=(String) hmRE.get((String) alEmp.get(i))%>hrs

					<%//System.out.println("VCE1Emp.jsp/481--Date="+alDt.get(k)+"--OUT_="+hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT")); %>
					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT")) != null) { %>
						

					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">View Reason</a>
					<div class="highslide-maincontent"><h3>Given Reason</h3>
						<%-- <%=(String)hmReason_IN.get((String)alEmp.get(i)) %> --%>
						<!-- Started By Dattatray Date:07-10-21-->
						Out Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT"+"_OUT_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
						<%-- Reason : <%=((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT"))%> --%>
					<!-- </div> -->
			<!-- ===start parvez date: 02-12-2021=== -->
					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT"+"_STATUS")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT_STATUS"))) == -1) { %>
						Self Reason : <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT") %><br>
						Manager Reason : <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_OUT"+"_MANAGER_REASON") %>
					</div>
					<a href="javascript:void(0);" onclick="addReason('OUT','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
					<%} else{ %>
						Reason : <%=((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_OUT"))%>
						
						<!-- Ended By Dattatray Date:07-10-21-->
			
					</div>
					<!-- created by parvez -->
					
					<%} %>
			<!-- end parvez date: 02-12-2021=== -->

					<% } else { %>
						<span id="myDiv3_<%=i%>_<%=j%>_<%=k%>">
					<% if (alSalPaidEmpList.contains((String) alEmp.get(i))) { %>
							<font size="1"><i>(Payroll has been processed for this date.)</i></font> 
					<% } else { %>
						<!-- ===start parvez on 26-07-2021=== -->
						<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT)) && hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS") != null) { 
							if((uF.parseToInt(appliedExceptionCount) < uF.parseToInt(hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS").get(0)))){%>
								<a href="javascript:void(0);" onclick="addReason('OUT','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
							<%} else{ %>
								<a href="javascript:void(0);" onclick="getAlert('<%= hmFeatureUserTypeId.get(IConstants.F_SET_TIME_EXCEPTION_APPLY_LIMIT+"_USER_IDS").get(0) %>');">Enter Reason</a>
							<%} %>
						<%} else{ %>
							
								<a href="javascript:void(0);" onclick="addReason('OUT','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
								
						<%} %>
						<!-- ===end parvez on 26-07-2021=== -->
						<%-- <a href="AddReason.action?DATE=<%=(String) alDt.get(k)%>&SID=<%=(String) alService.get(j)%>&EID=<%=(String) alEmp.get(i)%>"
							onclick="return hs.htmlExpand(this, {objectType: 'ajax' , width:400, height:180})">Enter Reason</a> --%> 
					<% } %>
						</span>
					<% } %>

				</div>
				 <!-- Started By Dattatray Date:18-Oct-21 -->
				<% } else if(((String) hmAE.get((String) alEmp.get(i))).length() == 0 && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+ "_STATUS"))) == -1){ %>
					<%-- <%System.out.println("VCE1Emp.jsp/512--Date="+alDt.get(k)+"--OUT_STATUS="+hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT"+ "_STATUS")); %> --%>
					<div class="exceptions">
					<% if (((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT_STATUS")) != null && uF.parseToInt(((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT_STATUS"))) == -1) { %>
					<i class="fa fa-circle" aria-hidden="true" style="color:#E61626" title="Denied approval" ></i>
					<% } %>

					I neither clocked on nor clocked off on
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDayFormat()) %>,
					<%=uF.getDateFormat((String) alDt.get(k), IConstants.DBDATE, CF.getStrReportDateFormat()) %>
					for
					<%=(String) hmServicesMap.get((String) alService.get(j)) %>
					because ....
					<%
						if (((String) hmExceptionInner.get((String) alDt.get(k) + "_"+ (String) alService.get(j)+"_IN_OUT")) != null) {
					%>

					<a href="javascript:void(0)" onclick="return hs.htmlExpand(this)">View Reason</a>
					<div class="highslide-maincontent"><h4>Given Reason</h4>
					In Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT"+"_IN_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
					Out Time : <%=uF.getDateFormat((String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT"+"_OUT_TIME"), IConstants.DBTIMESTAMP, IConstants.TIME_FORMAT) %><br>
					Self Reason : <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT") %><br>
					Manager Reason : <%=(String) hmExceptionInner.get((String) alDt.get(k) + "_" + (String) alService.get(j)+"_IN_OUT"+"_MANAGER_REASON") %>
					</div>
					<a href="javascript:void(0);" onclick="addReason('IN_OUT','<%=(String) alDt.get(k)%>','<%=(String) alService.get(j)%>','<%=(String) alEmp.get(i)%>');">Enter Reason</a>
					<% } %>
				</div>
				<%} %><!-- Ended By Dattatray Date:18-Oct-21 -->
				<%}}}}

					if (i == 0) {
				%>
				<div class="msg nodata">
					<span> Either you are not roster dependent or you do not
						have any pending exception. </span>
				</div>
				<% } if (i != 0) { %>
				<div class="custom-legends">
				  <div class="custom-legend pending">
				    <div class="legend-info">Waiting for approval</div>
				  </div>
				  <div class="custom-legend pullout">
				    <div class="legend-info">Waiting for your valid reason</div>
				  </div>
				  <!-- Started By Dattatray Date:18-Oct-21 -->
				  <div class="custom-legend denied">
				    <div class="legend-info">Denied</div>
				  </div>
				   <!-- Ended By Dattatray Date:18-Oct-21 -->
				</div>
				<% } %>
		</div>


	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content" style="width:400px;">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:200px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
	
	