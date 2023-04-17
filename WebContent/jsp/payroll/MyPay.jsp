<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<% 	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	String callFrom = (String) request.getAttribute("callFrom"); 
	String alertID = (String) request.getAttribute("alertID");
	String []arrEnabledModules = CF.getArrEnabledModules(); 
	
//===start parvez date: 05-08-2022===	
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
	
//===end parvez date: 05-08-2022===

%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom"> 
                
	          <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_PAY_DISABLE_TAB))){ 
	        	  	List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_PAY_DISABLE_TAB);
	        	  	//System.out.println("hmFeatureUserTypeId="+hmFeatureUserTypeId);
	           %>
           			<ul class="nav nav-tabs">
	                    <% if(disableTabList != null && disableTabList.contains("SALARY_MY_PAY_CHECKS")){ %>
		                    <%if(arrEnabledModules!=null && (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0)) { %>
		                    	<li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('ViewPaySlips', '');" data-toggle="tab">Salary/ My Pay Checks</a></li>
		                    <% } %>
	                    <% } %>
	                    
	                    <% if(disableTabList != null && disableTabList.contains("EXPENSES")){ %>
		                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
		                    	<li <% if(callFrom != null && callFrom.equals("MyDashReimbursements")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('Reimburse' ,'');" data-toggle="tab">Expenses</a></li>
		                    <% } %>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		                   <% if(disableTabList != null && disableTabList.contains("IT_DECLARATIONS")){ %>
		                   		<li <% if(callFrom != null && callFrom.equals("NotiITDeclarations")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('InvestmentForm', '');" data-toggle="tab">IT Declarations</a></li>
		                   <% } %>
		                   <% if(disableTabList != null && disableTabList.contains("LOANS")){ %>
		                   		<li <% if(callFrom != null && callFrom.equals("LoanApplicationReport")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('LoanApplicationReport', '');" data-toggle="tab">Loans</a></li>
		                   <% } %>
		                   <% if(disableTabList != null && disableTabList.contains("PERKS")){ %>
		                   		<li <% if(callFrom != null && callFrom.equals("NotiPerkApprove")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('Perk', '');" data-toggle="tab">Perks</a></li>
		                   <% } %>
		                   <% if(disableTabList != null && disableTabList.contains("CTC_VARIABLE")){ %>
		                   		<li <% if(callFrom != null && callFrom.equals("NotiLTA")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('CTCVariable' ,'');" data-toggle="tab">CTC Variable</a></li>
		                   <% } %>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
		                    <% if(disableTabList != null && disableTabList.contains("LEAVE_ENCASHMENTS")){ %>
			                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
			                    	<li <% if(callFrom != null && callFrom.equals("MyLeaveEncashment")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('LeaveEncashment', '');" data-toggle="tab">Leave Encashments</a></li>
			                    <% } %>
		                    <% } %>
	                    <% } %>
	                </ul>
           
				<% } else { %>
                
	                <ul class="nav nav-tabs">
	                    <%if(arrEnabledModules!=null && (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0)) { %>
	                    	<li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('ViewPaySlips', '');" data-toggle="tab">Salary/ My Pay Checks</a></li>
	                    <% } %>
	                    <%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
		                    <li <% if(callFrom != null && callFrom.equals("MyDashLeaveSummary") || callFrom != null && callFrom.equals("NotiApplyTravel")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('EmployeeLeaveEntryReport', 'L');" data-toggle="tab">Leave & Travel</a></li>
	                    <% } %> --%>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
	                    	<li <% if(callFrom != null && callFrom.equals("MyDashReimbursements")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('Reimburse' ,'');" data-toggle="tab">Expenses</a></li>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		                   <li <% if(callFrom != null && callFrom.equals("NotiITDeclarations")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('InvestmentForm', '');" data-toggle="tab">IT Declarations</a></li>
		                   <li <% if(callFrom != null && callFrom.equals("LoanApplicationReport")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('LoanApplicationReport', '');" data-toggle="tab">Loans</a></li>
		                   <li <% if(callFrom != null && callFrom.equals("NotiPerkApprove")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('Perk', '');" data-toggle="tab">Perks</a></li>
		                   <li <% if(callFrom != null && callFrom.equals("NotiLTA")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('CTCVariable' ,'');" data-toggle="tab">CTC Variable</a></li>
	                    <% } %>
	                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>
		                    <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		                    	<li <% if(callFrom != null && callFrom.equals("MyLeaveEncashment")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getMyPayPage('LeaveEncashment', '');" data-toggle="tab">Leave Encashments</a></li>
		                    <% } %>
	                    <% } %>
	                </ul>
                <% } %>
                <input type="hidden" name="alertID" id="alertID"/>
                <div class="tab-content">  
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;"></div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	
	<% if(callFrom != null && callFrom.equals("MyDashReimbursements")) { %>
		getMyPayPage('Reimburse',"");
	<%} else if(callFrom != null && callFrom.equals("NotiPerkApprove")) { %>
		getMyPayPage('Perk',"");
	<%} else if(callFrom != null && callFrom.equals("NotiITDeclarations")) { %>
		getMyPayPage('InvestmentForm',"");
	<% } else if(callFrom != null && callFrom.equals("LoanApplicationReport")) { %>
		getMyPayPage('LoanApplicationReport',"");
	<% } else if(callFrom != null && callFrom.equals("NotiLTA")) { %>
		getMyPayPage('CTCVariable',"");
	<% } else if(callFrom != null && callFrom.equals("MyLeaveEncashment")) { %>
		getMyPayPage('LeaveEncashment',"");
	<% }else { %>
		
		<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_PAY_DISABLE_TAB))){ 
		  	List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_PAY_DISABLE_TAB);
		%>
			<% if(disableTabList != null && disableTabList.get(0).equals("SALARY_MY_PAY_CHECKS")){ %>
				getMyPayPage('ViewPaySlips',"");
			<% } else if(disableTabList != null && disableTabList.get(0).equals("EXPENSES")){ %>
				getMyPayPage('Reimburse' ,'');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("IT_DECLARATIONS")){ %>
				getMyPayPage('InvestmentForm', '');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("LOANS")){ %>
				getMyPayPage('LoanApplicationReport', '');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("PERKS")){ %>
				getMyPayPage('Perk', '');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("CTC_VARIABLE")){ %>
				getMyPayPage('CTCVariable' ,'');
			<% } else if(disableTabList != null && disableTabList.get(0).equals("LEAVE_ENCASHMENTS")){ %>
				getMyPayPage('LeaveEncashment', '');
			<% }%>	
		<% } else{ %>
			getMyPayPage('ViewPaySlips',"");
		<% } %>	
	<% } %>
   
});

function getMyPayPage(strAction,type){
	//alert("strAction ===>> " + strAction);
	var action = strAction+'.action?alertID='+<%=alertID%>+'&dataType='+type;
	<% if(callFrom != null && callFrom.equals("NotiITDeclarations")) { %>
		var strEmployeeId = '<%=(String) request.getAttribute("strEmployeeId")%>';
		var f_strFinancialYear = '<%=(String) request.getAttribute("f_strFinancialYear")%>';
		action +='&f_strFinancialYear='+f_strFinancialYear+'&strEmployeeId='+strEmployeeId;
	<%}%>
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		cache: true, 
		//async: false,
		success: function(result){
			//alert("result ===>> " + result);
			$("#divResult").html(result);
   		}
	});
}


</script>
