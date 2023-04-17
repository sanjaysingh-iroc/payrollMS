<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<jsp:include page="../performance/KRATarget1_js.jsp"></jsp:include>
<%  UtilityFunctions uF = new UtilityFunctions();
	String fromPage = (String) request.getAttribute("fromPage"); 
	Map<String, String> hmFeatureStatus = (Map<String, String>) request.getAttribute("hmFeatureStatus");
	if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	if(hmFeatureUserTypeId == null) hmFeatureUserTypeId = new HashMap<String, List<String>>();
	 Map<String, Map<String, String>> hmPerspectiveData =(Map<String, Map<String, String>>)request.getAttribute("hmPerspectiveData");
	 String  Perspectcolor = null;
     String  PerspectName = null;
%>
<script type="text/javascript"
	src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.js"> </script>
	
	
<script type="text/javascript">

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
    	
    });
    function updateComment1(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,accessFlag) 
    {
    	
    	var completedPercent = 0;
        if(goalType == 'KRA') {
        completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).value;
        } else {
        completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId).value;
        }
    	 if(confirm('Are you sure, you wish to Add task RAting and Comment?')) {
    		 xmlhttp = GetXmlHttpObject();
             if (xmlhttp == null) {
                     alert("Browser does not support HTTP Request");
                     return;
             } else {
                     var xhr = $.ajax({
                            url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId+'&kraTaskId='+kraTaskId
                            	+'&completedPercent='+completedPercent+'&type=GoalKRA&goalType='+goalType,
                            cache : false,
                            success : function(data) {
                            	if(goalType == 'KRA') {
                                  		document.getElementById("TaskRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).style.display = "block";
                                    }else{
                             			document.getElementById("GoalRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId).style.display = "block";
									}
                            	}
                            });
                            	
                     }
    		 
    	 }
    }
    function updateComment(empId, goalid, goalFreqId, kraId, kraTaskId, goalType,accessFlag) {
    	
    
    	var completedPercent = 0;
        if(goalType == 'KRA') {
        completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).value;
        } else {
        completedPercent = document.getElementById("completedPercent_"+empId+"_"+goalid+"_"+goalFreqId).value;
        }
    	 if(confirm('Are you sure, you wish to Add task RAting and Comment?')) {
    		 xmlhttp = GetXmlHttpObject();
             if (xmlhttp == null) {
                     alert("Browser does not support HTTP Request");
                     return;
             } else {
                     var xhr = $.ajax({
                            url : 'UpdateKRATaskStatus.action?empId='+empId+'&goalid='+goalid+'&goalFreqId='+goalFreqId+'&kraId='+kraId+'&kraTaskId='+kraTaskId
                            	+'&completedPercent='+completedPercent+'&type=GoalKRA&goalType='+goalType,
                            cache : false,
                            success : function(data) {
                            	if(goalType == 'KRA') {
                                  		document.getElementById("TaskRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId+"_"+kraId+"_"+kraTaskId).style.display = "block";
                                    }else{
                             			document.getElementById("GoalRatingDiv_"+empId+"_"+goalid+"_"+goalFreqId).style.display = "block";
									}
                            	}
                            });
                            	
                     }
    		 
    	 }
	
		}
  </script>
<%
    CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
	String []arrEnabledModules = CF.getArrEnabledModules();
	 String strSessionUserType = (String) session.getAttribute(IConstants.USERTYPE);
    String strSessionEmpId = (String) session.getAttribute(IConstants.EMPID);
    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
    Map<String, String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
  	Map<String, String> hmEmpName = (Map<String, String>) request.getAttribute("hmEmpName");
     Map<String, List<List<String>>> hmEmpKra = (Map<String, List<List<String>>>) request.getAttribute("hmEmpKra");
     List<String> alCheckList = (List<String>) request.getAttribute("alCheckList");
    Map<String, String> hmOrientationViewAccess = (Map<String, String>) request.getAttribute("hmOrientationViewAccess");
	if(hmOrientationViewAccess == null) hmOrientationViewAccess = new HashMap<String,String>();
	Map<String, String> hmOrientationEditAccess = (Map<String, String>) request.getAttribute("hmOrientationEditAccess");
	if(hmOrientationEditAccess == null) hmOrientationEditAccess = new HashMap<String,String>();
	Map<String, String> hmSectionGivenQueCnt = (Map<String, String>) request.getAttribute("hmSectionGivenQueCnt");
	if(hmSectionGivenQueCnt == null) hmSectionGivenQueCnt = new HashMap<String, String>();
	Map<String, String> hmSectionQueCnt = (Map<String, String>) request.getAttribute("hmSectionQueCnt");
	if(hmSectionQueCnt == null) hmSectionQueCnt = new HashMap<String, String>();
	Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
	if (hmEmpProfile == null) {
		hmEmpProfile = new HashMap<String, String>();
	}
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	  %>
<section class="content">
<div class="row jscroll">
	<section class="col-lg-12 connectedSortable">
	<div class="box box-none nav-tabs-custom">
		<ul class="nav nav-tabs">
			<% String dataType = (String) request.getAttribute("dataType");
	            if(dataType != null && dataType.equals("L")) { %>
			<li class="active"><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','L','<%=fromPage%>')" data-toggle="tab">Live</a>
			</li>
			<li><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','C','<%=fromPage%>')" data-toggle="tab">Closed</a>
			</li>
			<% } else if(dataType != null && dataType.equals("C")) { %>
			<li><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','L','<%=fromPage%>')" data-toggle="tab">Live</a>
			</li>
			<li class="active"><a href="javascript:void(0)" onclick="getKRATargetData('KRATarget','C','<%=fromPage%>')" data-toggle="tab">Closed</a>
			</li>
			<% } %>
		</ul>
		<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
			<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
			<section class="content">
			<div class="row jscroll">
				<section class="col-lg-4 connectedSortable">
	 			<div class="box box-widget widget-user widget-user1">
			            <!-- Add the bg color to the header using any of the bg-* classes -->
			            <div class="widget-user-header bg-aqua-active">
			         <!-- ====start parvez on 27-10-2022===== -->     
			              <%-- <img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
			              
			        		<%if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
								List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
								System.out.println("KRATarget_1");
							%>
								<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" data-original='<%=docRetriveLocation+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
							<% } else{ %>
								<img class="lazy" src="images1/user-background-photo.jpg" data-original='<%=docRetriveLocation+IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
							<% } %>
			         <!-- ====end parvez on 27-10-2022===== -->     
			              <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: 0px;"><span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>  <!--  -->
			              	<span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
			              </h3>
			              <h5 class="widget-user-desc"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
			            </div>
			            <div class="widget-user-image">
			            	<%if(docRetriveLocation==null) { %> 
			              	<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + hmEmpProfile.get("IMAGE")%>">
			              	<%} else { %>
			              	<img class="img-circle lazy" id="profilecontainerimg" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+(String) session.getAttribute(IConstants.EMPID)+"/"+IConstants.I_100x100+"/"+hmEmpProfile.get("IMAGE")%>">
			              	<%} %>
			            </div>
			            <div class="box-footer">
			              <div class="row">
			                <div class="col-sm-12">
			                  <div class="description-block">
			                    <h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> </h5> <%-- [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>] --%>
			                    <span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%> </span> <%-- [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] --%>
			                    <p class="description-text"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%> </p>
			                    <%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
			                   	  <span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong> </span>
			                  	<% } else { %>
			                  	  You don't have a reporting manager.
			                  	<% } %>
			                  </div>
			                  </div>
			              </div>
			             </div>
			          </div>
                   <div class="box box-info">
			          <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){ 
		            	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
		            	Map<String,List<List<String>>> hmElementAttribute=(Map<String,List<List<String>>>)request.getAttribute("hmElementAttribute");
						Map<String, String> hmScoreAggregateMap =(Map<String, String>) request.getAttribute("hmScoreAggregateMap");
		            	double dblScorePrimary = 0, aggregeteMarks = 0, totAllAttribMarks = 0;
		 				int count = 0;
		
		 				for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
		 					List<String> innerList = elementouterList.get(i);
		 					List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
		 					
		 					if(attributeouterList1 != null && !attributeouterList1.isEmpty()) {
			 					for (int j = 0; j < attributeouterList1.size(); j++) {
			 						List<String> attributeList1 = attributeouterList1.get(j);
			 						totAllAttribMarks += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
			 						count++;
			 					}
		 					} else {
		 						count++;
		 					}
		 				}
		 				aggregeteMarks = totAllAttribMarks / count;
						dblScorePrimary = aggregeteMarks; 
		             %>
	                <div class="box-header with-border">
	                  <h3 class="box-title">My Ratings</h3>
	                  <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                 <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	 <div id="profilecontainer">
							<div class="content1">
								<div class="holder" style="max-height: 250px;">
									<div style="float:left;width:100%">
									<div style="float:left;width:200px;">Overall: </div>
										<div id="starAllPrimary" style="float:left; width: 100px;"></div>
							   			<input type="hidden" id="gradeAllwithrating" value="0" />
											<script type="text/javascript">
										        	$('#starAllPrimary').raty({
										        		readOnly: true,
										        		start: <%=dblScorePrimary %>,
										        		half: true,
										        		targetType: 'number',
										        		click: function(score, evt) {
										        			$('#gradeAllwithrating').val(score);
										        		}
										        	});
								        	</script>
								</div>
								<%
								for(int i=0; elementouterList != null && i<elementouterList.size();i++){
									List<String> innerList=elementouterList.get(i);
								%><div style="float:left; padding-left: 10px; width:100%"><strong><%=innerList.get(1)%></strong></div>
									<%List<List<String>> attributeouterList1=hmElementAttribute.get(innerList.get(0).trim());
								       for(int j=0; attributeouterList1 != null && j<attributeouterList1.size();j++) {
										List<String> attributeList1=attributeouterList1.get(j);
								    %><div style="float:left; padding-left: 17px; width:98%;">
										<div style="float:left;width:200px;"><%=attributeList1.get(1) %>: </div>
										<div id="starPrimary<%=i %><%=j %>" style="float:left; width: 100px;"></div>
							   			<input type="hidden" id="gradewithrating<%=i %><%=j%>" value="<%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>"; name="gradewithrating<%=i %><%=j%>" />
											<script type="text/javascript">
										        	$('#starPrimary<%=i %><%=j %>').raty({
										        		readOnly: true,
										        		start: <%=hmScoreAggregateMap.get(attributeList1.get(0).trim()) != null ? uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim())) + "" : "0"%>,
										        		half: true,
										        		targetType: 'number',
										        		click: function(score, evt) {
										        			$('#gradewithrating<%=i %><%=j %>').val(score);
										        			}
										        	});
								        	</script>
										</div>
								      <% }}%>
								<%
								if(hmElementAttribute == null || hmElementAttribute.isEmpty()){
								%>
								<div class="nodata msg" style="width: 93%">
									<span>No attribute aligned with this level</span>
								</div>
								<%} %>
								 </div>
			                </div>
						</div>
						<% } %>
	                </div><!-- /.box-body -->
	              </div>
	              <div class="box box-default">
			          <% List<List<String>> alSkills = (List<List<String>>)request.getAttribute("alSkills"); %>
	                <div class="box-header with-border">
	                	<h3 class="box-title">My Skills</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-gray"><%=alSkills != null ? alSkills.size() : "0" %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                 <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="profilecontainer">
							<div class="content1">
								<div class="holder">
							<%
								Map<String, String> hmSkillAvgRating = (Map<String, String>) request.getAttribute("hmSkillAvgRating");
								int i=0;
								for(;alSkills!=null && i<alSkills.size();i++) {
									List<String> alInner = (List<String>)alSkills.get(i);
									
									String skillValue = "0";
									double skillrate = 0.0d;
									String ratingBy = "(Self Rated)";
									if(hmSkillAvgRating != null && uF.parseToDouble(hmSkillAvgRating.get(alInner.get(0))) > 0) {
										skillrate = uF.parseToDouble(hmSkillAvgRating.get(alInner.get(0))) / 20;
										ratingBy = "";
									} else {
										skillrate = uF.parseToDouble(alInner.get(2)) / 2;
									}
								%>
								<div style="float:left;width:100%">
									<div style="float:left; width: 200px;"><%=alInner.get(1) %>&nbsp;: </div>
										<div id="starSkills_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
											        	$('#starSkills_<%=i %>').raty({
											        		readOnly: true,
											        		start: <%=skillrate %>,
											        		half: true, 
											        		targetType: 'number'
											        	});
								        	</script>
										</div>
								<% }
								if(i==0) {
									%>
									<div class="tdDashLabel" style="width: 93%">
									<span>No skills aligned.</span>
									</div>
								<% } %>
								</div>
							</div>
						</div>
	                </div>
	              </div>
	              <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
					%>
	              <div class="box box-success">
			          <%	List<Map<String,String>> myKRAList= (List<Map<String,String>>)request.getAttribute("myKRAList"); 
							List<List<String>> goalIdList = (List<List<String>>)request.getAttribute("goalIdList");
							int intKRACnt = myKRAList != null ? myKRAList.size() : 0;
							int intGoalCnt = goalIdList != null ? goalIdList.size() : 0;
						%>
	                <div class="box-header with-border">
	                	<h3 class="box-title">My Goals</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-green"><%=(intKRACnt + intGoalCnt) %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="profilecontainer">
							<div class="content1">
								<div class="holder" style="max-height: 300px;">
							<%
								int cnt = 0;
								i=0;
								for(;myKRAList!=null && i<myKRAList.size();i++) {
									Map<String, String> hmKRA =(Map<String, String>)myKRAList.get(i);
									cnt++;
									if(cnt > 20) {
										break;
									}
								%><div style="float:left;width:100%;padding-top: 2px;padding-bottom: 2px;">
								<div style="float:left; min-width: 200px;"><%=hmKRA.get("KRA_NAME") %>: </div>
										
										<%if(uF.parseToDouble(hmKRA.get("KRA_AVERAGE"))<=0) { %>
										 	<div>Not Rated</div>
										<%} else { %>
										<div id="starKRA_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
												        	$('#starKRA_<%=i %>').raty({
												        		readOnly: true,
												        		start: <%=hmKRA.get("KRA_AVERAGE") != null ? uF.parseToDouble(hmKRA.get("KRA_AVERAGE")) + "" : "0"%>,
												        		half: true,
												        		targetType: 'number'
												        	});
								        	</script>
										<% } %>
										</div>
								<% } 
								Map<String, String> hmGoalAverage = (Map<String, String>) request.getAttribute("hmGoalAverage");
								i=0;
								for(;goalIdList!=null && i<goalIdList.size();i++) {
									List<String> goalInnerList =goalIdList.get(i);
									cnt++;
									if(cnt > 20) {
										break;
									}
								%><div style="float:left;width:100%;padding-top: 2px;padding-bottom: 2px;">
								<div style="float:left; min-width: 200px;"><%=goalInnerList.get(1) %>: </div>
									<div id="starMYGOALS_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
											        	$('#starMYGOALS_<%=i %>').raty({
											        		readOnly: true,
											        		start: <%=hmGoalAverage.get(goalInnerList.get(0)) != null ? uF.parseToDouble(hmGoalAverage.get(goalInnerList.get(0))) + "" : "0"%>,
											        		half: true,
											        		targetType: 'number'
											        	});
								        	</script>
										</div>
								<% } %>
								<% if(i==0) { %>
									<div class="tdDashLabel" style="width: 93%">
									<span>No Goals found.</span>
									</div>
								<%} %>
								</div>
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	 		</section>
	 		
				<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_SHOW_GOAL_KRA_TARGET))) { %>
				<section class="col-lg-4 connectedSortable">
				<h3 style="margin-top: 0px; font-size: 14px !important; font-weight: 600;" class="pagetitle">My Goal</h3> <!-- My Goals, KRAs & Targets -->
				<% if (strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
				<div>
					<input type="hidden" name="hideOrgid" id="hideOrgid" value="<%=request.getAttribute("strID")%>"> 
					<a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myGoal();" class="" title="Add New Objective1">
						<i class="fa fa-plus-circle" aria-hidden="true"></i>&nbsp;&nbsp;Add new Objective</a>  <!-- Add New Personal Goals & Targets -->
				</div>
				<% } %> <br />
				<%
                                Map<String, Map<String, Map<String, List<List<String>>>>> hmGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmGoalKraEmpwise");
                                if(hmGoalKraEmpwise == null) hmGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                                
                                Map<String, List<String>> hmGoalDetails = (Map<String, List<String>>) request.getAttribute("hmGoalDetails");
                                if(hmGoalDetails == null) hmGoalDetails = new HashMap<String, List<String>>();
                           		Map<String, List<String>> hmGoalKraPerspective = (Map<String, List<String>>) request.getAttribute("hmGoalKraPerspective");
                                if(hmGoalKraPerspective == null) hmGoalKraPerspective = new HashMap<String, List<String>>();
								  Map<String, List<List<String>>> hmKRATasks = (Map<String, List<List<String>>>) request.getAttribute("hmKRATasks");
                                if(hmKRATasks == null) hmKRATasks = new HashMap<String, List<List<String>>>();
                                
                                Map<String, String> hmKRATaskStatusAndRating = (Map<String, String>) request.getAttribute("hmKRATaskStatusAndRating");
                                if(hmKRATaskStatusAndRating == null) hmKRATaskStatusAndRating = new HashMap<String, String>();
                                
                                Map<String, String> hmTargetRatingAndComment = (Map<String, String>) request.getAttribute("hmTargetRatingAndComment");
                                if(hmTargetRatingAndComment == null) hmTargetRatingAndComment = new HashMap<String, String>();
                                
                                Map<String, String> hmEmpwiseKRARating = (Map<String, String>) request.getAttribute("hmEmpwiseKRARating");
                                if(hmEmpwiseKRARating == null) hmEmpwiseKRARating = new HashMap<String, String>();
                                  Map<String, String> hmEmpwiseGoalRating = (Map<String, String>) request.getAttribute("hmEmpwiseGoalRating");
                                if(hmEmpwiseGoalRating == null) hmEmpwiseGoalRating = new HashMap<String, String>();
                                Map<String, String> hmEmpwiseGoalAndTargetRating = (Map<String, String>) request.getAttribute("hmEmpwiseGoalAndTargetRating");
                                if(hmEmpwiseGoalAndTargetRating == null) hmEmpwiseGoalAndTargetRating = new HashMap<String, String>();
                                 Map<String, String> hmEmpwiseKRACnt = (Map<String, String>) request.getAttribute("hmEmpwiseKRACnt");
                                if(hmEmpwiseKRACnt == null) hmEmpwiseKRACnt = new HashMap<String, String>();
                                Map<String, String> hmTargetValue = (Map<String, String>)request.getAttribute("hmTargetValue");
                                Map<String, String> hmTargetID = (Map<String, String>)request.getAttribute("hmTargetID");
                                Map<String, String> hmTargetRemark = (Map<String, String>)request.getAttribute("hmTargetRemark");
                                Map<String, String> hmUpdateBy = (Map<String, String>)request.getAttribute("hmUpdateBy");
                                String strCurrency = (String) request.getAttribute("strCurrency");
                                List<String> empList=(List<String>)request.getAttribute("empList");
                                Map<String, String> hmHrIds = (Map<String, String>) request.getAttribute("hmHrIds");
                                if(hmHrIds == null) hmHrIds = new HashMap<String, String>();
                                
                                Map<String, String> hmEmpCodeName = (Map<String, String>)request.getAttribute("hmEmpName");
                                
                                Map<String, Map<String, Map<String, List<List<String>>>>> hmIndividualGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmIndividualGoalKraEmpwise");
                                if(hmIndividualGoalKraEmpwise == null) hmIndividualGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                                
                                Map<String, List<String>> hmIndividualGoalDetails = (Map<String, List<String>>) request.getAttribute("hmIndividualGoalDetails");
                                if(hmIndividualGoalDetails == null) hmIndividualGoalDetails = new HashMap<String, List<String>>();
                                
                                Map<String, String> hmKraAverage = (Map<String, String>)request.getAttribute("hmKraAverage");
                                if(hmKraAverage == null) hmKraAverage = new HashMap<String, String>();
                                
                                Map<String, String> hmCheckGTWithAllowance = (Map<String, String>)request.getAttribute("hmCheckGTWithAllowance");
                                if(hmCheckGTWithAllowance == null) hmCheckGTWithAllowance = new HashMap<String, String>();
                                
                                Map<String, String> hmCheckKWithAllowance = (Map<String, String>)request.getAttribute("hmCheckKWithAllowance");
                                if(hmCheckKWithAllowance == null) hmCheckKWithAllowance = new HashMap<String, String>();
                                
                                
                                Map<String, String> hmEmpwiseGoalAndTargetEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseGoalAndTargetEmpRating");
                                if(hmEmpwiseGoalAndTargetEmpRating == null) hmEmpwiseGoalAndTargetEmpRating = new HashMap<String, String>();
                                
                                Map<String, String> hmKRATaskEmpRating = (Map<String, String>)request.getAttribute("hmKRATaskEmpRating");
                                if(hmKRATaskEmpRating == null) hmKRATaskEmpRating = new HashMap<String, String>();
                                
                                Map<String, String> hmEmpwiseKRAEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseKRAEmpRating");
                                if(hmEmpwiseKRAEmpRating == null) hmEmpwiseKRAEmpRating = new HashMap<String, String>();
                                
                                Map<String, String> hmEmpwiseGoalEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseGoalEmpRating");
                                if(hmEmpwiseGoalEmpRating == null) hmEmpwiseGoalEmpRating = new HashMap<String, String>();
                                
                                Map<String, List<String>> hmActualAchievedGoal = (Map<String, List<String>>)request.getAttribute("hmActualAchievedGoal");
                    			if(hmActualAchievedGoal == null) hmActualAchievedGoal = new HashMap<String, List<String>>();
                                
                    			 Map<String,String> hmGoaldetailsData = ( Map<String,String>)request.getAttribute("hmGoaldetailsData");
								 Map<String, Map<String, List<List<String>>>> hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(strSessionEmpId);

								 String perspectiveId = hmGoaldetailsData.get(strSessionEmpId);
                    			  Map<String,String> hmGoal = ( Map<String,String>)request.getAttribute("hmGoal");
	                    		
                    			 if(hmPerspectiveData==null) hmPerspectiveData = new HashMap<String, Map<String, String>>();
                    			 	Map<String, String> hmPerspectData = hmPerspectiveData.get(perspectiveId);

                                	String perspectColor = hmPerspectData.get("PERSPECTIVE_COLOR");
                                	String perspectName = hmPerspectData.get("PERSPECTIVE_NAME");
                                
                                  	 %>
                                 
                                   	
                                	<div class="box box-primary collapsed-box" style="border-top-color: #E0E0E0;"> 
                                	
                                	  	<div class="box-header with-border" id="headerName" style="height: 40px;">
                                	  		
											<h3 class="box-title" id ="nameper" style="font-size: 14px !important; color: #000000 !important; font-weight: 500 !important;"><%= perspectName %>
											</h3>
										<div class="box-tools pull-right">
											<button class="btn btn-box-tool" data-widget="collapse" ><i class="fa fa-plus"></i></button>
											<button class="btn btn-box-tool" data-widget="remove" onClick="showPerData();"><i class="fa fa-times"></i></button>
										</div>
										</div>
										
									
										<div class="box-body" style="padding: 5px; overflow-y: auto; display: none; ">
										<!--  margin-top=-60px;-->
											
										 <div align="center" style="float: left; margin-top:20px; width: 3%;padding-top:150px;word-wrap:break-word;border-radius:5px;height:500px;background-color:<%=perspectColor%>">
                                              <%=perspectName%>
										</div> 
										
										<div style="float: left; width: 95%; margin-bottom: 5px;  margin-left:5px;">
										<!--  margin-top:5px;-->
										<!-- 1214 -->
										 <%
									 if (hmGoal != null && !hmGoal.isEmpty()) {
										Iterator<String> itGoalS = hmGoal.keySet().iterator();
										
				                           while (itGoalS.hasNext()) {
				                          String GoalId = itGoalS.next();
				                         if (hmGoalKraSuperIdwise != null && !hmGoalKraSuperIdwise.isEmpty()) {
	                                   	Iterator<String> itGoalSIKRA = hmGoalKraSuperIdwise.keySet().iterator();
	                                    	
                                    		int goalCount = 0;
                                    	while (itGoalSIKRA.hasNext()) {
                                			String superId = itGoalSIKRA.next();
                                    		Map<String, List<List<String>>> hmGoalKra = hmGoalKraSuperIdwise.get(superId);
                                    		
                                    		if(hmGoalKra == null) hmGoalKra = new HashMap<String, List<List<String>>>();
                                    		
                                    		Iterator<String> itgKRA = hmGoalKra.keySet().iterator();
                                    		 while(itgKRA.hasNext()) {
                                    			goalCount++;
                                    			String goalAndFreqId = itgKRA.next();
                                    		
                                    			List<String> gInnerList = hmGoalKraPerspective.get(GoalId);
                                    			if(gInnerList!=null)
                                    		{		
                                    			String goalid = gInnerList.get(1);
                                    			String goalFreqId = gInnerList.get(32);
                                    			double avgGoalRating = 0.0d;
                                    			double dblWeightScore = 0.0d;
                                    			if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                    				String goalRating = hmEmpwiseGoalRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                    				String goalTaskCount = hmEmpwiseGoalRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                    				
                                    				String goalEmpRating = hmEmpwiseGoalEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
                                    				String goalEmpTaskCount = hmEmpwiseGoalEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                    				if(uF.parseToInt(goalTaskCount) > 0 || uF.parseToInt(goalEmpTaskCount) > 0) {
                                    					avgGoalRating = (uF.parseToDouble(goalRating) + uF.parseToDouble(goalEmpRating)) / (uF.parseToInt(goalTaskCount) + uF.parseToInt(goalEmpTaskCount));
                                    				}
                                    				dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                                    			} else {
                                    				double avgEmpGoalRating = 0;
                                    				avgGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING"));
                                    				if(uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT")) > 0) {
                                    					avgEmpGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING"))/ uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT"));
                                    					if(avgGoalRating > 0) {
                                    						avgGoalRating = (avgGoalRating + avgEmpGoalRating) / 2; 
                                    					} else {
                                    						avgGoalRating = avgEmpGoalRating;
                                    					}
                                    				}
                                    				
                                    				dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                                    			}
                                    			
                                    			
                                    			String strGoalType = "Goal";
                                    			if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                    				strGoalType = "KRA";
                                    			} else if(gInnerList.get(20) != null && gInnerList.get(20).equals("Measure")) {
                                    				strGoalType = "Target";
                                    			}
                                    			String strPHeight = "";
                                    			if (uF.parseToInt(gInnerList.get(18)) == IConstants.PERSONAL_GOAL && strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) {
                                    				strPHeight = "height: auto;";
                                    			}
                                    			
                                    			
                                    	%> <%  
										 boolean flag = true;
										 if(gInnerList.get(34)!= null && uF.parseToInt(gInnerList.get(34)) > 5) {
											 flag = false;
										 }	
									 	  String orientaionKey = gInnerList.get(34)+"_"+strUserTypeId;
									 	  String userId = gInnerList.get(36);
									 	
										 if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationViewAccess.get(orientaionKey)) || gInnerList.get(35).contains(","+strSessionEmpId+",")))) { 
										 %>
				 <div style="border-top-color: #E0E0E0;">
					 <div  style="height: 40px;">
						<h3 class="box-title"
							style="font-size: 14px !important; color: #000000 !important; font-weight: 500 !important;"><%=gInnerList.get(3) %>
							<%=(gInnerList.get(33) != null && !gInnerList.get(33).equals("")) ? "["+gInnerList.get(33)+"]" : "" %>
							(<%=strGoalType %>)
							<% if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
							<% if (uF.parseToInt(gInnerList.get(18)) == IConstants.PERSONAL_GOAL && strSessionUserType.equalsIgnoreCase(IConstants.EMPLOYEE)) { %>
							<%if(!uF.parseToBoolean(gInnerList.get(12))) { %>
							<a href="javascript:void(0);" onclick="closeGoal('<%=goalid %>', 'close');" title="Close Goal"><i class="fa fa-times-circle-o" aria-hidden="true"></i>
							</a>
							<% if(!alCheckList.contains(goalid)) { %>
							<a href="javascript:void(0)" style="float: right;" class="del" title="Delete Initiative" onclick="deleteGoal('<%=goalid %>')"><span class="fa fa-trash" style="color: rgb(218, 0, 0);"></span>
							</a>
							<% } else { %>
							<a href="javascript:void(0)" class="del" onclick="alert('You have already updated the Goal. You can not delete this Goal.');"> <span class="fa fa-trash" style="color: rgb(218, 0, 0);"></span>
							</a>
							<% } %>
							<a href="javascript:void(0)" style="float: right; padding-right: 3px;" class="edit_lvl" onclick="editGoal('<%=goalid %>')" title="Edit Initiative"><i class="fa fa-pencil-square-o" aria-hidden="true"></i>
							</a>
							<% } else { %>
							<a href="javascript:void(0);" onclick="closeGoal('<%=goalid %>', 'view');" title="Close Initiative Reason"><i class="fa fa-comment-o" aria-hidden="true"></i></a>
							<a href="javascript:void(0);" onclick="openGoalForLive('<%=goalid%>','open');" title="Open Initiative for Live"> <i class="fa fa-reply" aria-hidden="true"></i> </a>
							<%} %>
							<% } 
			                                               }%>
							<% if (uF.parseToInt(gInnerList.get(6)) > 0) { %>
							<a href="javascript:void(0)" style="float: right; margin: 3px 5px;" onclick="goalChart('<%=gInnerList.get(8) %>','<%=gInnerList.get(7) %>','<%=gInnerList.get(6) %>','<%=goalid %>')" title="Goal Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>
							<% } else if (uF.parseToInt(gInnerList.get(18)) != IConstants.PERSONAL_GOAL) { %>
							<span style="float: right; margin: 0px 5px; font-weight: normal;">(Not Aligned)</span>
							<% } %>
							<br />
							<% if (uF.parseToBoolean(gInnerList.get(25)) && gInnerList.get(26) != null && !gInnerList.get(26).equals("")) { %>
							<span style="font-weight: normal; font-size: 11px;">This target aligned with</span> <span style="font-size: 11px;"><%=gInnerList.get(26)%></span>
							<span style="font-weight: normal; font-size: 11px;">(Team Key Result).</span>
							<% } %>
						</h3>
					</div>
						<div style="float: left; width: 100%; margin-bottom: 5px;">
							<div style="float: left; width: 100%;">
								<div style="float: left; width: 100%;">
									<div style="float: right; margin-top: 5px;">
										<div
											id="starPrimaryG<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>"
											style="float: left; margin-top:-18px; width:100%"></div>
										<div style="float: right; margin-right: 10px; margin-top:-14px;">
											<b>Rated Score:</b>
											<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblWeightScore+"")) %>/<%=uF.parseToDouble(gInnerList.get(16)) %></div>
										<div
											style="float: right; margin: 10px 25px 1px 0px; padding-bottom: 3px; padding-right: 3px;text-align:center;">
											<%
												List<String> goalAchieved = hmActualAchievedGoal.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
												 if(goalAchieved == null) goalAchieved = new ArrayList<String>();
											%>
											<table cellspacing="0" cellpadding="2"
												class="table table-bordered">
												<tr>
													<th colspan="4" style="padding: 0px;text-align:center; ">Finalisation Score</th>
												</tr>
												<tr>
													<th style="width: 70px; padding: 0px;text-align:center;">Weightage</th>
													<th style="width: 100px; padding: 0px;text-align:center;">Actual Achieved</th>
													<th style="width: 105px; padding: 0px;text-align:center;">Achieved Share</th>
												</tr>

												<tr>
													<td><%=uF.parseToDouble(gInnerList.get(16))%></td>
													<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(5) : "-", "-")%> </td>
													<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(4) : "-", "-")%> </td>
												</tr>
											</table>
										</div>
									</div>
								</div>
								<div style="float: left; width: 100%;">
									<span class="<%=gInnerList.get(10) %>" style="float: left; margin-left: 15px; font-size: 12px; line-height: 18px;"><b>Obj:
									</b><%=uF.showData(gInnerList.get(13), "-") %></span>
								</div>
								<div
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC;">
									<span class="<%=gInnerList.get(10) %>" style="float: left; margin-left: 15px; font-size: 12px; line-height: 18px;">-
										assigned by <%=gInnerList.get(4)%>, attribute <%=gInnerList.get(14) %>, effective date <%=gInnerList.get(15) %>, due date <%=gInnerList.get(5)%>
									</span>
								</div>
								<script type="text/javascript">
                                    $('#starPrimaryG<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>').raty({
                                   		readOnly: true,
                                   		start: <%=avgGoalRating %>,
                                   		half: true,
                                   		targetType: 'number'
                                    });
                                 </script>
							</div>
							<% if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
			                                                	List<List<String>> goalOuterList = hmGoalKra.get(goalAndFreqId);
			                                                	 for(int j=0; goalOuterList!=null && !goalOuterList.isEmpty() && j<goalOuterList.size(); j++) {
			                                                	 List<String> innerList=goalOuterList.get(j);
			                                                	 List<List<String>> taskOuterList = hmKRATasks.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11));
			                                                	 	double avgKRARating = 0.0d;
			                                                		double dblKRAWeightScore = 0.0d;
			                                                	 	String kraRating = hmEmpwiseKRARating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
			                                                		String kraTaskCount = hmEmpwiseKRARating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
			                                                		if(uF.parseToInt(kraTaskCount) > 0) {
			                                                			avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
			                                                		}
			                                                		
			                                                		String strUserCnt = "";
			                                                		if(hmKRATaskEmpRating != null) {
			                                                			String strUserRating = hmEmpwiseKRAEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
			                                                			strUserCnt = hmEmpwiseKRAEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
			                                                			double avgUserRating = 0;
			                                                			if(uF.parseToInt(strUserCnt) > 0) {
			                                                				avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
			                                                			}
			                                                			if(avgKRARating > 0 && avgUserRating > 0) {
			                                                				avgKRARating = (avgKRARating + avgUserRating) / 2;
			                                                			} else if(avgUserRating > 0) {
			                                                				avgKRARating = avgUserRating;
			                                                			}
			                                                		}
			                                                		dblKRAWeightScore = (avgKRARating * uF.parseToDouble(innerList.get(27))) / 5;
			                       %>
							<ul
								style="float: left; margin: 0px 10px; width: 100%; padding-left: 0px;">
								<li
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-top: 10px; margin-bottom: 5px;">
									<div style="float: left; width: 100%;">
										<div style="float: left; margin: 0px 0px 0px 0px; width: 100%;">
											<span class="<%=innerList.get(10) %>"
												style="margin: 0px 0px 0px 15px; float: left;"><strong>KRA:</strong>&nbsp;<%=uF.showData(innerList.get(2), "-") %></span>
										</div>
										<div  width: 100%;">
											<div 
												id="starPrimaryGK<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>"
												style="float: left;"></div>
											<div style="float: right; margin-right:10px;">
												<b>Rated Score:</b>
												<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblKRAWeightScore+"")) %>/<%=uF.parseToDouble(innerList.get(27)) %></div>
										</div>
										<script type="text/javascript">
                                          $('#starPrimaryGK<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>').raty({
                                          	readOnly: true,
                                          	start: <%=avgKRARating %>,
                                          	half: true,
                                          	targetType: 'number'
                                          });
                                      </script>
									</div> <% if(taskOuterList!=null && !taskOuterList.isEmpty()) { %>
									<div style="float: left; width: 100%;">
										<div style="float: left; width: 100%; line-height: 12px; margin-top:10px;margin-left:-30px;">
											<span style="font-weight: bold; margin-left: 30px; color: gray;">Tasks:</span>
										</div>
										<% for(int a=0; a<taskOuterList.size(); a++) {
			                                                        	List<String> taskInnerList = taskOuterList.get(a);
			            												String taskStatusPercent = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_STATUS");
			            												String managerRating = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_RATING");
			            												String hrRating = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_RATING");
			            												String managerComment = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_COMMENT");
			            												String hrComment = hmKRATaskStatusAndRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_COMMENT");
			                                                            
			                                                            String hrMngrReview = "block";
			                                                            if(uF.parseToInt(taskStatusPercent) == 100 || uF.parseToInt(taskInnerList.get(2)) == 1) {
			                                                            	hrMngrReview = "block";
			                                                            }
			                                                            double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
			                                                            if(managerRating == null) {
			                                                            	avgRating = uF.parseToDouble(hrRating);
			                                                            } else if(hrRating == null) {
			                                                            	avgRating = uF.parseToDouble(managerRating);
			                                                            }
			                                                            
			                                                            String strUserTaskCnt = "";
			                                                            if(hmKRATaskEmpRating != null) {
			                                                            	String strUserRating = hmKRATaskEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_RATING");
			                                                            	strUserTaskCnt = hmKRATaskEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_COUNT");
			                                                            	double avgUserRating = 0;
			                                                            	if(uF.parseToInt(strUserCnt) > 0) {
			                                                            		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserTaskCnt);
			                                                            	}
			                                                            	if(avgRating > 0 && avgUserRating > 0) {
			                                                            		avgRating = (avgRating + avgUserRating) / 2;
			                                                            	} else if(avgUserRating > 0) {
			                                                            		avgRating = avgUserRating;
			                                                            	}
			                                                            }
			                                                            
			                                                            int commentCnt = 0;
			                                                            if(managerComment != null && !managerComment.equals("null")) {
			                                                            	commentCnt++;
			                                                            }
			                                                            if(hrComment != null && !hrComment.equals("null")) {
			                                                            	commentCnt++;
			                                                            }
			                                                            %>
										<div style="float: left; width: 100%;">
											<div
												style="float: left; margin: 20px 0px 0px 0px; line-height: 15px; width: 25%;"><%=taskInnerList.get(1) %></div>
											<div id="KTProBarDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
												style="float: left; margin: 3px 25px 3px 25px; width: 15%;">
												<div class="anaAttrib1">
													<span
														style="margin-left:<%=uF.parseToDouble(taskStatusPercent) > 95 ? uF.parseToDouble(taskStatusPercent) - 10 : uF.parseToDouble(taskStatusPercent) - 4%>%;"><%=uF.showData(taskStatusPercent, "0")%>%</span>
												</div>
												<div id="outbox" style=" width: 100px";>
													<% if (uF.parseToDouble(taskStatusPercent) < 33.33) { %>
													<div id="redbox"
														style="width: <%=uF.showData(taskStatusPercent, "0") %>%;"></div>
													<% } else if (uF.parseToDouble(taskStatusPercent) >= 33.33 && uF.parseToDouble(taskStatusPercent) < 66.67) { %>
													<div id="yellowbox"
														style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
													<% } else if (uF.parseToDouble(taskStatusPercent) >= 66.67) { %>
													<div id="greenbox"
														style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
													<% } %>
												</div>
												<div class="anaAttrib1" style="float: left; width: 100px;">
													<span style="float: left; margin-left: -4%;">0%</span> <span
														style="float: right; margin-right: -10%;">100%</span>
												</div>
											</div>
											<% String addedBy = taskInnerList.get(4);
												if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(addedBy)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) {%>
											<div
												style="float: left; margin: 15px 0px 0px 50px;">
												<input type="text"
													name="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													id="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													value="<%=uF.showData(taskStatusPercent, "") %>"
													style="width: 40px !important;"
													onkeypress="return isNumberKey(event)" /> <a
													onclick="updateCompletedPercent('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>','<%=taskInnerList.get(0) %>', 'KRA');"
													href="javascript:void(0);" title="Update"><i
													class="fa fa-pencil-square-o" aria-hidden="true"></i>
												</a> <br />
												<span
													id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>
											</div>
											<% } else { %>
											<div
												style="float: left; margin: 20px 5px 3px 15px; width: 15%;">
												<label style="width: 40px;"><%=uF.showData(taskStatusPercent, "") %></label>
												<br />
												<span
													id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>
											</div>
											<% } %>

											
											<div
												id="GivenTaskRatingDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
												style="float: left; margin-left:-20px;width:100%;">
												<div style="width:40%;">
												 <a style="height: 1px; width: 2px; padding-top:2px;" href="javascript:void(0)" onclick="viewAddComments('<%=strSessionEmpId %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA','Myself')"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Comment</a>
												</div>
												<div id="starPrimaryGKT<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													style="float: left;width:40%;padding-left:5px;  margin-top:-24px;margin-left:120px;"></div>
												<% if(commentCnt > 0 || uF.parseToInt(strUserTaskCnt) > 0) { %>
												<div style="float:right;margin-top:-21px;">
													<a href="javascript:void(0);"
														onclick="viewManagerAndHRComments('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA')">Comments
														<%=(commentCnt + uF.parseToInt(strUserTaskCnt)) %></a>
												</div>
												<% } %>
											</div>
											
											<div
												id="TaskRatingDiv_<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
												style="display:none; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
												<input type="hidden"
													name="hideGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													id="hideGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>">
												<div
													id="starGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
													style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
												<br />
												<div style="float: left; display:none; margin: 0px 0px 5px 0px;">
													<textarea rows="1" cols="40"
														name="strComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
														id="strComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></textarea>
												</div>
											<div style="float: left;  display:none; margin: 0px 0px 5px 7px;">
													<a href="javascript:void(0);"
														onclick="updateKRATaskRatingAndComment('<%=strSessionEmpId %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA','Myself')"><input
														type="button" class="btn btn-primary" name="update"
														style="padding-bottom: 3px; padding-top: 3px;"
														value="Update">
													</a>
												</div>
											</div>
											<script type="text/javascript">
			                                                                $('#starPrimaryGKT<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
			                                                                	readOnly: true,
			                                                                	start: <%=avgRating %>,
			                                                                	half: true,
			                                                                	targetType: 'number'
			                                                                });
			                                                                
			                                                                $('#starGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
			                                                                 readOnly: false,
			                                                                 start: 0,
			                                                                 half: true,
			                                                                 targetType: 'number',
			                                                                 click: function(score, evt) {
			                                                                 	$('#hideGKTRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').val(score);
			                                                                }
			                                                                });
			                                                            </script>
										</div>
										<% } %>
									</div> <% } %>
								</li>
							</ul>
							<% } %>
							<% } else if(gInnerList.get(20) != null && gInnerList.get(20).equals("Measure")) { %>
							<%
			                                                String target=hmTargetValue.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
			                                                String targetID = hmTargetID.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
			                                                String targetRemark = hmTargetRemark.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId);
			                                                String assignedTarget = "", measureType="";
			                                                
			                                                String managerRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
			                                                String hrRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
			                                                String managerComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
			                                                String hrComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
			                                                
			                                                String hrMngrReview = "block";
			                                                if(uF.parseToInt(gInnerList.get(23)) == 1) {
			                                                	hrMngrReview = "block";
			                                                }
			                                                
			                                                double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
			                                                if(managerRating == null) {
			                                                	avgRating = uF.parseToDouble(hrRating);
			                                                } else if(hrRating == null) {
			                                                	avgRating = uF.parseToDouble(managerRating);
			                                                }
			                                                
			                                                String strUserCnt = "";
			                                                if(hmEmpwiseGoalAndTargetEmpRating != null) {
			                                                	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
			                                                	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
			                                                	double avgUserRating = 0;
			                                                	if(uF.parseToInt(strUserCnt) > 0) {
			                                                		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
			                                                	}
			                                                	if(avgRating > 0 && avgUserRating > 0) {
			                                                		avgRating = (avgRating + avgUserRating) / 2;
			                                                	} else if(avgUserRating > 0) {
			                                                		avgRating = avgUserRating;
			                                                	}
			                                                }
			                                                
			                                                int commentCnt = 0;
			                                                if(managerComment != null && !managerComment.equals("null")) {
			                                                	commentCnt++;
			                                                }
			                                                if(hrComment != null && !hrComment.equals("null")) {
			                                                	commentCnt++;
			                                                }
			                                                %>
							<ul
								style="float: left; margin: 0px 10px; width: 98%; padding-left: 0px;">
								<li
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px; margin-top: 10px;">
									<div style="float: left; width: 100%;">
										<%  
			                                                            String twoDeciTotProgressAvg = "0";
			                                                            String twoDeciTot = "0";
			                                                            String total="100";
			                                                            double totalTarget=0;
			                                                            if(gInnerList.get(19)!=null && !gInnerList.get(19).equals("Effort")) {
			                                                            	if(uF.parseToDouble(gInnerList.get(21)) == 0) {
			                                                            		totalTarget=100;
			                                                            	} else {
			                                                            		totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(gInnerList.get(21)))*100;
			                                                            	}
			                                                            	twoDeciTot=""+Math.round(totalTarget);
			                                                            	if(uF.parseToInt(twoDeciTot+"") >= 100) {
			                                                            		hrMngrReview = "block";
			                                                            	}
			                                                            } else {
			                                                            	
			                                                            	String t=""+uF.parseToDouble(target);
			                                                            	String days="0";
			                                                            	String hours="0";
			                                                            	if(t.contains(".")){
			                                                            		t=t.replace(".","_");
			                                                            		String[] temp=t.split("_");
			                                                            		days=temp[0];
			                                                            		hours=temp[1];
			                                                            	}	
			                                                            	String t1=""+uF.parseToDouble(gInnerList.get(22));
			                                                            	String targetDays = "0";
			                                                            	String targetHrs = "0";
			                                                            	if(t1.contains(".")){
			                                                            		t1=t1.replace(".","_");
			                                                            		String[] temp=t1.split("_");
			                                                            		targetDays=temp[0];
			                                                            		targetHrs=temp[1];
			                                                            	}
			                                                            	int daysInHrs = uF.parseToInt(days) * 8;
			                                                            	int inttotHrs = daysInHrs + uF.parseToInt(hours);
			                                                            	
			                                                            	int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
			                                                            	int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
			                                                            	if(inttotTargetHrs != 0){
			                                                            		totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
			                                                            	}else{
			                                                            		totalTarget = 100;
			                                                            	}
			                                                            	twoDeciTot = ""+Math.round(totalTarget);
			                                                            	
			                                                            	if(uF.parseToInt(twoDeciTot+"") >= 100) {
			                                                            		hrMngrReview = "block";
			                                                            	}
			                                                            }
			                                                            if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
			                                                            	double totalTarget1=(totalTarget/150)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="150";
			                                                            }else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
			                                                            	double totalTarget1=(totalTarget/200)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="200";
			                                                            }else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
			                                                            	double totalTarget1=(totalTarget/250)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="250";
			                                                            }else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
			                                                            	double totalTarget1=(totalTarget/300)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="300";
			                                                            }else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
			                                                            	double totalTarget1=(totalTarget/350)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="350";
			                                                            }else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
			                                                            	double totalTarget1=(totalTarget/400)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="400";
			                                                            }else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
			                                                            	double totalTarget1=(totalTarget/450)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="450";
			                                                            }else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
			                                                            	double totalTarget1=(totalTarget/500)*100;
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
			                                                            	total="500";
			                                                            }else{
			                                                            	twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
			                                                            	if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
			                                                            		twoDeciTotProgressAvg = "100";
			                                                            		total=""+Math.round(totalTarget);
			                                                            	}else{
			                                                            		total="100";
			                                                            	}
			                                                            }
			                                                            
			                                                            %>
										<div class="row row_without_margin">
											<div class="col-lg-3 col-md-3" style="padding-left: 0px;">
												<div id="<%=i %>ProBarDiv<%=goalCount %>">
													<!-- <div style="width: 100%;"> -->
													<div class="anaAttrib1">
														<span
															style="margin-left:<%=uF.parseToDouble(twoDeciTotProgressAvg) > 97 ? uF.parseToDouble(twoDeciTotProgressAvg)-6 : uF.parseToDouble(twoDeciTotProgressAvg)-3 %>%;"><%=twoDeciTot%>%</span>
													</div>
													<div id="outbox">
														<%if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33){ %>
														<div id="redbox"
															style="width: <%=twoDeciTotProgressAvg %>%;"></div>
														<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67){ %>
														<div id="yellowbox"
															style="width: <%=twoDeciTotProgressAvg%>%;"></div>
														<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67){ %>
														<div id="greenbox"
															style="width: <%=twoDeciTotProgressAvg%>%;"></div>
														<%} %>
													</div>
													<div class="anaAttrib1" style="float: left; width: 100%;">
														<span style="float: left; margin-left: -3%;">0%</span> <span
															style="float: right; margin-right: -6%;"><%=total %>%</span>
													</div>
												</div>
											</div>

											<div class="col-lg-6 col-md-6" style="padding-left: 20px;">
												<div>
													<table class="table-bordered table autoWidth">
														<tr>
															<td><strong><u>Target</u>
															</strong>
															</td>
															<td style="padding-left: 10px;"><strong><u>Actual</u>
															</strong>
															</td>
														</tr>
														<tr>
															<td nowrap="nowrap"
																style="vertical-align: top; padding-right: 10px;">
																<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
					                                                                            measureType= "effort";
					                                                                            assignedTarget = gInnerList.get(21);
					                                                                            %>
																<%=gInnerList.get(21) %> <% } else {
					                                                                            measureType= "amount";
					                                                                            assignedTarget = gInnerList.get(21);
					                                                                            %>
																<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																<%=uF.showData(strCurrency,"")%>&nbsp; <% } %> <%=""+uF.parseToDouble(gInnerList.get(21))%>
																<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																&nbsp;% <% } %> <% } %>
															</td>
															<% if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
															<td style="padding-left: 10px;">
																<div id="<%=i%>spanid<%=goalCount %>">
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
						                                                                                String t=""+uF.parseToDouble(target);
						                                                                                String days="0";
						                                                                                String hours="0";
						                                                                                if(t.contains(".")) {
						                                                                                	t=t.replace(".","_");
						                                                                                	String[] temp=t.split("_");
						                                                                                	days=temp[0];
						                                                                                	hours=temp[1];
						                                                                                }
						                                                                                %>
																	<table>
																		<tr>
																			<td><input type="text" name="mDays"
																				id="<%=i%>mDays<%=goalCount %>"
																				style="width: 30px !important; text-align: right;"
																				value="<%=days %>"
																				onkeyup="checkHrsLimit('<%=i %>','<%=goalCount %>','<%=strSessionEmpId %>')"
																				onkeypress="return isOnlyNumberKey(event)" />
																			</td>
																			<td><input type="text" name="mHrs"
																				style="width: 30px !important; text-align: right;"
																				id="<%=i%>msHrs<%=goalCount %>" value="<%=hours %>"
																				onkeyup="checkHrsLimit('<%=i %>','<%=goalCount %>','<%=strSessionEmpId %>')"
																				onkeypress="return isOnlyNumberKey(event)" />
																			</td>
																			<td><a href="javascript:void(0);"
																				onclick="updateTarget('<%=i%>','<%=goalCount %>','<%=gInnerList.get(19) %>','<%=gInnerList.get(21) %>','<%=gInnerList.get(22) %>', '<%=strSessionEmpId%>', '<%=goalid %>', '<%=goalFreqId %>');"
																				title="Update"><i class="fa fa-pencil-square-o"
																					aria-hidden="true"></i>
																			</a> <%
						                                                                               				String disp = "none";
									                                                                                if(targetRemark == null || targetRemark.equals("")) { 
									                                                                                	disp = "inline";
									                                                                                }
						                                                                               			 %>
																				<span id="<%=i%>remarkSpan<%=goalCount %>"
																				style="display: <%=disp %>;"> <a
																					href="javascript:void(0);"
																					onclick="getMemberData('<%=strSessionEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'remark','<%=assignedTarget %>','<%=measureType %>');"
																					title="Add Remark"><i class="fa fa-bookmark"
																						aria-hidden="true"></i>
																				</a> <%-- updatePersonalTagetRemark('<%=targetID %>'); --%>
																			</span></td>
																		</tr>
																		<tr>
																			<td style="padding-right: 5px;">Days</td>
																			<td>Hrs</td>
																			<td></td>
																		</tr>
																	</table>
																	<% } else { %>
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																	<%=uF.showData(strCurrency,"")%>&nbsp;
																	<% } %>
																	<input
																		style="width: 65px !important; text-align: right;"
																		type="text" name="emptarget"
																		id="<%=i%>emptarget<%=goalCount%>"
																		value="<%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %>"
																		onkeypress="return isNumberKey(event)" />
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																	&nbsp;%
																	<% } %>
																	<a href="javascript:void(0);"
																		onclick="updateTarget('<%=i%>','<%=goalCount %>','<%=gInnerList.get(19) %>','<%=gInnerList.get(21) %>','<%=gInnerList.get(22) %>', '<%=strSessionEmpId%>', '<%=goalid %>', '<%=goalFreqId %>');"
																		title="Update"><i class="fa fa-pencil-square-o"
																		aria-hidden="true"></i>
																	</a>
																	<%
                                                                        String disp = "none";
                                                                        if(targetRemark == null || targetRemark.equals("")) { 
                                                                        	disp = "inline";
                                                                        }
                                                                     %>
																	<span id="<%=i%>remarkSpan<%=goalCount %>"
																		style="display: <%=disp %>;"> <a
																		href="javascript:void(0);"
																		onclick="getMemberData('<%=strSessionEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'remark','<%=assignedTarget %>','<%=measureType %>');"
																		title="Add Remark"><i class="fa fa-bookmark"
																			aria-hidden="true"></i>
																	</a> </span>
																	<% } %>
																	<br />
																	<span id="<%=i%>targetStatusDiv<%=goalCount %>"></span>
																</div></td>
															<% } else { %>
															<td>
																<div id="<%=i%>spanid<%=goalCount %>"
																	style="padding-left: 10px;">
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
																										String t=""+uF.parseToDouble(target);
																										String days="0";
																										String hours="0";
																										if(t.contains(".")) {
																											t=t.replace(".","_");
																											String[] temp=t.split("_");
																											days=temp[0];
																											hours=temp[1];
																										}
																									%>
																	&nbsp;<label style="width: 20px; text-align: right;"><%=days %></label>&nbsp;Days&nbsp;
																	<label style="width: 20px; text-align: right;"><%=hours %></label>&nbsp;Hrs

																	<% } else { %>
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																	<%=uF.showData(strCurrency,"")%>&nbsp;
																	<% } %>
																	<label style="width: 65px; text-align: right;"><%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %></label>
																	<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																	&nbsp;%
																	<% } %>
																	<% } %>

																	<br />
																	<span id="<%=i%>targetStatusDiv<%=goalCount %>"></span>
																</div></td>
															<% } %>
														</tr>
														<%if(hmUpdateBy.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId)!=null){ %>
														<tr>
															<td colspan="2"><a href="javascript:void(0)"
																onclick="getMemberData('<%=strSessionEmpId%>', '<%=goalid%>', '<%=goalFreqId %>', 'status','<%=assignedTarget %>','<%=measureType %>');"
																style="font-size: 10px;">Last updated by <%=hmUpdateBy.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId)!=null ? hmUpdateBy.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId) : "" %></a>
															</td>
														</tr>
														<%} else { %>
														<tr>
															<td colspan="2">Not updated yet.</td>
														</tr>
														<%} %>
													</table>
												</div>
											</div>
											<div class="col-lg-3 col-md-3" style="padding: 0px;">
												<div style="float: left; margin: 7px 0px 0px 16px;">
													<div
														id="starPrimaryGTarget<%=strSessionEmpId%>_<%=goalid %>_<%=goalFreqId %>"></div>
													<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
													<div>
														<a href="javascript:void(0);"
															onclick="viewManagerAndHRComments('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET')">Comments
															<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
													</div>
													<% } %>
													<script type="text/javascript">
					                                                                $('#starPrimaryGTarget<%=strSessionEmpId%>_<%=goalid %>_<%=goalFreqId %>').raty({
					                                                                	readOnly: true,
					                                                                	start: <%=avgRating %>,
					                                                                	half: true,
					                                                                	targetType: 'number'
					                                                                });
					                                                            </script></div></div>
										</div>
										<div class="row row_without_margin">
											<div class="col-lg-12 col-md-12">
												<div
													id="TargetRatingDiv_<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
													style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
													<input type="hidden"
														name="hideGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
														id="hideGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>">
													<div
														id="starGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
														style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
													<br />
													<div style="float: left; margin: 0px 0px 5px 0px;">
														<textarea rows="1" cols="40"
															name="strTargetComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
															id="strTargetComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"></textarea>
													</div>
													<div style="float: left; margin: 0px 0px 5px 7px;">
														<a href="javascript:void(0);"
															onclick="updateKRATaskRatingAndComment('<%=strSessionEmpId %>','<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'TARGET', 'Myself')"><input
															type="button" class="btn btn-primary" name="update"
															style="padding-bottom: 3px; padding-top: 3px;"
															value="Update">
														</a>
													</div>
													<script type="text/javascript">
					                                                                $('#starGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').raty({
					                                                                	readOnly: false,
					                                                                	start: 0,
					                                                                	half: true,
					                                                                	targetType: 'number',
					                                                                	click: function(score, evt) {
					                                                                			$('#hideGTargetRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').val(score);
					                                                                	}
					                                                                });
					                                                            </script>
												</div>
											</div>
										</div>
									</div></li>
							</ul>
							<% } else { %>
										<% String goalStatusPercent = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_STATUS");
			                                                String managerRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
			                                                String hrRating = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
			                                                String managerComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
			                                                String hrComment = hmTargetRatingAndComment.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
			                                                
			                                                String hrMngrReview = "block";
			                                                if(uF.parseToInt(gInnerList.get(23)) == 1) {
			                                                	hrMngrReview = "block";
			                                                }
			                                                
			                                                double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
			                                                if(managerRating == null) {
			                                                	avgRating = uF.parseToDouble(hrRating);
			                                                } else if(hrRating == null) {
			                                                	avgRating = uF.parseToDouble(managerRating);
			                                                }
			                                                
			                                                String strUserCnt = "";
			                                                if(hmEmpwiseGoalAndTargetEmpRating != null) {
			                                                	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_RATING");
			                                                	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(strSessionEmpId+"_"+goalid+"_"+goalFreqId+"_COUNT");
			                                                	double avgUserRating = 0;
			                                                	if(uF.parseToInt(strUserCnt) > 0) {
			                                                		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
			                                                	}
			                                                	if(avgRating > 0 && avgUserRating > 0) {
			                                                		avgRating = (avgRating + avgUserRating) / 2;
			                                                	} else if(avgUserRating > 0) {
			                                                		avgRating = avgUserRating;
			                                                	}
			                                                }
			                                                
			                                                int commentCnt = 0;
			                                                if(managerComment != null && !managerComment.equals("null")) {
			                                                	commentCnt++;
			                                                }
			                                                if(hrComment != null && !hrComment.equals("null")) {
			                                                	commentCnt++;
			                                                }
			                                                %>
							<ul style="float: left; width: 98%; padding-left: 0px;">
								<li
									style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px; margin-top: 10px;">
									<div style="float: left; width: 100%;">
										<div
											style="float: left; margin: 20px 5px 3px 55px; line-height: 15px; width: 25%;">No
											Measure or KRA.</div>
										<div
											id="GoalProBarDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
											style="float: left; margin: 3px 25px 3px 25px; width: 15%;">
											<div class="anaAttrib1">
												<span
													style="margin-left:<%=uF.parseToDouble(goalStatusPercent) > 95 ? uF.parseToDouble(goalStatusPercent) - 10 : uF.parseToDouble(goalStatusPercent) - 4%>%;"><%=uF.showData(goalStatusPercent, "0")%>%</span>
											</div>
											<div id="outbox">
												<% if (uF.parseToDouble(goalStatusPercent) < 33.33) { %>
												<div id="redbox"
													style="width: <%=uF.showData(goalStatusPercent, "0") %>%;"></div>
												<% } else if (uF.parseToDouble(goalStatusPercent) >= 33.33 && uF.parseToDouble(goalStatusPercent) < 66.67) { %>
												<div id="yellowbox"
													style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
												<% } else if (uF.parseToDouble(goalStatusPercent) >= 66.67) { %>
												<div id="greenbox"
													style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
												<% } %>
											</div>
											<div class="anaAttrib1" style="float: left; width: 100%;">
												<span style="float: left; margin-left: -4%;">0%</span> <span
													style="float: right; margin-right: -10%;">100%</span>
											</div>
										</div>
										<% if(flag || (strSessionUserType != null && (strSessionUserType.equals(IConstants.ADMIN) || uF.parseToInt(userId)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { %>
										<div
											style="float: left; margin: 20px 5px 3px 15px; width: 15%;">
											<input type="text"
												name="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
												id="completedPercent_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
												value="<%=uF.showData(goalStatusPercent, "") %>"
												style="width: 40px !important;"
												onkeypress="return isNumberKey(event)" /> <a
												onclick="updateCompletedPercent('<%=strSessionEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '0' ,'0', 'GOAL');"
												href="javascript:void(0);" title="Update"><i
												class="fa fa-pencil-square-o" aria-hidden="true"></i>
											</a> <br />
											<span
												id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"></span>
										</div>
										<% } else { %>
										<div
											style="float: left; margin: 20px 5px 3px 15px; width: 15%;">
											<label style="width: 40px;"><%=uF.showData(goalStatusPercent, "") %></label>
											<br />
											<span
												id="completedPercentStatusSpan_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"></span>
										</div>
										<% } %>
										<div
											id="GivenGoalRatingDiv_<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
											style="float: left;">
											<div
												id="starPrimaryGoal<%=strSessionEmpId %>_<%=goalid %>_<%=goalFreqId %>"
												style="float: left; margin: 20px 0px 0px 0px;"></div>
											<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
											<div style="margin-left: 25px;">
												<a href="javascript:void(0);"
													onclick="viewManagerAndHRComments('<%=strSessionEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET')">Comments
													<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
											</div>
											<% } %>
										</div>
										<div
											id="GoalRatingDiv_<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
											style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
											<input type="hidden"
												name="hideGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
												id="hideGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>">
											<div
												id="starGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
												style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
											<br />
											<div style="float: left; margin: 0px 0px 5px 0px;">
												<textarea rows="1" cols="40"
													name="strGoalComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"
													id="strGoalComment<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>"></textarea>
											</div>
											<div style="float: left; margin: 0px 0px 5px 7px;">
												<a href="javascript:void(0);"
													onclick="updateKRATaskRatingAndComment('<%=strSessionEmpId %>', '<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'GOAL', 'Myself')"><input
													type="button" class="btn btn-primary" name="update"
													style="padding-top: 3px; padding-bottom: 3px;"
													value="Update">
												</a>
											</div>
										</div>
										<script type="text/javascript">
			                                                            $('#starPrimaryGoal<%=strSessionEmpId%>_<%=goalid %>_<%=goalFreqId %>').raty({
			                                                            	readOnly: true,
			                                                            	start: <%=avgRating %>,
			                                                            	half: true,
			                                                            	targetType: 'number'
			                                                            });
			                                                            
			                                                            	$('#starGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').raty({
			                                                            		readOnly: false,
			                                                            		start: 0,
			                                                            		half: true,
			                                                            		targetType: 'number',
			                                                            		click: function(score, evt) {
			                                                            			$('#hideGoalRating<%=strSessionEmpId %>_<%=goalid%>_<%=goalFreqId%>').val(score);
			                                                            }
			                                                            });
			                                                        </script>
									</div></li>
							</ul>
							<% } %>
						</div>
				</div>
					<%} %> <% } %> <% } %> <% } %> <% } %> <% } %> <% } %>
				</div>
				
				<!-- 2753 -->
				</div>
				</div>
			
				
			 <% if((hmGoalKraEmpwise == null || hmGoalKraEmpwise.isEmpty()) ) { %>
				<div class="nodata msg" style="margin-top: 0px; width: 96%;">No
					data available.</div>
				<% } %>


				<h3 style="margin-top: 0px; font-size: 14px !important; font-weight: 600;" class="pagetitle">Goal Forms</h3> <!-- Goal, KRA, Target Forms -->
				<%
                                    i=0;
                                    for(;empList!=null && !empList.isEmpty() && i<empList.size(); i++) {
                                    	int kraSize = 0;
                                    	hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(empList.get(i));
                                    	kraSize = uF.parseToInt(hmEmpwiseKRACnt.get(empList.get(i)));
                                    
                                    	List<List<String>> empKraOuterList = hmEmpKra.get(empList.get(i));		
                                    	
                                    	String empname = hmEmpCodeName.get(empList.get(i).trim()); 
                                    	if(empList.get(i)!=null && strSessionEmpId!=null && empList.get(i).trim().equals(strSessionEmpId)) {
                                    		continue;
                                    	}
                                    	String strImage =empImageMap.get(empList.get(i).trim());
                                    	
                                    %>
				<div class="box box-primary collapsed-box"
					style="border-top-color: #E0E0E0;">
					<div class="box-header with-border" style="height: 40px;">
						<h3 class="box-title"
							style="font-size: 14px !important; color: #000000 !important; font-weight: 500 !important;">
							<img height="20" width="20" class="lazy img-circle"
								src="userImages/avatar_photo.png"
								data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+strImage%>" />
							<%=empname %>

						</h3>
						<div class="box-tools pull-right">
							<span id="empStatus<%=empList.get(i) %>"
								style="margin-right: 1cm;" class="badge bg-gray"><%=kraSize %></span>
							<button class="btn btn-box-tool" data-widget="collapse">
								<i class="fa fa-plus"></i>
							</button>
							<button class="btn btn-box-tool" data-widget="remove">
								<i class="fa fa-times"></i>
							</button>
						</div>
					</div>
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; display: none;">
						<ul>
							<% if(hmGoalKraSuperIdwise!=null && !hmGoalKraSuperIdwise.isEmpty()) { %>
							<%
                                                Iterator<String> itGoalSIKRA = hmGoalKraSuperIdwise.keySet().iterator();
                                                int goalCount = 0;
                                                while(itGoalSIKRA.hasNext()) {
                                                	String superId = itGoalSIKRA.next();								
                                                	Map<String, List<List<String>>> hmGoalKra = hmGoalKraSuperIdwise.get(superId);
                                                	if(hmGoalKra == null) hmGoalKra = new HashMap<String, List<List<String>>>();
                                                	String strBorder = "";
                                                	String strHr = "";
                                                	String strMargin = "0px 0px 0px 25px";
                                                	if(hmGoalKra.size() >1) {
                                                		strBorder = "2px solid #3F82BF;";
                                                		strMargin = "10px 0px 10px 25px";
                                                		strHr = "<hr width=\"3%\" style=\"float: left; border: 1px solid #3F82BF;\">";
                                                	}
                                                	%>
							<li style="border-left: <%=strBorder %>"><%=strHr %>
								<ul style="float: left; width: 99%; padding-left: 0px;">
									<%
									
                                                        Iterator<String> itgKRA = hmGoalKra.keySet().iterator();
                                                        while(itgKRA.hasNext()) {
                                                         goalCount++;
                                                         String goalAndFreqId = itgKRA.next();
                                                          List<String> gInnerList = hmGoalDetails.get(goalAndFreqId);
                                                         String goalid = gInnerList.get(1);
                                                         String goalFreqId = gInnerList.get(32);
                                                        	double avgGoalRating = 0.0d;
                                                        	double dblWeightScore = 0.0d;
                                                        	if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                												String goalRating = hmEmpwiseGoalRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                												String goalTaskCount = hmEmpwiseGoalRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                												
                												String goalEmpRating = hmEmpwiseGoalEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                												String goalEmpTaskCount = hmEmpwiseGoalEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                												if(uF.parseToInt(goalTaskCount) > 0 || uF.parseToInt(goalEmpTaskCount) > 0) {
                													avgGoalRating = (uF.parseToDouble(goalRating) + uF.parseToDouble(goalEmpRating)) / (uF.parseToInt(goalTaskCount) + uF.parseToInt(goalEmpTaskCount));
                												}
                												dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                											} else {
                												double avgEmpGoalRating = 0;
                												avgGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING"));
                												if(uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT")) > 0) {
                													avgEmpGoalRating = uF.parseToDouble(hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING"))/ uF.parseToInt(hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT"));
                													if(avgGoalRating > 0) {
                														avgGoalRating = (avgGoalRating + avgEmpGoalRating) / 2; 
                													} else {
                														avgGoalRating = avgEmpGoalRating;
                													}
                												}
                												
                												dblWeightScore = (avgGoalRating * uF.parseToDouble(gInnerList.get(16))) / 5;
                											}
                                                        	
                                                        	String strRole = "-";
                											if(gInnerList.get(28) != null) {
                												List<String> al = Arrays.asList(gInnerList.get(28).split(","));
                												if(al.contains(strSessionEmpId)) {
                													strRole = "Peer";
                												}
                											}
                											
                											if(gInnerList.get(31) != null) {
                												List<String> al = Arrays.asList(gInnerList.get(31).split(","));
                												if(al.contains(strSessionEmpId)) {
                													strRole = "Anyone";
                												}
                											}
                                                        	
                                                        %>
									<li
										style="float: left; width: 96%; margin: 10px 0px 10px 0px; padding: 5px; border: 1px solid #CCCCCC;">
										<div style="float: left; width: 100%;">
											<div style="float: left; width: 100%;">
												<div style="float: left; width: 100%;">
													<div class="<%=gInnerList.get(10) %>"
														style="float: left; line-height: 18px;">
														<b>Goal: </b><%=gInnerList.get(3) %>
														<%=(gInnerList.get(33) != null && !gInnerList.get(33).equals("")) ? "["+gInnerList.get(33)+"]" : "" %>
													</div>
													<div style="float: left; margin-left: 2%;">
														[Role- <b><%=strRole %></b>]
													</div>
													<div style="float: right; width: 50%;">
														<div
															id="starPrimaryG<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=goalCount %>"
															style="float: left;"></div>
														<div style="float: left;">
															<b>Rated Score:</b>
															<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblWeightScore+"")) %>/<%=uF.parseToDouble(gInnerList.get(16)) %></div>

														<div
															style="float: right; margin: 3px 0px 1px 0px; width: 100%; padding-bottom: 3px; padding-right: 3px;">
															<%
																				List<String> goalAchieved = hmActualAchievedGoal.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
																				 if(goalAchieved == null) goalAchieved = new ArrayList<String>();
																			%>
															<table cellspacing="0" cellpadding="2"
																class="table table-bordered">
																<tr>
																	<th colspan="4" style="padding: 0px;">Finalisation
																		Score</th>
																</tr>
																<tr>
																	<th style="width: 70px; padding: 0px;">Weightage</th>
																	<th style="width: 100px; padding: 0px;">Actual
																		Achieved</th>
																	<th style="width: 105px; padding: 0px;">Achieved
																		Share</th>
																</tr>
																<tr>
																	<td><%=uF.parseToDouble(gInnerList.get(16))%></td>
																	<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(5) : "-", "-")%>
																	</td>
																	<td><%=uF.showData(goalAchieved.size()>0 ? goalAchieved.get(4) : "-", "-")%>
																	</td>
																</tr>
															</table>
														</div>
													</div>
												</div>
												<div style="float: left; width: 100%;">
													<span class="<%=gInnerList.get(10) %>"
														style="float: left; margin-left: 15px; font-size: 10px; line-height: 18px;"><b>Obj:
													</b><%=uF.showData(gInnerList.get(13), "-") %></span>
												</div>
												<div
													style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC;">
													<span class="<%=gInnerList.get(10) %>"
														style="float: left; margin-left: 15px; font-size: 10px; line-height: 18px;">-
														assigned by <%=gInnerList.get(4)%>, attribute <%=gInnerList.get(14) %>,
														effective date <%=gInnerList.get(15) %>, due date <%=gInnerList.get(5)%>
													</span>
												</div>
											</div>
										</div> <%
                                                            if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                                            List<List<String>> goalOuterList = hmGoalKra.get(goalAndFreqId);
                                                             for(int j=0; goalOuterList!=null && !goalOuterList.isEmpty() && j<goalOuterList.size(); j++) {
                                                             List<String> innerList=goalOuterList.get(j);
                                                             List<List<String>> taskOuterList = hmKRATasks.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11));
                                                             	double avgKRARating = 0.0d;
                                                            	double dblKRAWeightScore = 0.0d;
                                                             	String kraRating = hmEmpwiseKRARating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                            	String kraTaskCount = hmEmpwiseKRARating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                            	
                                                            	if(uF.parseToInt(kraTaskCount) > 0) {
                                                            		avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
                                                            	}
                                                            	
                                                            	String strUserCnt = "";
                                                            	if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                                            		String strUserRating = hmEmpwiseKRAEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                            		strUserCnt = hmEmpwiseKRAEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                            		double avgUserRating = 0;
                                                            		if(uF.parseToInt(strUserCnt) > 0) {
                                                            			avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                                            		}
                                                            		if(avgKRARating > 0 && avgUserRating > 0) {
                                                            			avgKRARating = (avgKRARating + avgUserRating) / 2;
                                                            		} else if(avgUserRating > 0) {
                                                            			avgKRARating = avgUserRating;
                                                            		}
                                                            	}
                                                            	dblKRAWeightScore = (avgKRARating * uF.parseToDouble(innerList.get(27))) / 5;
                                                            	
                                                            %>
										<ul style="float: left; margin: 0px 10px; width: 98%;">
											<li
												style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
												<div style="float: left; width: 100%;">
													<div
														style="float: left; margin: 0px 0px 0px 15px; width: 50%;">
														<span class="<%=innerList.get(10) %>"
															style="margin: 0px 0px 0px 15px; float: left;"><strong>KRA:</strong>&nbsp;<%=uF.showData(innerList.get(2), "-") %>
															<span style="float: left; width: 100%;"> <% if(innerList.get(11) != null && hmCheckKWithAllowance != null && uF.parseToBoolean(hmCheckKWithAllowance.get(innerList.get(11)))) { %>
																<span style="font-style: italic;"><a
																	href="javascript:void(0);"
																	style="font-size: 10px; font-weight: normal;"
																	onclick="viewPerformanceAllowanceData('<%=innerList.get(11) %>', 'K');">This
																		KRA has Performance Incentives attached</a>
															</span> <% } else { %> <span
																style="font-size: 10px; font-style: italic; color: gray;">This
																	KRA has no Performance Incentives attached</span> <% } %> </span> </span>
													</div>
													<div style="float: right; width: 42%;">
														<div
															id="starPrimaryGK<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11)%>_<%=j%>"
															style="float: left;"></div>
														<div style="float: left;">
															<b>Rated Score:</b>
															<%=uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(dblKRAWeightScore+"")) %>/<%=uF.parseToDouble(innerList.get(27)) %></div>
													</div>
												</div> <% if(taskOuterList!=null && !taskOuterList.isEmpty()) { %>
												<div style="float: left; width: 100%;">
													<div style="float: left; width: 100%; line-height: 12px;">
														<span
															style="font-weight: bold; margin-left: 30px; color: gray;">Tasks:</span>
													</div>
													<% for(int a=0; a<taskOuterList.size(); a++) {
                                                                        List<String> taskInnerList = taskOuterList.get(a);
                                                                        String taskStatusPercent = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_STATUS");
                                                                        String managerRating = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_RATING");
                                                                        String hrRating = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_RATING");
                                                                        String managerComment = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_COMMENT");
                                                                        String hrComment = hmKRATaskStatusAndRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_COMMENT");
                                                                        
                                                                        String hrMngrReview = "block";
                                                                        if(uF.parseToInt(taskStatusPercent) >= 100 || uF.parseToInt(taskInnerList.get(2)) == 1) {
                                                                        	hrMngrReview = "block";
                                                                        }
                                                                        double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                                        if(managerRating == null) {
                                                                        	avgRating = uF.parseToDouble(hrRating);
                                                                        } else if(hrRating == null) {
                                                                        	avgRating = uF.parseToDouble(managerRating);
                                                                        }
                                                                        String strUserTaskCnt = "";
                                                                        if(hmKRATaskEmpRating != null) {
                                                                        	String strUserRating = hmKRATaskEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_RATING");
                                                                        	strUserTaskCnt = hmKRATaskEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_COUNT");
                                                                        	double avgUserRating = 0;
                                                                        	if(uF.parseToInt(strUserCnt) > 0) {
                                                                        		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserTaskCnt);
                                                                        	}
                                                                        	if(avgRating > 0 && avgUserRating > 0) {
                                                                        		avgRating = (avgRating + avgUserRating) / 2;
                                                                        	} else if(avgUserRating > 0) {
                                                                        		avgRating = avgUserRating;
                                                                        	}
                                                                        }
                                                                        
                                                                        int commentCnt = 0;
                                                                        if(managerComment != null && !managerComment.equals("null")) {
                                                                        	commentCnt++;
                                                                        }
                                                                        if(hrComment != null && !hrComment.equals("null")) {
                                                                        	commentCnt++;
                                                                        }
                                                                        %>
													<div style="float: left; width: 100%;">

														<div
															style="float: left; margin: 20px 5px 3px 55px; line-height: 15px; width: 16%;"><%=uF.showData(taskInnerList.get(1), "-") %></div>
														<div
															id="KTProBarDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
															style="float: left; margin: 3px 25px 3px 25px; width: 20%;">
															<div class="anaAttrib1">
																<span style="margin-left:<%=uF.parseToDouble(taskStatusPercent) > 95 ? uF.parseToDouble(taskStatusPercent) - 10 : uF.parseToDouble(taskStatusPercent) - 4 %>%;"><%=uF.showData(taskStatusPercent, "0")%>%</span>
															</div>
															<div id="outbox">
																<% if (uF.parseToDouble(taskStatusPercent) < 33.33) { %>
																<div id="redbox" style="width: <%=uF.showData(taskStatusPercent, "0") %>%;"></div>
																<% } else if (uF.parseToDouble(taskStatusPercent) >= 33.33 && uF.parseToDouble(taskStatusPercent) < 66.67) { %>
																<div id="yellowbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
																<% } else if (uF.parseToDouble(taskStatusPercent) >= 66.67) { %>
																<div id="greenbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
																<% } %>
															</div>
															<div class="anaAttrib1" style="float: left; width: 100%;">
																<span style="float: left; margin-left: -4%;">0%</span> <span style="float: right; margin-right: -10%;">100%</span>
															</div>
														</div>
														<div style="float: left; margin: 20px 5px 3px 5px; width: 7%;">
															<%=uF.showData(taskStatusPercent, "0") %>%
														</div>
														<div
															id="GivenTaskRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
															style="float: left;">
															<div
																id="starPrimaryGKT<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																style="float: left; margin: 20px 0px 0px 25px;"></div>
															<% if(commentCnt > 0 || uF.parseToInt(strUserTaskCnt) > 0) { %>
															<div style="margin-left: 25px;">
																<a href="javascript:void(0);"
																	onclick="viewManagerAndHRComments('<%=empList.get(i) %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA')">Comments
																	<%=(commentCnt + uF.parseToInt(strUserTaskCnt)) %></a>
															</div>
															<% } %>
															<script type="text/javascript">
                                                                                $('#starPrimaryGKT<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
                                                                                	readOnly: true,
                                                                                	start: <%=avgRating %>,
                                                                                	half: true,
                                                                                	targetType: 'number'
                                                                                });
                                                                            </script>
														</div>
														<% if((uF.parseToInt(hmHrIds.get(empList.get(i))) == uF.parseToInt(strSessionEmpId) && strSessionUserType != null && strSessionUserType.equals(IConstants.HRMANAGER)) || (strSessionUserType != null && !strSessionUserType.equals(IConstants.HRMANAGER))) { %>
														<div
															id="TaskRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
															style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
															<input type="hidden"
																name="hideGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																id="hideGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>">
															<div
																id="starGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
															<br />
															<div style="float: left; margin: 0px 0px 5px 0px;">
																<textarea rows="1" cols="40"
																	name="strComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																	id="strComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
																	class="form-control autoWidth"></textarea>
															</div>
															<div style="float: left; margin: 0px 0px 5px 7px;">
																<a href="javascript:void(0);"
																	onclick="updateKRATaskRatingAndComment('<%=empList.get(i) %>', '<%=goalid%>', '<%=goalFreqId%>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA','<%=strRole %>')"><input
																	type="button" class="btn btn-primary" name="update"
																	style="padding-bottom: 3px; padding-top: 3px;"
																	value="Update">
																</a>
															</div>
															<script type="text/javascript">
	                                                                                $('#starGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
	                                                                                	readOnly: false,
	                                                                                	start: 0,
	                                                                                	half: true,
	                                                                                	targetType: 'number',
	                                                                                	click: function(score, evt) {
	                                                                                			$('#hideGKTRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').val(score);
	                                                                                }
	                                                                                });
	                                                                            </script>
														</div>
														<% } %>
													</div>
													<% } %>
												</div> <% } %>
											</li>
										</ul> <% } %> <% } else if(gInnerList.get(20) != null && gInnerList.get(20).equals("Measure")) {
                                                            String target=hmTargetValue.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
                                                            String targetID = hmTargetID.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
                                                            String targetRemark = hmTargetRemark.get(empList.get(i)+"_"+goalid+"_"+goalFreqId);
                                                            String assignedTarget = "", measureType="";
                                                            
                                                            String managerRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
                                                            String hrRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
                                                            String managerComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
                                                            String hrComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
                                                            
                                                            String hrMngrReview = "block";
                                                            if(uF.parseToInt(gInnerList.get(23)) == 1) {
                                                            	hrMngrReview = "block";
                                                            }
                                                            
                                                            double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                            if(managerRating == null) {
                                                            	avgRating = uF.parseToDouble(hrRating);
                                                            } else if(hrRating == null) {
                                                            	avgRating = uF.parseToDouble(managerRating);
                                                            }
                                                            
                                                            String strUserCnt = "";
                                                            if(hmEmpwiseGoalAndTargetEmpRating != null) {
                												String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                												strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                												double avgUserRating = 0;
                												if(uF.parseToInt(strUserCnt) > 0) {
                													avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                												}
                												if(avgRating > 0 && avgUserRating > 0) {
                													avgRating = (avgRating + avgUserRating) / 2;
                												} else if(avgUserRating > 0) {
                													avgRating = avgUserRating;
                												}
                											}
                                                            
                                                            int commentCnt = 0;
                											if(managerComment != null && !managerComment.equals("null")) {
                												commentCnt++;
                											}
                											if(hrComment != null && !hrComment.equals("null")) {
                												commentCnt++;
                											}
                                                            %>
										<ul style="padding-left: 0px;">
											<li
												style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
												<div
													style="float: left; width: 100%; margin: 0px 0px 0px 15px">
													<%  
                                                                	String twoDeciTotProgressAvg = "0";
                													String twoDeciTot = "0";
                													String total="100";
                													double totalTarget=0;
                													if(gInnerList.get(19)!=null && !gInnerList.get(19).equals("Effort")) {
                														if(uF.parseToDouble(gInnerList.get(21)) == 0) {
                															totalTarget=100;
                														} else {
                															totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(gInnerList.get(21)))*100;
                														}
                														twoDeciTot=""+Math.round(totalTarget);
                														if(uF.parseToInt(twoDeciTot+"") >= 100) {
                															hrMngrReview = "block";
                														}
                													} else {
                														
                														String t=""+uF.parseToDouble(target);
                														String days="0";
                														String hours="0";
                														if(t.contains(".")){
                															t=t.replace(".","_");
                															String[] temp=t.split("_");
                															days=temp[0];
                															hours=temp[1];
                														}	
                														String t1=""+uF.parseToDouble(gInnerList.get(22));
                														String targetDays = "0";
                														String targetHrs = "0";
                														if(t1.contains(".")){
                															t1=t1.replace(".","_");
                															String[] temp=t1.split("_");
                															targetDays=temp[0];
                															targetHrs=temp[1];
                														}
                														int daysInHrs = uF.parseToInt(days) * 8;
                														int inttotHrs = daysInHrs + uF.parseToInt(hours);
                														
                														int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
                														int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
                														if(inttotTargetHrs != 0) {
                															totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
                														} else {
                															totalTarget = 100;
                														}
                														twoDeciTot = ""+Math.round(totalTarget);
                														
                														if(uF.parseToInt(twoDeciTot+"") >= 100) {
                															hrMngrReview = "block";
                														}
                													}
                													if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
                														double totalTarget1=(totalTarget/150)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="150";
                													}else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
                														double totalTarget1=(totalTarget/200)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="200";
                													}else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
                														double totalTarget1=(totalTarget/250)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="250";
                													}else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
                														double totalTarget1=(totalTarget/300)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="300";
                													}else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
                														double totalTarget1=(totalTarget/350)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="350";
                													}else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
                														double totalTarget1=(totalTarget/400)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="400";
                													}else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
                														double totalTarget1=(totalTarget/450)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="450";
                													}else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
                														double totalTarget1=(totalTarget/500)*100;
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
                														total="500";
                													}else{
                														twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
                														if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
                															twoDeciTotProgressAvg = "100";
                															total=""+Math.round(totalTarget);
                														}else{
                															total="100";
                														}
                													}
                                                                        
                                                                        %>
													<div class="row row_without_margin">
														<div class="col-lg-3 col-md-3">
															<div id="<%=i %>ProBarDiv<%=goalCount %>">
																<!-- <div style="width: 100%;"> -->
																<div class="anaAttrib1">
																	<span
																		style="margin-left:<%=uF.parseToDouble(twoDeciTotProgressAvg) > 97 ? uF.parseToDouble(twoDeciTotProgressAvg)-6 : uF.parseToDouble(twoDeciTotProgressAvg)-3 %>%;"><%=twoDeciTot%>%</span>
																</div>
																<div id="outbox">
																	<%if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33) { %>
																	<div id="redbox"
																		style="width: <%=twoDeciTotProgressAvg %>%;"></div>
																	<%} else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67) { %>
																	<div id="yellowbox"
																		style="width: <%=twoDeciTotProgressAvg%>%;"></div>
																	<%} else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67) { %>
																	<div id="greenbox"
																		style="width: <%=twoDeciTotProgressAvg%>%;"></div>
																	<% } %>
																</div>
																<div class="anaAttrib1"
																	style="float: left; width: 100%;">
																	<span style="float: left; margin-left: -3%;">0%</span>
																	<span style="float: right; margin-right: -6%;"><%=total %>%</span>
																</div>
															</div>
														</div>
														<div class="col-lg-5 col-md-5">
															<div>
																<table class="table table-bordered"
																	style="margin-top: 10px;">
																	<tr>
																		<td><strong><u>Target</u>
																		</strong>
																		</td>
																		<td style="padding-left: 5px;"><strong><u>Actual</u>
																		</strong>
																		</td>
																	</tr>
																	<tr>
																		<td nowrap="nowrap">
																			<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
																								measureType= "effort";
																								assignedTarget = gInnerList.get(21);
																								%> <%=gInnerList.get(21) %> <% } else {
																									measureType= "amount";
																									assignedTarget = gInnerList.get(21);
																								%> <%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																			<%=uF.showData(strCurrency,"")%>&nbsp; <% } %> <%=""+uF.parseToDouble(gInnerList.get(21))%>
																			<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																			&nbsp;% <% } %> <% } %>
																		</td>
																		<td style="padding-left: 5px;">
																			<div id="<%=i%>spanid<%=goalCount %>"
																				style="float: left; text-align: center;">
																				<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Effort")) {
			                                                                                            String t=""+uF.parseToDouble(target);
			                                                                                            String days="0";
			                                                                                            String hours="0";
			                                                                                            if(t.contains(".")) {
			                                                                                            	t=t.replace(".","_");
			                                                                                            	String[] temp=t.split("_");
			                                                                                            	days=temp[0];
			                                                                                            	hours=temp[1];
			                                                                                            }
			                                                                                            if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)) {
			                                                                                            %>
																				<%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
																				<% } else { %>
																				<%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
																				<% }
			                                                                                      } else { 
		                                                                                            	if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)) {
				                                                                                           %>
																				<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																				<%=uF.showData(strCurrency,"")%>&nbsp;
																				<% } %>
																				<%=target!=null ? uF.getAmountInCrAndLksFormat(uF.parseToDouble(target)) : "0" %>
																				<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																				&nbsp;%
																				<% } %>
																				<% } else { %>
																				<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Amount")) { %>
																				<%=uF.showData(strCurrency,"")%>&nbsp;
																				<% } %>
																				<%=target!=null ? uF.getAmountInCrAndLksFormat(uF.parseToDouble(target)) : "0" %>
																				<%if(gInnerList.get(19)!=null && gInnerList.get(19).equals("Percentage")) { %>
																				&nbsp;%
																				<% } %>
																				<% }
		                                                                                            } %>
																			</div></td>
																	</tr>
																</table>
															</div>
														</div>
														<div class="col-lg-4 col-md-4">
															<div>
																<div
																	id="starPrimaryGTarget<%=empList.get(i)%>_<%=goalid%>_<%=goalFreqId%>"></div>
																<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
																<div>
																	<a href="javascript:void(0);"
																		onclick="viewManagerAndHRComments('<%=empList.get(i) %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'TARGET')">Comments
																		<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
																</div>
																<% } %>
																<script type="text/javascript">
		                                                                            $('#starPrimaryGTarget<%=empList.get(i)%>_<%=goalid%>_<%=goalFreqId%>').raty({
		                                                                            	readOnly: true,
		                                                                            	start: <%=avgRating %>,
		                                                                            	half: true,
		                                                                            	targetType: 'number'
		                                                                            });
		                                                                        </script>
															</div>
														</div>
													</div>
													<% if((uF.parseToInt(hmHrIds.get(empList.get(i))) == uF.parseToInt(strSessionEmpId) && strSessionUserType != null && strSessionUserType.equals(IConstants.HRMANAGER)) || (strSessionUserType != null && !strSessionUserType.equals(IConstants.HRMANAGER))) { %>
													<div
														id="TargetRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="margin-top: 10px;display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
														<input type="hidden"
															name="hideGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															id="hideGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>">
														<div
															id="starGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
														<br />
														<div style="float: left; margin: 0px 0px 5px 0px;">
															<textarea rows="1" cols="40"
																name="strTargetComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																id="strTargetComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																class="form-control autoWidth"></textarea>
														</div>
														<div style="float: left; margin: 0px 0px 5px 7px;">
															<a href="javascript:void(0);"
																onclick="updateKRATaskRatingAndComment('<%=empList.get(i) %>','<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'TARGET', '<%=strRole %>')"><input
																type="button" class="btn btn-primary" name="update"
																style="padding-bottom: 3px; padding-top: 3px;"
																value="Update">
															</a>
														</div>
														<script type="text/javascript">
	                                                                            $('#starGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').raty({
	                                                                            	readOnly: false,
	                                                                            	start: 0,
	                                                                            	half: true,
	                                                                            	targetType: 'number',
	                                                                            	click: function(score, evt) {
	                                                                            			$('#hideGTargetRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').val(score);
	                                                                            }
	                                                                            });
	                                                                        </script>
													</div>
													<% } %>
												</div>
												<div style="float: left; width: 100%;">
													<% if(uF.parseToBoolean(hmCheckGTWithAllowance.get(gInnerList.get(1)))) { %>
													<span style="font-style: italic;"><a
														href="javascript:void(0);"
														style="font-size: 10px; font-weight: normal;"
														onclick="viewPerformanceAllowanceData('<%=gInnerList.get(1) %>', 'GT');">This
															Target has Performance Incentives attached</a>
													</span>
													<% } else { %>
													<span
														style="font-size: 10px; font-style: italic; color: gray;">This
														Target has no Performance Incentives attached</span>
													<% } %>
												</div></li>
										</ul> <% } else { %> <%
                                                            String goalStatusPercent = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_STATUS");
                                                            String managerRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_RATING");
                                                            String hrRating = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_RATING");
                                                            String managerComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_MGR_COMMENT");
                                                            String hrComment = hmTargetRatingAndComment.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_HR_COMMENT");
                                                            
                                                            String hrMngrReview = "block";
                                                            if(uF.parseToDouble(goalStatusPercent) >=100 || uF.parseToInt(gInnerList.get(23)) == 1) {
                                                            	hrMngrReview = "block";
                                                            }
                                                            
                                                            double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                            if(managerRating == null) {
                                                            	avgRating = uF.parseToDouble(hrRating);
                                                            } else if(hrRating == null) {
                                                            	avgRating = uF.parseToDouble(managerRating);
                                                            }
                                                            
                                                            String strUserCnt = "";
                                                            if(hmEmpwiseGoalAndTargetEmpRating != null) {
                                                            	String strUserRating = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_RATING");
                                                            	strUserCnt = hmEmpwiseGoalAndTargetEmpRating.get(empList.get(i)+"_"+goalid+"_"+goalFreqId+"_COUNT");
                                                            	double avgUserRating = 0;
                                                            	if(uF.parseToInt(strUserCnt) > 0) {
                                                            		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                                            	}
                                                            	if(avgRating > 0 && avgUserRating > 0) {
                                                            		avgRating = (avgRating + avgUserRating) / 2;
                                                            	} else if(avgUserRating > 0) {
                                                            		avgRating = avgUserRating;
                                                            	}
                                                            }
                                                            
                                                            int commentCnt = 0;
                                                            if(managerComment != null && !managerComment.equals("null")) {
                                                            	commentCnt++;
                                                            }
                                                            if(hrComment != null && !hrComment.equals("null")) {
                                                            	commentCnt++;
                                                            }
                                                            
                                                            %>
										<ul style="float: left; margin: 0px 10px; width: 98%;">
											<li
												style="float: left; width: 100%; border-bottom: 1px solid #CCCCCC; margin-bottom: 5px;">
												<div style="float: left; width: 100%;">
													<div
														style="float: left; margin: 20px 0px 3px 0px; line-height: 15px; width: 21.5%;">
														No Measure or KRA. <br />
														<br />
														<% if(hmCheckGTWithAllowance != null && gInnerList.get(1) != null && uF.parseToBoolean(hmCheckGTWithAllowance.get(gInnerList.get(1)))) { %>
														<span style="font-style: italic;"><a
															href="javascript:void(0);"
															style="font-size: 10px; font-weight: normal;"
															onclick="viewPerformanceAllowanceData('<%=gInnerList.get(1) %>', 'GT');">This
																Goal has Performance Incentives attached</a>
														</span>
														<% } else { %>
														<span
															style="font-size: 10px; font-style: italic; color: gray;">This
															Goal has no Performance Incentives attached</span>
														<% } %>
													</div>
													<div
														id="GoalProBarDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="float: left; margin: 3px 25px 3px 25px; width: 10%;">
														<div class="anaAttrib1">
															<span
																style="margin-left:<%=uF.parseToDouble(goalStatusPercent) > 95 ? uF.parseToDouble(goalStatusPercent) - 10 : uF.parseToDouble(goalStatusPercent) - 4%>%;"><%=uF.showData(goalStatusPercent, "0")%>%</span>
														</div>
														<div id="outbox">
															<% if (uF.parseToDouble(goalStatusPercent) < 33.33) { %>
															<div id="redbox"
																style="width: <%=uF.showData(goalStatusPercent, "0") %>%;"></div>
															<% } else if (uF.parseToDouble(goalStatusPercent) >= 33.33 && uF.parseToDouble(goalStatusPercent) < 66.67) { %>
															<div id="yellowbox"
																style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
															<% } else if (uF.parseToDouble(goalStatusPercent) >= 66.67) { %>
															<div id="greenbox"
																style="width: <%=uF.showData(goalStatusPercent, "0")%>%;"></div>
															<% } %>
														</div>
														<div class="anaAttrib1" style="float: left; width: 100%;">
															<span style="float: left; margin-left: -4%;">0%</span> <span
																style="float: right; margin-right: -10%;">100%</span>
														</div>
													</div>

													<div
														style="float: left; margin: 20px 5px 3px 15px; width: 7%;">
														<%=uF.showData(goalStatusPercent, "") %>% <input
															type="text"
															name="completedPercent_<%=empList.get(i) %>_<%=gInnerList.get(1) %>"
															id="completedPercent_<%=empList.get(i) %>_<%=gInnerList.get(1) %>"
															value="<%=uF.showData(goalStatusPercent, "") %>"
															style="width: 40px !important;"
															onkeypress="return isNumberKey(event)" /> <a
															onclick="updateCompletedPercent('<%=empList.get(i) %>','<%=gInnerList.get(1) %>','0', 'GOAL');"
															href="javascript:void(0);" title="Update"><i
															class="fa fa-pencil-square-o" aria-hidden="true"></i>
														</a> <br />
														<span
															id="completedPercentStatusSpan_<%=empList.get(i) %>_<%=gInnerList.get(1) %>"></span>
													</div>
													<div
														id="GivenGoalRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="float: left;">
														<div
															id="starPrimaryGoal<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															style="float: left; margin: 20px 0px 0px 25px;"></div>
														<% if(commentCnt > 0 || uF.parseToInt(strUserCnt) > 0) { %>
														<div style="margin-left: 25px;">
															<a href="javascript:void(0);"
																onclick="viewManagerAndHRComments('<%=empList.get(i) %>', '<%=goalid %>', '<%=goalFreqId %>', '0', '0', 'GOAL')">Comments
																<%=(commentCnt + uF.parseToInt(strUserCnt)) %></a>
														</div>
														<% } %>
														<script type="text/javascript">
                                                                            $('#starPrimaryGoal<%=empList.get(i) %>_<%=goalid %>_<%=goalFreqId %>').raty({
                                                                            	readOnly: true,
                                                                            	start: <%=avgRating %>,
                                                                            	half: true,
                                                                            	targetType: 'number'
                                                                            });
                                                                        </script>
													</div>
													<% if((uF.parseToInt(hmHrIds.get(empList.get(i))) == uF.parseToInt(strSessionEmpId) && strSessionUserType != null && strSessionUserType.equals(IConstants.HRMANAGER)) || (strSessionUserType != null && !strSessionUserType.equals(IConstants.HRMANAGER))) { %>
													<div
														id="GoalRatingDiv_<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
														style="display: <%=hrMngrReview %>; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
														<input type="hidden"
															name="hideGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															id="hideGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>">
														<div
															id="starGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
															style="float: left; margin: 5px 0px 0px 5px; width: 110px;"></div>
														<br />
														<div style="float: left; margin: 0px 0px 5px 0px;">
															<textarea rows="1" cols="40"
																name="strGoalComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																id="strGoalComment<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>"
																class="form-control autoWidth"></textarea>
														</div>
														<div style="float: left; margin: 0px 0px 5px 7px;">
															<a href="javascript:void(0);"
																onclick="updateKRATaskRatingAndComment('<%=empList.get(i) %>', '<%=goalid%>', '<%=goalFreqId%>', '0', '0', 'GOAL', '<%=strRole %>')"><input
																type="button" class="btn btn-primary" name="update"
																style="padding-bottom: 3px; padding-top: 3px;"
																value="Update">
															</a>
														</div>
														<script type="text/javascript">
                                                                            $('#starGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').raty({
                                                                            	readOnly: false,
                                                                            	start: 0,
                                                                            	half: true,
                                                                            	targetType: 'number',
                                                                            	click: function(score, evt) {
                                                                            		$('#hideGoalRating<%=empList.get(i) %>_<%=goalid%>_<%=goalFreqId%>').val(score);
                                                                            }
                                                                            });
                                                                        </script>
													</div>
													<% } %>
												</div></li>
										</ul> <% }  %>
									</li>
									<% } %>
								</ul> <%=strHr %></li>
							<%} %>
							<%} %>
						</ul>
						<%if((hmGoalKraSuperIdwise == null || hmGoalKraSuperIdwise.isEmpty()) && (empKraOuterList == null || empKraOuterList.isEmpty())) { %>
						<div class="nodata"
							style="border-radius: 4px 4px 4px 4px; padding: 10px; width: 95%; margin: 5px 5px 5px 5px">
							No Goal, KRA, Target assigned to
							<%=empname %>.
						</div>
						<% } %>
					</div>
					<!-- /.box-body -->
				</div>
			
				<% } %> </section>
				<% } %>
			<section class="col-lg-4 connectedSortable" style="width: 60%;">
				<div class="box box-primary"
					style="border-top-color: #cda55f /*#E0E0E0;*/">
					<div class="box-header with-border">
						<h3 class="box-title" style="font-size: 14px !important;">Initiatives</h3>
					</div>
					<!-- /.box-header -->
					<div class="box-body"
						style="padding: 5px; overflow-y: auto; max-height: 100%;">
						<%Map<String, List<String>> hmGoalDetails = (Map<String, List<String>>) request.getAttribute("hmGoalDetails");
						   Map<String, Map<String, Map<String, List<List<String>>>>> hmGoalKraEmpwise = (Map<String, Map<String, Map<String, List<List<String>>>>>) request.getAttribute("hmGoalKraEmpwise");
                           if(hmGoalKraEmpwise == null) hmGoalKraEmpwise = new HashMap<String, Map<String, Map<String, List<List<String>>>>>();
                          if(hmGoalDetails == null) {
                        	  hmGoalDetails = new HashMap<String, List<String>>();
                        	}
                          String strEmpId = strSessionEmpId;
                     	  String currUserType = (String) request.getAttribute("currUserType");
                     	  String strBaseUserType=(String) session.getAttribute(IConstants.BASEUSERTYPE);
                     	  String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
                        String strUserType=(String) session.getAttribute(IConstants.USERTYPE);

                          Map<String, String> hmKraCompletedPercentage = (Map<String, String>)request.getAttribute("hmKraCompletedPercentage");
                          if(hmKraCompletedPercentage == null) hmKraCompletedPercentage = new HashMap<String, String>();
                        	
                          List<String> membersAccessList = (List<String>)request.getAttribute("orientationMembersList");
                          if(membersAccessList == null ) membersAccessList = new ArrayList<String>();
                          Map<String, List<List<String>>> hmKRATasks = (Map<String, List<List<String>>>) request.getAttribute("hmKRATasks");
                          if(hmKRATasks == null) hmKRATasks = new HashMap<String, List<List<String>>>();
                          
                          Map<String, String> hmEmpSuperIds = (Map<String, String>)request.getAttribute("hmEmpSuperIds");
                          if(hmEmpSuperIds == null) hmEmpSuperIds = new HashMap<String, String>();
                          
                          Map<String, String> hmKRATaskEmpRating = (Map<String, String>)request.getAttribute("hmKRATaskEmpRating");
                          if(hmKRATaskEmpRating == null) hmKRATaskEmpRating = new HashMap<String, String>();
                          
                          Map<String, String> hmEmpwiseKRARating = (Map<String, String>) request.getAttribute("hmEmpwiseKRARating");
                          if(hmEmpwiseKRARating == null) hmEmpwiseKRARating = new HashMap<String, String>();
                          
                          Map<String, String> hmEmpwiseKRAEmpRating = (Map<String, String>)request.getAttribute("hmEmpwiseKRAEmpRating");
                          if(hmEmpwiseKRAEmpRating == null) hmEmpwiseKRAEmpRating = new HashMap<String, String>();
                          
                          Map<String, String> hmKRATaskStatusAndRating = (Map<String, String>) request.getAttribute("hmKRATaskStatusAndRating");
                          if(hmKRATaskStatusAndRating == null) hmKRATaskStatusAndRating = new HashMap<String, String>();
                      
                          Map<String, String> hmCheckKWithAllowance = (Map<String, String>)request.getAttribute("hmCheckKWithAllowance");
                          if(hmCheckKWithAllowance == null) hmCheckKWithAllowance = new HashMap<String, String>();
                          Map<String, String> hmHrIds = (Map<String, String>) request.getAttribute("hmHrIds");
                          if(hmHrIds == null) hmHrIds = new HashMap<String, String>();
                          
                          Map<String, List<String>> hmActualAchievedGoal = (Map<String, List<String>>)request.getAttribute("hmActualAchievedGoal");
                          if(hmActualAchievedGoal == null) hmActualAchievedGoal = new HashMap<String, List<String>>();
                        	 Map<String, Map<String, List<List<String>>>> hmGoalKraSuperIdwise = hmGoalKraEmpwise.get(strSessionEmpId);
                             if (hmGoalKraSuperIdwise != null && !hmGoalKraSuperIdwise.isEmpty()) {
                             	Iterator<String> itGoalSIKRA = hmGoalKraSuperIdwise.keySet().iterator();
                             	int goalCount = 0;
                             
                             	while (itGoalSIKRA.hasNext()) {
                             		String superId = itGoalSIKRA.next();	
                             		
                             		Map<String, List<List<String>>> hmGoalKra = hmGoalKraSuperIdwise.get(superId);
                             		if(hmGoalKra == null) hmGoalKra = new HashMap<String, List<List<String>>>();
                             		Iterator<String> itgKRA = hmGoalKra.keySet().iterator();
                             		 while(itgKRA.hasNext()) {
                             			goalCount++;
                             			String goalAndFreqId = itgKRA.next();
                             		List<String> gInnerList = hmGoalDetails.get(goalAndFreqId);
		                        	String goalid = gInnerList.get(1);
		                       		String goalFreqId = gInnerList.get(32);
									String goalPerspectiveId = gInnerList.get(gInnerList.size()-2);
		                       		double avgGoalRating = 0.0d;
                               		double dblWeightScore = 0.0d;
							Map<String, String> hmPerspectData = hmPerspectiveData.get(goalPerspectiveId);
							if(hmPerspectData!=null && hmPerspectData.size() > 0 ){
                                Perspectcolor = hmPerspectData.get("PERSPECTIVE_COLOR");
                                PerspectName = hmPerspectData.get("PERSPECTIVE_NAME");
                             }%>
                             <div id = "maindiv" style = "float:left;">
                             	
							 <% if(PerspectName != null && Perspectcolor!=null){ %>
                       		 <div align="center" style="float: left; width: 3%;word-wrap:break-word;margin: 30px 10px 10px 0px;padding:150px 17px 0px 10px;border-radius:5px;height:400px;margin-top:70px;background-color:<%=Perspectcolor%>">
                        			 <%=PerspectName%>
							</div>
								
							<%} %>
							
							  <div style="float: left; width:50%;background-color:white;margin-top:30px; padding-left:10px;border-radius:5px;">
                                                    	 <div style="float: left; width: 100%; margin-top:10px;">
                                                     		<div class="<%=gInnerList.get(10) %>"
                                                                   style="float: left; line-height: 18px; font-size: 17px;"">
                                                                   <b>Objective </b>
                                                                   <br><%=gInnerList.get(3) %>
                                                                   <%=(gInnerList.get(33) != null && !gInnerList.get(33).equals("")) ? "["+gInnerList.get(33)+"]" : "" %></br>
                                                               
                                                               </div>
                                                               <div style="float: left; width: 100%; border-right: 1 px solid #CCCCCC;font-size: 20px !important; margin-top:-25px;">
                                                               	<br></br>
                                                                   <span class="<%=gInnerList.get(10) %>" style="float: left;  font-size: 15px; line-height: 18px;"><b>Goal
                                                                <br>   </b><%=uF.showData(gInnerList.get(13), "-") %></br></span>
                                                               </div>
                                                                <div style="float: left; width: 100%;">
                                                                   <span class="<%=gInnerList.get(10) %>" style="float: left;font-size: 15px; line-height: 18px;">-
                                                                   assigned by <%=gInnerList.get(4)%>, attribute <%=gInnerList.get(14) %>, effective date <%=gInnerList.get(15) %>, due date <%=gInnerList.get(5)%>
                                                                 
                                                                   </span>
                                                               </div>
                                                                 <%
                                                                   if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                                                   	int kraTotalTaskCount = 0;
                                                                   	double totalKraStatus =0;
                                                                   	double kraStatus = 0;
                                                                   	totalKraStatus = uF.parseToDouble(hmKraCompletedPercentage.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_PERCENTAGE"));
                                                                   	
                                                                   	kraTotalTaskCount= uF.parseToInt(hmKraCompletedPercentage.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_TASKCOUNT")); 
                                                                   	kraStatus = totalKraStatus / kraTotalTaskCount;
                                                                   	
                                                                   %>
                                                                   <div id="KraPercentage_<%=strEmpId %>_<%=goalid %>"
                                                                   style="float: left; margin: 0px 25px 3px 25px; width:70%;">
                                                                   <div class="anaAttrib1">
                                                                       <span style="margin-left:<%=kraStatus > 95 ? kraStatus - 10 : kraStatus - 4%>%;"><%=uF.showData(""+kraStatus, "0")%>%</span>
                                                                   </div>
                                                                   <div id="outbox" style="width: 100% !important;">
                                                                       <% if (kraStatus < 33.33) { %>
                                                                       <div id="redbox" style="width: <%=uF.showData(""+kraStatus, "0") %>%;"></div>
                                                                       <% } else if (kraStatus >= 33.33 && kraStatus < 66.67) { %>
                                                                       <div id="yellowbox" style="width: <%=uF.showData(""+kraStatus, "0")%>%;"></div>
                                                                       <% } else if (kraStatus >= 66.67) { %>
                                                                       <div id="greenbox" style="width: <%=uF.showData(""+kraStatus, "0")%>%;"></div>
                                                                       <% } %>
                                                                   </div>
                                                                   <div class="anaAttrib1"
                                                                       style="float: left; width: 100%;">
                                                                       <span style="float: left; margin-left: -4%;">0%</span>
                                                                       <span style="float: right; margin-right: -10%;">100%</span>
                                                                   </div>
                                                               </div>
                                                                <% } %>
                                                           </div>
                                                     		<div style="float: left; width: 100%;">
                                                     			<script type="text/javascript">
					                                                   $(function() {
					                                                   	$('#starPrimaryG<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>').raty({
					                                                   		readOnly: true,
					                                                   		start: <%=avgGoalRating %>,
					                                                   		half: true,
					                                                   		targetType: 'number'
					                                                   });
					                                                   });
				                                               		</script>
                                               
                                                                   <div id="starPrimaryG<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=goalCount %>" style="float: left;margin-top:10px; width:100%;" ></div>
                                                                   <div style="float: right; margin-top:10px;">
                                                                     <b>Rated Score:</b>
                                                                       <%=uF.formatIntoOneDecimalWithOutComma(dblWeightScore) %>/<%=uF.parseToDouble(gInnerList.get(16)) %>
                                                                     &nbsp;&nbsp;</div>
                                                               	</div>
                                                               	  <%--updated by kalpana on 21/10/2016 - start --%>
                                                               <div style="float: left; width: 99%; margin-top: 5px;">
                                                               
                                                                   <%	
                                                                   		List<String> goalAchieved = hmActualAchievedGoal.get(strEmpId+"_"+goalid+"_"+goalFreqId);
                                                                       if(goalAchieved == null) goalAchieved = new ArrayList<String>();
                                                                       String strActualAchieved = uF.formatIntoOneDecimalWithOutComma(dblWeightScore);
                                                                       double dblActualAchieved = (dblWeightScore * 100) / uF.parseToDouble(gInnerList.get(16));
                                                                       String strAchievedShare = uF.formatIntoOneDecimalWithOutComma(dblActualAchieved);
                                                                       if(goalAchieved != null && goalAchieved.size()>0) {
                                                                    	   strAchievedShare = goalAchieved.get(4);
                                                                    	   strActualAchieved = goalAchieved.get(5);
                                                                       }
                                                                       %>
                                                                   <table cellspacing="0" cellpadding="2" class="table table-bordered">
                                                                       <tr>
                                                                           <th colspan="4" style="padding: 0px;">
                                                                               <%if(strUserType !=null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) {%>
                                                                               Finalise & Close: <% } else { %> Finalisation Score:
                                                                               <% } %>
                                                                           </th>
                                                                       </tr>
                                                                       <tr style="text-align: center !important;">
                                                                           <th style="width: 18%; padding: 0px; text-align:center;">Weightage</th>
                                                                           <th style="width: 23%; padding: 0px; text-align:center;">Actual Achieved</th>
                                                                           <th style="width: 24%; padding: 0px; text-align:center;">Achieved Share</th>
                                                                       </tr>
                                                                       <tr  style="text-align: center !important;">
                                                                           <td><%=uF.parseToDouble(gInnerList.get(16))%></td>
                                                                           <td><input type="text" name="actualAchieved" id="actualAchieved_<%=goalCount%>" style="width: 40px !important;"
                                                                               value="<%=uF.parseToDouble(strActualAchieved) > 0 ? strActualAchieved : "" %>" onkeypress="return isNumberKey(event)"
                                                                               onkeyup="getAchievedValue('<%=uF.parseToDouble(gInnerList.get(16))%>','<%=goalCount%>')"
                                                                               <%if((strUserType !=null && !strUserType.equals(IConstants.HRMANAGER) && !strUserType.equals(IConstants.ADMIN)) || uF.parseToBoolean(gInnerList.get(12))) { %>
                                                                               readonly="readonly" <% } %> />
                                                                           </td>
                                                                           <td>
                                                                           		<input type="text" name="achievedShare" id="achievedShare_<%=goalCount%>" value="<%=uF.parseToDouble(strAchievedShare) > 0 ? strAchievedShare : "" %>" style="width: 40px !important;" readonly="readonly" />
                                                                           </td>
                                                                       </tr>
                                                                       <tr>
                                                                          <td colspan="3" style="border:none; text-align: center; padding-top:10px !important">
                                                   							<%if(strUserType !=null && (strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN)) && (dataType == null || dataType.equals("L"))) { %>
                                                                           <input type="button" name="saveBtn" id="saveBtn" class="btn btn-primary" style="width: 60px !important; margin-top: 1px;" value="Save"
                                                                               	onclick="updateActualAchievedGoal('<%=strEmpId%>','<%=goalid%>','<%=goalFreqId%>','<%=goalCount%>','<%=uF.parseToDouble(gInnerList.get(16))%>', 'SAVE', '<%=gInnerList.get(37) %>', '<%=gInnerList.get(38) %>','<%=dataType%>','<%=currUserType %>')" />&nbsp;&nbsp;
                                                                             
                                                                            <input type="button" name="saveBtnClose" id="saveBtnClose" class="btn btn-primary" style="margin-top: 1px;" value="Finalize"
                                                                                   	onclick="updateActualAchievedGoal('<%=strEmpId%>','<%=goalid%>','<%=goalFreqId%>','<%=goalCount%>','<%=uF.parseToDouble(gInnerList.get(16))%>', 'SAVECLOSE', '<%=gInnerList.get(37) %>', '<%=gInnerList.get(38) %>','<%=dataType%>','<%=currUserType %>')" />
                                                                           <% } %>
                                                                           </td>
                                                                       </tr>
                                                                   </table>
                                                               </div>
                                                    		 </div>
                                                    		 
												 <%	boolean accessFlag = false;
                                                   boolean managerAccessFlag = false;
                                                   if(membersAccessList.contains(gInnerList.get(34)+"_5") || membersAccessList.contains(gInnerList.get(34)+"_13")) {
                                                   accessFlag = true;
                                                   }
                                                   if(uF.parseToInt(strSessionEmpId) == uF.parseToInt(hmEmpSuperIds.get(strEmpId))) {
                                                   managerAccessFlag = true;
                                                   }
                                                  if(gInnerList.get(20) != null && gInnerList.get(20).equals("KRA")) {
                                                   List<List<String>> goalOuterList = hmGoalKra.get(goalAndFreqId);
                                                  %><div style="float: left; width: 30%; margin-top:20px;">                                                  
                                                  <%
                                                    for(int j=0; goalOuterList!=null && !goalOuterList.isEmpty() && j<goalOuterList.size(); j++) {
                                                    List<String> innerList = goalOuterList.get(j);
                                                    List<List<String>> taskOuterList = hmKRATasks.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11));
                                                   	double avgKRARating = 0.0d;
                                                   	double dblKRAWeightScore = 0.0d;
                                                   	String kraRating = hmEmpwiseKRARating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                   	String kraTaskCount = hmEmpwiseKRARating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                   	if(uF.parseToInt(kraTaskCount) > 0) {
                                                   		avgKRARating = uF.parseToDouble(kraRating) / uF.parseToInt(kraTaskCount);
                                                   	}
                                                   	String strUserCnt = "";
                                                   	if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                                   		String strUserRating = hmEmpwiseKRAEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_RATING");
                                                   		strUserCnt = hmEmpwiseKRAEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_COUNT");
                                                   		double avgUserRating = 0;
                                                   		if(uF.parseToInt(strUserCnt)>0) {
                                                   			avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserCnt);
                                                   		}
                                                   		if(avgKRARating > 0 && avgUserRating > 0) {
                                                   			avgKRARating = (avgKRARating + avgUserRating) / 2;
                                                   		} else if(avgUserRating > 0) {
                                                   			avgKRARating = avgUserRating;
                                                   		}
                                                   	}
                                                   	dblKRAWeightScore = (avgKRARating * uF.parseToDouble(innerList.get(27))) / 5;
                                                   %> <script type="text/javascript">
                                                   $(function() {
                                                   	$('#starPrimaryGK<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>').raty({
                                                   		readOnly: true,
                                                   		start: <%=avgKRARating %>,
                                                   		half: true,
                                                   		targetType: 'number'
                                                   });
                                                   });
                                               </script>
                                              
                                                <ul style="float: left; width:100%; margin-top:15px;">
                                               
                                               	 <li  style="float: left; width: 100%; margin-left:-10px; margin-bottom:-10px; background-color:white;border-radius:5px;">
                                                  <div id = "the_div1">
                                                   <div  style="float: left; width: 100%;">
                                                             <div style="float: left; margin: 10px 0px 0px 10px">
                                                               <span class="<%=innerList.get(10) %>" style="margin: 0px 0px 0px 0px; float: left;"><strong>Initiative:</strong>&nbsp;<%=uF.showData(innerList.get(2), "-") %>
                                                               <span style="float: left; width: 100%;"> <% if(uF.parseToBoolean(hmCheckKWithAllowance.get(innerList.get(11)))) { %>
                                                               <span style="font-style: italic;"><a href="javascript:void(0);" style="font-size: 10px; font-weight: normal;"
                                                                   onclick="viewPerformanceAllowanceData('<%=innerList.get(11) %>', 'K');">This KRA has Performance Incentives attached</a>
                                                               </span> <% } else { %> <span style="font-size: 10px; font-style: italic; color: gray;">This KRA has no Performance Incentives attached</span> <% } %> </span> </span>
                                                           </div>
                                                           <div style="float: right;padding-left:10px; width: 100%; margin-top:5px; border-bottom: 1px solid #CCCCCC;">
                                                               <div   id="starPrimaryGK<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11)%>_<%=j%>" style="float: left;  width: 100%;margin-top:-5px;"></div>
                                                               <div style="float: right;padding-right:10px">
                                                                   <b>Rated Score:</b>
                                                                   <%=uF.formatIntoOneDecimalWithOutComma(dblKRAWeightScore) %>/<%=uF.parseToDouble(innerList.get(27)) %>
                                                               </div>
                                                           </div>
                                                     </div>
                                                    <% if(taskOuterList!=null && !taskOuterList.isEmpty()) { %>
                                                       <div style="float: left; width: 100%; ">
                                                           <div style="float: left; width: 100%; line-height: 12px; margin-top:10px;margin-left:-5px;" >
                                                               <span style="font-weight: bold; margin-left: 30px; color: gray;">Tasks:</span>
                                                           </div>
                                                           <% 
                                                               //System.out.println("taskOuterList==>"+taskOuterList.size());
                                                               for(int a=0; a<taskOuterList.size(); a++) {
                                                               
                                                               List<String> taskInnerList = taskOuterList.get(a);
                                                               //System.out.println("EMPID ===>> " + strEmpId + " -- KRAID ===>> " + innerList.get(11) + " -- TASKID ===>> " + taskInnerList.get(0));
                                                               String taskStatusPercent = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_STATUS");
                                                               //System.out.println("taskStatusPercent ===>> " + taskStatusPercent);
                                                               String managerRating = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_RATING");
                                                               String hrRating = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_RATING");
                                                               String managerComment = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_MGR_COMMENT");
                                                               String hrComment = hmKRATaskStatusAndRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_HR_COMMENT");
                                                               
                                                               String hrMngrReview = "block";
                                                               if(uF.parseToInt(taskStatusPercent) >= 100 || uF.parseToInt(taskInnerList.get(2)) == 1) {
                                                               	hrMngrReview = "block";
                                                               }
                                                               double avgRating = (uF.parseToDouble(managerRating) + uF.parseToDouble(hrRating)) / 2;
                                                               if(managerRating == null) {
                                                               	avgRating = uF.parseToDouble(hrRating);
                                                               } else if(hrRating == null) {
                                                               	avgRating = uF.parseToDouble(managerRating);
                                                               }
                                                               
                                                               String strUserTaskCnt = "";
                                                               if(hmKRATaskEmpRating != null && !hmKRATaskEmpRating.isEmpty()) {
                                                               	String strUserRating = hmKRATaskEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_RATING");
                                                               	strUserTaskCnt = hmKRATaskEmpRating.get(strEmpId+"_"+goalid+"_"+goalFreqId+"_"+innerList.get(11)+"_"+taskInnerList.get(0)+"_COUNT");
                                                               	double avgUserRating = 0;
                                                               	if(uF.parseToInt(strUserCnt)>0) {
                                                               		avgUserRating = uF.parseToDouble(strUserRating) / uF.parseToInt(strUserTaskCnt);
                                                               	}
                                                               	if(avgRating > 0 && avgUserRating > 0) {
                                                               		avgRating = (avgRating + avgUserRating) / 2;
                                                               	} else if(avgUserRating > 0) {
                                                               		avgRating = avgUserRating;
                                                               	}
                                                               }
                                                               
                                                               
                                                               int commentCnt = 0;
                                                               if(managerComment != null && !managerComment.equals("null")) {
                                                               	commentCnt++;
                                                               }
                                                               if(hrComment != null && !hrComment.equals("null")) {
                                                               	commentCnt++;
                                                               }
                                                               %>
                                                                 
                                                           <div style="float: left; width: 100%;">
                                                               <script type="text/javascript">
                                                                   $(function() {
                                                                   	$('#starPrimaryGKT<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
                                                                   		readOnly: true,
                                                                   		start: <%=avgRating %>,
                                                                   		half: true,
                                                                   		targetType: 'number'
                                                                   });
                                                                   });
                                                               </script>
                                                            <!-- <div style="float: left;width:100%;"> -->
                                                            <div style="float: left; margin-left:10px; line-height: 15px; width: 40%; margin-top:10px;"><%=uF.showData(taskInnerList.get(1), "-") %></div>
																 <div id="KTProBarDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; width: 30%;">                                                                   
																	<div class="anaAttrib1">
                                                                       <span style="margin-left:<%=uF.parseToDouble(taskStatusPercent) > 95 ? uF.parseToDouble(taskStatusPercent) - 10 : uF.parseToDouble(taskStatusPercent) - 4%>%;"><%=uF.showData(taskStatusPercent, "0")%>%</span>
                                                                   </div>
                     												<div id="outbox" style="margin-left:10px">
                                                                       <% if (uF.parseToDouble(taskStatusPercent) < 33.33) { %>
                                                                       <div id="redbox" style="width: <%=uF.showData(taskStatusPercent, "0") %>%;"></div>
                                                                       <% } else if (uF.parseToDouble(taskStatusPercent) >= 33.33 && uF.parseToDouble(taskStatusPercent) < 66.67) { %>
                                                                       <div id="yellowbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
                                                                       <% } else if (uF.parseToDouble(taskStatusPercent) >= 66.67) { %>
                                                                       <div id="greenbox" style="width: <%=uF.showData(taskStatusPercent, "0")%>%;"></div>
                                                                       <% } %>
                                                                   </div>
                                                                  <div class="anaAttrib1" style="float: left; width: 100%; margin-left:0px;">
                                                                       <span style="float: left; margin-left: -4%;">0%</span>
                                                                       <span style="float: right; margin-left: -10%;">100%</span>
                                                                   </div>
                                                               </div>
                                                               <%
                                                               boolean flag = true;
                      										
                      									 	  String orientaionKey = gInnerList.get(34)+"_"+strUserTypeId;
                                                                   String addedBy = taskInnerList.get(4);
                                                                   if(flag ||  (strUserType != null && (strUserType.equals(IConstants.ADMIN) || uF.parseToInt(addedBy)== uF.parseToInt(strSessionEmpId) || uF.parseToBoolean(hmOrientationEditAccess.get(orientaionKey))))) { 
                                                                	   if(dataType == null || dataType.equals("L")) {
                                                                   %>
	                                                               <div >
	                                                                   <input type="text" name="completedPercent_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" id="completedPercent_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" value="<%=uF.showData(taskStatusPercent, "") %>" style="width: 30px !important; margin-left:18px; margin-top:10px;" onkeypress="return isNumberKey(event)" />
	                                                                    <a onclick="updateCompletedPercent('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>','<%=taskInnerList.get(0) %>', 'KRA','<%=accessFlag %>');" href="javascript:void(0);" title="Update">
	                                                                      <i class="fa fa-pencil-square-o" aria-hidden="true" style = "margin-left:20px;"></i>
                             											</a> <br />
 																		<span id="completedPercentStatusSpan_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>	                                                              
 																 </div>
                                                               <% } %>
                                                               <%} else { %>
                                                               <div style="float: left; margin: 20px 5px 3px 15px; width: 7%;">
                                                                   <label style="width: 40px !important;"><%=uF.showData(taskStatusPercent, "") %></label>
                                                                   <br />
 																	<span id="completedPercentStatusSpan_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></span>                                                               
 																</div>
                                                               <% } %>
 																 <div id="GivenTaskRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; width:100%">
                                                                     <div style="float: left; padding-left: 0px; margin-top:-5px; ">		
                                                                     <% 
                                                                     	String feedbackUserType = null;
                                                                     	if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                                           	feedbackUserType = strBaseUserType;
                                                                           }
                                                                    	    String taskInnerListdata =  taskInnerList.get(0);
                                                                    	    String innerListdata = innerList.get(11);
                                                                    	    
                                                                   		%>
																	 <a style="height: 1px; width: 2px; padding-top:2px;" href="javascript:void(0)" onclick="updateComment1('<%=strEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '<%=innerListdata %>','<%=taskInnerList.get(0) %>', 'KRA','<%=accessFlag %>')"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add Comment</a>
																	 </div>
                                                                   <div id="starPrimaryGKT<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="float: left; margin: -5px 0px 0px 150px;"></div>
                                                                   <% if(commentCnt > 0 || uF.parseToInt(strUserTaskCnt) > 0) { %>
                                                                  
                                                                   <div>
                                                                       <a href="javascript:void(0);" onclick="viewManagerAndHRComments('<%=strEmpId %>', '<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>', '<%=taskInnerList.get(0) %>', 'KRA')">Comments
                                                                       <%=(commentCnt + uF.parseToInt(strUserTaskCnt)) %></a>
                                                                   </div>
                                                                   <% } %>
                                                               </div>
                                                               <% 
                                                                   String feedbackUserType1 = "";
                                                                   if(currUserType != null && currUserType.equals(strBaseUserType)) {
                                                                   	feedbackUserType1 = strBaseUserType;
                                                                   }
                                                                   /* if((uF.parseToInt(hmHrIds.get(strEmpId)) == uF.parseToInt(strSessionEmpId) && strUserType != null && strUserType.equals(IConstants.HRMANAGER)) || (strUserType != null && !strUserType.equals(IConstants.HRMANAGER) && ((managerAccessFlag && strBaseUserType != null && (strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HOD))) || (strBaseUserType != null && !strBaseUserType.equals(IConstants.CEO) && !strBaseUserType.equals(IConstants.HOD)) ) ) ) { */
                                                                   	if((uF.parseToInt(hmHrIds.get(strEmpId)) == uF.parseToInt(strSessionEmpId) && strUserType != null && strUserType.equals(IConstants.HRMANAGER)) || (strUserType != null && !strUserType.equals(IConstants.HRMANAGER)) ) { %>
                                                                   <% if(dataType == null || dataType.equals("L")) {%>	
		                                                               <div id="TaskRatingDiv_<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" style="display:none; float: left; padding-left: 10px; border-left: 1px solid #CCCCCC;">
		                                                                   <script type="text/javascript">
		                                                                       $(function() {
		                                                                       	$('#starGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').raty({
		                                                                       		readOnly: false,
		                                                                       		start: 0,
		                                                                       		half: true,
		                                                                       		targetType: 'number',
		                                                                       		click: function(score, evt) {
		                                                                       				$('#hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>').val(score);
		                                                                       }
		                                                                       });
		                                                                       });
		                                                                   </script>
		                                                                   <input type="hidden"
		                                                                       name="hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
		                                                                       id="hideGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>">
		                                                                   <div
		                                                                       id="starGKTRating<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"
		                                                                       style="float: left; margin: -4px 0px 0px 25px; width: 110px;"></div>
		                                                                   <br />
		                                                                   <div style="float: left; margin: 0px 0px 5px 0px;">
		                                                                       <textarea rows="1" cols="40" name="strComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>" id="strComment<%=strEmpId %>_<%=goalid %>_<%=goalFreqId %>_<%=innerList.get(11) %>_<%=taskInnerList.get(0) %>"></textarea>
		                                                                   </div>
		                                                                   <div style="float: left; margin: 0px 0px 5px 7px;">
		                                                                       <a href="javascript:void(0);" onclick="updateKRATaskRatingAndComment('<%=strEmpId %>','<%=goalid %>', '<%=goalFreqId %>', '<%=innerList.get(11) %>','<%=taskInnerList.get(0) %>', 'KRA','<%=feedbackUserType %>','<%=dataType%>','<%=currUserType%>')">
		                                                                           <input type="button" class="btn btn-primary" name="update" value="Update">
		                                                                       </a>
		                                                                   </div>
		                                                               </div>
		                                                           <% } %>    
                                                               <% } %>
                                                           </div>
                                                           <% } %>
                                                       </div>
                                                       <% } %>
                                                       </div>
                                                   </li>
                                               
                                               </ul>
                                             <% } }%></div> 
                                                  
                                                  <% }%>
                                                   </div>
                                                   <% }} %>
                            
                               </br>
						</div>
				 </div>
		     </section>
				<div class="custom-legends">
					<div class="custom-legend pullout">
						<div class="legend-info">Waiting for workflow(Workflow)</div>
					</div>
					<div class="custom-legend pending">
						<div class="legend-info">Not filled yet(Myself)</div>
					</div>
					<div class="custom-legend approved">
						<div class="legend-info">Completed(Myself)</div>
					</div>
					<div class="custom-legend denied">
						<div class="legend-info">Expired</div>
					</div>
					<div class="custom-legend re_submit">
						<div class="legend-info">Waiting for completion(Workflow)</div>
					</div>
					<br />
					<div class="custom-legend no-borderleft-for-legend">
						<div class="legend-info high">High Priority</div>
					</div>
					<div class="custom-legend no-borderleft-for-legend">
						<div class="legend-info medium">Medium Priority</div>
					</div>
					<div class="custom-legend no-borderleft-for-legend">
						<div class="legend-info low">Low Priority</div>
					</div>
				</div>
			</div>
			</section>
		</div>
	</div>
	</section>
</div>
</section>
<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">View Information</h4>
			</div>
			<div class="modal-body"
				style="height: 400px; overflow-y: auto; padding-left: 25px;">
			</div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default"
					data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>