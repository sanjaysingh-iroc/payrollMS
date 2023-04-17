<%@page import="java.util.*,com.konnect.jpms.util.*"%> 
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript">
    hs.graphicsDir = '<%=request.getContextPath()%>/images1/highslide/graphics/';
    hs.outlineType = 'rounded-white';
    hs.wrapperClassName = 'draggable-header';
    
    
    function show_employees() {
    	dojo.event.topic.publish("show_employees");
    }
    
</script>
<%!
    UtilityFunctions uF = new UtilityFunctions();
    double dblPayAmount = 0.0;
    double dblTotalPayAmount = 0.0;
    
    	String showDataAdd(String strData) {
    		if (strData == null) {
    			return "0";
    		} else {
    			dblPayAmount += uF.parseToDouble(strData);
    			dblTotalPayAmount += uF.parseToDouble(strData);
    			return strData;
    		}
    	}%>
<%
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions); 
    dblPayAmount = 0.0;
    dblTotalPayAmount = 0.0;
    %>
<%
    String strReqEmpID = (String) request.getAttribute("EMPID");
    String strEmpID = null; 
    
    
    if (strReqEmpID != null) {
    	strEmpID = "&EMPID=" + strReqEmpID;
    } else {
    	strEmpID = "";
    }
    
    String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
    
    List alInOut = (List) request.getAttribute("alInOut");
    List alDate = (List) request.getAttribute("alDate");
    List alDay = (List) request.getAttribute("alDay");
    Map hmDateServices = (HashMap) request.getAttribute("hmDateServices_TS");
    
    
    Map hmHours = (HashMap) request.getAttribute("hmHours");
    Map hmStart = (HashMap) request.getAttribute("hmStart");
    Map hmEnd = (HashMap) request.getAttribute("hmEnd");
    
    Map hmRosterStart = (HashMap) request.getAttribute("hmRosterStart");
    Map hmRosterEnd = (HashMap) request.getAttribute("hmRosterEnd");
    
    Map hmWeekEndList = (HashMap) request.getAttribute("hmWeekEndList");
    String strWLocationId = (String)request.getAttribute("strWLocationId");
    
    Map hmDailyRate = (HashMap) request.getAttribute("hmDailyRate");
    Map hmHoursRates = (HashMap) request.getAttribute("hmHoursRates");
    Map hmServicesWorkedFor = (HashMap) request.getAttribute("hmServicesWorkedFor");
    Map hmServices = (HashMap) request.getAttribute("hmServices");

    String TOTALW1 = (String) request.getAttribute("TOTALW1");
    String TOTALW2 = (String) request.getAttribute("TOTALW2");
    String DEDUCTION = (String) request.getAttribute("DEDUCTION");
    
    String PAYW1 = (String) request.getAttribute("PAYTOTALW1");
    String PAYW2 = (String) request.getAttribute("PAYTOTALW2");
    String _PAYTOTAL = (String) request.getAttribute("_PAYTOTAL");
    
    String _TOTALRosterW1 = (String) request.getAttribute("_TOTALRosterW1");
    String _TOTALRosterW2 = (String) request.getAttribute("_TOTALRosterW2");
    String _ALLOWANCE = (String) request.getAttribute("ALLOWANCE");
    
    
    String strPayMode = (String) request.getAttribute("strPayMode");
    String strFIXED = (String) request.getAttribute("FIXED");
    
    Map hmLeavesMap = (Map) request.getAttribute("hmLeaves");
    if(hmLeavesMap==null)hmLeavesMap=new HashMap();
    
    //out.println("hmLeavesMap===>"+hmLeavesMap);
    
    
    Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
    Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");
    
    List _alHolidays = (List) request.getAttribute("_alHolidays");
    if(_alHolidays==null)_alHolidays=new ArrayList();
    //	out.println(_alHolidays);
    //	out.println(hmLeavesMap);
    if(_hmHolidaysColour==null)_hmHolidaysColour=new HashMap();
    
    String strEmpName = (String) request.getAttribute("EMP_NAME");
    Map hmRosterHours = (Map) request.getAttribute("hmRosterHours");
    
    //String strD1 = (String)request.getParameter("D1");
    //String strD2 = (String)request.getParameter("D2");
    
    String paycycle = (String) request.getParameter("paycycle");
    
    String[] strPayCycleDates = null;
    
    String strD1 = "", strD2 = "";
    
    if (paycycle != null) {    
    
    	strPayCycleDates = paycycle.split("-");
    	strD1 = strPayCycleDates[0];
    	strD2 = strPayCycleDates[1];
    
    } else {
    	 
    	strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
    	strD1 = strPayCycleDates[0];
    	strD2 = strPayCycleDates[1];
    	
    }	
    
    String strType = (String)request.getParameter("T");
    String strPC = (String)request.getParameter("PC");
    
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
    
    
    %>
