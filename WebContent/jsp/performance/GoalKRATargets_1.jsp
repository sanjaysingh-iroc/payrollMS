<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
</script>
<%
String callFrom = (String)request.getAttribute("callFrom"); 
//System.out.println("callFrom::"+callFrom);
String empId = (String)request.getAttribute("empId");
String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
String pType = (String) request.getAttribute("pType");
String alertStatus = (String) request.getAttribute("alertStatus");
String alert_type = (String) request.getAttribute("alert_type");
String alertID = (String) request.getAttribute("alertID");
String Flag = "true";
 String strClassL1 ="class=\"active\"";
 String strClassL2 ="";
	if(callFrom != null && callFrom.equals("KTDash")) {
		strClassL1 ="";
		strClassL2 ="class=\"active\"";
	}
%> 
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
			<div class="col-md-12" style="padding-left: 0px;min-height: 600px;">
				 <div class="box box-none nav-tabs-custom">
           	 		<ul class="nav nav-tabs">
           	 		   	<li <%=strClassL1 %>><a href="javascript:void(0)" onclick="getGoalsData('GoalSummaryDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>');" data-toggle="tab">OKR</a></li>
     					<%-- <li><a href="javascript:void(0)"  onclick="getGoalsData('GoalKRATargetDashboard_1','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>');" data-toggle="tab">Balance Scorecard</a></li> --%> <!-- Goals, KRAs, Targets -->
     					<li <%=strClassL2 %>><a href="javascript:void(0)"  onclick="getGoalsData('GoalKRATargetDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>');" data-toggle="tab">
     					<% if(strUserType!=null && strUserType.equals(IConstants.MANAGER)) { %>
     						Team KRAs
     					<% } else { %>
     						Employee KRAs
     					<% } %>
     					</a></li>
           	 		</ul>
           	 		<div class="active tab-pane" id="divGoalsResult" style="min-height: 600px;">
				   </div>
			    </div>
			 </div>
		 </section>
	 </div>
</section>


 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() { 
		<%if(callFrom != null && callFrom.equals("KTDash")){%>
			getGoalsData('GoalKRATargetDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>','');
		<%} else if(callFrom != null && callFrom.equals("GoalKRABsc")){%>
			getGoalsData('GoalKRATargetDashboard_1','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>','GoalKRABsc');
		<%}	else if(callFrom != null && callFrom.equals("GoalKRAComments")){%>
			getGoalsData('GoalKRATargetDashboard_1','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>','<%=empId%>');
		<%}else {%>
			getGoalsData('GoalSummaryDashboard','<%=alertStatus%>','<%=alert_type %>','<%=pType%>','<%=alertID%>','');
		<% }%>
	});
	
	function getGoalsData(strAction,alertStatus,alert_type,pType,alertID,Flag){
		//alert("GoalKRATargetDashboard jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		$("#divGoalsResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?fromPage=GKTS&alertStatus='+alertStatus+'&alert_type='+alert_type+'&pType='+pType+'&alertID='+alertID+'&Flag='+Flag,
			cache: true,
			success: function(result){
			
				$("#divGoalsResult").html(result);
	   		}
		});
	}

</script>