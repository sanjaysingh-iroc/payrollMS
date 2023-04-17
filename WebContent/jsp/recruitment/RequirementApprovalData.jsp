<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
 
 <%	
 	UtilityFunctions uF = new UtilityFunctions();
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    String currUserType = (String) request.getAttribute("currUserType");
   // System.out.println("currUserType==>"+currUserType);
%> 
 		<div class="nav-tabs-custom">
    	 	<ul class="nav nav-tabs">
    	 		<li class="active"><a href="javascript:void(0)" onclick="getReqApprovalData('RequirementApproval','MYTEAM');" data-toggle="tab">MyTeam</a></li>
			<li><a href="javascript:void(0)" onclick="getReqApprovalData('RequirementApproval','<%=strBaseUserType %>');" data-toggle="tab"><%=strBaseUserType %></a></li>
    		</ul>
           <div class="tab-content" >
         	   <div class="active tab-pane" id="subDivResult" style="min-height: 600px;"></div>
       	</div>
      	</div>

<script type="text/javascript" charset="utf-8">
	$(function() {
		getReqApprovalData('RequirementApproval','MYTEAM');
	});
	
	function getReqApprovalData(strAction,currUserType){
		$("#subDivResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?currUserType='+currUserType,
			cache: true,
			success: function(result){
				//console.log("result2==>"+result);
				$("#subDivResult").html(result);
	   		}
		});
	}
	

</script>          
    