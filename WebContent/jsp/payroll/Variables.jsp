<%@page import="java.util.*"%>
<%@page import="com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 

<%String callFrom = (String) request.getAttribute("callFrom");%>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="nav-tabs-custom">
                <ul class="nav nav-tabs">
                    <!-- Started By Dattatray Date:19-10-21  -->
                    <li <% if(callFrom == null || callFrom.equals("IVariable")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getVariablePage('VariableForm','0');" data-toggle="tab" id="variablesId_0">Bulk Employee Variables</a></li>
                    <li <% if(callFrom != null && callFrom.equals("EmpVariable")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getVariablePage('EmpVariableForm','1');" data-toggle="tab" id="variablesId_1">Individual Employee Variables</a></li>
               <!-- Ended By Dattatray Date:19-10-21  -->
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
	<% if(callFrom != null && callFrom.equals("EmpVariable")) { %>
	getVariablePage('EmpVariableForm','1');	//Created By Dattatray Date:19-10-21
	<% } else if(callFrom != null && callFrom.equals("IVariable")) { %>	
	getVariablePage('VariableForm','0');//Created By Dattatray Date:19-10-21
	<% }  else { %>	
	getVariablePage('VariableForm','0');//Created By Dattatray Date:19-10-21
	<% } %>
});

function getVariablePage(strAction,index){
	disabledPointerAddAndRemove(2,'variablesId_',index,true);//Created By Dattatray Date:19-10-21
	var action = strAction+'.action';
	$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({ 
		type : 'POST',
		url: action,
		data: $("#"+this.id).serialize(),
		cache: true,
		success: function(result){
			$("#subDivResult").html(result);
			disabledPointerAddAndRemove(2,'variablesId_',index,false);//Created By Dattatray Date:19-10-21
   		}
	});
}

</script>