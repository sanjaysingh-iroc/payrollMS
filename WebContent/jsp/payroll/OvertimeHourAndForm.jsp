<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<% String paycycle=(String)request.getAttribute("paycycle");
System.out.println("paycycle"+paycycle);
String fromPage=(String) request.getAttribute("fromPage");
System.out.println("fromPage"+fromPage);
%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<section class="content" style="padding: 0px;" >
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <li class="active"><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getOverTimePage('OvertimeForm');" data-toggle="tab">Overtime Form</a></li>
                    <li><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getOverTimePage('OverTimeHours');" data-toggle="tab">Overtime Hours</a></li>
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
	getOverTimePage('OvertimeForm');
});

function getOverTimePage(strAction){
	var paramenters="";
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');

	<% if(paycycle != null || fromPage!= null) { %>
		var paycycle= '<%= paycycle %>';
		var fromPage= '<%= fromPage%>' ;
			paramenters ='?paycycle='+paycycle+'&fromPage='+fromPage;
	<%}%>
	
	
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action'+paramenters,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#subDivResult").html(result);
   		}
	});
}

</script>