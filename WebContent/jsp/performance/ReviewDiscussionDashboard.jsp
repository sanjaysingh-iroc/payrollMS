<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<% 
	String fromPage = (String) request.getAttribute("fromPage"); 

	String dataType = (String) request.getAttribute("dataType"); 
	String currUserType = (String) request.getAttribute("currUserType");
	String strEmpId = (String) request.getAttribute("strEmpId");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    //System.out.println("fromPage="+fromPage);
%>

<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable"> 
	        <div class="box box-primary">
	          <div class="box-body">
	          	<div class="col-md-12" style="padding-left: 0px;">
	          		<div class="active tab-pane" id="oneOneDiscussionData">
					</div>
	          	</div>
	          	<div class="clr"></div>
	          </div>
	        </div>
		</section>
	</div>
</section>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getGoalKRATargetDashboardData('ReviewDiscussionDashboardData','L','MYTEAM', '<%=strEmpId %>');
	});
	
	function getGoalKRATargetDashboardData(strAction,dataType,currUserType,strEmpId){
		//alert("GoalKRATargetDashboard jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		$("#oneOneDiscussionData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?dataType='+dataType+'&currUserType='+currUserType+'&strEmpId='+strEmpId,
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#oneOneDiscussionData").html(result);
	   		}
		});
	}
</script>