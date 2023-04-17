<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%	
	String dataType = (String) request.getAttribute("dataType"); 
    String fromPage = (String) request.getAttribute("fromPage"); 
    
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
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )){ %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
    <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<%} %>

<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )){ %>
 <section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
			<div class="col-md-12" style="padding-left: 0px;min-height: 600px;"> 
<%}%>
				 <%if(isFlag){ %>
					 	<div class="box box-none nav-tabs-custom">
	            	 		<ul class="nav nav-tabs">
	            	 			<li class="active"><a href="javascript:void(0)" onclick="getHRReviewDashboardData('HRReviewDashboardData','<%=dataType %>','MYTEAM');" data-toggle="tab">MyTeam</a></li>
	      						<li><a href="javascript:void(0)" onclick="getHRReviewDashboardData('HRReviewDashboardData','<%=dataType %>','<%=strBaseUserType %>');" data-toggle="tab"><%=strBaseUserType %></a></li>
	            	 		</ul>
	            	 		
	            	 		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
								<div class="nav-tabs-custom">
									<div class="tab-content" >
			             				 <div class="active tab-pane" id="resultData" style="min-height: 600px;">
						
			        				      </div>
			           				</div>
			        			</div>
						    </div>
	            	 	</div>
				 <%} else { %>
					 	 <div class="box-none" id="resultData" style="padding: 5px; overflow-y: auto; min-height: 600px;">
							
						</div>
     			 <%} %>
     		<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )){ %>	 
			  </div>
		 </section>
	 </div>
</section> 
<%} %>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		var flag = '<%=isFlag%>'; 
		if(flag === true) {
			getHRReviewDashboardData('HRReviewDashboardData','L','MYTEAM');
		} else {
			getHRReviewDashboardData('HRReviewDashboardData','L','<%=currUserType%>');
		}
		
	});
	
	function getHRReviewDashboardData(strAction,dataType,currUserType){
		//alert("getSummaryDashboard jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		$("#resultData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?dataType='+dataType+'&currUserType='+currUserType,
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#resultData").html(result);
	   		}
		});
	}

</script>