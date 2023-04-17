
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	String dataType = (String)request.getParameter("dataType");
	String currUserType = (String)request.getParameter("currUserType");
	String f_org = (String)request.getParameter("f_org");
	String strEmpId = (String)request.getAttribute("strEmpId");
	Map<String, List<String>> hmCorporate = (Map<String, List<String>>) request.getAttribute("hmCorporate");
	if(hmCorporate == null) hmCorporate = new HashMap<String, List<String>>(); 
	
    String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
    
%>

	<ul class="products-list product-list-in-box">
		<%
		String goalId = null;
		if(hmCorporate != null && hmCorporate.size()>0) { 
			Iterator<String> it = hmCorporate.keySet().iterator();
			while(it.hasNext()) {
				String goalID = it.next();
				List<String> innerList = hmCorporate.get(goalID);
				if(goalId==null) {
					goalId = goalID;
				}
		%>
	 			<li class="item">
	 				<span style="float: left; width: 100%;">
	 					<a href="javascript:void(0);" <%if(uF.parseToInt(goalId) == uF.parseToInt(goalID)) { %> class="activelink" <% } %> onclick="getGoalSummary('GoalSummary', '<%=goalID %>', '<%=dataType %>', '<%=currUserType %>','')"><%=innerList.get(3) %></a>
				 		<br>
						<!-- attribute: --> <%=innerList.get(5) %>
			   		</span>
	 	    	</li>
 	    	<% } %>
 	    
 	 <%  } else { %>
			<li class="nodata msg">No Goals available.</li>
	<% } %>
 </ul>
 <input type="hidden" name="goalId" id="goalId" value="<%=goalId %>" />
 
  <script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		getGoalSummary('GoalSummary','<%=goalId%>','<%=dataType%>','<%=currUserType%>','0');//Created By Dattatray Date:19-10-21
		/* $("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});
		$(window).bind("load", function() {
		    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
		}); */ 
	});

	function getGoalSummary(strAction, goalId, dataType, currUserType,index){
		//	alert("getGoalDashboardData jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		disabledPointerAddAndRemove(2,'lcvID',index,true);//Created By Dattatray Date:19-10-21
		document.getElementById("goalId").value = goalId;
		var f_org = '<%=f_org %>';
		var paramValues = 'dataType='+dataType+'&currUserType='+currUserType+'&goalId='+goalId+'&f_org='+f_org;
		$("#corporateGoalDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues,
			cache: true,
			success: function(result){
				//console.log("result2==>"+result);
				$("#corporateGoalDetails").html(result);
				disabledPointerAddAndRemove(2,'lcvID',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
		document.getElementById("glSummary").className = "active";
		document.getElementById("glChart").className = "";
	}
	
	function getGoalList(strAction, currUserType,index){
		//	alert("getGoalDashboardData jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		var goalId = document.getElementById("goalId").value;
		var dataType = '<%=dataType %>';
		var f_org = '<%=f_org %>';
		var paramValues = 'dataType='+dataType+'&currUserType='+currUserType+'&goalId='+goalId+'&f_org='+f_org;
		disabledPointerAddAndRemove(2,'lcvID',index,true);//Created By Dattatray Date:19-10-21
		$("#corporateGoalDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues,
			cache: true,
			success: function(result){
				//console.log("result2==>"+result);
				$("#corporateGoalDetails").html(result);
				disabledPointerAddAndRemove(2,'lcvID',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}
	
	function getGoalChart(strAction, currUserType,index){
		//	alert("getGoalDashboardData jsp action" + strAction+"==>dataType==>"+dataType+"==currUserType==>"+currUserType);
		disabledPointerAddAndRemove(2,'lcvID',index,true);//Created By Dattatray Date:19-10-21
		var goalId = document.getElementById("goalId").value;
		var paramValues = 'strGoalId='+goalId;
		$("#corporateGoalDetails").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({ 
			type : 'POST',
			url: strAction+'.action?'+paramValues,
			cache: true,
			success: function(result){
				//console.log("result2==>"+result);
				$("#corporateGoalDetails").html(result);
				disabledPointerAddAndRemove(2,'lcvID',index,false);//Created By Dattatray Date:19-10-21
	   		}
		});
	}
</script>
