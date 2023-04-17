<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>

	<% 	//String callFrom = (String) request.getAttribute("callFrom");
		CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
		
		String pageType = (String)request.getAttribute("pageType");
		String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
		//System.out.println("pageType ===>> " + pageType);
		
	%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
            	<input type="hidden" name="pageType" id="pageType" value="<%=pageType %>" />
            	<input type="hidden" name="strPaycycle" id="strPaycycle" />
                <ul class="nav nav-tabs">
                    <% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
                    	<li class="active"><a href="javascript:void(0)" onclick="getTimesheetsApprovalPage('ProjectTimesheets','');" data-toggle="tab">Unapproved</a></li>
                    	<li><a href="javascript:void(0)" onclick="getTimesheetsApprovalPage('ProjectTimesheets','EA');" data-toggle="tab">Approved</a></li>
                    <% } %>
                    <% if(pageType == null || !pageType.equals("MP")) { %>
                    	<span style="float: left; margin: 5px 0px; height: 30px; border-left: 3px solid lightgray;">&nbsp;</span>
	                    <li><a href="javascript:void(0)" onclick="getTimesheetsApprovalPage('ProjectTimesheets','PC');" data-toggle="tab">Current Timesheets</a></li>
	                    <li><a href="javascript:void(0)" onclick="getTimesheetsApprovalPage('ProjectTimesheets','PA');" data-toggle="tab">Approved Timesheets</a></li>
	                    <% if(strUserType!=null && !strUserType.equals(IConstants.CUSTOMER)) { %>
	                    	<li><a href="javascript:void(0)" onclick="getTimesheetsApprovalPage('ProjectTimesheets','PU');" data-toggle="tab">Unused Timesheets</a></li>
	                    <% } %>
                    <% } %>
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

$(function(){
	getTimesheetsApprovalPage('ProjectTimesheets', 'EU');
});

function getTimesheetsApprovalPage(strAction, timesheetType) {
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	var pageType = document.getElementById("pageType").value;
	var paycycle = '';
	if(document.getElementById("strPaycycle")) {
		paycycle = document.getElementById("strPaycycle").value;
	}
	//alert("paycycle ==>> " + paycycle+ " -- pageType ===>> " + pageType);
	$.ajax({
		url: strAction+'.action?timesheetType='+timesheetType+'&pageType='+pageType+'&paycycle='+paycycle,
		success: function(result){
			//alert(result);
			$("#divResult").html(result);
   		}
	});
}

</script>


