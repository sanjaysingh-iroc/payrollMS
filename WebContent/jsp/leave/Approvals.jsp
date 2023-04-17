<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<% String callFrom = (String) request.getAttribute("callFrom"); 
String alertID = (String) request.getAttribute("alertID");

String strpaycycle1=(String)request.getAttribute("strpaycycle1");
String fromPage=(String)request.getAttribute("fromPage");

String strUserType = (String) session.getAttribute(IConstants.USERTYPE);
%>
<section class="content"> 
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <%if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.OTHER_HR)){ %>
                    	<li <% if(callFrom == null || callFrom.equals("") || callFrom.equals("NotiApplyLeave")) { %> class="active" <% } %>><a  href="javascript:void(0)" onclick="getApprovalPage('ManagerLeaveApprovalReport','','0');" data-toggle="tab" id="approvalsId_0">Leave Approval</a></li>
                    <% } else{ %>
	                    <li <% if(callFrom == null || callFrom.equals("") || callFrom.equals("NotiApplyLeave")) { %> class="active" <% } %>><a  href="javascript:void(0)" onclick="getApprovalPage('ManagerLeaveApprovalReport','','0');" data-toggle="tab" id="approvalsId_0">Leave Approval</a></li>
	                    <li <% if(callFrom != null && callFrom.equals("NotiApplyTravel")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getApprovalPage('TravelApprovalReport','','1');" data-toggle="tab" id="approvalsId_1">Travel Approval</a></li>
                	<%} %>
                </ul>
                <div class="tab-content" >   
                    <div class="active tab-pane" id="divResult" style="min-height: 600px;">
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<%if(strUserType != null && strUserType.equalsIgnoreCase(IConstants.OTHER_HR)){ %>
		getApprovalPage('ManagerLeaveApprovalReport','<%=alertID%>','0');
	<%} else{ %>
		<% if(callFrom != null && callFrom.equals("NotiApplyTravel")) { %>
			getApprovalPage('TravelApprovalReport','<%=alertID%>','1');
		<% } else{ %>
			getApprovalPage('ManagerLeaveApprovalReport','<%=alertID%>','0');
		<%} %>
	<%} %>
});

function getApprovalPage(strAction,alertID,index){
	disabledPointerAddAndRemove(2,'approvalsId_',index,true);//Created By Dattatray Date:18-10-21
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	<% if(strpaycycle1!=null || fromPage!=null ){%>

	var strpaycycle1= "<%= strpaycycle1%>";
	var fromPage= "<%= fromPage%>";
	
	alertID='&strpaycycle1='+strpaycycle1+'&fromPage='+fromPage;
	
	
<%}%>
	$.ajax({
		type : 'POST',
		url: strAction+'.action?alertID='+alertID,
		cache: true,
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divResult").html(result);
			disabledPointerAddAndRemove(2,'approvalsId_',index,false);//Created By Dattatray Date:18-10-21
   		}
	});
}


</script>