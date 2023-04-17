<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<% String fromPage = (String) request.getAttribute("fromPage");  
   
%>
<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )){ %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
    <script type="text/javascript" src="scripts/jquery-ui.min.js"> </script>
<%} %>
<script>

	function myKRA(currUserType) { 
		
		var strID = '';
		if(document.getElementById("f_org")) {
		strID = getSelectedValue("f_org"); 
	    }
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Individual KRA');
		$("#modalInfo").show();
		if($(window).width() >= 1100){
			$(".modal-dialog").width(1100);
		}
		if($(window).height() >= 500){
			$('.modal-body').height(500);
		}
		$.ajax({
			url : 'MyGoalPopUp.action?operation=A&type=type&typeas=KRA&fromPage=GKT&strID='+strID+'&currUserType='+currUserType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function myTarget(currUserType) { 
		var strID = '';
		if(document.getElementById("f_org")){
			strID = getSelectedValue("f_org"); 
	    }
		//alert("strID == "+strID);
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Target Status');
		$("#modalInfo").show();
		if($(window).width() >= 1100){
			$(".modal-dialog").width(1100);
		}
		if($(window).height() >= 500){
			$('.modal-body').height(500);
		}
		$.ajax({
			url : 'MyGoalPopUp.action?operation=A&type=type&typeas=target&fromPage=GKT&strID='+strID+'&currUserType='+currUserType,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
	}


	   function getSelectedValue(selectId) {
	    	var choice = document.getElementById(selectId);
	    	var exportchoice = "";
	    	for ( var i = 0, j = 0; i < choice.options.length; i++) {
	    		if (choice.options[i].selected == true) {
	    			if (j == 0) {
	    				exportchoice = choice.options[i].value;
	    				j++;
	    			} else {
	    				exportchoice += "," + choice.options[i].value;
	    				j++;
	    			}
	    		}
	    	}
	    	return exportchoice;
	    }	
	    
	    
	    function goalChart(corpGoalID, managerGoalID, teamGoalID, goalID) {
	    	//alert("corpGoalID ===> "+corpGoalID+" managerGoalID ===> "+managerGoalID+" teamGoalID ===> "+teamGoalID+" goalID ===> "+goalID);
	    	var dialogEdit = '.modal-body';
	    	$(dialogEdit).empty();
	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$('.modal-title').html('Goal Chart');
	    	$("#modalInfo").show();
	    	$.ajax({
	    		url : "GoalChartIndividualGoal.action?corpGoalID=" + corpGoalID + "&managerGoalID=" + managerGoalID +"&teamGoalID=" + teamGoalID 
	    		+"&goalID=" + goalID,
	    		cache : false,
	    		success : function(data) {
	    			$(dialogEdit).html(data);
	    		}
	    	});
	    }
	    
	    
	    function closeGoal(goalId, type, kratype) {
	    	//alert("openQuestionBank id "+ id)
	    	var pageTitle = 'Close Goal';
	    	var pageCountLimit = document.getElementById("pageCount").value;
	       	var allData = pageCountLimit.split("_");
	       	var pageCnt = allData[0];
	       	var minLimit = allData[1];
	    	if(type=='view') {
	    		pageTitle = 'Close Goal Reason';
	    	}
	    	var dialogEdit = '.modal-body';
	    	$(dialogEdit).empty();
	    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    	$('.modal-title').html(''+pageTitle);
	    	$("#modalInfo").show();
	    	$.ajax({
	    		url : "CloseGoalTargetKRA.action?goalId="+goalId+"&fromPage=GoalKRA&kratype="+kratype+"&proPage="+pageCnt+"&minLimit="+minLimit,
	    		cache : false,
	    		success : function(data) {
	    			$(dialogEdit).html(data);
	    		}
	    	});
	    }
</script>
<%	
	String dataType = (String) request.getAttribute("dataType"); 
	String currUserType = (String) request.getAttribute("currUserType");
	String strEmpId = (String) request.getAttribute("strEmpId");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
    boolean isFlag = false;
    if(strUserType != null && strUserType.equals(IConstants.MANAGER) && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) {
    	isFlag = true;
    }
    System.out.println("fromPage="+fromPage);
%>

<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )){ %>
 <section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable"> 
        <div class="box box-primary">
          <div class="box-body">
<% } %>
			<div class="col-md-12" style="padding-left: 0px;">
				 <% if(isFlag) { %>
					 	<div class="box box-none nav-tabs-custom">
	            	 		<ul class="nav nav-tabs">
	            	 			<li class="active"><a href="javascript:void(0)" onclick="getGoalKRATargetDashboardData('GoalKRATargetDashboardData','<%=dataType %>','MYTEAM', '');" data-toggle="tab">MyTeam</a></li>
	      						<li><a href="javascript:void(0)" onclick="getGoalKRATargetDashboardData('GoalKRATargetDashboardData','<%=dataType %>','<%=strBaseUserType %>', '');" data-toggle="tab"><%=strBaseUserType %></a></li>
	            	 		</ul>
	            	 		
	            	 		<div class="box-body" style="padding: 5px; overflow-y: auto;">
								<div class="nav-tabs-custom">
									<div class="tab-content">
			             				 <div class="active tab-pane" id="goalKraTargetData">
						
			        				      </div>
			           				</div>
			        			</div>
						    </div>
	            	 	</div>
				 <%} else { %>
					 	 <div  class="active tab-pane" id="goalKraTargetData">
						</div>
     			 <%} %>
			  </div> 
			  <div class="clr"></div>
		<%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null" )){ %>
		    </div>
		  </div>
		</section>
	 </div>
</section> 
<% } %>

 <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		var flag = '<%=isFlag%>'; 
		//alert("isFlag==>"+flag);
		if(flag === true) {
			getGoalKRATargetDashboardData('GoalKRATargetDashboardData','L','MYTEAM', '<%=strEmpId %>');
		} else {
			getGoalKRATargetDashboardData('GoalKRATargetDashboardData','L','<%=currUserType%>', '<%=strEmpId %>');
		}
	});
	
	function getGoalKRATargetDashboardData(strAction,dataType,currUserType,strEmpId){
		//alert("GoalKRATargetDashboard jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		$("#goalKraTargetData").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?dataType='+dataType+'&currUserType='+currUserType+'&strEmpId='+strEmpId,
			cache: true,
			success: function(result){
				//alert("result1==>"+result);
				$("#goalKraTargetData").html(result);
	   		}
		});
	}

</script>