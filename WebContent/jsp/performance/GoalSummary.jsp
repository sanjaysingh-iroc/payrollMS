<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>

<style>
 
#greenbox {
height: 11px;
background-color:#00FF00; /* the critical component */
}
#redbox {
height: 11px;
background-color:#FF0000; /* the critical component */
}
#yellowbox {
height: 11px;
background-color:#FFFF00; /* the critical component */
}
#outbox {

height: 11px;
width: 100%; 
background-color:#D8D8D8; /* the critical component */

}

.anaAttrib1 {
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}

</style>

<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
<script type="text/javascript">

	function openPanelEmpProfilePopup(empId) {
		
			var id = document.getElementById("panelDiv");
			if(id) {
				id.parentNode.removeChild(id);
			}
		
			var dialogEdit = '#proBody';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$("#profileInfo").show();
			if($(window).width() >= 900){
				$(".proDialog").width(900);
			}
			$.ajax({
				//url : "ApplyLeavePopUp.action",  
				url :"MyProfile.action?empId="+empId+"&proPopup=proPopup" ,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
	}
	function editGoal(goalID, type, score, goalname, goalParentID, goalTitle, dataType, currUserType, compGoalId) { 

		goalTitle = goalTitle.replace("%", " percent");
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Edit '+goalname);
		$("#modalInfo").show();
		if($(window).width() >= 1100){
			$(".modal-dialog").width(1100);
		}
		$.ajax({
			url : 'EditGoalPopUp.action?goalid=' + goalID+ '&goaltype=' + type +'&score='+score +'&goalParentID='+ goalParentID
					+'&goalTitle='+goalTitle+'&dataType='+dataType+'&currUserType='+currUserType+'&compGoalId='+compGoalId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}

	
	function newGoal(goalID, type, score, goalname, createStatus, dataType, currUserType, compGoalId) {

		
		//alert("createStatus == "+createStatus + " type == "+type);
		if(type=="3" || type=="4") {
			//alert("createStatus 1 == "+createStatus + " type 1 == "+type);
			if(createStatus=="0"){
				//alert("createStatus 2 == "+createStatus + " type 2 == "+type);
				alert("You are not authorised user ");
			}else{
				var dialogEdit = '.modal-body';
				$(dialogEdit).empty();
				$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
				$('.modal-title').html('New '+goalname);
				$("#modalInfo").show();
				if($(window).width() >= 1100){
					$(".modal-dialog").width(1100);
				}
				$.ajax({
					url : 'NewGoalPopUp.action?goalid='+goalID+'&goaltype='+type+'&score='+score+'&dataType='+dataType
						+'&currUserType='+currUserType+'&compGoalId='+compGoalId,
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
			}
		}else{
			
			var strID = '';
			if(document.getElementById("f_org")) {
				strID = getSelectedValue("f_org"); 
		    }
			//alert("strID ===>> " + strID); +' Goal'
			var dialogEdit = '.modal-body';
			$(dialogEdit).empty();
			$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$('.modal-title').html('New '+goalname);
			$("#modalInfo").show();
			if($(window).width() >= 1100){
				$(".modal-dialog").width(1100);
			}
			$.ajax({
				url : 'NewGoalPopUp.action?goalid=' + goalID + '&goaltype=' + type+'&score='+score+'&strOrg='+strID
					  +'&dataType='+dataType+'&currUserType='+currUserType+'&compGoalId='+compGoalId,
				cache : false,
				success : function(data) {
					$(dialogEdit).html(data);
				}
			});
		}
	}
	
	function deleteGoal(id,del,type,goalTitle,dataType,currUserType, compGoalId) {
		
		/* var f_org = document.getElementById('f_org').value; *//* Created by dattatray Note :  Removed f_org unused */
		if(confirm('Are you sure you want to delete this objective?')) {
			var strAction = "NewGoal.action?id="+id+"&del="+del+"&type="+type+"&goalTitle="+goalTitle
			+"&dataType="+dataType+"&currUserType="+currUserType;
			$.ajax({ 
				type : 'POST',
				url: strAction,
				cache: true,
				success: function(result) {
					if(type=='corporate') {
						getCorporateGoalNameList('CorporateGoalNameList', dataType, currUserType);
					} else {
						getGoalSummary('GoalSummary', compGoalId, dataType, currUserType);
					}
				},
				error: function(result){
					if(type=='corporate') {
						getCorporateGoalNameList('CorporateGoalNameList', dataType, currUserType);
					} else {
						getGoalSummary('GoalSummary', compGoalId, dataType, currUserType);
					}
				}
			});
  		}
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
		
	
	function goalChart(goalID) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Goal Chart');
		$("#modalInfo").show();
		$.ajax({
			url : "GoalChart.action?strGoalId=" + goalID,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function goalChartManager(corpGoalID, managerGoalID, teamGoalID, goalID) {
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
	
	
	function getEmpProfile(val, empName){
		document.getElementById("profile").innerHTML = ''; 
		document.getElementById("EditGoal1").innerHTML = '';
		document.getElementById("NewGoal1").innerHTML = '';
		//document.getElementById("chartGoal").innerHTML = '';
		
		
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html(''+empName+'');
		$("#modalInfo").show();

		$.ajax({
			url : "AppraisalEmpProfile.action?empId=" + val ,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function closeGoal(goalId, type,dataType,currUserType) {
		//alert("openQuestionBank id "+ id)
		var pageTitle = 'Close Goal';
		if(type=='view') {
			pageTitle = 'Close Goal Reason';
		}
		var f_org = document.getElementById("f_org").value;
		
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html(''+pageTitle);
		$("#modalInfo").show();
		$.ajax({
			url : 'CloseGoalTargetKRA.action?goalId='+goalId+'&dataType='+dataType+'&currUserType='+currUserType+'&f_org='+f_org+'&fromPage=GS',
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	function openGoalForLive(goalId, type,dataType,currUserType) {
	 	   
    	var dialogEdit = '.modal-body';
    	$(dialogEdit).empty();
    	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$('.modal-title').html('Open Goal for Live');
    	$("#modalInfo").show();
    	$.ajax({
    		url : 'CloseGoalTargetKRA.action?goalId='+goalId+'&type='+type+'&dataType='+dataType+'&currUserType='+currUserType+'&fromPage=GS',
    		cache : false,
    		success : function(data) {
    			$(dialogEdit).html(data);
    		}
    	});
    }
	
</script>
<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Goals" name="title" /> 
</jsp:include> --%>
<%
	CommonFunctions CF= (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	String dataType = (String) request.getAttribute("dataType");
	String currUserType = (String) request.getAttribute("currUserType");
	String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
	
	Map<String, List<String>> hmCorporate = (Map<String, List<String>>) request.getAttribute("hmCorporate");
	Map<String, List<List<String>>> hmManager = (Map<String, List<List<String>>>) request.getAttribute("hmManager");
	Map<String, List<List<String>>> hmTeam = (Map<String, List<List<String>>>) request.getAttribute("hmTeam");
	Map<String, List<List<String>>> hmIndividual = (Map<String, List<List<String>>>) request.getAttribute("hmIndividual");
	Map<String, String> hmGoalType = (Map<String, String>)request.getAttribute("hmGoalType");

	UtilityFunctions uF = new UtilityFunctions();
	String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
	String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
	
	Map<String, List<List<String>>> hmKRA = (Map<String, List<List<String>>>)request.getAttribute("hmKRA");
	Map<String, List<List<String>>> hmKRATasks = (Map<String, List<List<String>>>)request.getAttribute("hmKRATasks");
	
	Map<String,String> empImageMap = (Map<String,String>)request.getAttribute("empImageMap");
	
	Map<String,String> hmCorporateTargetValue = (Map<String,String>)request.getAttribute("hmCorporateTargetValue");
	Map<String,String> hmTargetedValue = (Map<String,String>)request.getAttribute("hmTargetedValue");
	Map<String,Boolean> hmCorporateFlag = (Map<String,Boolean>)request.getAttribute("hmCorporateFlag");
	
	Map<String, String> hmEmpName = (Map<String, String>)request.getAttribute("hmEmpName");
	Map<String,String> parentScoreMp = (Map<String, String>)request.getAttribute("parentScoreMp");
	Map<String,String> goalScoreMp = (Map<String, String>)request.getAttribute("goalScoreMp");
	
	Map<String, String> hmTargetValue = (Map<String, String>)request.getAttribute("hmTargetValue");
	Map<String,Map<String,String>> hmIndGoalCal = (Map<String, Map<String,String>>)request.getAttribute("hmIndGoalCal");
	Map<String, Map<String, String>> hmIndGoalCalDetailsTeam = (Map<String, Map<String, String>>)request.getAttribute("hmIndGoalCalDetailsTeam");
	Map<String, Map<String, String>> hmTeamGoalCalDetailsManager = (Map<String, Map<String, String>>)request.getAttribute("hmTeamGoalCalDetailsManager");
	Map<String, Map<String, String>> hmManagerGoalCalDetailsCorporate = (Map<String, Map<String, String>>)request.getAttribute("hmManagerGoalCalDetailsCorporate");
	
	Map<String, String> hmCorpGoalAverage = (Map<String, String>) request.getAttribute("hmCorpGoalAverage");
	Map<String, String> hmManagerGoalAverage = (Map<String, String>) request.getAttribute("hmManagerGoalAverage");
	Map<String, String> hmTeamGoalAverage = (Map<String, String>) request.getAttribute("hmTeamGoalAverage");
	Map<String, String> hmGoalAverage = (Map<String, String>) request.getAttribute("hmGoalAverage");
	
	List<String> alCheckList = (List<String>) request.getAttribute("alCheckList");
	if (alCheckList == null) alCheckList = new ArrayList<String>();
	String strCurrency = (String) request.getAttribute("strCurrency");
	
	Map<String, String> hmGoalAndTargetRating = (Map<String, String>) request.getAttribute("hmGoalAndTargetRating");
	
	Map<String, String> hmKRATaskRating = (Map<String, String>) request.getAttribute("hmKRATaskRating");
	if(hmKRATaskRating == null) hmKRATaskRating = new HashMap<String, String>();
	
	Map<String, String> hmKRARating = (Map<String, String>) request.getAttribute("hmKRARating");
	if(hmKRARating == null) hmKRARating = new HashMap<String, String>();
	
	Map<String, String> hmGoalRating = (Map<String, String>) request.getAttribute("hmGoalRating");
	if(hmGoalRating == null) hmGoalRating = new HashMap<String, String>();
	
	Map<String, String> hmKRATaskStatus = (Map<String, String>) request.getAttribute("hmKRATaskStatus");
	if(hmKRATaskStatus == null) hmKRATaskStatus = new HashMap<String, String>();
	
	Map<String, String> hmKRAStatus = (Map<String, String>) request.getAttribute("hmKRAStatus");
	if(hmKRAStatus == null) hmKRAStatus = new HashMap<String, String>();
	
	Map<String, String> hmGoalStatus = (Map<String, String>) request.getAttribute("hmGoalStatus");
	if(hmGoalStatus == null) hmGoalStatus = new HashMap<String, String>();
	
	Map<String, String> hmCorpGoalRating = (Map<String, String>) request.getAttribute("hmCorpGoalRating");
	if(hmCorpGoalRating == null) hmCorpGoalRating = new HashMap<String, String>();
	
	Map<String, String> hmMngrGoalRating = (Map<String, String>) request.getAttribute("hmMngrGoalRating");
	if(hmMngrGoalRating == null) hmMngrGoalRating = new HashMap<String, String>();
	
	Map<String, String> hmTeamGoalRating = (Map<String, String>) request.getAttribute("hmTeamGoalRating");
	if(hmTeamGoalRating == null) hmTeamGoalRating = new HashMap<String, String>();
	
%>
<%--  <section class="content">
	<div class="row jscroll">
		 <section class="col-lg-12 connectedSortable">
		 	  <div class="box box-primary nav-tabs-custom"> --%>
		 	 <%--  <ul class="nav nav-tabs">
					<%
					String dataType = (String) request.getAttribute("dataType");
					if(dataType != null && dataType.equals("L")) { %>
						<li class="active"><a href="javascript:void(0)" onclick="window.location='GoalSummary.action?dataType=L'" data-toggle="tab">Live</a></li>
						<li><a href="javascript:void(0)" onclick="window.location='GoalSummary.action?dataType=C'" data-toggle="tab">Closed</a></li>
					<% } else if(dataType != null && dataType.equals("C")) { %>
						<li><a href="javascript:void(0)" onclick="window.location='GoalSummary.action?dataType=L'" data-toggle="tab">Live</a></li>
						<li class="active"><a href="javascript:void(0)" onclick="window.location='GoalSummary.action?dataType=C'" data-toggle="tab">Closed</a></li>
					<% } %>
				</ul> --%>
				<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE), "") %>
		 	  	<% session.setAttribute(IConstants.MESSAGE, ""); %>
		 	  	 
                <div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
                	
                 	<%--
						//System.out.println("request.getAttribute(IConstants.ADD_ACCESS) ===> "+request.getAttribute(IConstants.ADD_ACCESS));
				 	if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))) { --%> 
				 		<div style="float: right; margin-bottom: 10px">
						   <%if(strSessionUserType!=null && (strSessionUserType.equals(IConstants.HRMANAGER) || strSessionUserType.equals(IConstants.ADMIN))) {
								if(uF.parseToDouble(parentScoreMp.get(IConstants.CORPORATE_GOAL+"_0"))<100 && (dataType == null || dataType.equals("L"))) { %>
									<a href="javascript:void(0)"><input type="button" class="btn btn-primary" onclick="newGoal('0','1','<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.CORPORATE_GOAL+"_0"))%>','Company Objective','','<%=dataType %>','<%=currUserType %>', '0')" value="Add Company Objective">&nbsp;</a><!-- Add New OKR, Corporate Goal -->
							  <% } 
							 } %> 
						</div>
					<%-- } --%>
						<div class="clr"></div>
						<%-- if(uF.parseToBoolean((String)request.getAttribute(IConstants.VIEW_ACCESS))) {--%>
						<%
							  if(strSessionUserType != null && (strSessionUserType.equals(IConstants.HRMANAGER) || strSessionUserType.equals(IConstants.MANAGER) || strSessionUserType.equals(IConstants.ADMIN))) {
									int ccount = 0;
									if(hmCorporate != null && !hmCorporate.isEmpty()) {
									Iterator<String> it = hmCorporate.keySet().iterator();
								
									while (it.hasNext()) {
										String key = it.next();
										List<String> cinnerList = hmCorporate.get(key);
										ccount++;
										String pClass = cinnerList.get(32);
										
										String withDepart = (uF.parseToInt(cinnerList.get(35))==1) ? "block" : "none";
										String withTeam = (uF.parseToInt(cinnerList.get(36))==1) ? "block" : "none";
										
										//System.out.println("hmManagerGoalCalDetailsCorporate -----> "+hmManagerGoalCalDetailsCorporate);
						 				String alltwoDeciTotProgressAvgCorporate = "0";
										String alltotal100Corporate = "100";
										String strtwoDeciTotCorporate = "0";
										if(hmManagerGoalCalDetailsCorporate != null && !hmManagerGoalCalDetailsCorporate.isEmpty()){
						 				Map<String, String> hmManagerGoalCalDetailsParentCorporate = hmManagerGoalCalDetailsCorporate.get(cinnerList.get(0));
											
											if(hmManagerGoalCalDetailsParentCorporate != null && !hmManagerGoalCalDetailsParentCorporate.isEmpty()){
												alltwoDeciTotProgressAvgCorporate = hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0)+"_PERCENT");
												alltotal100Corporate = hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0)+"_TOTAL");
												strtwoDeciTotCorporate = hmManagerGoalCalDetailsParentCorporate.get(cinnerList.get(0)+"_STR_PERCENT");	
											}
										}
										boolean checkFlag = false;
										if(alCheckList != null && alCheckList.contains(cinnerList.get(0))){
											checkFlag =true;
										}
										
										double avgCorpGoalRating = 0.0d;
										String corpGoalRating = hmCorpGoalRating.get(cinnerList.get(0)+"_RATING");
										String corpGoalTaskCount = hmCorpGoalRating.get(cinnerList.get(0)+"_COUNT");
										if(uF.parseToInt(corpGoalTaskCount) > 0) {
											avgCorpGoalRating = uF.parseToDouble(corpGoalRating) / uF.parseToInt(corpGoalTaskCount);
										}
								%>
								<% 
									String goalTitle = "OKR";
									if(cinnerList.get(3)!=null && cinnerList.get(1)!=null){
										goalTitle = cinnerList.get(3)+ "("+hmGoalType.get(cinnerList.get(1)) +")";
									}
								%>
									<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">
										<div class="box box-info" style="border-top-color: #E6EBEE; margin-bottom: 5px;">
							                <div class="box-header with-border" style="background-color:#d2d6de;">
							                    <h5 class="box-title" style="font-size: 16px;">
							                    	<strong><%=ccount%>)</strong> <%=cinnerList.get(3)%> (<%=hmGoalType.get(cinnerList.get(1)) %>)
													<% if (strSessionUserType != null && (strSessionUserType.equals(IConstants.HRMANAGER)  || strSessionUserType.equals(IConstants.ADMIN))) { %>
														<span id="corporateEditId" style="float: right; margin-right: 1cm;">
															<%-- <a href="javascript:void(0)" onclick="goalChart('<%=cinnerList.get(0)%>')"  style="float:left; margin-right: 7px; margin-top: 3px;" title="Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>&nbsp; --%>
															<% if((dataType == null || dataType.equals("L"))) { %>
																<a href="javascript:void(0)" class="edit_lvl" onclick="editGoal('<%=cinnerList.get(0)%>','1','<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.CORPORATE_GOAL+"_0"))%>','Company Objective','','<%=goalTitle %>','<%=dataType%>','<%=currUserType %>','<%=cinnerList.get(0)%>')" title="Edit Company Objective"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a><!-- Corporate Goal -->
																<% if (!checkFlag){ %>
																	<a style="color: #F02F37;" onclick="deleteGoal('<%=cinnerList.get(0)%>','del','corporate','<%=goalTitle %>','<%=dataType %>','<%=currUserType %>','<%=cinnerList.get(0)%>')" href="javascript:void(0)" title="Delete Company Objective" )"><i class="fa fa-trash-o" aria-hidden="true"></i> </a>
																<%} else { %>
																	<a style="color: #F02F37;" href="javascript:void(0)" onclick="alert('Employee has already updated the objective. You can not delete this objective.');"><i class="fa fa-trash-o" aria-hidden="true"></i></a>
																<%} %> 
															<% } %>
															
															<%if(!uF.parseToBoolean(cinnerList.get(33))){ %>
																	<a style="color: #F02F37;" href="javascript:void(0);" title="Close Company Objective" onclick="closeGoal('<%=cinnerList.get(0)%>','close','<%=dataType %>','<%=currUserType %>');"><i class="fa fa-times-circle-o" aria-hidden="true"></i></a><!-- Goal -->
															<% } else { %>
																<a href="javascript:void(0);" title="Close Company Objective Reason" onclick="closeGoal('<%=cinnerList.get(0)%>','view','<%=dataType %>','<%=currUserType %>');"><i class="fa fa-comment-o" aria-hidden="true"></i></a><!-- Goal -->
																<a href="javascript:void(0);" onclick="openGoalForLive('<%=cinnerList.get(0)%>','open','<%=dataType %>','<%=currUserType %>');"  title="Open OKR for Live"><i class="fa fa-reply" aria-hidden="true"></i></a><!-- Goal -->
															<% } %>
														</span>
													<% } %>
							                    </h5>
							                    <div class="box-tools pull-right">
							                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
							                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
							                    </div>
							                </div>
							                <!-- /.box-header -->
							             <div class="box-body" style="padding: 5px; overflow-y: auto;">
							               <div class="content2">
											<ul class="level_list">
												<li style="width: 100%">
													<div class="row  row_without_margin">
														<div class="col-lg-8 col-md-8 col-sm-12">
															<div style="float: left; width: 100%;">
																<span class="<%=pClass %>" style="float: left; font-size:11px; line-height: 18px;"><b>Obj: </b><%=cinnerList.get(4) %></span>
															</div>
															
															<div style="float: left; width: 100%;">
																<span class="<%=pClass %>" style="float: left; font-size:11px; line-height: 18px;">- assigned by <%=cinnerList.get(34)%>, 
																attribute <%=cinnerList.get(6) %>, effective date <%=cinnerList.get(31) %>, due date <%=cinnerList.get(16) %>, weightage <%=cinnerList.get(19)%>% </span>
															</div>
															
															<div style="float: left; width: 100%;">
																<span class="<%=pClass %>" style="float: left; font-size:11px; line-height: 18px;"><b>Desc:</b> <%=cinnerList.get(5)%></span>
															</div>
															
															<% if (cinnerList.get(7)!=null && cinnerList.get(7).equals("Effort") && uF.parseToBoolean(cinnerList.get(23))) { %>
																	<div style="float: left; width: 100%;">
																		<span class="<%=pClass %>"><strong>Target:</strong>&nbsp;<%=cinnerList.get(10)%>&nbsp;Days&nbsp;<%=cinnerList.get(11)%>&nbsp;Hrs</span>
																	</div>
															<% } else if (cinnerList.get(7)!=null && cinnerList.get(7).equals("Amount") && uF.parseToBoolean(cinnerList.get(23))) { %>
																	<div style="float: left; width: 100%;">
																		<span class="<%=pClass %>"><strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(cinnerList.get(8)))%></span>
																	</div>
															<% } else if (cinnerList.get(7)!=null && cinnerList.get(7).equals("Percentage") && uF.parseToBoolean(cinnerList.get(23))) { %>
																	<div style="float: left; width: 100%; ">
																		<span class="<%=pClass %>"><strong>Target:</strong>&nbsp;<%=cinnerList.get(8) %> %</span>
																	</div>
															<% } %>
															
															<div style="float:left; width:100%; margin-left: 0px;"><strong>Assigned To:</strong></div>
														<%
															if(cinnerList.get(29)!=null){ 
																List<String> emplistID=Arrays.asList(cinnerList.get(29).split(","));
														%>
														<div style="float:left;width:100%; margin-left: 15px;">
															<% for(int i=0; emplistID!=null && i<emplistID.size();i++) {
																if(emplistID.get(i)!=null && !emplistID.get(i).equals("")) {
																	String empName = hmEmpName.get(emplistID.get(i).trim());
																	String empimg = uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
															%>
																	<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>', '<%=empName %>');"><span style="float:left;width:20px;height:20px;margin:2px;">
																	<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
																	</span></a>
															  <% } 
															} %>
														</div>
												   <% } %>
												   
												   <%if(hmKRA != null && !hmKRA.isEmpty()) { 
														List<List<String>> outerList = hmKRA.get(cinnerList.get(0));
														if(outerList != null && !outerList.isEmpty()) {
													%>
														<div style="float: left; width: 100%;">
															<div style="float: left; width: 100%; font-weight: bold;">KRA:&nbsp;</div>
															<% 
																for(int ii=0;outerList!=null && ii<outerList.size();ii++) {
																	List<String> innerList = outerList.get(ii);
																	List<List<String>> taskOuterList = hmKRATasks.get(innerList.get(0));
																	double avgKRARating = 0.0d;
																	double avgKRAStatus = 0.0d;
																	String kraRating = hmKRARating.get(innerList.get(0)+"_RATING");
																	String kraTaskCount = hmKRARating.get(innerList.get(0)+"_COUNT");
																	if(uF.parseToInt(kraTaskCount) > 0) {
																		avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
																	}
																	
																	String kraStatus = hmKRAStatus.get(innerList.get(0)+"_STATUS");
																	String kraTaskStatusCount = hmKRAStatus.get(innerList.get(0)+"_COUNT");
																	if(uF.parseToInt(kraTaskStatusCount) > 0) {
																		avgKRAStatus = uF.parseToDouble(kraStatus) / uF.parseToInt(kraTaskStatusCount);
																	}
																%>
																<div style="float: left; width: 100%; margin-left: 30px; padding: 3px 0px;">
																	<div style="float: left; width: 46%;"><%=ii+1 %>. <%=innerList.get(7) %></div>
																	<%-- <div style="float: left; width: 21%; margin: 0px 15px;">
																		<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=avgKRAStatus > 85 ? avgKRAStatus - 20 : avgKRAStatus - 4%>%;"><%=uF.showData(""+avgKRAStatus, "0")%>%</span></div>
																		<div id="outbox">
																		<% if (avgKRAStatus < 33.33) { %>
																		
																		<div id="redbox" style="width: <%=uF.showData(""+avgKRAStatus, "0") %>%;"></div>
																		<% } else if (avgKRAStatus >= 33.33 && avgKRAStatus < 66.67) { %>
																		
																		<div id="yellowbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																		<% } else if (avgKRAStatus >= 66.67) { %>
																		
																		<div id="greenbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																		<% } %>
																		</div>
																		<div class="anaAttrib1" style="float: left; width: 100%; line-height: 16px;"><span style="float: left; margin-left:-4%;">0%</span>
																		<span style="float: right; margin-right:-10%;">100%</span></div>
																	</div> --%>
																	<%-- <div style="float: left; margin-left: 10px;">
																		<div id="starPrimaryIGK<%=innerList.get(0) %>"></div>
																		<script type="text/javascript">
																        	$('#starPrimaryIGK<%=innerList.get(0) %>').raty({
																        		readOnly: true,
																        		start: <%=avgKRARating %>,
																        		half: true,
																        		targetType: 'number'
																			});
																		</script>
																	</div> --%>
																</div>
																<% } %>
															</div>
														<% } 
															} %>
																		
											  </div>
											  <div class="col-lg-4 col-md-4 col-sm-12">
												<p style="font-size: 10px; text-align: right; padding-right: 20px; font-style: italic;">Last updated by <%=hmEmpName.get(cinnerList.get(22))%> on <%=cinnerList.get(21)%></p>
												<div style="background-repeat: no-repeat; background-position: right top; width: 74%;">
													<div id="starPrimaryCG<%=cinnerList.get(0)%>"></div>
													<script type="text/javascript">
												        	$('#starPrimaryCG<%=cinnerList.get(0)%>').raty({
												        		readOnly: true,
												        		start: <%=avgCorpGoalRating %>,
												        		half: true,
												        		targetType: 'number'
											        		});
													</script>
												</div>
												<div style="min-height: 40px; padding-right: 40px;">
													<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=uF.parseToInt(alltwoDeciTotProgressAvgCorporate) > 94 ? uF.parseToInt(alltwoDeciTotProgressAvgCorporate)-6 : uF.parseToInt(alltwoDeciTotProgressAvgCorporate)-2.5 %>%;"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(uF.parseToDouble(strtwoDeciTotCorporate)) %>%</span></div>
														<div id="outbox">
															<%if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 33.33){ %> 
																<div id="redbox" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;"></div>
															<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) < 66.67){ %>
																<div id="yellowbox" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;"></div>
															<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgCorporate) >= 66.67){ %>
																<div id="greenbox" style="width: <%=alltwoDeciTotProgressAvgCorporate %>%;"></div>
															<%} %>
														</div>
														<div class="anaAttrib1" style="float: left; width: 100%; margin-bottom: -10px;"><span style="float: left; margin-left:-1.5%;">0%</span>
														<span style="float: right;  margin-right:-7%;"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(uF.parseToDouble(alltotal100Corporate)) %>%</span></div>
														<span style="color: #808080;">Slow</span>
														<span style="margin-left:70px; color: #808080;">Steady</span>
														<span style="float: right; color: #808080;">Momentum</span>
													</div>
												</div>
											</div>
										
											<div style="float: left; background-repeat: no-repeat; background-position: right top;width:60%;">
												<%if(uF.parseToDouble(parentScoreMp.get(IConstants.MANAGER_GOAL+"_"+cinnerList.get(0))) < 100 && (dataType == null || dataType.equals("L"))){ %>
												<p style="margin: 0px 0px 0px 15px" class="addnew desgn">
													<%--if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ --%>
														<a href="javascript:void(0)" class="add_lvl" onclick="newGoal('<%=cinnerList.get(0)%>','2','<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.MANAGER_GOAL+"_"+cinnerList.get(0)))%>','Departmental Objective','','<%=dataType %>','<%=currUserType %>', '<%=cinnerList.get(0)%>')" title="Add Departmental Objective"> Add Departmental Objective</a><!-- Manager Goal -->
													<%--} --%>
												</p>
												<%} %>
											</div>
												
											<div class="clr"></div> 
											<%
												List<List<String>> mouterList = hmManager.get(cinnerList.get(0));
									 			int mcount = 0;
									 			for (int j = 0; mouterList != null && j < mouterList.size(); j++) {
									 				mcount++;
									 				List<String> minnerList = mouterList.get(j);
									 				String goalCreaterID =minnerList.get(32);
									 				String createStatus = "0";
									 				String pClassM=minnerList.get(33);
									 				if(goalCreaterID != null && goalCreaterID.equals(strSessionEmpId)){
									 					createStatus = "1";
									 				}else if(goalCreaterID == null || goalCreaterID.equals("0")){
									 					createStatus = "1";
									 				}
									 				String alltwoDeciTotProgressAvgManager = "0";
													String alltotal100Manager = "100";
													String strtwoDeciTotManager = "0";
													if(hmTeamGoalCalDetailsManager != null && !hmTeamGoalCalDetailsManager.isEmpty()){
									 				Map<String, String> hmTeamGoalCalDetailsParentManager = hmTeamGoalCalDetailsManager.get(minnerList.get(0));
														
														if(hmTeamGoalCalDetailsParentManager != null && !hmTeamGoalCalDetailsParentManager.isEmpty()){
															alltwoDeciTotProgressAvgManager = hmTeamGoalCalDetailsParentManager.get(minnerList.get(0)+"_PERCENT");
													 		alltotal100Manager = hmTeamGoalCalDetailsParentManager.get(minnerList.get(0)+"_TOTAL");
													 		strtwoDeciTotManager = hmTeamGoalCalDetailsParentManager.get(minnerList.get(0)+"_STR_PERCENT");	
														}
													}
													boolean checkFlag1 = false;
													if(alCheckList != null && alCheckList.contains(minnerList.get(0))){
														checkFlag1 =true;
													}
													
													double avgMngrGoalRating = 0.0d;
								 					String mngrGoalRating = hmMngrGoalRating.get(minnerList.get(0)+"_RATING");
													String mngrGoalTaskCount = hmMngrGoalRating.get(minnerList.get(0)+"_COUNT");
													if(uF.parseToInt(mngrGoalTaskCount) > 0) {
														avgMngrGoalRating = uF.parseToDouble(mngrGoalRating) / uF.parseToInt(mngrGoalTaskCount);
													}
									 %> 
									 		<%
												String mgoalTitle = "Departmental Objective";
												if(minnerList.get(3)!=null && minnerList.get(1)!=null){
													mgoalTitle = minnerList.get(3)+"("+hmGoalType.get(minnerList.get(1))+")";
												}
											%>
											<div class="box box-primary collapsed-box" style="margin-top: <%=withDepart.equals("block") ? "10px" : "0px" %>; border-top-color: #FFEDE0;">
								                <div class="box-header with-border" style="display: <%=withDepart %>; background-color:#FFEDE0;">
								                    <h3 class="box-title" style="font-size: 16px;">
								                    	<strong><%=mcount%>)</strong> <%=minnerList.get(3)%> (<%=hmGoalType.get(minnerList.get(1)) %>)
															<span id="corporateEditId" style="float: right; margin-right: 1cm;">
																<% if (strSessionUserType != null && strSessionUserType.equals(IConstants.MANAGER)) { %>
																	<a href="javascript:void(0)" onclick="goalChartManager('<%=cinnerList.get(0)%>','<%=minnerList.get(0)%>','','')"  style="float:left; margin-right: 7px; margin-top: 3px;" title="Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>
																<% } %>
																<% if((dataType == null || dataType.equals("L"))) { %>
																	<a href="javascript:void(0)" class="edit_lvl" onclick="editGoal('<%=minnerList.get(0)%>','2','<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.MANAGER_GOAL+"_"+cinnerList.get(0)))%>','Departmental Objective','<%=cinnerList.get(0)%>','<%=mgoalTitle %>','<%=dataType%>','<%=currUserType %>','<%=cinnerList.get(0)%>')" title="Edit Departmental Objective"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																	<% if (!checkFlag1) { %>
																		<a style="color: #F02F37;" onclick="deleteGoal('<%=minnerList.get(0)%>','del','manager','<%=mgoalTitle %>','<%=dataType %>','<%=currUserType %>','<%=cinnerList.get(0)%>')"  href="javascript:void(0)" title="Delete Departmental Objective" ><i class="fa fa-trash-o" aria-hidden="true"></i></a>
																	<% } else { %>
																		<a style="color: #F02F37;" href="javascript:void(0)" onclick="alert('Employee has already updated the objective. You can not delete this objective.');"><i class="fa fa-trash-o" aria-hidden="true"></i></a>
																	<% } %>
																<% } %>
																<%if(!uF.parseToBoolean(minnerList.get(34))) { %>
																	<a style="color: #F02F37;" href="javascript:void(0);" title="Close Departmental Objective" onclick="closeGoal('<%=minnerList.get(0)%>','close','<%=dataType %>','<%=currUserType %>');"><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
																<% } else { %>
																	<a href="javascript:void(0);" title="Close Departmental Objective Reason" onclick="closeGoal('<%=minnerList.get(0)%>','view','<%=dataType %>','<%=currUserType %>');"><i class="fa fa-comment-o" aria-hidden="true"></i></a>
																	<a href="javascript:void(0);" onclick="openGoalForLive('<%=minnerList.get(0)%>','open','<%=dataType %>','<%=currUserType %>');"  title="Open Departmental Objective for Live"><i class="fa fa-reply" aria-hidden="true"></i></a>
																<% } %>
															</span>
								                    </h3>
								                    <div class="box-tools pull-right">
								                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
								                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
								                    </div>
								                </div>
								                <!-- /.box-header -->
								                <div class="box-body" style="padding: 5px; overflow-y: auto; display: <%=withDepart.equals("block") ? "none" : "block" %>;">
								                    <ul class="level_list" style="padding-left:0px;">
												<li class="desgn">
													<div class="content1">
														<ul class="level_list">
															<li>
															<div class="row row_without_margin" style="display: <%=withDepart %>; margin-right:0px;">
															<div class="col-lg-8 col-md-8 col-sm-12">
																<div style="float: left; width: 100%;">
																	<span class="<%=pClassM %>" style="float: left; font-size:11px; line-height: 18px;"><b>Obj: </b><%=minnerList.get(4) %></span>
																</div>
																
																<div style="float: left; width: 100%;">
																	<span class="<%=pClassM %>" style="float: left; font-size:11px; line-height: 18px;">- assigned by <%=minnerList.get(35)%>, 
																	attribute <%=minnerList.get(6) %>, effective date <%=minnerList.get(31) %>, due date <%=minnerList.get(16) %>, weightage <%=minnerList.get(19)%>% </span>
																</div>
																
																<div style="float: left; width: 100%;">
																	<span class="<%=pClassM %>" style="float: left; font-size:11px; line-height: 18px;"><b>Desc:</b> <%=minnerList.get(5)%></span>
																</div>
																
																<% if (minnerList.get(7)!=null && minnerList.get(7).equals("Effort") && uF.parseToBoolean(minnerList.get(23))) { %>
																<div style="float: left; width: 100%;">
																	<span class="<%=pClassM %>"><strong>Target:</strong>&nbsp;<%=minnerList.get(10)%>&nbsp;Days&nbsp;<%=minnerList.get(11)%>&nbsp;Hrs</span>
																</div>
																<% } else if (minnerList.get(7)!=null && minnerList.get(7).equals("Amount") && uF.parseToBoolean(minnerList.get(23))) { %>
																<div style="float: left; width: 100%;">
																	<span class="<%=pClassM %>"><strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(minnerList.get(8)))%></span>
																</div>
																<% } else if (minnerList.get(7)!=null && minnerList.get(7).equals("Percentage") && uF.parseToBoolean(minnerList.get(23))) { %>
																<div style="float: left; width: 100%; ">
																	<span class="<%=pClassM %>"><strong>Target:</strong>&nbsp;<%=minnerList.get(8) %> %</span>
																</div>
																<% } %>
															
																<div style="float:left; width:100%; margin-left: 0px;">
																	<strong>Assigned To:</strong>
																</div>
															
																<%
																if(minnerList.get(29)!=null){
																List<String> emplistID=Arrays.asList(minnerList.get(29).split(","));
																%>
																<div style="float:left; margin-left: 15px; margin-left: 0px;">
																	<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
																		if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
																			String empName = hmEmpName.get(emplistID.get(i).trim());
																			String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
																		%>
																	<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left;width:20px;height:20px;margin:2px;">
																	<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
																	</span></a>
																	<% } } %>
																</div>
																<% } %>
																
																<%if(hmKRA != null && !hmKRA.isEmpty()) { 
																	List<List<String>> outerList = hmKRA.get(minnerList.get(0));
																	if(outerList != null && !outerList.isEmpty()) {
																%>
																	<div style="float: left; width: 100%;">
																		<div style="float: left; width: 100%; font-weight: bold;">KRA:&nbsp;</div>
																		<% 
																			for(int ii=0;outerList!=null && ii<outerList.size();ii++) {
																				List<String> innerList = outerList.get(ii);
																				List<List<String>> taskOuterList = hmKRATasks.get(innerList.get(0));
																				double avgKRARating = 0.0d;
																				double avgKRAStatus = 0.0d;
																				String kraRating = hmKRARating.get(innerList.get(0)+"_RATING");
																				String kraTaskCount = hmKRARating.get(innerList.get(0)+"_COUNT");
																				if(uF.parseToInt(kraTaskCount) > 0) {
																					avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
																				}
																				
																				String kraStatus = hmKRAStatus.get(innerList.get(0)+"_STATUS");
																				String kraTaskStatusCount = hmKRAStatus.get(innerList.get(0)+"_COUNT");
																				if(uF.parseToInt(kraTaskStatusCount) > 0) {
																					avgKRAStatus = uF.parseToDouble(kraStatus) / uF.parseToInt(kraTaskStatusCount);
																				}
																			%>
																			<div style="float: left; width: 100%; margin-left: 30px; padding: 3px 0px;">
																				<div style="float: left; width: 46%;"><%=ii+1 %>. <%=innerList.get(7) %></div>
																				<%-- <div style="float: left; width: 21%; margin: 0px 15px;">
																					<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=avgKRAStatus > 85 ? avgKRAStatus - 20 : avgKRAStatus - 4%>%;"><%=uF.showData(""+avgKRAStatus, "0")%>%</span></div>
																					<div id="outbox">
																					<% if (avgKRAStatus < 33.33) { %>
																					
																					<div id="redbox" style="width: <%=uF.showData(""+avgKRAStatus, "0") %>%;"></div>
																					<% } else if (avgKRAStatus >= 33.33 && avgKRAStatus < 66.67) { %>
																					
																					<div id="yellowbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																					<% } else if (avgKRAStatus >= 66.67) { %>
																					
																					<div id="greenbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																					<% } %>
																					</div>
																					<div class="anaAttrib1" style="float: left; width: 100%; line-height: 16px;"><span style="float: left; margin-left:-4%;">0%</span>
																					<span style="float: right; margin-right:-10%;">100%</span></div>
																				</div> --%>
																				<%-- <div style="float: left; margin-left: 10px;">
																					<div id="starPrimaryIGK<%=innerList.get(0) %>"></div>
																					<script type="text/javascript">
																			        	$('#starPrimaryIGK<%=innerList.get(0) %>').raty({
																			        		readOnly: true,
																			        		start: <%=avgKRARating %>,
																			        		half: true,
																			        		targetType: 'number'
																						});
																					</script>
																				</div> --%>
																			</div>
																			<% } %>
																		</div>
																	<% } 
																		} %>
																
															</div>
															<div class="col-lg-4 col-md-4 col-sm-12">
																<p style="font-size: 10px; text-align: right; padding-right: 20px; font-style: italic;">Last updated by <%=hmEmpName.get(minnerList.get(22))%> on <%=minnerList.get(21)%></p>
																
																<div style="background-repeat: no-repeat; background-position: right top; margin-top: 20px; width: 75%;">
																	<div id="starPrimaryMG<%=minnerList.get(0)%>"></div>
																	<script type="text/javascript">
																        	$('#starPrimaryMG<%=minnerList.get(0)%>').raty({
																        		readOnly: true,
																        		start: <%=avgMngrGoalRating %>,
																        		half: true,
																        		targetType: 'number'
															        		});
																	</script>
																</div>
															
																<div style=" min-height: 40px; padding-right: 40px;">
																	<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=uF.parseToInt(alltwoDeciTotProgressAvgManager) > 94 ? uF.parseToInt(alltwoDeciTotProgressAvgManager)-6 : uF.parseToInt(alltwoDeciTotProgressAvgManager)-3.5 %>%;"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(uF.parseToDouble(strtwoDeciTotManager))%>%</span></div>
																		<div id="outbox">
																			<%if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 33.33){ %> 
																				<div id="redbox" style="width: <%=alltwoDeciTotProgressAvgManager %>%;"></div>
																			<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 66.67){ %>
																				<div id="yellowbox" style="width: <%=alltwoDeciTotProgressAvgManager %>%;"></div>
																			<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 66.67){ %>
																				<div id="greenbox" style="width: <%=alltwoDeciTotProgressAvgManager %>%;"></div>
																			<%} %>
																		</div>
																		<div class="anaAttrib1" style="float: left; width: 100%; margin-bottom: -10px;"><span style="float: left; margin-left: -1.5%;">0%</span>
																		<span style="float: right; margin-right: -7%;"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(uF.parseToDouble(alltotal100Manager)) %>%</span></div>
																		<span style="color: #808080;">Slow</span>
																		<span style="margin-left:55px; color: #808080;">Steady</span>
																		<span style="float: right; color: #808080;">Momentum</span>
																 </div>
															</div>
														</div>
													
														<div style="float: left; background-repeat: no-repeat; background-position: right top;">
															<% if(uF.parseToDouble(parentScoreMp.get(IConstants.TEAM_GOAL+"_"+minnerList.get(0)))<100 && (dataType == null || dataType.equals("L"))){ %>
																<p style="margin: 0px 0px 0px 0px" class="addnew desgn">
																	<%--if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ --%>
																	<a href="javascript:void(0)" class="add_lvl" onclick="newGoal('<%=minnerList.get(0)%>','3','<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.TEAM_GOAL+"_"+minnerList.get(0)))%>','Team Objective','<%=createStatus %>','<%=dataType %>','<%=currUserType %>', '<%=cinnerList.get(0)%>')" title="Add Team Objective"> Add Team Objective</a> <!-- Add New Key Result -->
																	<%--} --%>
																</p>
															<%} %>
														</div>
														
											<div class="clr"></div> 
									<%
						 				List<List<String>> touterList = hmTeam.get(minnerList.get(0));
						 				int tcount = 0;
						 				for (int k = 0; touterList != null && k < touterList.size(); k++) {
						 					tcount++;
						 					List<String> tinnerList = touterList.get(k);
						 					String pClassT= tinnerList.get(32);
						 					Map<String, String> hmIndGoalCalDetailsParent = hmIndGoalCalDetailsTeam.get(tinnerList.get(0));
						 					String alltwoDeciTotProgressAvgTeam = "0";
						 					String alltotal100Team = "100";
						 					String strtwoDeciTotTeam = "0";
						 					if(hmIndGoalCalDetailsParent != null && !hmIndGoalCalDetailsParent.isEmpty()){
						 						alltwoDeciTotProgressAvgTeam = hmIndGoalCalDetailsParent.get(tinnerList.get(0)+"_PERCENT");
						 				 		alltotal100Team = hmIndGoalCalDetailsParent.get(tinnerList.get(0)+"_TOTAL");
						 				 		strtwoDeciTotTeam = hmIndGoalCalDetailsParent.get(tinnerList.get(0)+"_STR_PERCENT");	
						 					}
						 					
						 					boolean checkFlag2 = false;
						 					if(alCheckList != null && alCheckList.contains(tinnerList.get(0))){
						 						checkFlag2 =true;
						 					}
						 					
						 					double avgTeamGoalRating = 0.0d;
						 					String teamGoalRating = hmTeamGoalRating.get(tinnerList.get(0)+"_RATING");
											String teamGoalTaskCount = hmTeamGoalRating.get(tinnerList.get(0)+"_COUNT");
											if(uF.parseToInt(teamGoalTaskCount) > 0) {
												avgTeamGoalRating = uF.parseToDouble(teamGoalRating) / uF.parseToInt(teamGoalTaskCount);
											}
						 				%>
								 			
											<%
												String tgoalTitle = "Team Objective";
												if(tinnerList.get(3)!=null && tinnerList.get(1)!=null){
													tgoalTitle = tinnerList.get(3)+"("+hmGoalType.get(tinnerList.get(1))+")";
												}
											%>
														
														<div class="box box-primary collapsed-box" style="margin-top:<%=withTeam.equals("block") ? "30px" : "0px" %>; border-top-color: #F0F8CF;">
	                
											                <div class="box-header with-border"  style="display: <%=withTeam %>; background-color:#F0F8CF;">
											                    <h3 class="box-title" style="font-size: 16px;">
											                    	<strong><%=tcount%>)</strong> <%=tinnerList.get(3)%> (<%=hmGoalType.get(tinnerList.get(1)) %>) 
																	<span id="corporateEditId" style="float: right; margin-right: 1cm;"> 
																		<% if((dataType == null || dataType.equals("L"))) { %>
																			<a href="javascript:void(0)" class="edit_lvl" onclick="editGoal('<%=tinnerList.get(0)%>','3','<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.TEAM_GOAL+"_"+minnerList.get(0)))%>','Team Objective','<%=minnerList.get(0)%>','<%=tgoalTitle %>','<%=dataType%>','<%=currUserType %>','<%=cinnerList.get(0)%>')" title="Edit Team Objective"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																			<% if (!checkFlag2){ %>
																				<a style="color: #F02F37;" onclick="deleteGoal('<%=tinnerList.get(0)%>','del','team','<%=tgoalTitle %>','<%=dataType %>','<%=currUserType %>','<%=cinnerList.get(0)%>')" href="javascript:void(0)" style="color:#F02F37;" title="Delete Team Objective" ><i class="fa fa-trash-o" aria-hidden="true"></i></a>
																			<%} else { %>
																				<a style="color: #F02F37;" href="javascript:void(0)" onclick="alert('Employee has already updated the objective. You can not delete this objective.');" style="color:#F02F37;"><i class="fa fa-trash-o" aria-hidden="true"></i></a>
																			<%} %>
																		<%} %>
																		<%if(!uF.parseToBoolean(tinnerList.get(33))){ %>
																			<a style="color: #F02F37;" href="javascript:void(0);" onclick="closeGoal('<%=tinnerList.get(0)%>','close','<%=dataType %>','<%=currUserType %>');" style="color:#F02F37;" title="Close Team Objective" ><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
																		<% } else { %>
																			<a href="javascript:void(0);" onclick="closeGoal('<%=tinnerList.get(0)%>','view','<%=dataType %>','<%=currUserType %>');" title="Close Team Objective Reason"><i class="fa fa-comment-o" aria-hidden="true"></i></a>
																			<a href="javascript:void(0);" onclick="openGoalForLive('<%=tinnerList.get(0)%>','open','<%=dataType %>','<%=currUserType %>');"  title="Open Team Objective for Live"><i class="fa fa-reply" aria-hidden="true"></i></a>
																		<% } %>
																	</span>
											                    </h3>
											                    <div class="box-tools pull-right">
											                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
											                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
											                    </div>
											                </div>
											                <!-- /.box-header -->
											                <div class="box-body" style="padding: 5px; overflow-y: auto; display: <%=withTeam.equals("block") ? "none" : "block"  %>;">
											                    <ul class="level_list" style="padding-left:0px;">
															<li class="desgn">
																<div class="content1">
																<ul class="level_list">
																	<li>
																	<div class="row row_without_margin"  style="display: <%=withTeam %>; margin-right:0px;">
																		<div class="col-lg-8 col-md-8 col-sm-12">
																			<div style="float: left; width: 100%;">
																				<span class="<%=pClassT %>" style="float: left; font-size:11px; line-height: 18px;"><b>Obj: </b><%=tinnerList.get(4) %></span>
																			</div>
																			
																			<div style="float: left; width: 100%;">
																				<span class="<%=pClassT %>" style="float: left; font-size:11px; line-height: 18px;">- assigned by <%=tinnerList.get(34)%>, 
																				attribute <%=tinnerList.get(6) %>, effective date <%=tinnerList.get(31) %>, due date <%=tinnerList.get(16) %>, weightage <%=tinnerList.get(19)%>% </span>
																			</div>
																			
																			<div style="float: left; width: 100%;">
																				<span class="<%=pClassT %>" style="float: left; font-size:11px; line-height: 18px;"><b>Desc:</b> <%=tinnerList.get(5)%></span>
																			</div>
																			
																			<% if (tinnerList.get(7)!=null && tinnerList.get(7).equals("Effort") && uF.parseToBoolean(tinnerList.get(23))) { %>
																			<div style="float: left; width: 100%;">
																				<span class="<%=pClassT %>"><strong>Target:</strong>&nbsp;<%=tinnerList.get(10)%>&nbsp;Days&nbsp;<%=tinnerList.get(11)%>&nbsp;Hrs</span>
																			</div>
																			<% } else if (tinnerList.get(7)!=null && tinnerList.get(7).equals("Amount") && uF.parseToBoolean(tinnerList.get(23))) { %>
																			<div style="float: left; width: 100%;">
																				<span class="<%=pClassT %>"><strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(tinnerList.get(8)))%></span>
																			</div>
																			<% } else if (tinnerList.get(7)!=null && tinnerList.get(7).equals("Percentage") && uF.parseToBoolean(tinnerList.get(23))) { %>
																			<div style="float: left; width: 100%; ">
																				<span class="<%=pClassT %>"><strong>Target:</strong>&nbsp;<%=tinnerList.get(8) %> %</span>
																			</div>
																			<% } %>
																			
																			<div style="float:left; width:100%; margin-left: 0px;">
																				<strong>Assigned To:</strong>
																			</div>
																		<%
																			if(tinnerList.get(29)!=null) {
																			List<String> emplistID=Arrays.asList(tinnerList.get(29).split(","));
																		%>
																			<div style="float:left; margin-left: 15px; margin-left: 0px;">
																				<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
																					if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
																						String empName = hmEmpName.get(emplistID.get(i).trim());
																						String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
																					%>
																				<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left;width:20px;height:20px;margin:2px;">
																				<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
																				</span></a>
																				<% } } %>
																			</div>
																		<% } %>
																		
																		<%if(hmKRA != null && !hmKRA.isEmpty()) { 
																			List<List<String>> outerList = hmKRA.get(tinnerList.get(0));
																			if(outerList != null && !outerList.isEmpty()) {
																		%>
																			<div style="float: left; width: 100%;">
																				<div style="float: left; width: 100%; font-weight: bold;">KRA:&nbsp;</div>
																				<% 
																					for(int ii=0;outerList!=null && ii<outerList.size();ii++) {
																						List<String> innerList = outerList.get(ii);
																						List<List<String>> taskOuterList = hmKRATasks.get(innerList.get(0));
																						double avgKRARating = 0.0d;
																						double avgKRAStatus = 0.0d;
																						String kraRating = hmKRARating.get(innerList.get(0)+"_RATING");
																						String kraTaskCount = hmKRARating.get(innerList.get(0)+"_COUNT");
																						if(uF.parseToInt(kraTaskCount) > 0) {
																							avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
																						}
																						
																						String kraStatus = hmKRAStatus.get(innerList.get(0)+"_STATUS");
																						String kraTaskStatusCount = hmKRAStatus.get(innerList.get(0)+"_COUNT");
																						if(uF.parseToInt(kraTaskStatusCount) > 0) {
																							avgKRAStatus = uF.parseToDouble(kraStatus) / uF.parseToInt(kraTaskStatusCount);
																						}
																					%>
																					<div style="float: left; width: 100%; margin-left: 30px; padding: 3px 0px;">
																						<div style="float: left; width: 46%;"><%=ii+1 %>. <%=innerList.get(7) %></div>
																						<%-- <div style="float: left; width: 21%; margin: 0px 15px;">
																							<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=avgKRAStatus > 85 ? avgKRAStatus - 20 : avgKRAStatus - 4%>%;"><%=uF.showData(""+avgKRAStatus, "0")%>%</span></div>
																							<div id="outbox">
																							<% if (avgKRAStatus < 33.33) { %>
																							
																							<div id="redbox" style="width: <%=uF.showData(""+avgKRAStatus, "0") %>%;"></div>
																							<% } else if (avgKRAStatus >= 33.33 && avgKRAStatus < 66.67) { %>
																							
																							<div id="yellowbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																							<% } else if (avgKRAStatus >= 66.67) { %>
																							
																							<div id="greenbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																							<% } %>
																							</div>
																							<div class="anaAttrib1" style="float: left; width: 100%; line-height: 16px;"><span style="float: left; margin-left:-4%;">0%</span>
																							<span style="float: right; margin-right:-10%;">100%</span></div>
																						</div> --%>
																						<%-- <div style="float: left; margin-left: 10px;">
																							<div id="starPrimaryIGK<%=innerList.get(0) %>"></div>
																							<script type="text/javascript">
																					        	$('#starPrimaryIGK<%=innerList.get(0) %>').raty({
																					        		readOnly: true,
																					        		start: <%=avgKRARating %>,
																					        		half: true,
																					        		targetType: 'number'
																								});
																							</script>
																						</div> --%>
																					</div>
																					<% } %>
																				</div>
																			<% } 
																				} %>
																		
																	</div>
																	<div class="col-lg-4 col-md-4 col-sm-12">
																			<p style="font-size: 10px; text-align: right; padding-right: 20px; font-style: italic;">Last updated by <%=hmEmpName.get(tinnerList.get(22))%> on <%=tinnerList.get(21)%></p>
																			
																			<div style=" background-repeat: no-repeat; background-position: right top; margin-top: 20px; width: 76%;">
																				<div id="starPrimaryTG<%=tinnerList.get(0)%>"></div>
																				<script type="text/javascript">
																			        	$('#starPrimaryTG<%=tinnerList.get(0)%>').raty({
																			        		readOnly: true,
																			        		start: <%=avgTeamGoalRating %>,
																			        		half: true,
																			        		targetType: 'number'
																						});
																				</script>
																				
																			</div>
																			
																			<div style="min-height: 40px; padding-right: 20px;">
																				<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=uF.parseToInt(alltwoDeciTotProgressAvgTeam) > 94 ? uF.parseToInt(alltwoDeciTotProgressAvgTeam)-6 : uF.parseToInt(alltwoDeciTotProgressAvgTeam)-3.5 %>%;"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(uF.parseToDouble(strtwoDeciTotTeam)) %>%</span></div>
																				<div id="outbox">
																				<%if(uF.parseToDouble(alltwoDeciTotProgressAvgTeam) < 33.33){ %> 
																				<div id="redbox" style="width: <%=alltwoDeciTotProgressAvgTeam %>%;"></div>
																				<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgTeam) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgTeam) < 66.67){ %>
																				<div id="yellowbox" style="width: <%=alltwoDeciTotProgressAvgTeam %>%;"></div>
																				<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgTeam) >= 66.67){ %>
																				<div id="greenbox" style="width: <%=alltwoDeciTotProgressAvgTeam %>%;"></div>
																				<%} %>
																				</div>
																				<div class="anaAttrib1" style="float: left; width: 100%; margin-bottom: -10px"><span style="float: left; margin-left: -1.5%;">0%</span>
																				<span style="float: right; margin-right: -7%;"><%=uF.formatIntoOneDecimalIfDecimalValIsThere(uF.parseToDouble(alltotal100Team)) %>%</span></div>
																				<span style="color: #808080;">Slow</span>
																				<span style="margin-left:55px; color: #808080;">Steady</span>
																				<span style="float: right; color: #808080;">Momentum</span>
																			</div>
																		</div>
															</div>
													
													<div style="float: left; background-repeat: no-repeat; background-position: right top; width:60%;">
														<% if(uF.parseToDouble(parentScoreMp.get(IConstants.INDIVIDUAL_GOAL+"_"+tinnerList.get(0)))<100 && (dataType == null || dataType.equals("L"))){ %>
															<p style="margin: 0px 0px 0px 0px" class="addnew desgn">
															<%--if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ --%>
																<a href="javascript:void(0)" class="add_lvl" onclick="newGoal('<%=tinnerList.get(0)%>', '4', '<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.INDIVIDUAL_GOAL+"_"+tinnerList.get(0)))%>','Individual OKR','<%=createStatus %>','<%=dataType %>','<%=currUserType %>', '<%=cinnerList.get(0)%>')" title="Add Individual OKR"> Add Individual OKR</a> <!-- Add New Initiative -->
															<%--} --%>
															</p>
														<%} %>
													</div>
											<div class="clr"></div> 
									<%
						 				List<List<String>> iouterList = hmIndividual.get(tinnerList.get(0));
						 					int icount = 0;
						 					for (int a = 0; iouterList != null && a < iouterList.size(); a++) {
						 						icount++;
						 						List<String> iinnerList = iouterList.get(a);
						 						String pClassI= iinnerList.get(32);
						 						String alltwoDeciTotProgressAvg = "0";
						 						String alltotal100 = "100";
						 						String strtwoDeciTot ="0";
						 						String strTotTarget = "0";
						 						String strTotDays = "0";
						 						String strTotHrs = "0";
						 						Map<String, String> hmIndGoalCalDetails = hmIndGoalCal.get(iinnerList.get(0));
						 						if(hmIndGoalCalDetails != null && !hmIndGoalCalDetails.isEmpty()){
							 						alltwoDeciTotProgressAvg = hmIndGoalCalDetails.get(iinnerList.get(0)+"_PERCENT");
							 				 		alltotal100 = hmIndGoalCalDetails.get(iinnerList.get(0)+"_TOTAL");
							 				 		strtwoDeciTot = hmIndGoalCalDetails.get(iinnerList.get(0)+"_STR_PERCENT");
							 				 		strTotTarget = hmIndGoalCalDetails.get(iinnerList.get(0)+"_ACHIVED_TARGET");
							 				 		strTotDays = hmIndGoalCalDetails.get(iinnerList.get(0)+"_ACHIVED_DAYS");
							 				 		strTotHrs = hmIndGoalCalDetails.get(iinnerList.get(0)+"_ACHIVED_HRS");
						 						}
						 						
						 						boolean checkFlag3 = false;
						 	 					if(alCheckList != null && alCheckList.contains(iinnerList.get(0))){
						 	 						checkFlag3 =true;
						 	 					}
						 	 					
						 	 					String igoalTitle = "Individual OKR";
						 	 					if(iinnerList.get(3)!=null && iinnerList.get(1)!=null){
						 	 						igoalTitle = iinnerList.get(3)+"("+hmGoalType.get(iinnerList.get(1))+")"; 
						 	 					}
											%>
													<div class="box box-primary collapsed-box" style="margin-top:30px;border-top-color: #ECD0F1;">
	                
										                <div class="box-header with-border"  style="background-color:#ECD0F1;">
										                    <h3 class="box-title" style="font-size: 16px;">
										                    	<strong><%=icount%>)</strong> <%=iinnerList.get(3)%> (<%=hmGoalType.get(iinnerList.get(1)) %>) 
																<span id="corporateEditId" style="float: right; margin-right: 1cm;">
																<% if((dataType == null || dataType.equals("L"))) { %>
																	<a href="javascript:void(0)" class="edit_lvl" onclick="editGoal('<%=iinnerList.get(0)%>','4','<%=100-uF.parseToDouble(parentScoreMp.get(IConstants.INDIVIDUAL_GOAL+"_"+tinnerList.get(0)))%>','Individual OKR','<%=tinnerList.get(0)%>','<%=igoalTitle %>','<%=dataType%>','<%=currUserType %>','<%=cinnerList.get(0)%>')" title="Edit Individual OKR"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
																	<% if (!checkFlag3) { %> 
																		<a style="color: #F02F37;" onclick="deleteGoal('<%=iinnerList.get(0)%>','del','individual','<%=igoalTitle %>','<%=dataType %>','<%=currUserType %>','<%=cinnerList.get(0)%>')" href="javascript:void(0)" title="Delete Individual OKR" ><i class="fa fa-trash-o" aria-hidden="true"></i></a>
																	<% } else { %>
																		<a style="color: #F02F37;" href="javascript:void(0)" onclick="alert('Employee has already updated the objective. You can not delete this objective.');"><i class="fa fa-trash-o" aria-hidden="true"></i></a>
																	<% } %>
																<% } %>
																<%if(!uF.parseToBoolean(iinnerList.get(33))){ %>
																	<a href="javascript:void(0);" style="color: #F02F37;" onclick="closeGoal('<%=iinnerList.get(0)%>','close','<%=dataType %>','<%=currUserType %>');" title="Close Individual OKR"><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
																<% } else { %>
																	<a href="javascript:void(0);" onclick="closeGoal('<%=iinnerList.get(0)%>','view','<%=dataType %>','<%=currUserType %>');" title="Close Individual OKR Reason"><i class="fa fa-comment-o" aria-hidden="true"></i></a>
																	<a href="javascript:void(0);" onclick="openGoalForLive('<%=iinnerList.get(0)%>','open','<%=dataType %>','<%=currUserType %>');"  title="Open Individual OKR for Live"><i class="fa fa-reply" aria-hidden="true"></i></a>
																<% } %>
																</span>
										                    </h3>
										                    <div class="box-tools pull-right">
										                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
										                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
										                    </div>
										                </div>
										                <!-- /.box-header -->
										                <div class="box-body" style="padding: 5px; overflow-y: auto;display:none;">
										                    <ul class="level_list" style="padding-left:0px;">
														<li class="desgn">
															<div class="content1">
																<ul class="level_list">
																	<li>
																	<div style="float: right; width: 35%; padding: 0px;">
																		<p style="font-size: 10px; text-align: right; padding-right: 20px; font-style: italic;">Last updated by <%=hmEmpName.get(iinnerList.get(22))%> on <%=iinnerList.get(21)%></p>
																		
																	<%
																		String leftDivWidth = "65%";
																		if(iinnerList.get(13)!=null && iinnerList.get(13).equals("Measure") && uF.parseToBoolean(iinnerList.get(23))) {
																		leftDivWidth = "65%";
																		double avgGoalRating = 0.0d;
																		String goalRating = hmGoalAndTargetRating.get(iinnerList.get(0)+"_RATING");
																		String goalTaskCount = hmGoalAndTargetRating.get(iinnerList.get(0)+"_COUNT");
																		if(uF.parseToInt(goalTaskCount) > 0) {
																			avgGoalRating = uF.parseToDouble(goalRating) / uF.parseToInt(goalTaskCount);
																		}
																	%>
																		<div style=" background-repeat: no-repeat; background-position: right top; width: 80%;">
																			<div  id="starPrimaryIT<%=iinnerList.get(0)%>"></div>
																			<script type="text/javascript">
																	        	$('#starPrimaryIT<%=iinnerList.get(0)%>').raty({
																	        		readOnly: true,
																	        		start: <%=avgGoalRating %>,
																	        		half: true,
																	        		targetType: 'number'
																				});
																			</script>
																		</div>
																		<div style="float: right; width: 80%;">
																			<span style=""><strong>Target:</strong> &nbsp;
																			<%if(iinnerList.get(7)!=null && !iinnerList.get(7).equals("Effort")) { %>
																				<%if(iinnerList.get(7)!=null && iinnerList.get(7).equals("Amount")) { %>
																				 	<%=uF.showData(strCurrency,"")%>&nbsp;
																				 <% } %>
																				 	<%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(strTotTarget)) %>
																			 	<%if(iinnerList.get(7)!=null && iinnerList.get(7).equals("Percentage")) { %>
																				 	&nbsp;%
																				 <% } %>
																			 <% } else { %>
																					 <%=strTotDays %>&nbsp;Days&nbsp;<%=strTotHrs %>&nbsp;Hrs
																			 <%} %>
																			 </span>
																		 </div>
																		
																		<div style="min-height: 40px; padding-right: 10px; width: 80%;">
																			<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=uF.parseToInt(alltwoDeciTotProgressAvg) > 94 ? uF.parseToInt(alltwoDeciTotProgressAvg)-6 : uF.parseToInt(alltwoDeciTotProgressAvg)-4 %>%;"><%=strtwoDeciTot%>%</span></div>
																			<div id="outbox">
																			<%if(uF.parseToDouble(alltwoDeciTotProgressAvg) < 33.33){ %> 
																			<div id="redbox" style="width: <%=alltwoDeciTotProgressAvg %>%;"></div>
																			<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvg) < 66.67){ %>
																			<div id="yellowbox" style="width: <%=alltwoDeciTotProgressAvg %>%;"></div>
																			<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvg) >= 66.67){ %>
																			<div id="greenbox" style="width: <%=alltwoDeciTotProgressAvg %>%;"></div>
																			<%} %>
																			</div>
																			<div class="anaAttrib1" style="float: left; width: 100%; margin-bottom: -7px"><span style="float: left; margin-left: -3.5%;">0%</span>
																			<span style="float: right; margin-right: -10%;"><%=alltotal100 %>%</span></div>
																			<span style="color: #808080;">Slow</span>
																			<span style="margin-left:55px; color: #808080;">Steady</span>
																			<span style="float: right; color: #808080;">Momentum</span>
																		</div>
																		<% } else if(iinnerList.get(13)!=null && iinnerList.get(13).equals("KRA") && uF.parseToBoolean(iinnerList.get(23))) { 
																			double avgGoalRating = 0.0d;
																			String goalRating = hmGoalRating.get(iinnerList.get(0)+"_RATING");
																			String goalTaskCount = hmGoalRating.get(iinnerList.get(0)+"_COUNT");
																			if(uF.parseToInt(goalTaskCount) > 0) {
																				avgGoalRating = uF.parseToDouble(goalRating) / uF.parseToInt(goalTaskCount);
																			}
																		%>
																			
																			<div style="background-repeat: no-repeat; background-position: right top; width: 80%;">
																				<div id="starPrimaryIG<%=iinnerList.get(0)%>"></div>
																				<script type="text/javascript">
																		        	$('#starPrimaryIG<%=iinnerList.get(0)%>').raty({
																		        		readOnly: true,
																		        		start: <%=avgGoalRating %>,
																		        		half: true,
																		        		targetType: 'number'
																					});
																				</script>
																			</div>
																		<% } else {
																			double avgGoalRating = 0.0d;
																			String goalRating = hmGoalAndTargetRating.get(iinnerList.get(0)+"_RATING");
																			String goalTaskCount = hmGoalAndTargetRating.get(iinnerList.get(0)+"_COUNT");
																			if(uF.parseToInt(goalTaskCount) > 0) {
																				avgGoalRating = uF.parseToDouble(goalRating) / uF.parseToInt(goalTaskCount);
																			}
																		%>
																			<div style="background-repeat: no-repeat; background-position: right top; width: 80%;">
																				<div id="starPrimaryIG<%=iinnerList.get(0)%>"></div>
																				<script type="text/javascript">
																			        	$('#starPrimaryIG<%=iinnerList.get(0)%>').raty({
																			        		readOnly: true,
																			        		start: <%=avgGoalRating %>,
																			        		half: true,
																			        		targetType: 'number'
																						});
																				</script>
																			</div>
										
																		<% } %>
																	</div>
																									
																	<div style="float: left; background-repeat: no-repeat; background-position: right top; width: <%=leftDivWidth %>;">
																		<div style="float: left; width: 100%;">
																			<span class="<%=pClassI %>" style="float: left; font-size:11px; line-height: 18px;"><b>Obj: </b><%=iinnerList.get(4) %></span>
																		</div>
																		
																		<div style="float: left; width: 100%;">
																			<span class="<%=pClassI %>" style="float: left; font-size:11px; line-height: 18px;">- assigned by <%=iinnerList.get(34)%>, 
																			attribute <%=iinnerList.get(6) %>, effective date <%=iinnerList.get(31) %>, due date <%=iinnerList.get(16) %>, weightage <%=iinnerList.get(19)%>% </span>
																		</div>
																		
																		<div style="float: left; width: 100%;">
																			<span class="<%=pClassI %>" style="float: left; font-size:11px; line-height: 18px;"><b>Desc:</b> <%=iinnerList.get(5)%></span>
																		</div>
																		
																		<% if (iinnerList.get(7)!=null && iinnerList.get(7).equals("Effort") && uF.parseToBoolean(iinnerList.get(23))) { %>
																		<div style="float: left; width: 100%;">
																			<span class="<%=pClassI %>"><strong>Target:</strong>&nbsp;<%=iinnerList.get(10)%>&nbsp;Days&nbsp;<%=iinnerList.get(11)%>&nbsp;Hrs</span>
																		</div>
																		<% } else if (iinnerList.get(7)!=null && iinnerList.get(7).equals("Amount") && uF.parseToBoolean(iinnerList.get(23))) { %>
																		<div style="float: left; width: 100%;">
																			<span class="<%=pClassI %>"><strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(iinnerList.get(8)))%></span>
																		</div>
																		<% } else if (iinnerList.get(7)!=null && iinnerList.get(7).equals("Percentage") && uF.parseToBoolean(iinnerList.get(23))) { %>
																		<div style="float: left; width: 100%; ">
																			<span class="<%=pClassI %>"><strong>Target:</strong>&nbsp;<%=iinnerList.get(8) %> %</span>
																		</div>
																		<% } %>
						
																		<div style="float:left; width:100%; margin-left: 0px;">
																			<strong>Assigned To:</strong>
																		</div>
																		
																		<%
																		if(iinnerList.get(29)!=null) {
																		List<String> emplistID=Arrays.asList(iinnerList.get(29).split(","));
																		%>
																		<div style="float:left; width:100%; margin-left: 0px;">
																			<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
																				if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
																					String empName = hmEmpName.get(emplistID.get(i).trim());
																					String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
																				%>
																			<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left;width:20px;height:20px;margin:2px;">
																			<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
																			</span></a>
																			<% } }%>
																		</div>
																		<% } %>
																		
																		<%if(hmKRA != null && !hmKRA.isEmpty()) { 
																			List<List<String>> outerList = hmKRA.get(iinnerList.get(0));
																			if(outerList != null && !outerList.isEmpty()) {
																		%>
																			<div style="float: left; width: 100%;">
																				<div style="float: left; width: 100%; font-weight: bold;">KRA:&nbsp;</div>
																				<% 
																					for(int ii=0;outerList!=null && ii<outerList.size();ii++) {
																						List<String> innerList = outerList.get(ii);
																						List<List<String>> taskOuterList = hmKRATasks.get(innerList.get(0));
																						double avgKRARating = 0.0d;
																						double avgKRAStatus = 0.0d;
																						String kraRating = hmKRARating.get(innerList.get(0)+"_RATING");
																						String kraTaskCount = hmKRARating.get(innerList.get(0)+"_COUNT");
																						if(uF.parseToInt(kraTaskCount) > 0) {
																							avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
																						}
																						
																						String kraStatus = hmKRAStatus.get(innerList.get(0)+"_STATUS");
																						String kraTaskStatusCount = hmKRAStatus.get(innerList.get(0)+"_COUNT");
																						if(uF.parseToInt(kraTaskStatusCount) > 0) {
																							avgKRAStatus = uF.parseToDouble(kraStatus) / uF.parseToInt(kraTaskStatusCount);
																						}
																					%>
																					<div style="float: left; width: 100%; margin-left: 30px; padding: 3px 0px;">
																						<div style="float: left; width: 46%;"><%=ii+1 %>. <%=innerList.get(7) %></div>
																						<div style="float: left; width: 21%; margin: 0px 15px;">
																							<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=avgKRAStatus > 85 ? avgKRAStatus - 20 : avgKRAStatus - 4%>%;"><%=uF.showData(uF.formatIntoOneDecimalIfDecimalValIsThere(avgKRAStatus), "0")%>%</span></div>
																							<div id="outbox">
																							<% if (avgKRAStatus < 33.33) { %>
																							
																							<div id="redbox" style="width: <%=uF.showData(""+avgKRAStatus, "0") %>%;"></div>
																							<% } else if (avgKRAStatus >= 33.33 && avgKRAStatus < 66.67) { %>
																							
																							<div id="yellowbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																							<% } else if (avgKRAStatus >= 66.67) { %>
																							
																							<div id="greenbox" style="width: <%=uF.showData(""+avgKRAStatus, "0")%>%;"></div>
																							<% } %>
																							</div>
																							<div class="anaAttrib1" style="float: left; width: 100%; line-height: 16px;"><span style="float: left; margin-left:-4%;">0%</span>
																							<span style="float: right; margin-right:-10%;">100%</span></div>
																						</div>
																						<div style="float: left; margin-left: 10px;">
																							
																								<div id="starPrimaryIGK<%=innerList.get(0) %>"></div>
																								<script type="text/javascript">
																						        	$('#starPrimaryIGK<%=innerList.get(0) %>').raty({
																						        		readOnly: true,
																						        		start: <%=avgKRARating %>,
																						        		half: true,
																						        		targetType: 'number'
																									});
																							</script>
																						</div>
																					</div>
																					<% if(taskOuterList != null && !taskOuterList.isEmpty()) { %>
																						<div style="float: left; width: 100%; font-weight: bold; margin-left: 30px;">Tasks:</div>
																					<% 
																					//System.out.println("hmKRATaskRating ===>> " + hmKRATaskRating);
																					for(int jj=0;taskOuterList!=null && jj<taskOuterList.size(); jj++) {
																							List<String> taskInnerList = taskOuterList.get(jj);
																							double avgKRATaskRating = 0.0d;
																							double avgKRATaskStatus = 0.0d;
																							String kraTaskRating = hmKRATaskRating.get(taskInnerList.get(0)+"_RATING");
																							String KRATaskCount = hmKRATaskRating.get(taskInnerList.get(0)+"_COUNT");
																							//System.out.println("taskInnerList.get(0) ====>>> " + taskInnerList.get(0)+" -- kraTaskRating ===>> " + kraTaskRating + " -- KRATaskCount ===>> " + KRATaskCount);
																							if(uF.parseToInt(KRATaskCount) > 0) {
																								avgKRATaskRating = uF.parseToDouble(kraTaskRating) / uF.parseToInt(KRATaskCount);
																							}
																							
																							String kraTaskStatus = hmKRATaskStatus.get(taskInnerList.get(0)+"_STATUS");
																							String KRATaskStatusCount = hmKRATaskStatus.get(taskInnerList.get(0)+"_COUNT");
																							if(uF.parseToInt(KRATaskStatusCount) > 0) {
																								avgKRATaskStatus = uF.parseToDouble(kraTaskStatus) / uF.parseToInt(KRATaskStatusCount);
																							}
																							
																					%>
																					<div style="float: left; width: 100%; margin-left: 65px; padding: 3px 0px;">
																						<div style="float: left; width: 40%;"><%=ii+1 %>.<%=jj+1 %>. <%=taskInnerList.get(1) %></div>
																						<div style="float: left; width: 21%; margin: 0px 15px;">
																							<div class="anaAttrib1" style="line-height: 16px;"><span style="margin-left:<%=avgKRATaskStatus > 85 ? avgKRATaskStatus - 20 : avgKRATaskStatus - 4%>%;"><%=uF.showData(uF.formatIntoOneDecimalIfDecimalValIsThere(avgKRATaskStatus), "0")%>%</span></div>
																							<div id="outbox">
																							<% if (avgKRATaskStatus < 33.33) { %>
																							<div id="redbox" style="width: <%=uF.showData(""+avgKRATaskStatus, "0") %>%;"></div>
																							<% } else if (avgKRATaskStatus >= 33.33 && avgKRATaskStatus < 66.67) { %>
																							<div id="yellowbox" style="width: <%=uF.showData(""+avgKRATaskStatus, "0")%>%;"></div>
																							<% } else if (avgKRATaskStatus >= 66.67) { %>
																							<div id="greenbox" style="width: <%=uF.showData(""+avgKRATaskStatus, "0")%>%;"></div>
																							<% } %>
																							</div>
																							<div class="anaAttrib1" style="float: left; width: 100%; line-height: 16px;">
																								<span style="float: left; margin-left:-4%;">0%</span>
																								<span style="float: right; margin-right:-10%;">100%</span>
																							</div>
																						</div>
																						<div style="float: left; margin-left: 10px;">
																							
																								<div id="starPrimaryIGKT<%=taskInnerList.get(0) %>"></div>
																								<script type="text/javascript">
																						        	$('#starPrimaryIGKT<%=taskInnerList.get(0) %>').raty({
																						        		readOnly: true,
																						        		start: <%=avgKRATaskRating %>,
																						        		half: true,
																						        		targetType: 'number'
																									});
																							</script>
																						</div>
																					</div>
																						<% } %>
																					<% } %>
																					
																					<% } %>
																				</div>
																			<% } 
																				} %>
																		</div>
																	<div class="clr"></div></li>
																</ul>
						
															</div></li>
														</ul>
										                </div>
										                <!-- /.box-body -->
										            </div>
													 
													<% } %>
													</li>
						
													</ul>
						
													</div></li>
												</ul>
											                </div>
											                <!-- /.box-body -->
											            </div>
														
												<% } %>
												</li>
						
											</ul>
						
											</div></li>
										</ul> 
								                </div>
								                <!-- /.box-body -->
								            </div>
											
										<% } %>
									</li>
						
								</ul>
						
								</div>
					                </div>
					                <!-- /.box-body -->
					            </div>
								
							</div>
							<%
								}
								} if(ccount == 0) {
									if(dataType != null && dataType.equals("C")) {
						%>
						<div class="nodata msg">No company objective closed.</div>
						<% } else { %>
						<div class="nodata msg">No company objective specified.</div>
					<% } %>
						<%} 
							} else if (strSessionUserType != null && strSessionUserType.equals(IConstants.EMPLOYEE)) {
								Map<String, List<List<String>>> hmEmpTeam = (Map<String, List<List<String>>>) request.getAttribute("hmEmpTeam");
								Map<String, List<String>> hmEmpCorporate = (Map<String, List<String>>) request.getAttribute("hmEmpCorporate");
								Map<String, List<List<String>>> hmEmpManager = (Map<String, List<List<String>>>) request.getAttribute("hmEmpManager");
								Map<String, List<List<String>>> hmEmpIndividual = (Map<String, List<List<String>>>) request.getAttribute("hmEmpIndividual");
								
								int checkCount=0;
						%>
					
						<div class="clr"></div>
						<%
							Iterator<String> it1 = hmEmpCorporate.keySet().iterator();
								int cecount = 0;
								while (it1.hasNext()) {
									String key = it1.next();
									List<String> innerList = hmEmpCorporate.get(key);
									cecount++;
									checkCount++;
									String pClass= innerList.get(32);
						%>
						<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">
							<p class="past heading_dash"
								style="text-align: left; padding-left: 35px;">
								<strong><%=cecount%>)</strong> <%=hmGoalType.get(innerList.get(1)) %>
							</p>
							<div class="content1">
								<ul class="level_list">
					
									<li>
									<div style="float: right; width: 30%; padding: 0px;">
											<p style="font-size: 10px; padding-left: 42px; font-style: italic;">Last updated by <%=hmEmpName.get(innerList.get(22))%> on <%=innerList.get(21)%></p>
										</div>
										
										<div style="float: left; background-repeat: no-repeat; background-position: right top; width: 60%;">
											<p style="margin: 0px 0px 0px 15px">
												<strong>Objective:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(4)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Attribute:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(6)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Description:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(5)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
											<%
												if (innerList.get(7)!=null && innerList.get(7).equals("Effort") && uF.parseToBoolean(innerList.get(23))) { 
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(10)%>&nbsp;Days&nbsp;<%=innerList.get(11)%>&nbsp;Hrs</span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Amount") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<span class="<%=pClass %>"><%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(innerList.get(8)))%></span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Percentage") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(8) %> %</span>
											<%
												}
											%>
											</p>
											<p style="margin: 0px 0px 0px 0px">
												<strong>Effective Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(31)%></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												<strong>Due Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(16)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Weightage:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(19)%>%</span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Assigned To:</strong>&nbsp;
					
											</p>
					
										</div>
										<%
										if(innerList.get(20)!=null){
										List<String> emplistID=Arrays.asList(innerList.get(20).split(","));
										%>
										
										<div style="float:left;width:60%; margin-left: 0px;">
											
											<% for(int i=0; emplistID!=null && i<emplistID.size();i++){
												if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
													String empName = hmEmpName.get(emplistID.get(i).trim());
													String empimg=uF.showData(empImageMap.get(emplistID.get(i).trim()), "avatar_photo.png");
												%>
											<a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(i).trim()%>','<%=empName %>');"><span style="float:left;width:20px;height:20px;margin:2px;">
											<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
											</span></a>
											<%} 
											}%>
											
										</div>
										<%} %>
										<div class="clr"></div></li>
								</ul>
					
							</div>
					
						</div>
						<%
							}
						%>
					
					
						<div class="clr"></div>
						<%
							Iterator<String> it2 = hmEmpManager.keySet().iterator();
								int mecount = 0;
								while (it2.hasNext()) {
									String key = it2.next();
									List<List<String>> outerList = hmEmpManager.get(key);
									mecount++;
									checkCount++;
									for (int i = 0; outerList != null && i < outerList.size(); i++) {
										List<String> innerList = outerList.get(i);
										String pClass= innerList.get(32);
						%>
						<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">
							<p class="past heading_dash"
								style="text-align: left; padding-left: 35px;">
								<strong><%=mecount%>)</strong> <%=hmGoalType.get(innerList.get(1)) %>
							</p>
							<div class="content1">
								<ul class="level_list">
					
									<li>
									<div style="float: right; width: 30%; padding: 0px;">
											<p style="font-size: 10px; padding-left: 42px; font-style: italic;">Last updated by <%=hmEmpName.get(innerList.get(22))%> on <%=innerList.get(21)%></p>
										</div>
										
										<div style="float: left; background-repeat: no-repeat; background-position: right top; width: 60%;">
											<p style="margin: 0px 0px 0px 15px">
												<strong>Objective:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(4)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Attribute:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(6)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Description:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(5)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
											<%
												if (innerList.get(7)!=null && innerList.get(7).equals("Effort") && uF.parseToBoolean(innerList.get(23))) { 
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(10)%>&nbsp;Days&nbsp;<%=innerList.get(11)%>&nbsp;Hrs</span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Amount") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<span class="<%=pClass %>"><%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(innerList.get(8)))%></span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Percentage") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(8) %> %</span>
											<%
												}
											%>
											</p>
											<p style="margin: 0px 0px 0px 0px">
												<strong>Effective Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(31)%></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												<strong>Due Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(16)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Weightage:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(19)%>%</span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Assigned To:</strong>&nbsp;
											</p>
					
										</div>
									<%
									if(innerList.get(20)!=null){
									List<String> emplistID=Arrays.asList(innerList.get(20).split(","));
									%>
									<div style="float:left;width:60%; margin-left: 0px;">
										
										<% for(int j=0; emplistID!=null && j<emplistID.size();j++){
											if(emplistID.get(j)!=null && !emplistID.get(j).equals("")){
											String empName = hmEmpName.get(emplistID.get(i).trim());
											String empimg=uF.showData(empImageMap.get(emplistID.get(j).trim()), "avatar_photo.png");
											%>
										<div style="float:left;width:20px;height:20px;margin:2px;"><a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(j).trim()%>');">
										<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></div>
										<%} 
										}%>
										
									</div>
									<%} %>		
					
										<div class="clr"></div></li>
								</ul>
					
							</div>
					
						</div>
						<%
							}
								}
						%>
					
					
						<div class="clr"></div>
						<%
							Iterator<String> it = hmEmpTeam.keySet().iterator();
								int tecount = 0;
								while (it.hasNext()) {
									String key = it.next();
									List<List<String>> outerList = hmEmpTeam.get(key);
									checkCount++;
									
									for (int i = 0; outerList != null && i < outerList.size(); i++) {
										List<String> innerList = outerList.get(i);
										tecount++;
										String pClass= innerList.get(32);
						%>
						<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">
							<p class="past heading_dash"
								style="text-align: left; padding-left: 35px;">
								<strong><%=tecount%>)</strong> <%=hmGoalType.get(innerList.get(1)) %>
							</p>
							<div class="content1">
								<ul class="level_list">
					
									<li>
									<div style="float: right; width: 30%; padding: 0px;">
											<p style="font-size: 10px; padding-left: 42px; font-style: italic;">Last updated by <%=hmEmpName.get(innerList.get(22))%> on <%=innerList.get(21)%></p>
										</div>
										
										<div style="float: left; background-repeat: no-repeat; background-position: right top; width: 60%;">
											<p style="margin: 0px 0px 0px 15px">
												<strong>Objective:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(4)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Attribute:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(6)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Description:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(5)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
											<%
												if (innerList.get(7)!=null && innerList.get(7).equals("Effort") && uF.parseToBoolean(innerList.get(23))) { 
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(10)%>&nbsp;Days&nbsp;<%=innerList.get(11)%>&nbsp;Hrs</span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Amount") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<span class="<%=pClass %>"><%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(innerList.get(8)))%></span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Percentage") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(8) %> %</span>
											<%
												}
											%>
											</p>
											<p style="margin: 0px 0px 0px 0px">
												<strong>Effective Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(31)%></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												<strong>Due Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(16)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Weightage:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(19)%>%</span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Assigned To:</strong>&nbsp;
											</p>
					
										</div>
					
									<%
									if(innerList.get(20)!=null){
									List<String> emplistID=Arrays.asList(innerList.get(20).split(","));
									%>
									<div style="float:left;width:60%; margin-left: 0px;">
										
										<% for(int j=0; emplistID!=null && j<emplistID.size();j++){
											if(emplistID.get(j)!=null && !emplistID.get(j).equals("")){
												String empName = hmEmpName.get(emplistID.get(i).trim());			
											String empimg=uF.showData(empImageMap.get(emplistID.get(j).trim()), "avatar_photo.png");
											%>
										<div style="float:left;width:20px;height:20px;margin:2px;"><a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(j).trim()%>');">
										<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></div>
										<%} 
										}%>
										
									</div>
									<%} %>	
								<div class="clr"></div></li>
					
								</ul>
							</div>
							</div>
							<%
								}
									}
							%>
						
						<div class="clr"></div>
						<%
							Iterator<String> it3 = hmEmpIndividual.keySet().iterator();
								int iecount = 0;
								while (it3.hasNext()) {
									String key = it3.next();
									List<List<String>> outerList = hmEmpIndividual.get(key);
									checkCount++;
									for (int i = 0; outerList != null && i < outerList.size(); i++) {
										List<String> innerList = outerList.get(i);
										iecount++;
										String pClass= innerList.get(32);
						%>
						<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">
							<p class="past heading_dash" style="text-align: left; padding-left: 35px;">
								<strong><%=iecount%>)</strong> <%=hmGoalType.get(innerList.get(1)) %>
							</p>
							<div class="content1">
								<ul class="level_list">
					
									<li>
									<div style="float: right; width: 30%; padding: 0px;">
											<p style="font-size: 10px; padding-left: 42px; font-style: italic;">Last updated by <%=hmEmpName.get(innerList.get(22))%> on <%=innerList.get(21)%></p>
										</div>
										
										<div style="float: left; background-repeat: no-repeat; background-position: right top; width: 60%;">
											<p style="margin: 0px 0px 0px 15px">
												<strong>Objective:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(4)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Attribute:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(6)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Description:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(5)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
											<%
												if (innerList.get(7)!=null && innerList.get(7).equals("Effort") && uF.parseToBoolean(innerList.get(23))) { 
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(10)%>&nbsp;Days&nbsp;<%=innerList.get(11)%>&nbsp;Hrs</span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Amount") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target: <%=uF.showData(strCurrency,"")%></strong>&nbsp;<span class="<%=pClass %>"><%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(innerList.get(8)))%></span>
											<%
												} else if (innerList.get(7)!=null && innerList.get(7).equals("Percentage") && uF.parseToBoolean(innerList.get(23))) {
											%>
											<strong>Target:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(8) %> %</span>
											<%
												}
											%>
											</p>
											<p style="margin: 0px 0px 0px 0px">
												<strong>Effective Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(31)%></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												<strong>Due Date:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(16)%></span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Weightage:</strong>&nbsp;<span class="<%=pClass %>"><%=innerList.get(19)%>%</span>
											</p>
											<p style="margin: 0px 0px 0px 15px">
												<strong>Assigned To:</strong>&nbsp;
											</p>
					
										</div>
							
									<%
									if(innerList.get(20)!=null){
									List<String> emplistID=Arrays.asList(innerList.get(20).split(","));
									%>
									<div style="float:left;width:60%; margin-left: 0px;">
										
										<% for(int j=0; emplistID!=null && j<emplistID.size();j++){
											if(emplistID.get(j)!=null && !emplistID.get(j).equals("")){
												String empName = hmEmpName.get(emplistID.get(i).trim());
											String empimg=uF.showData(empImageMap.get(emplistID.get(j).trim()), "avatar_photo.png");
											%>
										<div style="float:left;width:20px;height:20px;margin:2px;"><a href="javascript:void(0)" title="<%=empName %>" onclick="getEmpProfile('<%=emplistID.get(j).trim()%>');">
										<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+emplistID.get(i).trim()+"/"+IConstants.I_22x22+"/"+empimg%>" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a></div>
										<%} }%>
									</div>
									<%} %>						
					
							<div class="clr"></div></li>
								</ul>
							</div>
						</div>
						<% } }
								
						if(checkCount==0) {
							if(dataType != null && dataType.equals("C")) {
						%>
						<div class="nodata msg">No OKR closed to you</div>
						<% } else { %>
						<div class="nodata msg">No OKR assigned to you</div>
						<% } } %>
					
						<%
						} else {
							if(dataType != null && dataType.equals("C")) {
						%>
							<div class="nodata msg">No OKR closed for you.</div>
						<% } else { %>
							<div class="nodata msg">No OKR assigned to you.</div>
						<% } %>
						<% } %>
						
					<%-- } --%>
                </div>
            <%-- </div>
		 </section>
	</div>
</section>	 --%>

<div id="profile"></div> 
<div id="EditGoal1"></div>
<div id="NewGoal1"></div>
<div id="chartGoal"></div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">-</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<div class="modal" id="profileInfo" role="dialog">
    <div class="modal-dialog proDialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title1">-</h4>
            </div>
            <div class="modal-body1" id="proBody" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton1" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<script>

$(document).ready(function(){
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','#closeButton1',function(){
		$(".proDialog").removeAttr('style');
		$("#proBody").height(400);
		$("#profileInfo").hide();
    });

});


  
</script>