<%String strTitle = ((strUserType != null && strUserType.equalsIgnoreCase(IConstants.EMPLOYEE) ? "My " : strEmpName + "'s ")) +"Timesheet "; %>	   
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=strTitle%>" name="title"/>
    </jsp:include> --%> 	
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
                <div class="box-header with-border">
                    <h3 class="box-title"><%=((strD1!=null && strD2!=null)?" Pay Cycle "+uF.getDateFormat(strD1, IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(strD2, IConstants.DATE_FORMAT, CF.getStrReportDateFormat()):"")%></h3>
                </div>
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                    <div class="leftbox reportWidth">
                        <div class="filter_div">
                            <s:form theme="simple" id="selectLevel" action="ClockEntries" method="post" name="frm_roster_actual"
                                cssClass="formcss" enctype="multipart/form-data">
                                <input type="hidden" name="T" value="<%= strType %>" />
                                <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
                                <s:select theme="simple" name="level" listKey="levelId" headerValue="All Levels"
                                    listValue="levelCodeName" headerKey="0" onchange="javascript:show_employees();return false;"
                                    list="levelList" key="" required="true" cssClass="form-control" cssStyle="width:auto;"/>
                                <s:select theme="simple" name="f_org" listKey="orgId" listValue="orgName" 
                                    onchange="javascript:show_employees();return false;" 
                                    list="orgList" key="" required="true"  cssClass="form-control" cssStyle="width:auto;"/>
                                <s:select theme="simple" label="Select Pay Cycle" name="paycycle" listKey="paycycleId"
                                    listValue="paycycleName" headerKey="0" 		
                                    list="payCycleList" key="" required="true" onchange="javascript:show_employees();return false;"  cssClass="form-control" cssStyle="width:auto;"/>
                                <%}else {%>
                                <s:select theme="simple" label="Select Pay Cycle" name="paycycle" listKey="paycycleId"
                                    listValue="paycycleName" headerKey="0" 		
                                    list="payCycleList" key="" required="true" onchange="document.frm_roster_actual.submit();"  cssClass="form-control" cssStyle="width:auto;"/>
                                <%}%>
                                <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
                                <s:url id="employees_url" action="GetEmployeeList" />
                                <div style="border:solid 0px #ccc; float:left; margin:0px 0px 0px 20px">
                                    <sx:div href="%{employees_url}" listenTopics="show_employees" formId="selectLevel" showLoadingText="true"></sx:div>
                                </div>
                                <%}%>
                            </s:form>
                        </div>
                        <%
                            if(strEmpID!=null)
                             
                            {%>
                        <br/>
                        <table cellpadding="2" cellspacing="1" align="left" class="table">
                            <tr>
                                <td>&nbsp;</td>
                                <td>&nbsp;</td>
                                <th class="reportHeading alignCenter">Cost Centre</th>
                                <th class="reportHeading alignCenter">Actual Start Time</th>
                                <th class="reportHeading alignCenter">Actual End Time</th>
                                <th class="reportHeading alignCenter">Daily Actual Hours</th>
                            </tr>
                            <%
                                for (int i = 0; i < alDay.size(); i++) {
                                	List alDateServices = (List)hmDateServices.get((String) alDate.get(i));
                                	if(alDateServices==null){alDateServices=new ArrayList();alDateServices.add("");}
                                	for (int ii = 0; ii < ((alDateServices.size()==0)?1:alDateServices.size()); ii++) {
                                %>
                            <tr>
                                <td class="reportHeading alignLeft"
                                    <%=((hmWeekEndList.containsKey(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) ? "style=\'background-color:" + (String) hmWeekEndList.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) + "\'" : ""))%>
                                    <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(String) alDay.get(i)%></td>
                                <td class="reportHeading alignCenter"
                                    <%=((hmWeekEndList.containsKey(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) ? "style=\'background-color:" + (String) hmWeekEndList.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) + "\'" : ""))%>
                                    <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=((alDate.size() > i) ? (String) alDate.get(i) : "")%></td>
                                <td class="reportLabel alignCenter"
                                    <%=((hmWeekEndList.containsKey(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) ? "style=\'background-color:" + (String) hmWeekEndList.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) + "\'" : ""))%>
                                    <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(String)hmServices.get((String)alDateServices.get(ii))%> <%-- <%=showData((String) hmServicesWorkedFor.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-")%> --%></td>
                                <td class="reportLabel alignCenter"
                                    <%=((hmWeekEndList.containsKey(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) ? "style=\'background-color:" + (String) hmWeekEndList.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) + "\'" : ""))%>
                                    <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=  ((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%></td>
                                <td class="reportLabel alignCenter"
                                    <%=((hmWeekEndList.containsKey(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) ? "style=\'background-color:" + (String) hmWeekEndList.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) + "\'" : ""))%>
                                    <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=((hmLeavesMap.containsKey(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)))?(String)hmLeavesMap.get(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT)):uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "-"))%></td>
                                <td class="reportLabel alignCenter"
                                    <%=((hmWeekEndList.containsKey(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) ? "style=\'background-color:" + (String) hmWeekEndList.get(((String) alDay.get(i)).toUpperCase()+"_"+strWLocationId) + "\'" : ""))%>
                                    <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>
                                    >
                                    <%if(strUserType!=null && !strUserType.equalsIgnoreCase(IConstants.EMPLOYEE)){ %>
                                    <%-- <a href="<%=request.getContextPath()%>/UpdateClockEntries.action?DATE=<%=(String) alDate.get(i) %>&EMPID=<%=strReqEmpID%>&T=T&SERVICEID=<%=(String)alDateServices.get(ii)%>"> --%>
                                    <a href="<%=request.getContextPath()%>/AddClockEntries.action?DATE=<%=uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), IConstants.DBDATE) %>&E=E&EID=<%=strReqEmpID%>&SID=<%=(String)alDateServices.get(ii)%>&AS=<%=uF.showData((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "") %>&AE=<%=uF.showData((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%>" onclick="return hs.htmlExpand(this, {objectType: 'ajax' , width:200});">
                                        <div class="time_edit_setting"></div>
                                    </a>
                                    <%} %>
                                    <%=uF.showData((String) hmEarlyLateReporting.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "")%><%=uF.showData((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)), "0")%>
                                </td>	
                            </tr>
                            <%
                                }
                                	}
                                %>
                            <tr>
                                <th colspan="2" class="reportHeading alignLeft">Total Hours</th>
                                <td colspan="3" class="reportLabel alignRight">&nbsp;</td>
                                <td class="reportLabel alignCenter"><%=uF.formatIntoTwoDecimal(uF.parseToDouble(TOTALW1) + uF.parseToDouble(TOTALW2)) %></td>
                            </tr>
                        </table>
                        <%
                            }
                            else
                            {%>
                        <!--Please <a href="PayCycleList.action?T="<%=strType%>"\">click here</a> to go back to the paycycles list or <a href="EmployeeReportPayCycle.action?T="<%=strType%>"&PC="<%=strPC%>+"&D1="<%=strD1%>"&D2="<%=strD2%>"\">click here</a> to go back to the employee list--> 
                        <div class="filter">
                            <div class="msg nodata"><span>Please select the employee from the drop down list above.</span></div>
                        </div>
                        <%}%>
                    </div>
                </div>
            </div>
        </section>
    </div>
</section>