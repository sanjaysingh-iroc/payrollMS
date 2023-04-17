<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<script src="scripts/charts/jquery.min.js"></script>
<% String callFrom = (String) request.getAttribute("callFrom");%>

<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                <!-- Started By Dattatray Date:19-10-21 -->
                    <li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getCompliancePage('InvestmentReport','0');" data-toggle="tab" id="compalianceId_0">Investment Declarations</a></li>
                    <li <% if(callFrom != null && callFrom.equals("NotiITDeclarations")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getCompliancePage('InvestmentForm','1');" data-toggle="tab" id="compalianceId_1">IT Declarations</a></li>
                    <li <% if(callFrom != null && callFrom.equals("ITDSProjection")) { %> class="active" <% } %> > <a href="javascript:void(0)" onclick="getCompliancePage('TDSProject','2');" data-toggle="tab" id="compalianceId_2">TDS Projection</a></li>
                    <li><a href="javascript:void(0)" onclick="getCompliancePage('ServiceTaxDependency','3');" data-toggle="tab" id="compalianceId_3">Service Tax Dependency</a></li>
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

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript" charset="utf-8">
$(document).ready(function() {
	<% if(callFrom == null || callFrom.equals("")) { %>
		getCompliancePage('InvestmentReport','0');
	<% } else if(callFrom != null && callFrom.equals("ITDSProjection")) { %>
		getCompliancePage('TDSProject','2');
	<% } else if(callFrom != null && callFrom.equals("NotiITDeclarations")) { %>
		getCompliancePage('InvestmentForm','1');
	<% } %>
});

function getCompliancePage(strAction,index){
	//alert("service ===>> " + service);
	disabledPointerAddAndRemove(4,'compalianceId_',index,true);//Created By Dattatray Date:19-10-21
	var action = strAction+'.action';
	<% if(callFrom != null && callFrom.equals("NotiITDeclarations")) { %>
		var strEmployeeId = '<%=(String) request.getAttribute("strEmployeeId")%>';
		var f_strFinancialYear = '<%=(String) request.getAttribute("f_strFinancialYear")%>';
		
		action +='?f_strFinancialYear='+f_strFinancialYear+'&strEmployeeId='+strEmployeeId;
	<%}%>
	
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		success: function(result){
			$("#divResult").html(result);
			disabledPointerAddAndRemove(4,'compalianceId_',index,false);//Created By Dattatray Date:19-10-21
   		}
	});
}
 
 

</script>

