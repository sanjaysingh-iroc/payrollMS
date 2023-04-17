<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<%	
	String dataType = (String) request.getAttribute("dataType"); 
	String from = (String) request.getAttribute("from"); 
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
   	String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    boolean isFlag = false;
    if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) {
    	isFlag = true;
    }  
%>
<% String callFrom = (String) request.getAttribute("callFrom"); %>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
			<div class="box box-none nav-tabs-custom">
	           <% if(strUserType != null && !strUserType.equals(IConstants.MANAGER)) { %>
	           <ul class="nav nav-tabs">
	           <!-- Started By Dattatray Date:19-10-21 -->
           			<li <% if(callFrom == null || callFrom.equals("")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getReqDashboardData('RequirementApproval','<%=currUserType %>',0);" data-toggle="tab" id="reqDashId0">New Requirements</a></li>
				    <li <% if(callFrom != null && callFrom.equals("HRDashJobApproval")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getReqDashboardData('JobProfilesApproval','',1);" data-toggle="tab" id="reqDashId1">Job Profile Approvals</a></li>
				    <li <% if(callFrom != null && callFrom.equals("HRDashProResReq")) { %> class="active" <% } %>><a href="javascript:void(0)" onclick="getReqDashboardData('ProjectResourceRequests','',2);" data-toggle="tab" id="reqDashId2">Resource Request for Projects</a></li>
	         <!-- Ended By Dattatray Date:19-10-21 -->
	          </ul>
	          <% } %>
	         <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
				<div class="nav-tabs-custom">
				    <div class="tab-content" >
			        <!-- ===start parvez date: 24-02-2023=== -->   
			           <div class="active tab-pane" id="divResult" style="min-height: 570px; max-height: 500px !important; overflow-y: hidden;">
					<!-- ===end parvez date: 24-02-2023=== -->	
			        	</div>
			       </div>
			    </div>
			</div>
	      </div>
	    </section>
	 </div>
</section>

 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		<% if((callFrom != null && callFrom.equals("HRDashJobApproval")) || (strUserType != null && strUserType.equals(IConstants.MANAGER))) { %>
			getReqDashboardData('JobProfilesApproval','','1');//Created By Dattatray Date:19-10-21
		<% } else if((callFrom != null && callFrom.equals("HRDashProResReq"))) { %>
			getReqDashboardData('ProjectResourceRequests','','2');//Created By Dattatray Date:19-10-21
		<% } else { %>
			var flag = '<%=isFlag%>'; 
			if(flag == 'true' || flag == true) {
				getReqDashboardData('RequirementApprovalData','','');//Created By Dattatray Date:19-10-21
			} else {
				getReqDashboardData('RequirementApproval','','0');//Created By Dattatray Date:19-10-21
			}
		<% } %>
	});
	
	function getReqDashboardData(strAction,currUserType,index){
		
		//console.log("getReqDashboardData jsp action" + strAction+"==currUserType==>"+currUserType);
		disabledPointerAddAndRemove(3,'reqDashId',index,true);//Created By Dattatray Date:19-10-21
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action',
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#divResult").html(result);
				disabledPointerAddAndRemove(3,'reqDashId',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}
	
	/* ===start parvez date: 24-02-2023=== */
	$(window).bind('mousewheel DOMMouseScroll', function(event){
	    if (event.originalEvent.wheelDelta > 0 || event.originalEvent.detail < 0) {
	        // scroll up
	        if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
	        	$("#divResult").scrollTop($("#divResult").scrollTop() - 30);
	        }
	    } else {
	        // scroll down
	        if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
	    		   $("#divResult").scrollTop($("#divResult").scrollTop() + 30);
	   		}
	    }
	});

	$(window).keydown(function(event){
		if(event.which == 40 || event.which == 34){
			if($(window).scrollTop() + $(window).height() > $(document).height() - 15) {
				$("#divResult").scrollTop($("#divResult").scrollTop() + 50);
	   		}
		} else if(event.which == 38 || event.which == 33){
			if($(window).scrollTop() == 0 && $("#divResult").scrollTop() != 0) {
		    	$("#divResult").scrollTop($("#divResult").scrollTop() - 50);
		    }
		}
	});
	/* ===end parvez date: 24-02-2023=== */

</script>