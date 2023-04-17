<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>

<% 	String callFrom = (String) request.getAttribute("callFrom"); 
	String profileEmpId = (String) request.getAttribute("profileEmpId");
	String paycycle1 =(String) request.getAttribute("strpaycycle1");
	String fromPage=(String)request.getAttribute("fromPage");
	//System.out.println("paycycle"+paycycle1+"fromPage"+fromPage);
%>
<script>
function Payroll_dashboard_link1(paycycle1)
{
	window.location='PayrollDashboard_2.action?strpaycycle1='+paycycle1;
}
</script>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                <!-- Started By Dattatray Date:18-10-21  -->
                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTimeApprovalPage('ApproveAttendance', '', '0');" data-toggle="tab" id="timeApprovalsID_0">Approve Clock Entries</a></li>
                    <li><a href="javascript:void(0)" onclick="getTimeApprovalPage('ClockEntriesReport', '','1');" data-toggle="tab" id="timeApprovalsID_1">Clock Entries Report</a></li>
                    <li <% if(callFrom != null && callFrom.equals("FactQuickLinkClockEntry")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTimeApprovalPage('ClockEntries', '','2');" data-toggle="tab" id="timeApprovalsID_2">Individual Clock Entries</a></li>
                    <li <% if(callFrom != null && callFrom.equals("HRDashTimeExceptions")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTimeApprovalPage('UpdateClockEntries', '','3');" data-toggle="tab" id="timeApprovalsID_3">Clock On/Off Exceptions</a></li>
                    <li <% if(callFrom != null && callFrom.equals("HRDashApproveOT")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getTimeApprovalPage('OvertimeApproval', '','4');" data-toggle="tab" id="timeApprovalsID_4">Approve Overtime</a></li>
               <!-- Ended By Dattatray Date:18-10-21  -->
                </ul>
                <div class="tab-content" >
                <!-- ===start parvez date: 24-02-2023=== -->
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;"></div>
               <!-- ===end parvez date: 24-02-2023=== -->     
                	 <div class="col-md-3"></div>
                     <div class="col-md-3"></div>
         		 	 <div class="col-md-3"></div>
          			 <% if(fromPage!= null && fromPage.equalsIgnoreCase("CP")){%>
           			<div class="col-md-3">
           				<button type="button" onclick="Payroll_dashboard_link1('<%=paycycle1%>')">Go Back to PayrollDashboard</button>
           			</div>
           			 <% } else if(fromPage!= null && fromPage.equalsIgnoreCase("AC")){%>
           			<div class="col-md-3">
           				<button type="button" onclick="Payroll_dashboard_link1('<%=paycycle1%>')">Go Back to PayrollDashboard</button>
           			</div>
           			<%} %>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(function() {
	<% if(callFrom != null && callFrom.equals("HRDashTimeExceptions")) { %>
		getTimeApprovalPage('UpdateClockEntries', '','3');//Created By Dattatray Date:18-10-21
	<% } else if(callFrom != null && callFrom.equals("FactQuickLinkClockEntry")) { %>
		var profileEmpId = '<%=profileEmpId %>';
		var strSelectedEmpId = '?strSelectedEmpId='+profileEmpId;
		getTimeApprovalPage('ClockEntries', strSelectedEmpId,'2');//Created By Dattatray Date:18-10-21
	<% } else { %>
		getTimeApprovalPage('ApproveAttendance', '','0');//Created By Dattatray Date:18-10-21
	<% } %>
});

function getTimeApprovalPage(strAction, paramenters,index) {
	//alert("service ===>> " + service);
	disabledPointerAddAndRemove(5,'timeApprovalsID_',index,true);//Created By Dattatray Date:18-10-21
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	<% if(paycycle1 != null || fromPage!= null) { %>
		var paycycle= '<%=paycycle1 %>';
		var fromPage= '<%=fromPage %>';
		if(paramenters == '') {
			paramenters = "?paycycle="+paycycle;
		} else {
			paramenters = paramenters +"&paycycle="+paycycle+'&fromPage='+fromPage;	
		}
	<% } %>

	$.ajax({
		type : 'POST',
		url: strAction+'.action'+paramenters,
		data: $("#"+this.id).serialize(),
		success: function(result) {
			disabledPointerAddAndRemove(5,'timeApprovalsID_',index,false);//Created By Dattatray Date:18-10-21
			$("#divResult").html(result);
			
   		}
	});
}


</script>

