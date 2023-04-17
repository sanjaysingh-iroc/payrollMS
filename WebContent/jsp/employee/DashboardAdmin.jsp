<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.Navigation"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%@page import="java.util.*"%>
<%-- <jsp:include page="../common/SubHeader.jsp"> 
	<jsp:param value="Control Panel" name="title"/>
</jsp:include> --%>
<style> 
li > span {
position: relative; 
top: 0px;
}
.level_list>li {
padding-top: 5px;
padding-bottom: 5px;
border-bottom: 1px solid rgb(240, 240, 240);
}
</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%String []arrModules = (String[])request.getAttribute("arrModules"); %>

      <!-- Main content -->
	<section class="content">
         <!-- title row -->
         
		<div class="row">

			<%
				UtilityFunctions uF = new UtilityFunctions();
				List alParentNavL = (List) session.getAttribute("alParentNavL");
				Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");
				
				List alParentNavR = (List) session.getAttribute("alParentNavR");
				Map hmChildNavR = (Map) session.getAttribute("hmChildNavR");
				Map hmNavigation = (Map) session.getAttribute("hmNavigation");
				Map hmNavigationParent = (Map) session.getAttribute("hmNavigationParent");
				
				CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
				String []arrEnabledModules = CF.getArrEnabledModules();
				String []arrAllModules = CF.getArrAllModules();
				String strProductType = (String) session.getAttribute(IConstants.PRODUCT_TYPE);
				String navigationId = (String) request.getAttribute("navigationId");
				String toPage = (String) request.getAttribute("toPage");
				String toTab = (String) request.getAttribute("toTab");
				
				String orgId = (String) request.getAttribute("strOrg");
				String strLocation = (String) request.getAttribute("strLocation");
				String strLevel = (String) request.getAttribute("strLevel");
				String strCFYear = (String) request.getAttribute("strCFYear");
				String strSalaryHeadId = (String) request.getAttribute("strSalaryHeadId");
				String strGrade = (String) request.getAttribute("strGrade");
				String salaryBand = (String) request.getAttribute("salaryBand");
				String paycycle = (String) request.getAttribute("paycycle");
				
				/* System.out.println("navigationId ===>> " + navigationId);
				System.out.println("toPage ===>> " + toPage);
				System.out.println("toTab ===>> " + toTab);
				System.out.println("orgId ===>> " + orgId);
				System.out.println("strCFYear ===>> " + strCFYear);
				System.out.println("paycycle ===>> " + paycycle); */ 	
			%>
		
			<div class="col-md-5">
				<div class="box box-body">
					<ul class="products-list product-list-in-box">
					 	<%
					 	List alListC = (List) hmChildNavL.get(navigationId);
					 	if(alListC==null)alListC = new ArrayList();
					 	/* if(toPage == null && ) */
					 	for(int ic=0; ic < alListC.size(); ic++) {
					 		Navigation navC = (Navigation)alListC.get(ic);
					 		if((toTab == null || toTab.trim().equals("") || toTab.trim().equalsIgnoreCase("NULL")) && navC.getStrLabelCode() != null && navC.getStrLabelCode().equals(IConstants.NAVI_112)) {
								toTab = "WFP";		
							} else if((toTab == null || toTab.trim().equals("") || toTab.trim().equalsIgnoreCase("NULL")) && navC.getStrLabelCode() != null && navC.getStrLabelCode().equals(IConstants.NAVI_146)) {
								toTab = "EGS";
							}
					 		%>
					 		<li class="item">
						 		<a href="MyDashboard.action?userscreen=<%=IConstants.ADMIN %>&navigationId=<%=navigationId %>&toPage=<%=uF.showData(navC.getStrLabelCode(), "-") %>&toTab=<%=toTab %>" style="float: left; font-size: 16px; width: 100%; font-weight: 600;"><%=navC.getStrLabel() %></a>
						       <span style="float: left; width: 100%;"><%=uF.showData(navC.getStrDescription(), "-")  %></span>
						    </li>
					 	<% } %>
				 	</ul>
				 	
					<%-- <%
					 for(int i=0; i<alParentNavL.size(); i++){
					 	Navigation nav = (Navigation)alParentNavL.get(i);
					 	if(uF.parseToInt(nav.getStrNavId())!=46){
					 		continue;
					 	}
				 	%>
				 	
				 	<div class="dashboard_linksholder comp_info">
					<h2 class="heading">- <%=nav.getStrLabel() %><p style="font-size:10px;font-weight:normal"><%=uF.showData(nav.getStrDescription(), "-") %></p></h2>
					<div class="content1">
				 	<%
				 	List alListC = (List) hmChildNavL.get(nav.getStrNavId());
				 	if(alListC==null)alListC=new ArrayList();
				 	for(int ic=0; ic<alListC.size(); ic++){
				 		Navigation navC = (Navigation)alListC.get(ic);
					%>
				 		<div class="mg_ln"> <a href="<%=navC.getStrAction() %>"><%=navC.getStrLabel() %></a>
					       <div class="clr"></div>
					       <span class="infotext"><%= uF.showData(navC.getStrDescription(), "-")  %></span>
					    </div>
					<% } %>
				 	</div>
				 	</div>
				 	<div class="clr"></div>
				 	<% } %> --%>
				 	
			 	</div>
			</div>
		
		
			<div class="col-md-7">
				<div class="box box-body" style="height: 900px;overflow-y: auto;" id="actionResult">
					<%  
					if(toPage != null && toPage.equals(IConstants.NAVI_103)) { %>
	                    <s:action name="ConfigSettings" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_104)) { %>
	                    <s:action name="WLocationReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action> 
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_105)) { %>
	                    <s:action name="ServiceReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_106)) { %>
	                    <s:action name="DepartmentReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_142)) { %>
	                    <s:action name="MiscSetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_107)) { %>
	                    <s:action name="ProjectServiceReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_159)) { %>
	                    <s:action name="ProductionLine" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_160)) { %>
	                    <s:action name="PeoplePolicy" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>	                
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_124)) { %>
	                    <s:action name="CompanyManual" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_129)) { %>
	                    <s:action name="PerkPolicy" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_133)) { %>
	                    <s:action name="DeductionReportIndia" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_136)) { %>
	                    <s:action name="ExemptionReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_135)) { %>
	                    <s:action name="SectionReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_139)) { %>
	                    <s:action name="ESISetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_140)) { %>
	                    <s:action name="LWFSetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_138)) { %>
	                    <s:action name="EPFSetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_137)) { %>
	                    <s:action name="HRAReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_134)) { %>
	                    <s:action name="DeductionReportTax" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_128)) { %>
	                    <s:action name="BonusReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="financialYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_132)) { %>
	                    <s:action name="StatutoryIDAndRegistrationInfoReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_146)) { %>
	                	<% if(toTab != null && toTab.equals("EGS")) { %>
		                    <s:action name="ExGratiaSlabs" executeResult="true">
		                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
		                    	<s:param name="navigationId"><%=navigationId %></s:param>
		                    	<s:param name="toPage"><%=toPage %></s:param>
		                    	<s:param name="toTab"><%=toTab %></s:param>
		                    </s:action>
	                    <% } else if(toTab != null && toTab.equals("EGP")) { %>
	                    	<s:action name="AddExGratia" executeResult="true">
		                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
		                    	<s:param name="navigationId"><%=navigationId %></s:param>
		                    	<s:param name="toPage"><%=toPage %></s:param>
		                    	<s:param name="toTab"><%=toTab %></s:param>
		                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    		<s:param name="financialYear"><%=strCFYear %></s:param>
	                    		<s:param name="paycycle"><%=paycycle %></s:param>
		                    </s:action>
						<% } %>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_148)) { %>
	                    <s:action name="PayrollSetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_141)) { %>
	                    <s:action name="GratuityReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_130)) { %>
	                    <s:action name="LoanPolicyReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_122)) { %>
	                    <s:action name="OverTimeReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="f_org"><%=orgId %></s:param>
	                    </s:action>
	                
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_121)) { %>
	                    <s:action name="LeaveTypeReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_123)) { %>
	                    <s:action name="HolidayReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="calendarYear"><%=strCFYear %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_156)) { %>
	                    <s:action name="AllowancePolicy" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="strLevel"><%=strLevel %></s:param>
	                    	<s:param name="strSalaryHeadId"><%=strSalaryHeadId %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_126)) { %>
	                    <s:action name="SalaryDetails" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="level"><%=strLevel %></s:param>
	                    	<s:param name="salaryBand"><%=salaryBand %></s:param>
	                    	<s:param name="strGrade"><%=strGrade %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_109)) { %>
	                    <s:action name="BankReport1" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                 <% } else if(toPage != null && toPage.equals(IConstants.NAVI_147)) { %>
	                    <s:action name="WeeklyOffPolicy" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_111)) { %>
	                    <s:action name="ManageACL" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_120)) { %>
	                    <s:action name="ShiftRoster" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_112)) { %>
						<% if(toTab != null && toTab.equals("WFP")) { %>
	                    <s:action name="WorkFlowPolicyReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="toTab"><%=toTab %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="strLocation"><%=strLocation %></s:param>
	                    </s:action>
	                    <% } else if(toTab != null && toTab.equals("AWF")) { %>
	                    	<s:action name="AssignWorkFlow" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="toTab"><%=toTab %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="strLocation"><%=strLocation %></s:param>
	                    </s:action>
	                    <% } %>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_119)) { %>
	                    <s:action name="RosterPolicyReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="strWLocation"><%=strLocation %></s:param>
	                    </s:action>
	                <% } else if(toPage != null && toPage.equals(IConstants.NAVI_145)) { %>
	                    <s:action name="SuccessionPlanReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    	<s:param name="strLocation"><%=strLocation %></s:param>
	                    </s:action>    
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_113)) { %>
	                    <s:action name="NotificationSettings" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_117)) { %>
	                    <s:action name="OrientationDetails" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_143)) { %>
	                    <s:action name="DocumentList" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_116)) { %>
	                    <s:action name="SkillSet" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_108)) { %>
	                    <s:action name="LevelReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_115)) { %>
	                    <s:action name="AppraisalAttributeDetails" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_144)) { %>
	                    <s:action name="EducationSet" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_149)) { %>
	                    <s:action name="TaxAndBillingSetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
	                <%-- <% } else if(toPage != null && toPage.equals(IConstants.NAVI_150)) { %>
	                    <s:action name="TaxAndBillingSetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action> --%>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_152)) { %>
	                    <s:action name="InformationDisplay" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_153)) { %>
	                    <s:action name="TimesheetSetting" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_155)) { %>
	                    <s:action name="DocumentCategories" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_157)) { %>
	                    <s:action name="FormList" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="strOrg"><%=orgId %></s:param>
	                    </s:action>
					<% } else if(toPage != null && toPage.equals(IConstants.NAVI_158)) { %>
	                    <s:action name="ReimbursementPolicy" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    	<s:param name="f_org"><%=orgId %></s:param>
	                    </s:action>
                    <% } else if(toPage != null && toPage.equals(IConstants.NAVI_161)) { %>
                    <s:action name="ClassAndDivisionList" executeResult="true">
                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
                    	<s:param name="navigationId"><%=navigationId %></s:param>
                    	<s:param name="toPage"><%=toPage %></s:param>
                    	<s:param name="strOrg"><%=orgId %></s:param>
                    </s:action>
                    <% } else if(toPage != null && toPage.equals(IConstants.NAVI_162)) { %>
                    <s:action name="SubjectList" executeResult="true">
                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
                    	<s:param name="navigationId"><%=navigationId %></s:param>
                    	<s:param name="toPage"><%=toPage %></s:param>
                    	<s:param name="strOrg"><%=orgId %></s:param>
                    </s:action>
                    <% } else if(toPage != null && toPage.equals(IConstants.NAVI_163)) { %>
                    <s:action name="ParameterList" executeResult="true">
                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
                    	<s:param name="navigationId"><%=navigationId %></s:param>
                    	<s:param name="toPage"><%=toPage %></s:param>
                    	<s:param name="strOrg"><%=orgId %></s:param>
                    </s:action>
              <!-- ===start parvez date: 21-11-2022=== -->      
                   <% } else if(toPage != null && toPage.equals(IConstants.NAVI_164)) { %>
	                    <s:action name="ProjectDomainReport" executeResult="true">
	                    	<s:param name="userscreen"><%=IConstants.ADMIN %></s:param>
	                    	<s:param name="navigationId"><%=navigationId %></s:param>
	                    	<s:param name="toPage"><%=toPage %></s:param>
	                    </s:action>
	                <% } %>
			<!-- ===end parvez date: 21-11-2022=== -->		
				</div>
			</div>
		
		</div>

</section>
<script>


var getUrlParameter = function getUrlParameter(sParam,url) {
    var sPageURL = url,
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};

var active_page = '<%=toPage%>';
//console.log(active_page);
$(document).ready(function(){
	$('.products-list a').each(function(){
	   var a_href= $(this).attr('href');
	  
	   if(getUrlParameter('toPage',a_href) === active_page){
		   $(this).addClass("activelink");
	   }
	});
});

</script>