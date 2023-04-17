<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%String callFrom = (String) request.getAttribute("callFrom");
String alertID = (String) request.getAttribute("alertID");
//String paycycle=(String) request.getAttribute("paycycle");
//String fromPage=(String)request.getAttribute("fromPage");

String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
//System.out.println("USERTYPE=="+ strUserType);
//System.out.println("callFrom=="+ callFrom +"alertID=="+alertID);

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
String []arrEnabledModules = CF.getArrEnabledModules();

%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
	                <%if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.OTHER_HR)){ %>
	                	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>    
		                    <li id="liReimbursement" <% if(callFrom != null && callFrom.equals("NotiApplyReimbursement")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('Reimburse');" data-toggle="tab">Reimbursements</a></li>
		                <% } %>
	                <%} else{ %>
		                <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('ApprovePay');" data-toggle="tab">Pay</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyLoan")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('LoanApplicationReport');" data-toggle="tab">Loan</a></li>
		                <% } %>
		                <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>    
		                    <li id="liReimbursement" <% if(callFrom != null && callFrom.equals("NotiApplyReimbursement")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('Reimburse');" data-toggle="tab">Reimbursements</a></li>
		                <% } %>    
		                <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>    
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyPerk")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('Perk');" data-toggle="tab">Perks</a></li>
		                    <li><a href="javascript:void(0)" onclick="getPayApprovalsPage('ExGratiaForm');" data-toggle="tab">Ex-Gratia</a></li>
							
							<!--<li><a href="javascript:void(0)" onclick="getPayApprovalsPage('OvertimeHourAndForm');" data-toggle="tab">Overtime</a></li>-->	                    
		                    
		                    <li <%if(callFrom !=null && callFrom.equals("NotiOvertime")){ %> class="active" <%}%>> <a href="javascript:void(0)" onclick="getPayApprovalsPage('OvertimeHourAndForm');" data-toggle="tab">Overtime</a></li>
		                  
		                    <li <% if(callFrom != null && callFrom.equals("NotiLeaveEncashment")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('LeaveEncashment');" data-toggle="tab">Leave Encashment</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("NotiApplyLTA")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('CTCVariable');" data-toggle="tab">CTC Variable</a></li>
		                    <li <% if(callFrom != null && callFrom.equals("AWF")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayApprovalsPage('AllowanceForm');" data-toggle="tab">Allowance</a></li>
		               		<li><a href="javascript:void(0)" onclick="getPayApprovalsPage('BonusForm');" data-toggle="tab">Bonus</a></li>
		               	<% } %>
	               	<% } %>
                </ul>
                <div class="tab-content"> 
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;"></div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<% if(callFrom != null && callFrom.equals("NotiApplyLoan")) { %>
		getPayApprovalsPage('LoanApplicationReport');
	<% } else if(callFrom != null && callFrom.equals("NotiApplyReimbursement")) { %>
		getPayApprovalsPage('Reimburse');
	<% } else if(callFrom != null && callFrom.equals("NotiApplyPerk")) { %>
		getPayApprovalsPage('Perk');
	<% } else if(callFrom != null && callFrom.equals("NotiLeaveEncashment")) { %>
		getPayApprovalsPage('LeaveEncashment');
	<% } else if(callFrom != null && callFrom.equals("NotiApplyLTA")) { %>
		getPayApprovalsPage('CTCVariable');
	<% } else if(callFrom != null && callFrom.equals("AWF")) { %>paycycle
		getPayApprovalsPage('AllowanceForm');
		
	<% } else if(callFrom != null && callFrom.equals("NotiOvertime")) {%>
		getPayApprovalsPage('OvertimeHourAndForm');
	
	<% } else { %>
		<%if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.OTHER_HR)){ %>
			getPayApprovalsPage('Reimburse');
			document.getElementById('liReimbursement').className = "active";
		<%} else{ %>
			<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
				getPayApprovalsPage('ApprovePay');
			<% } else { %>
				getPayApprovalsPage('Reimburse');
				document.getElementById('liReimbursement').className = "active";
			<% } %>
		<% } %>
	<% } %>
	
});

function getPayApprovalsPage(strAction){

	var action = strAction+'.action';
	
	var paycycle=null;
	
	<% if(callFrom != null && callFrom.equals("AWF")) { %>
	<%-- 	 paycycle = '<%=paycycle%>'; --%>
		 var paycycle = '<%=(String) request.getAttribute("paycycle")%>';
	
		var f_org = '<%=(String) request.getAttribute("f_org")%>';
		var f_level = '<%=(String) request.getAttribute("f_level")%>';
		action +='?paycycle='+paycycle+'&f_org='+f_org+'&f_level='+f_level+'&alertID='+<%=alertID%>;
	
	<%} else if(callFrom != null && callFrom.equals("NotiOvertime")){ System.out.println("in callFrom" +callFrom); %>	
		<%-- 
		 paycycle = '<%=paycycle%>'; --%>
		 var paycycle = '<%=(String) request.getAttribute("paycycle")%>';
		 var fromPage = '<%=(String) request.getAttribute("fromPage")%>';
	
		 action +='?paycycle='+paycycle+"&fromPage="+fromPage;
		
	
	<% } else { %>
		action +='?alertID='+<%=alertID%>;
	<%}%>
	
	var strLodingMsg = '<div id="ajaxLoadImage"></div>';
	
	if(strAction == 'ApprovePay'){
		strLodingMsg = '<div id="ajaxLoadImage" style="text-align:center;">Processing & Displaying</div>';
	}
	
	$("#divResult").html('<div id="the_div">'+strLodingMsg+'</div>');
	
	//alert("action==>"+action);
	
	
	$.ajax({ 
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result ===>> " + result);
			$("#divResult").html(result);
   		}
	});
}

 
</script>