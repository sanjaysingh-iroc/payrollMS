<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<% 
	String callFrom = (String) request.getAttribute("callFrom");
	
	String strUserType = (String) session.getAttribute(IConstants.USERTYPE);

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
		                    <li id="liReimbursement" <% if(callFrom != null && callFrom.equals("NotiApproveReimbursement")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayPayPage('PayReimburse','1');" data-toggle="tab" id="pay_1">Reimbursements</a></li>
						<% } %>
	                <% } else{ %>
		                <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayPayPage('PayPayroll','0');" data-toggle="tab" id="pay_0">Salary</a></li>
		                <% } %>
		                <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_EXPENSE_MANAGEMENT+"")>=0) { %>
		                    <li id="liReimbursement" <% if(callFrom != null && callFrom.equals("NotiApproveReimbursement")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayPayPage('PayReimburse','1');" data-toggle="tab" id="pay_1">Reimbursements</a></li>
						<% } %>
		                <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>    
		                    <li <% if(callFrom != null && callFrom.equals("NotiApprovePerk")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getPayPayPage('PaidUnpaidPerks','2');" data-toggle="tab" id="pay_2">Perks</a></li>
		                    <li><a href="javascript:void(0)" onclick="getPayPayPage('PayCTCVariable','3');" data-toggle="tab" id="pay_3">CTC Variable</a></li>
		                    <li><a href="javascript:void(0)" onclick="getPayPayPage('ExGratiaForm','4');" data-toggle="tab" id="pay_4">Ex-Gratia</a></li>
		                    <li><a href="javascript:void(0)" onclick="getPayPayPage('EmpGratuityReport','5');" data-toggle="tab" id="pay_5">Gratuity</a></li>
		                <% } %>
		                <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { %>    
		                    <li><a href="javascript:void(0)" onclick="getPayPayPage('AdvanceReport','6');" data-toggle="tab" id="pay_6">Travel Advance</a></li>
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
	<% if(callFrom != null && callFrom.equals("NotiApproveReimbursement")) { %>
		getPayPayPage('PayReimburse');
	<% } else if(callFrom != null && callFrom.equals("NotiApprovePerk")) { %>
		getPayPayPage('PaidUnpaidPerks');
	<% } else { %>
		<%if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.OTHER_HR)){ %>
			getPayPayPage('PayReimburse');
			document.getElementById('liReimbursement').className = "active";
		<% } else{ %>
	
			<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
				getPayPayPage('PayPayroll','0');//Created By Dattatray Date:19-10-21
			<% } else { %>
				getPayPayPage('PayReimburse');
				document.getElementById('liReimbursement').className = "active";
			<% } %>
		<% } %>
	<% } %>
});

function getPayPayPage(strAction,index){
	//alert("strAction ===>> " + strAction);
	disabledPointerAddAndRemove(7,'pay_',index,true);//Created By Dattatray Date:19-10-21
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action',
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result ===>> " + result);
			$("#divResult").html(result);
			disabledPointerAddAndRemove(7,'pay_',index,false);//Created By Dattatray Date:19-10-21
   		}
	});
}


</script>