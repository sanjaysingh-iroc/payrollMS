<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script src="scripts/charts/justgage/raphael-2.1.4.min.js" type="text/javascript"></script>
<script src="scripts/charts/justgage/justgage.js" type="text/javascript"></script>

<style>
.emp_perfmnc1{
border: solid 1px #ccc;
padding: 10px;
border-radius: 5px;
-moz-box-shadow: 0px 2px 4px #D2D2D2;
box-shadow: 0px 2px 4px #D2D2D2;
margin-top: 10px;
}
.info_row{
border-bottom: solid 1px #e4e4e4;
padding: 5px;
color: #666666;
background-color: rgb(243, 243, 243);
}
</style>

	<%
		String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
		String dataType = (String) request.getAttribute("dataType");
		String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		
		UtilityFunctions uF=new UtilityFunctions();
	%>
	
			<div class="row" style="margin-left: 0px; clear:both;"> <!-- margin-right: 0px; -->
				<%
				//Map hmProKPI = (Map)request.getAttribute("hmProKPI");
				Map hmEmpName = (Map)request.getAttribute("hmEmpName");
				Map hmEmpDesigMap = (Map)request.getAttribute("hmEmpDesigMap");
				Map hmKPI = (Map)request.getAttribute("hmKPI");
				if(hmKPI == null)hmKPI = new HashMap();
				
				Map<String, String> hmKPIData = (Map<String, String>)request.getAttribute("hmKPIData");
				if(hmKPIData == null) hmKPIData = new HashMap<String, String>();
				
				/* Map<String, String> hmAnalysisSummaryMap = (Map<String, String>) request.getAttribute("hmAnalysisSummaryMap");
				if(hmAnalysisSummaryMap == null) hmAnalysisSummaryMap = new HashMap<String, String>(); */
				
				Set set = hmKPI.keySet();
				Iterator it = set.iterator();
				int i=0; 
				while(it.hasNext()) {
					String strEmpId = (String)it.next();
					i++;
					AngularMeter semiWorkedAbsent = (AngularMeter)hmKPI.get(strEmpId);
					
				%>
					
				<% if(i==1) { %>
				<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
				<% } %>
				<div class="emp_perfmnc1 col-lg-4 col-md-6 col-sm-12">
                  <div class="kpi_view1">
                      <div id="guage<%=strEmpId %>" class="gauge"></div>
                      <script>
						//document.addEventListener("DOMContentLoaded", function(event) {
						    var g1 = new JustGage({
						        id: "guage<%=strEmpId %>",
						     title: "",
						     label: "Performance",
						     value: <%=uF.parseToDouble(hmKPIData.get(strEmpId))%>,
						     min: 0,
						     max: 100,
						        decimals: 0,
						        gaugeWidthScale: 0.6,
						        levelColors: [
			                      "#FF0000",
			                      "#FFFF00",
			                      "#008000"
			                    ]
						    });
						//});
					</script>
                  </div>
                  
                  <div class="emp_info" style="text-align: center;">
                    <div class="info_row"><span>Name:</span><strong> <%= (String)hmEmpName.get(strEmpId)%></strong></div>
                    <div class="info_row"><span>Designation:</span> <strong><%= uF.showData((String)hmEmpDesigMap.get(strEmpId), "-")%></strong></div>
                  </div>
				</div>
				<% if(i==3) { %>
				</div>
				<% i=0; } %>
			<% }
			if(hmKPI.size()==0) {
			%>
				<div class="nodata msg"><span>You have not selected any employee from the list.</span></div>
			<% } %>
			</div>
	


