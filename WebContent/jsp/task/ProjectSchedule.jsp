<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" src="js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="js/jquery.sparkline.js"></script>

<script>
	$(function() {
	    $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
	    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
	});
    
    jQuery(document).ready(function() {

    	jQuery(".content1").hide();
    	//toggle the componenet with class msg_body
    	jQuery(".heading_dash").click(function() {
    		jQuery(this).next(".content1").slideToggle(500);
    		$(this).toggleClass("filter_close");
    	});
    });
</script>


<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
	$("#f_project_service").multiselect();
	$("#f_client").multiselect();
});    
</script>

<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 
	String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
%>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include>
 
 <script>
 
 function checkSelectType(value) {
		
		if(value == '1') {
			document.getElementById("fromToDIV").style.display = 'block';
			document.getElementById("financialYearDIV").style.display = 'none';
			document.getElementById("monthDIV").style.display = 'none';
			document.getElementById("paycycleDIV").style.display = 'none';
		} else if(value == '2') {
			document.getElementById("fromToDIV").style.display = 'none';
			document.getElementById("financialYearDIV").style.display = 'block';
			document.getElementById("monthDIV").style.display = 'none';
			document.getElementById("paycycleDIV").style.display = 'none';
		} else if(value == '3') {
			document.getElementById("fromToDIV").style.display = 'none';
			document.getElementById("financialYearDIV").style.display = 'none';
			document.getElementById("monthDIV").style.display = 'block';
			document.getElementById("paycycleDIV").style.display = 'none';
		} else if(value == '4') {
			document.getElementById("fromToDIV").style.display = 'none';
			document.getElementById("financialYearDIV").style.display = 'none';
			document.getElementById("monthDIV").style.display = 'none';
			document.getElementById("paycycleDIV").style.display = 'block';
		}
	}
 
 
 function loadMoreProjects(proPage, minLimit, proType) {
		
		document.frmProjectSchedule.proPage.value = proPage;
		document.frmProjectSchedule.minLimit.value = minLimit;
		document.frmProjectSchedule.submit();
			
	}
	
 </script>
 
<div class="leftbox reportWidth">
	<%  
		String proType = (String)request.getAttribute("proType");
		String proCount = (String)request.getAttribute("proCount");
	%>

	<div style="margin-bottom: 20px">
		<a class="<%=((proType == null || proType.equals("") || proType.equalsIgnoreCase("L")) ? "current" : "next") %>" href="ProjectSchedule.action?proType=L">Working Projects</a> | 
		<a class="<%=((proType != null && proType.equalsIgnoreCase("C")) ? "current" : "next") %>" href="ProjectSchedule.action?proType=C">Completed Projects</a>
	</div>
	


