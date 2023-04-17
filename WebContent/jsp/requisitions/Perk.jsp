<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<% String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String currUserType = (String)request.getAttribute("currUserType");
String alertID = (String) request.getAttribute("alertID");
String callFrom = (String)request.getAttribute("callFrom");
%>
<section class="content" style="padding: 0px;">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
	                <% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD)) && strUserType != null && strUserType.equals(IConstants.MANAGER)) { %>
	                	<li class="active"><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('CeoHodPerkRequest','','','');" data-toggle="tab">Perks</a></li>
	                <% } else { %>
	                    <li class="active"><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('Perks','','','');" data-toggle="tab">Perks</a></li>
                    <% } %>
                    <% if (strUserType != null && !strUserType.equals(IConstants.EMPLOYEE)) { %>
                    	<li><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('PerkIncentive','','','');" data-toggle="tab">Perk & Incentive</a></li>
                    <% } %>
                    <!-- <li><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('PerkInSalary');" data-toggle="tab">Perk in Salary</a></li> -->
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
	//alert("Perk ===>> ");
	<% if (strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD)) && strUserType != null && strUserType.equals(IConstants.MANAGER)) { %>
		getPerkPage('CeoHodPerkRequest','<%=currUserType %>','<%=alertID%>','<%=callFrom%>');
	<% } else { %>
		getPerkPage('Perks','MYTEAM','<%=alertID%>','<%=callFrom%>');
	<% } %>
});

function getPerkPage(strAction , currUserType ,alertID,callFrom){
	//alert("service ===>> " + service);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?currUserType='+currUserType+'&alertID='+alertID+'&callFrom='+callFrom,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#subDivResult").html(result);
   		}
	});
}
</script>

