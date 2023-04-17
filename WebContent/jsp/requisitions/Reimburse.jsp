<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<% String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
String strUserType = (String)session.getAttribute(IConstants.USERTYPE); 
String alertID = (String) request.getAttribute("alertID");

//===start parvez date: 05-08-2022===	
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>) request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
//===end parvez date: 05-08-2022===

%>
<section class="content" style="padding: 0px;">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_MY_PAY_DISABLE_LINK))){ 
							List<String> disableTabList = hmFeatureUserTypeId.get(IConstants.F_MY_PAY_DISABLE_LINK);
					
					%>
						<% if(disableTabList != null && disableTabList.contains("EXPENSE")){ %>
	                    	<li class="active"><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('Reimbursements');" data-toggle="tab">Expense</a></li>
	                    <% } %>
	                    <% if(disableTabList != null && disableTabList.contains("REIMBURSEMENT_PART_OF_CTC")){ %>
	                    	<li><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('ReimbursementCTC');" data-toggle="tab">Reimbursement Part of CTC</a></li>
	                    <% } %>
                <% } else { %>
                		<li class="active"><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('Reimbursements');" data-toggle="tab">Expense</a></li>
	                    <li><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('ReimbursementCTC');" data-toggle="tab">Reimbursement Part of CTC</a></li>
                <% } %>
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
	getPerkPage('Reimbursements');
});

function getPerkPage(strAction){
	//alert("service ===>> " + service);
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action?alertID='+<%=alertID%>,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#subDivResult").html(result);
   		}
	});
}
</script>