<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
		<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
			<%=(String)request.getAttribute("selectedFilter") %>
		</p>
		<div class="content1" style="height: 170px;">
		
    <s:form name="frmProjectSchedule" action="ProjectSchedule" theme="simple">
    	<s:hidden name="proType" id="proType" />
    	<s:hidden name=" proPage" id="proPage" />
    	<s:hidden name="minLimit" id="minLimit" />
			<div style="float: left; width: 100%; margin-top: -5px;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-filter"></i>
					</div>
					
					<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Organisation</p> 
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left; margin-right: 10px;" listValue="orgName" 
							onchange="document.frmProjectSchedule.submit();" list="organisationList" />
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;"
							listValue="wLocationName" list="wLocationList" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Department</p>
						<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" cssStyle="float:left; margin-right: 10px;" 
							listValue="deptName" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">SBU</p>
						<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
							listValue="serviceName" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Level</p>
						<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" cssStyle="float:left;margin-right: 10px;"
							listValue="levelCodeName" list="levelList" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 35px; width: 215px;">
						<p style="padding-left: 5px;">Service</p>
						<s:select name="f_project_service" id="f_project_service" list="projectServiceList" listKey="serviceId" cssStyle="float:left;margin-right: 10px;"
							listValue="serviceName" multiple="true"/>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Client</p>
						<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" cssStyle="float:left;margin-right: 10px;" list="clientList" 
							key="" multiple="true" />
					</div>
			</div>
			
			<div style="float: left; width: 100%;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-calendar"></i>
					</div>
					
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Select Period</p>
						<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="" 
							headerValue="Select Period" list="#{'1':'From-To', '2':'Financial Year', '3':'Month', '4':'Paycycle'}" onchange="checkSelectType(this.value);"/> <!--   -->
					</div>
					
					<div id="fromToDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:textfield name="strStartDate" id="strStartDate" value="From Date" onblur="fillField(this.id, 3);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
			      		<s:textfield name="strEndDate"  id="strEndDate" value="To Date" onblur="fillField(this.id, 4);" onclick="clearField(this.id);" cssStyle="width:65px"></s:textfield>
		      		</div>
		      		
		      		<div id="financialYearDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Financial Year</p>
						<s:select label="Select PayCycle" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName"
							headerValue="Select Financial Year" list="financialYearList" />
		      		</div>
		      		
		      		<div id="monthDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 325px;">
						<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
						<s:select label="Select PayCycle" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName"
							headerValue="Select Financial Year" list="financialYearList" /> 
						<s:select name="strMonth" id="strMonth" cssStyle="margin-left: 7px; width: 100px;" listKey="monthId" listValue="monthName" list="monthList" />	
		      		</div>
		      		
		      		<div id="paycycleDIV" style="float: left; display: none; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Paycycle</p>
						<s:select label="Select PayCycle" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName"
							headerValue="Select Paycycle" list="paycycleList" />
		      		</div>
		      		
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">&nbsp;</p>
						<s:submit value="Submit" cssClass="input_button" cssStyle="margin:0px" />
					</div>
			</div>
		</s:form>
	
		</div>
	</div>
	<%
		Map hmProPerformaceBillable = (Map)request.getAttribute("hmProPerformaceBillable"); 
		Map hmProPerformaceActual = (Map)request.getAttribute("hmProPerformaceActual");
		Map hmProPerformaceBudget = (Map)request.getAttribute("hmProPerformaceBudget");
		Map hmProPerformaceActualTime = (Map)request.getAttribute("hmProPerformaceActualTime");
		Map hmProPerformaceIdealTime = (Map)request.getAttribute("hmProPerformaceIdealTime");
		Map hmProPerformaceProjectName = (Map)request.getAttribute("hmProPerformaceProjectName");
		Map<String, String> hmProPerformaceCurrency = (Map<String, String>)request.getAttribute("hmProPerformaceCurrency");
		
		Map hmProPerformaceProjectManager = (Map)request.getAttribute("hmProPerformaceProjectManager");
		Map hmProPerformaceProjectProfit = (Map)request.getAttribute("hmProPerformaceProjectProfit");
		Map hmProPerformaceProjectAmountIndicator = (Map)request.getAttribute("hmProPerformaceProjectAmountIndicator");
		Map hmProPerformaceProjectTimeIndicator = (Map)request.getAttribute("hmProPerformaceProjectTimeIndicator");
		Map hmProjectClient = (Map)request.getAttribute("hmProjectClient");
		List alProjectId = (List)request.getAttribute("alProjectId");
		
		Map<String, String> hmProOwner = (Map<String, String>)request.getAttribute("hmProOwner");
		if(hmProOwner == null) hmProOwner = new HashMap<String, String>();
		Map<String, String> hmProActIdealTimeHRS = (Map<String, String>)request.getAttribute("hmProActIdealTimeHRS");
		if(hmProActIdealTimeHRS == null) hmProActIdealTimeHRS = new HashMap<String, String>();
		
		UtilityFunctions uF = new UtilityFunctions();
		
	%>
	<div style="float:left; width: 46%;">
		<h2>Project Schedule</h2>
		<div style="float: left; width: 100%;">
			<table class="tb_style" style="width: 100%;">
				<% if(alProjectId != null && !alProjectId.isEmpty()) { %>
					<tr>
						<th>Project Name</th>
						<th>Project Owner</th>
						<th>Deadline</th> 
						<th>Estimated Time<br/>(hrs)</th>
						<th>Time Spent<br/>(hrs)</th>
						<th>Indicator</th>
					</tr>
				<%
					for(int i=0; alProjectId != null && !alProjectId.isEmpty() && i<alProjectId.size(); i++){
						String strBullet = uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS"),"0")+","+uF.showData(hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS"),"0");
				%>
						<tr>
							<td><%=hmProPerformaceProjectName.get((String)alProjectId.get(i)) %></td>
							<td> <%=hmProOwner.get((String)alProjectId.get(i)) %></td>
							<td>
								<span id="bullet<%=(String)alProjectId.get(i)%>">Loading..</span>
								<script type="text/javascript">
								    $(function() {
								    	 $('#bullet<%=(String)alProjectId.get(i)%>').sparkline(new Array(<%=strBullet %>), {type: 'bullet',targetColor: '#b2b2b2',performanceColor: '#9acd32'} );
								    });
							    </script>
							</td>
							<td class="alignRight padRight20" ><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_IDEAL_TIME_HRS") %></td>
							<td class="alignRight padRight20" ><%=hmProActIdealTimeHRS.get((String)alProjectId.get(i)+"_ACT_TIME_HRS") %></td>
							<td align="center"><%=hmProPerformaceProjectTimeIndicator.get((String)alProjectId.get(i)) %></td>
						</tr>
					<%} %> 
				<%} else { %>
					<tr><td colspan="6"><div class="msg nodata"><span>Projects not available for this selection.</span></div> </td></tr>
				<% } %>
			</table>
		</div>

		<div style="text-align: center; float: left; width: 100%;">
			
			<% int intproCnt = uF.parseToInt(proCount);
				int pageCnt = 0;
				int minLimit = 0;
				
				for(int i=1; i<=intproCnt; i++) {
					minLimit = pageCnt * 10;
					pageCnt++;
			%>
			<% if(i ==1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				if(uF.parseToInt(strPgCnt) > 1) {
					 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
				}
				if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
			%>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
					<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');">
					<%="< Prev" %></a>
				<% } else { %>
					<b><%="< Prev" %></b>
				<% } %>
				</span>
				<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
					<b>...</b>
				<% } %>
			<% } %>
			
			<% if(i > 1 && i < intproCnt) { %>
			<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
				<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
			<% } %>
			<% } %>
			
			<% if(i == intproCnt && intproCnt > 1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
				 if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
				%>
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
					<b>...</b>
				<% } %>
			
				<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
					<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"><%="Next >" %></a>
				<% } else { %>
					<b><%="Next >" %></b>
				<% } %>
				</span>
			<% } %>
			<%} %>
			</div>
		</div>
		<div style="float:left; width: 48%; margin-left: 39px;">
			<h2>Project Health</h2>
			<div style="float: left; width: 100%;">
				<table class="tb_style" style="width: 100%;">
					<% if(alProjectId != null && !alProjectId.isEmpty()) { %>
						<tr>
							<th>Project Name</th>
							<th>Project Owner</th>
							<th>Budgeted</th> 
							<th>Actual Amount</th> 
							<th>Billable Amount</th>
							<th>Indicator</th>
						</tr>
					<%
						for(int i=0; alProjectId != null && !alProjectId.isEmpty() && i<alProjectId.size(); i++){
					%>
							<tr>
								<td><%=hmProPerformaceProjectName.get((String)alProjectId.get(i)) %></td>
								<td> <%=hmProOwner.get((String)alProjectId.get(i)) %></td>
								<td class="alignRight padRight20">
									<%=hmProPerformaceCurrency.get((String)alProjectId.get(i)) %> 
									<%=hmProPerformaceBudget.get((String)alProjectId.get(i)) %>
								</td>
								<td class="alignRight padRight20">
									<%=hmProPerformaceCurrency.get((String)alProjectId.get(i)) %> 
									<%=hmProPerformaceActual.get((String)alProjectId.get(i)) %>
								</td>
								<td class="alignRight padRight20">
									<%=hmProPerformaceCurrency.get((String)alProjectId.get(i)) %> 
									<%=hmProPerformaceBillable.get((String)alProjectId.get(i)) %>
								</td>
								<td class="alignRight padRight20"><%=hmProPerformaceProjectAmountIndicator.get((String)alProjectId.get(i)) %></td>
							</tr>
						<%} %> 
					<%} else { %>
					<tr><td colspan="6"><div class="msg nodata"><span>Projects not available for this selection.</span></div> </td></tr>
					<% } %>
				</table>
			</div>
	
		<div style="text-align: center; float: left; width: 100%;">
			
			<% intproCnt = uF.parseToInt(proCount);
				pageCnt = 0;
				minLimit = 0;
				
				for(int i=1; i<=intproCnt; i++) {
					minLimit = pageCnt * 10;
					pageCnt++;
			%>
			<% if(i ==1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				if(uF.parseToInt(strPgCnt) > 1) {
					 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
				}
				if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
			%>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
					<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');">
					<%="< Prev" %></a>
				<% } else { %>
					<b><%="< Prev" %></b>
				<% } %>
				</span>
				<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
					<b>...</b>
				<% } %>
			<% } %>
			
			<% if(i > 1 && i < intproCnt) { %>
			<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
				<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
			<% } %>
			<% } %>
			
			<% if(i == intproCnt && intproCnt > 1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
				 if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
				%>
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
					<b>...</b>
				<% } %>
			
				<span><a href="javascript:void(0);" onclick="loadMoreProjects('<%=pageCnt %>','<%=minLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
					<a href="javascript:void(0);" onclick="loadMoreProjects('<%=strPgCnt %>','<%=strMinLimit %>','<%=uF.showData((String)request.getAttribute("proType"), "L") %>');"><%="Next >" %></a>
				<% } else { %>
					<b><%="Next >" %></b>
				<% } %>
				</span>
			<% } %>
			<%} %>
			</div>
		</div>

<br/><br/>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <i class="fa fa-circle" aria-hidden="true" style="color:#e22d25"></i><!-- <img src="images1/icons/denied.png" width="17"> --></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Billable</div>
</div>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"> <!-- <img src="images1/icons/re_submit.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#f7ee1d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &gt; Budgeted and Actual &lt; Billable</div>
</div>

<div style="margin:2px;float:left;width:100%">
    <div style="width: 20px;float:left"><!-- <img src="images1/icons/approved.png" width="17"> --><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i></div> 
    <div style="float:left;padding-left:5px">Actual &lt; Budgeted</div>
</div>


</div>

