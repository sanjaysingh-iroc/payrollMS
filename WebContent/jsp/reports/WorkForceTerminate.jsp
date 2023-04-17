<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>

<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>



<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmActual = (Map) request.getAttribute("hmActual");
	Map hmRoster = (Map) request.getAttribute("hmRoster");
	List alId = (List) request.getAttribute("alId");

	List alPayCycles = (List) request.getAttribute("alPayCycles");
	Map hmTotal = (Map) request.getAttribute("hmTotal");
%> 
 
<script type="text/javascript">

var chartActual;

$(document).ready(function() {
	
	chartActual = new Highcharts.Chart({
   		
      chart: {
         renderTo: 'container_Actual',
         defaultSeriesType: 'column'
      },
      title: {
    	  text : 'Workforce'
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
	            text: 'Paycycle'
	     	}
      },
      credits: {
       	enabled: false
   	  },
      yAxis: {
         min: 0,
         title: {
            text: 'No of Resources'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
     series: [<%=request.getAttribute("sbActualHours")%>]
   });
	
});

function exporttoxls(id){
	
	window.location='workForcereport.action?id='+id;
}


</script>


<%
String strSubTitle = null;
String strColumnTitle = null;
String strP = (String)request.getParameter("param");
if(strP!=null && strP.equalsIgnoreCase("WLH")){
	strSubTitle="By Location";
	strColumnTitle = "Locations";
}else if(strP!=null && strP.equalsIgnoreCase("SH")){
	strSubTitle="By Service";
	strColumnTitle = "Services";
}else if(strP!=null && strP.equalsIgnoreCase("UTH")){
	strSubTitle="By Usertypes";
	strColumnTitle = "UserTypes";
}else if(strP!=null && strP.equalsIgnoreCase("DH")){
	strSubTitle="By Departments";
	strColumnTitle = "Departments";
}else{
	strSubTitle="By Employee";
	strColumnTitle = "Employees";
}
%>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Consolidated Work Force" name="title"/>
</jsp:include>


<div id="printDiv" class="leftbox reportWidth">


<s:form name="frmWorkForce" action="WorkForceTerminate" theme="simple">

<div class="filter_div">
    <s:select theme="simple" name="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         list="wLocationList" key=""  />
                    
    <s:select name="f_department" list="departmentList" listKey="deptId"  cssStyle="float:left;margin-right: 10px;"
    			listValue="deptName" headerKey="0" headerValue="All Departments" 
    			></s:select>
    			
    <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"  cssStyle="float:left;margin-right: 10px;"
	                            listValue="levelCodeName" headerKey="0" 
	                            list="levelList" key="" required="true" />
     
       
    
    <s:submit value="Submit" cssClass="input_button"  cssStyle="margin:0px"/>
    
    
    
    <div style="padding-top:10px">
	    <s:radio name="param" list="#{'WLH':'By Location','SH':'By Service','DH':'By Department','UTH':'By UserType'}" />
    </div>

    </div>
</s:form>

<span style="font-weight:bold">[<%=strSubTitle%>]</span>

	<div class="scroll">
		
<display:table name="alReport" cellspacing="1" class="itis" export="true" 
	pagesize="50" id="lt1" requestURI="WorkForceTerminate.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="WorkForce.xls" />
	<display:setProperty name="export.xml.filename" value="WorkForce.xml" />
	<display:setProperty name="export.csv.filename" value="WorkForce.csv" />
	
	<display:column style="text-align:left" nowrap="nowrap" title="<%=strColumnTitle%>" sort="true"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
		<%
			for (int ii=alPayCycles.size()-1; ii>=0; ii--){
				int count = 1+ii;
				%>
				<display:column  style="width:120px;text-align:center" nowrap="nowrap" title="<%=(String)alPayCycles.get(ii)%>" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(count)%></display:column>
				<%
			}  
			%>

	<display:footer>
			<tr>
			<th colspan="1">Total</th>
			<%
			for (int ii=alPayCycles.size()-1; ii>=0; ii--){
				
				%>
				<th colspan="1"><%=uF.showData((String)hmTotal.get(ii+""),"0") %></th>
				<%
			}  
			%>
			</tr>
	</display:footer>
	
</display:table>
		
		
		
		
	</div>
	
	<%if(strP!=null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH") || strP.equalsIgnoreCase("DH"))){ %>
	
	<div class="chartholder">
		<div style="float: right; text-decoration: underline;">Displaying only last 6 paycycles</div>
		<div id="container_Actual" style="height: 300px; width:95%; margin-top:20px; float: left;"></div>
	</div>
	<%} %>

</div>


<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../reports/ReportNavigation.jsp"></jsp:include>
   </div>