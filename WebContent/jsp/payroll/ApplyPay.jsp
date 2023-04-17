<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%String callFrom = (String) request.getAttribute("callFrom");%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                 <!-- Started By Dattatray Date:19-10-21 -->
                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="#leaveCal" onclick="getApplyPayPage('IncentiveForm','0');" data-toggle="tab" id="applyPayId_0">Incentives</a></li>
                    <li <% if(callFrom != null && callFrom.equals("IVariable")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getApplyPayPage('Variables','1');" data-toggle="tab" id="applyPayId_1">Variables</a></li>
                    <li <% if(callFrom != null && callFrom.equals("AVP")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getApplyPayPage('AnnualVariablePolicy','2');" data-toggle="tab" id="applyPayId_2">Assign Annual Variable</a></li>
                    <li><a href="javascript:void(0)" onclick="getApplyPayPage('AnnualVariableForm','3');" data-toggle="tab" id="applyPayId_3">Apply Annual Variable</a></li>
                    <li><a href="javascript:void(0)" onclick="getApplyPayPage('PayArrears','4');" data-toggle="tab" id="applyPayId_4">Arrears</a></li>
                    <li <% if(callFrom != null && callFrom.equals("ICONTRI")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getApplyPayPage('ContributionAmountAssignPolicy','5');" data-toggle="tab" id="applyPayId_5">Assign Contribution</a></li>
                <!-- Ended By Dattatray Date:19-10-21 -->
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
	<% if(callFrom != null && callFrom.equals("AVP")) { %>
		getApplyPayPage('AnnualVariablePolicy','2');//Created By Dattatray Date:19-10-21	
	<% } else if(callFrom != null && callFrom.equals("ICONTRI")) { %>
		getApplyPayPage('ContributionAmountAssignPolicy','5');	//Created By Dattatray Date:19-10-21
	<% } else if(callFrom != null && callFrom.equals("IVariable")){ %>	
		getApplyPayPage('Variables','1');//Created By Dattatray Date:19-10-21
	<% }  else { %>	
		getApplyPayPage('IncentiveForm','0');//Created By Dattatray Date:19-10-21
	<% } %>
});

function getApplyPayPage(strAction,index){
	disabledPointerAddAndRemove(6,'applyPayId_',index,true);//Created By Dattatray Date:19-10-21
	var action = strAction+'.action';
	<% if(callFrom != null && callFrom.equals("AVP")) { %>
		var financialYear = '<%=(String) request.getAttribute("financialYear")%>';
		var strOrg = '<%=(String) request.getAttribute("strOrg")%>';
		var strLevel = '<%=(String) request.getAttribute("strLevel")%>';
		var strSalaryHeadId = '<%=(String) request.getAttribute("strSalaryHeadId")%>';
		
		action +='?financialYear='+financialYear+'&strOrg='+strOrg+'&strLevel='+strLevel+'&strSalaryHeadId='+strSalaryHeadId;
	<% } %>
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#divResult").html(result);
			disabledPointerAddAndRemove(6,'applyPayId_',index,false);//Created By Dattatray Date:19-10-21
   		}
	});
}


</script>