<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>


<script type="text/javascript">

var chartRoster;
var chartActual;

$(document).ready(function() {
	chartRoster = new Highcharts.Chart({
   		
	      chart: {
	         renderTo: 'container_Exception',
	        	defaultSeriesType: 'column'
	      },
	      title: {
	         text: 'Exceptions (Late In & Early Out)'
	      },
	      credits: {
	         	enabled: false
	      },
	      xAxis: {
	         categories: [<%=(String)request.getAttribute("sbActualPC")%>],
	         labels: {
	             rotation: -45,
	             align: 'right',
	             style: {
	                 font: 'normal 10px Verdana, sans-serif'
	             }
	          },
	         title: {
		            text: 'Date'
		         }
	      },
	      yAxis: {
	         
	         title: {
	            text: 'Hours'
	         }
	      },
	      plotOptions: {
	      	column: {
	            pointPadding: 0.2,
	            borderWidth: 0
	         }
	      },
	     series: [<%=request.getAttribute("sbActualHours")%>,<%=request.getAttribute("sbRosterHours")%>]
	   });
	
	
});
</script>

<%

	UtilityFunctions uF = new UtilityFunctions();

	List _allDates = (List) request.getAttribute("_allDates");
	List _alHolidays = (List) request.getAttribute("_alHolidays");
	List alId = (List) request.getAttribute("alId");
	
	 
	
	Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
	Map hmInLateCount = (Map) request.getAttribute("hmInLateCount");	
	Map hmOutLateCount = (Map) request.getAttribute("hmOutLateCount");
	Map hmEmpCodeName = (Map) request.getAttribute("hmIDNameMap");
	
	String strFrom = (String)request.getAttribute("FROM");
	String strTo = (String)request.getAttribute("TO");
	
	if(_allDates==null){
		_allDates = new ArrayList();
	}
	if(_alHolidays==null){
		_alHolidays = new ArrayList();
	}
	
	if(_hmHolidaysColour==null){
		_hmHolidaysColour = new HashMap();
	}
	if(hmOutLateCount==null){
		hmOutLateCount = new HashMap();
	}
	if(hmInLateCount==null){
		hmInLateCount = new HashMap();
	}
	
	String strP = (String)request.getAttribute("strP");
	String strSubTitle = "";
	if(strP!=null && strP.equalsIgnoreCase("EXE")){
		strSubTitle = "By Employee (per employee)- by and between ["+strFrom +" to "+strTo +"]";
	}else if(strP!=null && strP.equalsIgnoreCase("EXWL")){
		strSubTitle = "By Worklocation (per employee)- by and between ["+strFrom +" to "+strTo +"]";
	}else if(strP!=null && strP.equalsIgnoreCase("EXS")){
		strSubTitle = "By service - by and between ["+strFrom +" to "+strTo +"]";
	}else if(strP!=null && strP.equalsIgnoreCase("EXD")){
		strSubTitle = "By Department - by and between ["+strFrom +" to "+strTo +"]";
	}else if(strP!=null && strP.equalsIgnoreCase("EXUT")){
		strSubTitle = "By Usertype - by and between ["+strFrom +" to "+strTo +"]";
	}
	
%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Resource Effort Reports" name="title"/>
</jsp:include>




<div id="printDiv" class="leftbox reportWidth">

<span><%=strSubTitle%></span>

<div style="float:right;">
	<a href="LateEmpClockReport.action?P=EXWL">By Location</a>|
	<a href="LateEmpClockReport.action?P=EXS">By Service</a>|
	<a href="LateEmpClockReport.action?P=EXD">By Department</a>|
	<a href="LateEmpClockReport.action?P=EXUT">By Usertype</a>|
	<a href="LateEmpClockReport.action?P=EXE">By Employee</a> 
</div>

<s:form cssStyle="margin-left:735px; margin-bottom:10px" theme="simple" method="post" name="frm_roster_actual">
<s:hidden name="strP"></s:hidden>
<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId" cssStyle="float:right"
		listValue="paycycleName" headerKey="0" headerValue="Select Paycycle"
		onchange="document.frm_roster_actual.submit();"
		list="paycycleList" key="" />
</s:form>




<div class="scroll" style="float:left;width:100%">
<table cellpadding="2" cellspacing="1">

	<tr>
		<td>&nbsp;</td>
		<%
			for (int i = 0; i<_allDates.size(); i++) {
		%>
		<td colspan="2" class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%=(((String) _allDates.get(i) == null) ? "" : (String) _allDates.get(i))%>
		</td>
		<%
			}
		%>
	</tr>
	
	<tr>
		<td>&nbsp;</td>
		<%
			for (int i = 0; i<_allDates.size(); i++) {
		%>
		<td class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>>IN<br/>(hrs)</td>
		<td class="reportHeading alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>>OUT<br/>(hrs)</td>
		<%
			}
		%>
	</tr>

	
	<%
	int j=0;
for(j=0; j<alId.size(); j++){
	
	String strCol = ((j%2==0)?"dark":"light");
	Employee objAl = (Employee)alId.get(j);
	String strId = objAl.getStrEmpId();
	String strName = objAl.getStrName();
	
	
	Map hmIn = (Map)hmInLateCount.get(strId);
	Map hmOut = (Map)hmOutLateCount.get(strId);
	if(hmIn==null){
		hmIn = new HashMap();
	}
	if(hmOut==null){
		hmOut = new HashMap();
	}
	
	%>
	
	
	
	<tr class="<%=strCol%>" title="<%=strName %>">
		<td class="reportHeading  alignLeft" nowrap="nowrap"><%=strName%></td>

		<%
			for (int i = 0; i<_allDates.size(); i++) {
		%>
		<td class="alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmIn.get((String)_allDates.get(i)),"0") %></td>
		<td class="alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmOut.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>



	</tr>


	<%-- <tr class="<%=strCol%>">
		<td class="reportHeading alignCenter" nowrap>Out (hrs)</td>

		<%
			for (int i = 0; i<_allDates.size(); i++) {
		%>
		<td class="alignCenter" <%=((_alHolidays.contains(i + "") ? "style=\'background-color:" + (String) _hmHolidaysColour.get(i + "") + "\'" : ""))%>><%= uF.showData((String)hmOut.get((String)_allDates.get(i)),"0") %></td>
		<%
			}
		%>
	</tr> --%>
	
	<%} 
	
if(j==0){
	%>
	
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td class="reportLabel alignCenter" colspan="31">No exceptions found</td>
	</tr>
	<%
}
	%>
	

</table>
</div>

<%if(strP!=null && (strP.equalsIgnoreCase("EXWL") || strP.equalsIgnoreCase("EXUT") || strP.equalsIgnoreCase("EXS") || strP.equalsIgnoreCase("EXD"))){ %>

<!-- <div class="chartholder">
	<div style="float: right; text-decoration: underline;">Displaying only last 6 paycycles</div>
	<div id="container_Exception" style="height: 300px; width:95%; float:left; margin-top:20px"></div>
</div> -->
<%}%>
</div>

<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../reports/ReportNavigation.jsp"></jsp:include>
   </div>