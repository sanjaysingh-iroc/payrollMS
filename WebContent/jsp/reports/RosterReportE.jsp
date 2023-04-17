<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="bsh.util.Util"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%-- <%!String showData(String str) {
    if (str != null) {
    	return str;
    } else { 
    	return "";
    }
} %> --%>


<script type="text/javascript">

	function submitForm(type) {
		var divResult = 'divResult';
		var strMonth = document.getElementById("strMonth").value;
		var calendarYear = document.getElementById("calendarYear").value;
		//alert("service ===>> " + service);
		$("#"+divResult).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'RosterReport.action?strMonth='+strMonth+'&calendarYear='+calendarYear,
			data: $("#"+this.id).serialize(),
			success: function(result) {
	        	$("#"+divResult).html(result);
	   		}
		});
	}
	
</script>

<% 
    List alDay = (List) request.getAttribute("alDay");
    List alDate = (List) request.getAttribute("alDate");
    List alEmpId = (List) request.getAttribute("alEmpId");
    Map hmRosterServiceId = (HashMap) request.getAttribute("hmRosterServiceId");
    Map hmRosterServiceName = (HashMap) request.getAttribute("hmRosterServiceName");
    List alServiceId = (List) request.getAttribute("alServiceId");
    
    List _alHolidays = (List) request.getAttribute("_alHolidays");
    Map hmHolidays = (HashMap) request.getAttribute("hmHolidays");
    Map hmHolidayDates = (HashMap) request.getAttribute("hmHolidayDates");
    Map _hmHolidaysColour = (HashMap) request.getAttribute("_hmHolidaysColour");
    Map hmWLocation = (HashMap) request.getAttribute("hmWLocation");
    Map hmWeekEnds = (HashMap) request.getAttribute("hmWeekEnds");
    Map hmList = (Map) request.getAttribute("hmList");
    
    String strEmpId = (String)session.getAttribute(IConstants.EMPID);
    
    String strWLocation = (String)hmWLocation.get(strEmpId);
    
    UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
    
    String strAction = (String)request.getAttribute("javax.servlet.forward.request_uri");
    if(strAction!=null) {
    	strAction = strAction.replace(request.getContextPath()+"/","");
    }
    
    
    Map<String, Set<String>> hmWeekEndList = (Map<String, Set<String>>) request.getAttribute("hmWeekEndList");
    if(hmWeekEndList == null) hmWeekEndList = new HashMap<String, Set<String>>();
    List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");;
    if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
    Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");;
    if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
    
    Set<String> weeklyOffSet= (Set<String>)hmWeekEndList.get(strWLocation);
    if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
    
    Map<String, Map<String, String>> hmServicesWorkrdFor = (Map<String, Map<String, String>>) request.getAttribute("hmServicesWorkrdFor");
    if(hmServicesWorkrdFor == null)  hmServicesWorkrdFor = new HashMap<String, Map<String, String>>();
    
    Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
    if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
    
    Map<String, List<String>> hmShiftData = (Map<String, List<String>>) request.getAttribute("hmShiftData");
    if(hmShiftData == null) hmShiftData = new HashMap<String, List<String>>();
    
    Map hmLeavesMap = (Map) request.getAttribute("hmLeavesMap");
    Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
    Map hmLeavesName = (Map) request.getAttribute("hmLeavesName");
    Map hmLeaves = (Map)hmLeavesMap.get(strEmpId);
    if(hmLeaves==null)hmLeaves=new HashMap();
    
    Map<String, String> hmShiftId = hmServicesWorkrdFor.get(strEmpId); 
    List<String> alShiftIds = new ArrayList<String>();
    
    //	out.println("<br/>_alHolidays====>"+_alHolidays);
    //	out.println("<br/>hmHolidays====>"+hmHolidays);
    //	out.println("<br/>hmHolidayDates====>"+hmHolidayDates);
    //	out.println("<br/>_hmHolidaysColour====>"+_hmHolidaysColour);
    //	out.println("<br/>hmWLocation====>"+hmWLocation);
    //	out.println("<br/>strWLocation====>"+strWLocation);
    //	out.println("<br/>hmWeekEnds====>"+hmWeekEnds);
    
    
    
    
    
    //	out.println("<br/>hmRosterServiceName====>"+hmRosterServiceName);
    //	out.println("<br/>hmRosterServiceId====>"+hmRosterServiceId);
    //	out.println("<br/>alServiceId====>"+alServiceId);
    
    %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="My Roster Schedules" name="title"/>
    </jsp:include> --%>
    
                <!-- /.box-header -->
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                    <s:form theme="simple" action="RosterReport" method="post" name="frm_roster_actual">
                        <div class="box box-default">  <!-- collapsed-box -->
							<%-- <div class="box-header with-border">
							    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
							    <div class="box-tools pull-right">
							        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
							        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							    </div>
							</div> --%>
							<div class="box-body" style="padding: 5px; overflow-y: auto;">
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
									</div>
								</div>
							</div>
						</div>

                        <br/>
                        
						<%
                            int lastCount=0;
                            if (hmList.size() != 0) {
						%>
						<table cellpadding="1" cellspacing="1" class="table table-bordered">
                            <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = 0; alDay!=null && i < ((alDay.size() >= 7) ? 7 : alDay.size()); i++) {
                                    	/* String strHolidayColour = null;
                                    	//strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
                                    	strHolidayColour = (String)hmWeekEnds.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_"+strWLocation);
                                    	if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation); */
                                    	
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDay.get(i), "")%></td>
                                <% } %>
                            </tr>
                            <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = 0; alDate!=null && i < ((alDate.size() >= 7) ? 7 : alDate.size()); i++) {
                                    	
                                    	/* String strHolidayColour = null;
                                    	//strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
                                    	strHolidayColour = (String)hmWeekEnds.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_"+strWLocation);
                                    	if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation); */
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDate.get(i), "")%></td>
                                <% } %>
                            </tr>
                            <%-- <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = 0; alDate!=null && i < ((alDate.size() >= 7) ? 7 : alDate.size()); i++) {
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter">IN</td>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter">OUT</td>
                                <% } %>
                            </tr> --%>
                            <%
                                int k = 0;
                                	for (k = 0; alServiceId!=null && alEmpId!=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
                                		String strCol = ((k % 2 == 0) ? "1" : "");
                                		Map hm = (Map) hmList.get((String) alEmpId.get(0));
                                		Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
                                		String strServiceId = (String) alServiceId.get(k);
                                		String strServiceName = (String) hmRosterServiceName.get(strServiceId);
                                		
                                		if(uF.parseToInt(strServiceId) == 0) {
                    						continue;
                    					}
                                %>
                            <tr>
                                <%	
                                    for (int i = 0; alDate!=null && i < ((alDate.size() >= 7) ? 7 : alDate.size()); i++) {
                                    	/* String strHolidayColour = null;
                                    	//strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
                                    	strHolidayColour = (String)hmWeekEnds.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_"+strWLocation);
                                    	if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation); */
                                    	String strShiftId = hmShiftId.get((String) alDate.get(i)+"SHIFT_ID");
                                    	if(!alShiftIds.contains(strShiftId)) {
                                    		alShiftIds.add(strShiftId);
                                    	}
                                    	List<String> alShiftData = hmShiftData.get(strShiftId);
                                    	if(alShiftData == null) alShiftData = new ArrayList<String>();
                                    	boolean shiftFlag = false;
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	String strShiftName = "-";
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    				strShiftName = "W/O";
                                    			} else if(alShiftData !=null && alShiftData.size()>0) {
                                        			strHolidayColour = alShiftData.get(0);
                                        			shiftFlag = true;
                                        			strShiftName = alShiftData.get(1);
                                        		}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			strShiftName = "W/O";
                                    		} else if(alShiftData !=null && alShiftData.size()>0) {
                                    			strHolidayColour = alShiftData.get(0);
                                    			shiftFlag = true;
                                    			strShiftName = alShiftData.get(1);
                                    		}
                                    	} else {
                                    		strShiftName = "H";
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    		strShiftName = "L";
                                    	}
                                    	
                                    	if (i == 0) {
                                    %>
                                <td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
                                <% } %>
                                <%-- <td style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-")%></td>
                                <td style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-")%></td> --%>
                                <td colspan="2" style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%> alignCenter"><%=strShiftName %></td>
                                
                                <% } %>
                            </tr>
                            <% } %>
                        </table>
                        
                        <table cellpadding="1" cellspacing="1" style="margin-top: 20px"  class="table table-bordered">
                            <%-- <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = 7; alDate!=null && i < ((alDate.size() > 15) ? 14 : alDate.size()); i++) {
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDay.get(i), "")%></td>
                                <%
                                    }
                                    %>
                            </tr> --%>
                            <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = 7; alDate!=null && i < ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : alDate.size()); i++) {
                                    	/* String strHolidayColour = null;
                                    	//strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
                                    	strHolidayColour = (String)hmWeekEnds.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_"+strWLocation);
                                    	if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation); */
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDate.get(i), "")%></td>
                                <% } %>
                            </tr>
                            <%-- <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = 7; alDate !=null && i < ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : alDate.size()); i++) {
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter">IN</td>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter">OUT</td>
                                <% } %>
                            </tr> --%>
                            <%
                                k = 0;
                                	for (k = 0; alServiceId!=null && alEmpId!=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
                                		String strCol = ((k % 2 == 0) ? "1" : "");
                                		Map hm = (Map) hmList.get((String) alEmpId.get(0));
                                		Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
                                		String strServiceId = (String) alServiceId.get(k);
                                		String strServiceName = (String) hmRosterServiceName.get(strServiceId);
                                		
                                		if(uF.parseToInt(strServiceId) == 0) {
                    						continue;
                    					}
                                %>
                            <tr>
                                <%
                                    for (int i = 7; alDate!=null && i < ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : alDate.size()); i++) {
                                    	/* String strHolidayColour = null;
                                    	//strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
                                    	strHolidayColour = (String)hmWeekEnds.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_"+strWLocation);
                                    	if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation); */
                                    	String strShiftId = hmShiftId.get((String) alDate.get(i)+"SHIFT_ID");
                                    	if(!alShiftIds.contains(strShiftId)) {
                                    		alShiftIds.add(strShiftId);
                                    	}
                                    	List<String> alShiftData = hmShiftData.get(strShiftId);
                                    	if(alShiftData == null) alShiftData = new ArrayList<String>();
                                    	boolean shiftFlag = false;
                                    	
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	String strShiftName = "-";
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    				strShiftName = "W/O";
                                    			} else if(alShiftData !=null && alShiftData.size()>0) {
                                        			strHolidayColour = alShiftData.get(0);
                                        			shiftFlag = true;
                                        			strShiftName = alShiftData.get(1);
                                        		}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			strShiftName = "W/O";
                                    		} else if(alShiftData !=null && alShiftData.size()>0) {
                                    			strHolidayColour = alShiftData.get(0);
                                    			shiftFlag = true;
                                    			strShiftName = alShiftData.get(1);
                                    		}
                                    	} else {
                                    		strShiftName = "H";
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    		strShiftName = "L";
                                    	}
                                    	
                                    	if (i == 7) {
                                    %>
                                <td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
                                <% } %>
                                <%-- <td style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-")%></td>
                                <td style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-")%></td> --%>
                                <td colspan="2" style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%> alignCenter"><%=strShiftName %></td>
                                <% } %>
                            </tr>
                            <% } %>
                        </table>
                        
                        <table cellpadding="1" cellspacing="1" style="margin-top: 20px" class="table table-bordered">
                            <%-- <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); alDate!=null && i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDay.get(i), "")%></td>
                                <%
                                    }
                                    %>
                            </tr> --%>
                            <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); alDate!=null && i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
                                    	/* String strHolidayColour = null;
                                    	//strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
                                    	strHolidayColour = (String)hmWeekEnds.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_"+strWLocation);
                                    	if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation); */
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDate.get(i), "")%></td>
                                <% } %>
                            </tr>
                            <%-- <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); alDate !=null && i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter">IN</td>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter">OUT</td>
                                <% } %>
                            </tr> --%>
                            <%
                                k = 0;
                                	for (k = 0; alServiceId!=null && alEmpId !=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
                                		String strCol = ((k % 2 == 0) ? "1" : "");
                                		Map hm = (Map) hmList.get((String) alEmpId.get(0));
                                		Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
                                		String strServiceId = (String) alServiceId.get(k);
                                		String strServiceName = (String) hmRosterServiceName.get(strServiceId);
                                		
                                		if(uF.parseToInt(strServiceId) == 0) {
                    						continue;
                    					}
                                %>
                            <tr>
                                <%
                                    for (int i = ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15); alDate!=null && i < ((alDate.size() >= 21) ? 21 : alDate.size()); i++) {
                                    	/* String strHolidayColour = null;
                                    	//strHolidayColour = (String)hmWeekEnds.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocation);
                                    	strHolidayColour = (String)hmWeekEnds.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)+"_"+strWLocation);
                                    	if((String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation)!=null)strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation); */
                                    	String strShiftId = hmShiftId.get((String) alDate.get(i)+"SHIFT_ID");
                                    	if(!alShiftIds.contains(strShiftId)) {
                                    		alShiftIds.add(strShiftId);
                                    	}
                                    	List<String> alShiftData = hmShiftData.get(strShiftId);
                                    	if(alShiftData == null) alShiftData = new ArrayList<String>();
                                    	boolean shiftFlag = false;
                                    	
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	String strShiftName = "-";
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    				strShiftName = "W/O";
                                    			} else if(alShiftData !=null && alShiftData.size()>0) {
                                        			strHolidayColour = alShiftData.get(0);
                                        			shiftFlag = true;
                                        			strShiftName = alShiftData.get(1);
                                        		}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			strShiftName = "W/O";
                                    		} else if(alShiftData !=null && alShiftData.size()>0) {
                                    			strHolidayColour = alShiftData.get(0);
                                    			shiftFlag = true;
                                    			strShiftName = alShiftData.get(1);
                                    		}
                                    	} else {
                                    		strShiftName = "H";
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    		strShiftName = "L";
                                    	}
                                    	
                                    			if (i == ((alDate.size() > 15) ? ((alDate.size() == 15)?15:14) : 15)) {
                                    %>
                                <td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
                                <% } %>
                                <%-- <td style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "FROM_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "FROM_" + strServiceId) : "-")%></td>
                                <td style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%>"><%=((hm.containsKey((String) alDate.get(i) + "TO_" + strServiceId)) ? (String) hm.get((String) alDate.get(i) + "TO_" + strServiceId) : "-")%></td> --%>
                                <td colspan="2" style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%> alignCenter"><%=strShiftName %></td>
                                <% } %>
                            </tr>
                            <% } %>
                        </table>
                        
                        <table cellpadding="1" cellspacing="1" style="margin-top: 20px" class="table table-bordered">
                            <tr>
                                <td class="">&nbsp;</td>
                                <%
                                    for (int i = ((alDate.size() > 22) ? ((alDate.size() == 22)?22:21) : 22); alDate!=null && i < ((alDate.size() >= 28) ? 28 : alDate.size()); i++) {
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    		}
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    	}
                                    %>
                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDate.get(i), "")%></td>
                                <% } %>
                            </tr>
                            <%
                                k = 0;
                                	for (k = 0; alServiceId!=null && alEmpId !=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
                                		String strCol = ((k % 2 == 0) ? "1" : "");
                                		Map hm = (Map) hmList.get((String) alEmpId.get(0));
                                		Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
                                		String strServiceId = (String) alServiceId.get(k);
                                		String strServiceName = (String) hmRosterServiceName.get(strServiceId);
                                		
                                		if(uF.parseToInt(strServiceId) == 0) {
                    						continue;
                    					}
                                %>
                            <tr>
                                <%
                                	for (int i = ((alDate.size() > 22) ? ((alDate.size() == 22)?22:21) : 22); alDate!=null && i < ((alDate.size() >= 28) ? 28 : alDate.size()); i++) {
                                    	String strShiftId = hmShiftId.get((String) alDate.get(i)+"SHIFT_ID");
                                    	if(!alShiftIds.contains(strShiftId)) {
                                    		alShiftIds.add(strShiftId);
                                    	}
                                    	List<String> alShiftData = hmShiftData.get(strShiftId);
                                    	if(alShiftData == null) alShiftData = new ArrayList<String>();
                                    	boolean shiftFlag = false;
                                    	
                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
                                    	String strShiftName = "-";
                                    	if(strHolidayColour==null) {
                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
                                    		if(strDay!=null)strDay=strDay.toUpperCase();
                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
                                    			if(rosterWeeklyOffSet.contains(strDay)) {
                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    				strShiftName = "W/O";
                                    			} else if(alShiftData !=null && alShiftData.size()>0) {
                                        			strHolidayColour = alShiftData.get(0);
                                        			shiftFlag = true;
                                        			strShiftName = alShiftData.get(1);
                                        		}
                                    		} else if(weeklyOffSet.contains(strDay)) {
                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
                                    			strShiftName = "W/O";
                                    		} else if(alShiftData !=null && alShiftData.size()>0) {
                                    			strHolidayColour = alShiftData.get(0);
                                    			shiftFlag = true;
                                    			strShiftName = alShiftData.get(1);
                                    		}
                                    	} else {
                                    		strShiftName = "H";
                                    	}
                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
                                    	if(strLeave!=null) {
                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
                                    		strShiftName = "L";
                                    	}
                                    	
                                    		if (i == ((alDate.size() > 22) ? ((alDate.size() == 22)?22:21) : 22)) {
                                    %>
                                <td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
                                <% } %>
                                <td colspan="2" style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%> alignCenter"><%=strShiftName %></td>
                                <% } %>
                            </tr>
                            <% } %>
                        </table>
                        
                        
                        <table cellpadding="1" cellspacing="1" style="margin-top: 20px" class="table table-bordered">
                            <tr>
                                <td class="">&nbsp;</td>
                                <%
	                                if(alDate.size() >28) {
	                                    for (int i = 28; alDate!=null && i < alDate.size(); i++) {
	                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
	                                    	if(strHolidayColour==null) {
	                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
	                                    		if(strDay!=null)strDay=strDay.toUpperCase();
	                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
	                                    			if(rosterWeeklyOffSet.contains(strDay)) {
	                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
	                                    			}
	                                    		} else if(weeklyOffSet.contains(strDay)) {
	                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
	                                    		}
	                                    	}
	                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
	                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
	                                    	if(strLeave!=null) {
	                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
	                                    	}
	                                    %>
	                                <td style="background-color:<%=strHolidayColour%>" class="reportHeading alignCenter" colspan="2"><%=uF.showData((String) alDate.get(i), "")%></td>
	                                <% } %>
                                
                                	<% if(alDate != null && alDate.size()==29) { %>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                <% } else if(alDate != null && alDate.size()==30) { %>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                <% } else if(alDate != null && alDate.size()==31) { %>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="reportHeading alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                <% } %>
								<% } else { %>
                                
                                <% } %>
                            </tr>
                            <%
                                k = 0;
                                	for (k = 0; alServiceId!=null && alEmpId!=null && k < alServiceId.size() && alEmpId.size() > 0; k++) {
                                		String strCol = ((k % 2 == 0) ? "1" : "");
                                		Map hm = (Map) hmList.get((String) alEmpId.get(0));
                                		Map hmServiceId = (HashMap) hmRosterServiceId.get((String) alEmpId.get(0));
                                		String strServiceId = (String) alServiceId.get(k);
                                		String strServiceName = (String) hmRosterServiceName.get(strServiceId);
                                		
                                		if(uF.parseToInt(strServiceId) == 0) {
                    						continue;
                    					}
                                %>
                            <tr>
                                <%
	                                if(alDate.size() >28) {
	                                    for (int i = 28; alDate!=null && i < alDate.size(); i++) {
	                                    	String strShiftId = hmShiftId.get((String) alDate.get(i)+"SHIFT_ID");
	                                    	if(!alShiftIds.contains(strShiftId)) {
	                                    		alShiftIds.add(strShiftId);
	                                    	}
	                                    	List<String> alShiftData = hmShiftData.get(strShiftId);
	                                    	if(alShiftData == null) alShiftData = new ArrayList<String>();
	                                    	boolean shiftFlag = false;
	                                    	
	                                    	String strHolidayColour = (String)hmHolidayDates.get((String) alDate.get(i)+"_"+strWLocation);
	                                    	String strShiftName = "-";
	                                    	if(strHolidayColour==null) {
	                                    		String strDay = uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
	                                    		if(strDay!=null)strDay=strDay.toUpperCase();
	                                    		if(alEmpCheckRosterWeektype.contains(strEmpId)) {
	                                    			if(rosterWeeklyOffSet.contains(strDay)) {
	                                    				strHolidayColour = IConstants.WEEKLYOFF_COLOR;
	                                    				strShiftName = "W/O";
	                                    			} else if(alShiftData !=null && alShiftData.size()>0) {
	                                        			strHolidayColour = alShiftData.get(0);
	                                        			shiftFlag = true;
	                                        			strShiftName = alShiftData.get(1);
	                                        		}
	                                    		} else if(weeklyOffSet.contains(strDay)) {
	                                    			strHolidayColour = IConstants.WEEKLYOFF_COLOR;
	                                    			strShiftName = "W/O";
	                                    		} else if(alShiftData !=null && alShiftData.size()>0) {
	                                    			strHolidayColour = alShiftData.get(0);
	                                    			shiftFlag = true;
	                                    			strShiftName = alShiftData.get(1);
	                                    		}
	                                    	} else {
	                                    		strShiftName = "H";
	                                    	}
	                                    	String strLeaveCode = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
	                                    	String strLeave = (String)hmLeaves.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT));
	                                    	if(strLeave!=null) {
	                                    		strHolidayColour = (String)hmLeavesColour.get(strLeave);
	                                    		strShiftName = "L";
	                                    	}
	                                    	
	                                    	if (i == 28) {
	                                    %>
	                                <td class="reportLabel<%=strCol%>"><%=((strServiceName != null) ? strServiceName : "-")%></td>
	                                <% } %>
	                                <td colspan="2" style="background-color:<%=strHolidayColour%>" class="timeLabel<%=strCol%> alignCenter"><%=strShiftName %></td>
	                                <% } %>
	                                <% if(alDate != null && alDate.size()==29) { %>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                <% } else if(alDate != null && alDate.size()==30) { %>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                <% } else if(alDate != null && alDate.size()==31) { %>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                	<td colspan="2" class="alignCenter">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	                                <% } %>
                                <% } else { %>
                                
                                <% } %>
                            </tr>
                            <% } %>
                            </table>
                            
                            <% } else { %>
                            <table cellpadding="1" cellspacing="1" class="table table-bordered">
	                            <tr>
	                                <td class="">&nbsp;</td>
	                                <td class="alignCenter" colspan="14">
	                                    <div class="msg nodata"><span>You have no roster allocated for the current Pay Cycle, please change 'Pay Cycle' to view other roster's.</span></div>
	                                </td>
	                            </tr>
                            </table>
                            <% } %>
                        
                         
                        
                        <%if (hmList.size() != 0) { %>
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
                        <% } %>
                        
                    </s:form>
                    
                    
                    <div class="col-lg-12 col-md-12 col-sm-12" style="padding: 20px 0px;">
                    <%	
                    	Iterator<String> it = hmShiftData.keySet().iterator();
                    	while(it.hasNext()) {
                    		String shiftID = it.next();
                    	//for(int z=0; z<hmShiftData.size(); z++) {
							List<String> alInn = hmShiftData.get(shiftID);
							if(alInn != null && alShiftIds.contains(shiftID)) {
								String strColour = alInn.get(0);
						%>
						<div class="col-lg-3 col-md-4 col-sm-6">
							<div class="box box-solid" style="border: 1px solid <%=strColour %>;">
					            <div class="box-header with-border" style="background: <%=strColour %>;background-color: <%=strColour %>;padding: 5px;">
					              <h3 class="box-title"><%=alInn.get(1) %></h3>
					              <div class="box-tools pull-right">
					                <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					              </div>
					            </div>
					            <div class="box-body" style="padding: 5px;">
						            <p style="padding-left: 5px; padding-right: 5px">
										<span style="font-weight: bold">Shift Start</span>
										<%=alInn.get(2) %>
										<span style="font-weight: bold">End</span>
										<%=alInn.get(3) %></p>
									<p style="padding-left: 5px; padding-right: 5px">
										<span style="font-weight: bold">Break Start</span>
										<%=alInn.get(4) %>
										<span style="font-weight: bold">End</span>
										<%=alInn.get(5) %></p>
					            </div>
					          </div>
						</div>
						<% } } %>
					</div>
						
                </div>
                <!-- /.box-body -->
