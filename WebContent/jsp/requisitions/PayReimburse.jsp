<%@page import="ChartDirector.AngularMeter"%>
<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<% String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
%>
<section class="content" style="padding: 0px;">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                <!-- Started By Dattatray Date:19-10-21 -->
                    <li class="active"><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('PaidUnpaidReimbursements','0');" data-toggle="tab" id="payReimburseId_0">Reimbursement</a></li>
                    <li><a href="javascript:void(0)" style="padding: 2px 12px;" onclick="getPerkPage('PayReimbursementCTC','1');" data-toggle="tab" id="payReimburseId_1">Reimbursement Part of CTC</a></li>
                <!-- Ended By Dattatray Date:19-10-21 -->
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
	getPerkPage('PaidUnpaidReimbursements','0');//Created By Dattatray Date:19-10-21
});

function getPerkPage(strAction,index){
	//alert("service ===>> " + service);
	disabledPointerAddAndRemove(2,'payReimburseId_',index,true);//Created By Dattatray Date:19-10-21
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: strAction+'.action',
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#subDivResult").html(result);
			disabledPointerAddAndRemove(2,'payReimburseId_',index,false);//Created By Dattatray Date:19-10-21
   		}
	});
}
</script>
