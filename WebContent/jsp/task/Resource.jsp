<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<% String callFrom = (String) request.getAttribute("callFrom"); %> 
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getResourcePage('Peoples');" data-toggle="tab">Working</a></li>
                    <li <% if(callFrom != null && callFrom.equals("ADDEMP")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getResourcePage('PendingPeople');" data-toggle="tab">Pending</a></li>
                    <li <% if(callFrom != null && callFrom.equals("USERS")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getResourcePage('PeopleUser');" data-toggle="tab">Users</a></li>
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
	<% if(callFrom != null && !callFrom.equals("ADDEMP")) { %>
		getResourcePage('PendingPeople');
	<% } else if(callFrom != null && !callFrom.equals("USERS")) { %>
		getResourcePage('PeopleUser');
	<% } else { %>
		getResourcePage('Peoples');
	<% } %>
});

function getResourcePage(strAction){
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action',
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			//alert("result ===>> " + result);
			$("#divResult").html(result);
   		}
	});
}

</script>