<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<% String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE); 
	String currUserType = (String)request.getAttribute("currUserType");
	String callFrom = (String)request.getAttribute("callFrom");
%>

<section class="content" style="padding: 0px;">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li <% if(currUserType == null || !currUserType.equals(strBaseUserType)) { %> class="active" <% } %>><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getCeoHodResignationRequestPage('ResignationReport', 'MYTEAM');" data-toggle="tab">My Team</a></li>
                    <li <% if(callFrom != null && callFrom.equals("NotiResignation") && currUserType != null && currUserType.equals(strBaseUserType)) { %> class="active" <% } %>><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getCeoHodResignationRequestPage('ResignationReport', '<%=strBaseUserType %>');" data-toggle="tab"><%=strBaseUserType %></a></li>
                </ul>
                <div class="tab-content" >
                    <div class="active tab-pane" id="subDivResult" style="min-height: 600px;">
						
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	//alert("Perks ===>> ");
	<% if(callFrom != null && callFrom.equals("NotiResignation") && currUserType != null && currUserType.equals(strBaseUserType)) { %> 
		getCeoHodResignationRequestPage('ResignationReport', '<%=strBaseUserType %>');
	<% } else { %>
		getCeoHodResignationRequestPage('ResignationReport', 'MYTEAM');
	<% } %>
});

function getCeoHodResignationRequestPage(strAction, currUserType) {
	//alert("service ===>> " + service);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?currUserType='+currUserType,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#subDivResult").html(result);
   		}
	});
}
</script>

