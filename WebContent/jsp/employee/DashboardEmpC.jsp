<%@page import="com.konnect.jpms.util.ArrayUtils"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.*, ChartDirector.*"%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>	
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>  
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/rateYo/2.2.0/jquery.rateyo.min.css">
<style>
.btn-info{
background-color: #00c0ef;
border-color: #00acd6;
color: #fff;
font-weight: 600;
}
.btn-info:hover {
background-color: #1298B9;
}

</style>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>

<script type="text/javascript" src="struts/optiontransferselect.js"></script>

<!-- For Local --> 
<%-- <sx:head extraLocales="af,ar-sa,ar-eg,ar-dz,ar-tn,ar-ye,ar-jo,ar-kw,ar-bh,eu,be,zh-tw,zh-hk,hr,da,nl-be,en-us,en-au,en-nz,en-za,en,en-tt,fo,fi,fr-be,fr-ch,gd,de,de-at,de-li,he,hu,id,it-ch,ko,lv,mk,mt,no,pt-br,rm,ro-mo,ru-mo,sr,sk,sb,es-mx,es-cr,es-do,es-co,es-ar,es-cl,es-py,es-sv,es-ni,sx,sv-fi,ts,tr,ur,vi,ji"/> --%>

<!-- For Server --> 
<!-- compressed="false" -->
<%-- <sx:head debug="false" cache="true" extraLocales="af,ar-sa,ar-eg,ar-dz,ar-tn,ar-ye,ar-jo,ar-kw,ar-bh,eu,be,zh-tw,zh-hk,hr,da,nl-be,en-us,en-au,en-nz,en-za,en,en-tt,fo,fi,fr-be,fr-ch,gd,de,de-at,de-li,he,hu,id,it-ch,ko,lv,mk,mt,no,pt-br,rm,ro-mo,ru-mo,sr,sk,sb,es-mx,es-cr,es-do,es-co,es-ar,es-cl,es-py,es-sv,es-ni,sx,sv-fi,ts,tr,ur,vi,ji"/> --%>  
  
 
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/jquery.jOrgChart.css" />
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/organisational/prettify.css" />
<script src="<%= request.getContextPath()%>/scripts/organisational/prettify.js" type="text/javascript"></script>
<script type="text/javascript" src="scripts/charts/highcharts.js"></script>
<script type="text/javascript" src="scripts/charts/exporting.js"></script> 

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
});
	
</script>
<style>
 
#greenbox {
height: 17px;
background-color:#00FF00; /* the critical component */
}
#redbox {
height: 17px;
background-color:#FF0000; /* the critical component */
}
#yellowbox {
height: 17px;
background-color:#FFFF00; /* the critical component */
}
#outbox {
height: 17px;
width: 100%;
background-color:#D8D8D8; /*D8D8D8 the critical component */
}

.anaAttrib1 {
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}
.site-stats li {
width: 29% !important;
}
</style>

<g:compress>
<script type="text/javascript">  
//<![CDATA[
    
	function getEmpProfile(val) {
    	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$(".modal-title").html('Employee Profile');
		$.ajax({
			url : "AppraisalEmpProfile.action?empId=" + val,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
  //]]>  
    </script>
 </g:compress> 


<ul id="org" style="display:none">
	<%=request.getAttribute("sbPosition")%>
</ul>
 



<script type="text/javascript">
//<![CDATA[
function validateNewTimeNotification(){
	
	if(document.frmClockEntries1.strNotify.checked){
		//document.getElementById("newTimeNotificationS").style.display="table-row";
	//	document.getElementById("newTimeNotificationE").style.display="table-row";
		document.getElementById("newTimeNotification").style.display="table-row";
	}else{
	//	document.getElementById("newTimeNotificationS").style.display="none";
	//	document.getElementById("newTimeNotificationE").style.display="none";
		document.getElementById("newTimeNotification").style.display="none";
	}
}


function validateForm(){
	var re5digit=/^\d{2}:\d{2}$/;
	
	/* if(document.frmClockEntries1.strNotify.checked){
		if(document.frmClockEntries1.strNewTime.value==""){
			alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
			return false;
		}else if(document.frmClockEntries1.strNewTime.value.search(re5digit)==-1){
			alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
			return false;
		}
	} */
	if(document.frmClockEntries1.strReason.value==""){
		alert('Please enter valid reason.');
		return false;
	}
	
}

function validateService(){	
	
	var re5digit=/^\d{2}:\d{2}$/;
	
	if(document.frmClockEntries1.strRosterStartTime.value==""){
		alert('Please enter roster start time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}else if(document.frmClockEntries1.strRosterStartTime.value.search(re5digit)==-1){
		alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}
	
	if(document.frmClockEntries1.strRosterEndTime.value==""){
		alert('Please enter roster end time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}else if(document.frmClockEntries1.strRosterEndTime.value.search(re5digit)==-1){
		alert('Please enter time in given format (HH:mm). \ne.g. for 3:00AM enter 03:00 and for 3:00PM enter 15:00'); 
		return false;
	}
	
	if(document.frmClockEntries1.service.options[0].selected){
		alert('Please choose the cost centre.'); 
		return false;
	}
}


/* function showClockMessage() {	
	dojo.event.topic.publish("showClockMessage");
}

function showClockLabel() {
	dojo.event.topic.publish("showClockLabel");
} */


function viewCertificate(strEmpId,planId) { 

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Certificate');
	 $.ajax({
			url : "ViewEmpCertificate.action?strEmpId="+strEmpId+"&planId="+planId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
}


function getTeamMembers() {

	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('My Team');
	 $.ajax({
			url : "TeamMembers.action?type=MyTeam",
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
} 

function approveDenyLeave(apStatus,leaveId,levelId,compensatory){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var action = 'ManagerLeaveApproval.action?type=type&apType=auto&apStatus='+apStatus+'&E='+leaveId+'&LID='+levelId+'&strCompensatory='+compensatory+'&mReason='+reason;
			//alert(action); 
			window.location = action;
		}
	}
}

function approveDenyRembursement(apStatus,reimbId){
	var status = '';
	if(apStatus == '1'){
		status='approve';
	} else if(apStatus == '-1'){
		status='deny';
	}
	if(confirm('Are you sure, do you want to '+status+' this request?')){
		var reason = window.prompt("Please enter your "+status+" reason.");
		if (reason != null) {
			var action = 'UpdateReimbursements.action?type=type&S='+apStatus+'&RID='+ reimbId +'&T=RIM&M=AA&mReason='+reason; 
			//alert(action); 
			window.location = action;
		}
	}
}


//]]> 
</script>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript" src="<%= request.getContextPath()%>/scripts/_rating/js/jquery.raty.min.js"> </script>


<%
UtilityFunctions uF = new UtilityFunctions();
/* List alSkills = (List) request.getAttribute("alSkills"); */
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
String []arrEnabledModules = CF.getArrEnabledModules();
String strBaseUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);
boolean blnWebClockOnOff = (Boolean) request.getAttribute("blnWebClockOnOff");

//String empUserTypeId = (String) request.getAttribute("EMP_USER_TYPE_ID");
String empUserTypeId = (String) request.getAttribute("strUserTypeId");
//Map<String,List<String>> hmThoughts = (Map<String, List<String>>) request.getAttribute("hmthoughts");
Map<String, String> hmEmpProfile = (Map<String, String>) request.getAttribute("hmEmpProfile");
if (hmEmpProfile == null) {
	hmEmpProfile = new HashMap<String, String>();
}

String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
String strUserType = (String)session.getAttribute(IConstants.BASEUSERTYPE);

Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");

%>

 <%-- <% List<String> strSkillsList = (List<String>) request.getAttribute("strSkillsList"); %> --%>
 

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="My Dashboard" name="title"/>
</jsp:include> --%>


<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
    <section class="content">
	 	<div class="row jscroll">
	 		<section class="col-lg-4 connectedSortable">
	 			<div class="box box-widget widget-user widget-user1">
			            <!-- Add the bg color to the header using any of the bg-* classes -->
			            <!-- <div class="widget-user-header bg-aqua-active"> -->
			       <!-- ====start parvez on 27-10-2022===== -->      
			              <%-- <img class="lazy" src="images1/user-background-photo.jpg" style="position: absolute;" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'> --%>
			              <%if(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO) !=null && uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_USER_BACKGROUND_PHOTO)) && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO)!=null && hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO).size()>0){ 
								List<String> alPhotoInner = hmFeatureUserTypeId.get(IConstants.F_USER_BACKGROUND_PHOTO);
							%>
								<div class="widget-user-header bg-aqua-active" style="height: 130px !important">
								<img class="lazy" src="images1/exusia_banner_<%=alPhotoInner.get(1) %>.png" style="position: absolute; height: 130px !important;" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
							<% } else{ %>
								<div class="widget-user-header bg-aqua-active">
								<img class="lazy" src="images1/user-background-photo.jpg" style="position: absolute;" data-original='<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE_COVER+"/"+hmEmpProfile.get("EMP_ID")+"/"+hmEmpProfile.get("COVER_IMAGE")%>'>
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
			            <div class="box-footer" style="padding-top: 7% !important;">
			              <div class="row">
			                <div class="col-sm-12">
			                  <div class="description-block">
			                    <h5 class="description-header"><%=uF.showData((String) hmEmpProfile.get("DESIGNATION_NAME"), "-")%> </h5> <%-- [<%=uF.showData((String) hmEmpProfile.get("LEVEL_NAME"), "-")%>] [<%=uF.showData((String) hmEmpProfile.get("GRADE_NAME"), "-")%>] --%>
			                    <span class="description-text"><%=uF.showData((String) hmEmpProfile.get("DEPARTMENT_NAME"), "-")%> </span> <%-- [<%=uF.showData((String) hmEmpProfile.get("SBU_NAME"), "-")%>] --%>
			                    <p class="description-text"><%=uF.showData((String) hmEmpProfile.get("WLOCATION_NAME"), "-")%> </p>
			                    <%-- <p class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p> --%>
			                    <%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
			                   	  <span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong> </span>
			                  	<% } else { %>
			                  	  You don't have a reporting manager.
			                  	<% } %>
			                  </div>
			                  <!-- /.description-block -->
			                </div>
			              </div>
			              <!-- /.row -->
			            </div>
			          </div>
	 		
	 		
	              <div class="box box-info">
			          <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){ 
		            	List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
		            	Map<String,List<List<String>>> hmElementAttribute=(Map<String,List<List<String>>>)request.getAttribute("hmElementAttribute");
						Map<String, String> hmScoreAggregateMap =(Map<String, String>) request.getAttribute("hmScoreAggregateMap");
		            	double dblScorePrimary = 0, aggregeteRating = 0, totAllAttribRating = 0;
		 				int count = 0;
		 				for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
		 					List<String> innerList = elementouterList.get(i);
		 					List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
		 					
		 					if(attributeouterList1 != null && !attributeouterList1.isEmpty()) {
			 					for (int j = 0; j < attributeouterList1.size(); j++) {
			 						List<String> attributeList1 = attributeouterList1.get(j);
			 						double dblAttribRating = uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
			 						if(dblAttribRating>0) {
				 						totAllAttribRating += uF.parseToDouble(hmScoreAggregateMap.get(attributeList1.get(0).trim()));
				 						count++;
			 						}
			 					}
		 					} else {
		 						count++;
		 					}
		 				}
		 				aggregeteRating = totAllAttribRating / count;
		 				dblScorePrimary = aggregeteRating; 
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
									<div style="float:left;width:200px;"><b>Overall:</b> </div>
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
										        	<%-- start: <%=dblScorePrimary %>, --%>
										        	/* hintList: ['Performs below Expectations', 'Occasionally meets expectations', 'Consistently meets expectations', 'Occasional Role Model', 'Consistent Role Model'], */
								        	</script>
										
								</div>
								
								
								<%
								for(int i=0; elementouterList != null && i<elementouterList.size();i++){
									List<String> innerList=elementouterList.get(i);
								%>
									<%-- <div style="float:left; padding-left: 10px; width:100%"><strong><%=innerList.get(1)%></strong></div> --%>
									<%List<List<String>> attributeouterList1 = hmElementAttribute.get(innerList.get(0).trim());
								       for(int j=0; attributeouterList1 != null && j<attributeouterList1.size();j++) {
										List<String> attributeList1 = attributeouterList1.get(j);
								    %>
										<div style="float:left; width:100%;"> <!--  padding-left: 10px; -->
										<div style="float:left;width:200px;"><%=attributeList1.get(1) %> (<%=innerList.get(1).substring(0, 2) %>): </div>
										
										<%-- <%if(hmScoreAggregateMap.get(attributeList1.get(0).trim())==null) { %>
										 	<div>Not Rated</div>
										<%} else { %> --%>
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
										<%-- <% } %> --%>
										</div>
								      <% }
									}
									%>
								
								<%
								if(hmElementAttribute == null || hmElementAttribute.isEmpty()){
								%>
								<div class="nodata msg" style="float: left; width: 100%">
									<span>No attribute aligned with this level.</span>
								</div>
								<%} %>
								
								<div class="viewmore" style="float: right;">
									<a href="MyHR.action">
									
									<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to My Reviews.."/> --%>
									<i class="fa fa-forward" aria-hidden="true" title="Go to My Reviews.."></i>
									
									</a>
								</div>
			                	<div class="clr"></div>		
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
										<%-- 
										<%if(skillrate <=0) { %>
										 	<div>Not Rated</div>
										<%} else { %> --%>
										<div id="starSkills_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
											        	$('#starSkills_<%=i %>').raty({
											        		readOnly: true,
											        		start: <%=skillrate %>,
											        		half: true, 
											        		targetType: 'number'
											        	});
								        	</script>
										<%-- <% } %> --%>
										</div>
									
								<% }
								if(i==0){
									%>
									<div class="nodata msg" style="width: 93%">
									<span>No skills aligned.</span>
									</div>
								<%} %>
								</div>
							</div>
						</div>
	                </div><!-- /.box-body -->
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
	                	<h3 class="box-title">My Goals, KRAs, Targets</h3>
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
								%> 
								
								<div style="float:left;width:100%;padding-top: 2px;padding-bottom: 2px;">
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
								<% } %>
								
								<%
								
								Map<String, String> hmGoalAverage = (Map<String, String>) request.getAttribute("hmGoalAverage");
								i=0;
								for(;goalIdList!=null && i<goalIdList.size();i++) {
									List<String> goalInnerList =goalIdList.get(i);
									cnt++;
									if(cnt > 20) {
										break;
									}
								%>
								
								<div style="float:left;width:100%;padding-top: 2px;padding-bottom: 2px;">
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
									<div class="nodata msg" style="width: 93%">
									<span>No KRAs, Goals found.</span>
									</div>
								<%} %>
								
								<div class="viewmore" style="float: right;"><a href="MyHR.action">
			           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to My Goals, KRAs, Targets.."/> --%>
			           				<i class="fa fa-forward" aria-hidden="true" title="Go to My Goals, KRAs, Targets.."></i>
			           				
			           				
			           				
			           			</a></div>
			           			<div class="clr"></div>
								</div>
								
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	              <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
					%>
	               <div class="box box-warning">
			          <% String myTargetsCnt = (String) request.getAttribute("myTargetsCnt"); %>
	                <div class="box-header with-border">
	                	<h3 class="box-title">My Aligned Targets</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-yellow"><%=myTargetsCnt %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                	<div id="profilecontainer">
							<div class="content1">
								<div style="width:95%; float:left">
									<jsp:include page="/jsp/chart/MyTargetDonutChart.jsp" />
								</div>
				                <div class="viewmore clr"><a href="MyHR.action" style="float: right;">
			           				<%-- <img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to My Targets.."/>--%>
			           				<i class="fa fa-forward" aria-hidden="true" title="Go to My Targets.."></i>
			           			</a></div>					
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	              
	 		</section>
	 		
	 		<section class="col-lg-4 connectedSortable" style="padding-left: 0px;padding-right: 0px;">
	 			<%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0 && CF.isClockOnOff() && blnWebClockOnOff) { %>
		 			<div class="box box-primary">	                
		                <div class="box-body" style="padding: 5px; overflow-y: auto;background-color: #2A3B3F;background-image: url(images1/clockon_bg.png);background-position: right top;background-repeat: no-repeat;">
		                	<div id="involmentcontainer" style="padding: 10px;">
								<div id="clockONOFF" style="color: #fff;"></div>
							</div>
		                </div><!-- /.box-body -->
		              </div>
	              <% } %> --%>
	              
	              <% if(arrEnabledModules!=null && (ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0 || ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0)) {
					String reviewEmpCount = (String) request.getAttribute("reviewEmpCount");
					String kraFormCount = (String) request.getAttribute("kraFormCount");
					String interviewCount = (String) request.getAttribute("interviewCount");
					
					int totFormCount = uF.parseToInt(reviewEmpCount) + uF.parseToInt(kraFormCount) + uF.parseToInt(interviewCount);
				%>   
	 		 	<div class="box box-info">
			          
	                <div class="box-header with-border">
	                	<h3 class="box-title">Forms</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-blue"><%=totFormCount %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div class="rosterweek"> 
							<div class="content1">
								<div class="holder">
									<div style="width:100%; float:left;">
									
										<div style="padding: 3px; margin: 3px; float: left; width: 97%;">
										 <ul class="site-stats">
							              
										<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
											<% if(strBaseUserType != null && (strBaseUserType.equals(IConstants.MANAGER) || strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HRMANAGER) || strBaseUserType.equals(IConstants.ADMIN))) { %>
											<%-- <a href="Login.action?role=2&userscreen=teamReviews">
												<div style="float: left; width: 29%; padding: 3px; margin: 0px 3px; border: 1px solid #CCCCCC; border-radius: 5px; text-align: center; background-color: #FCFCFC;">
													<div style="font-size: 22px; font-weight: normal; color: black;"><%=reviewEmpCount %></div>
													<div style="font-weight: bold; color: gray;">Reviews</div>
												</div>
											</a> --%>
											<a href="Login.action?role=2&userscreen=teamReviews"><li class="bg_lh"><i class="fa fa-signal" aria-hidden="true"></i><strong><%=reviewEmpCount %></strong> <small>Reviews</small></li></a>
											<a href="Login.action?role=2&userscreen=teamGoalsKRATarget"><li class="bg_lh"><i class="fa fa-signal" aria-hidden="true"></i><strong><%=kraFormCount %></strong> <small>KRA Reviews</small></li></a>
											<%-- <a href="Login.action?role=2&userscreen=teamGoalsKRATarget">
												<div style="float: left; width: 29%; padding: 3px; margin: 0px 3px; border: 1px solid #CCCCCC; border-radius: 5px; text-align: center; background-color: #FCFCFC;">
													<div style="font-size: 22px; font-weight: normal; color: black;"><%=kraFormCount %></div>
													<div style="font-weight: bold; color: gray;">KRA Reviews</div>
												</div>
											</a> --%>
											<% } %>
										<% } %>
										
										<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_ONBOARDING+"")>=0) { %>
											<%-- <a href="Login.action?role=3&userscreen=interviews">
												<div style="float: left; width: 35%; padding: 3px; margin: 0px 3px; border: 1px solid #CCCCCC; border-radius: 5px; text-align: center; background-color: #FCFCFC;">
													<div style="font-size: 22px; font-weight: normal; color: black;"><%=interviewCount %></div>
													<div style="font-weight: bold; color: gray;">Interviews (1w)</div>
												</div>
											</a> --%>
											<a href="Login.action?role=3&userscreen=interviews"><li class="bg_lh"><i class="fa fa-calendar" aria-hidden="true"></i><strong><%=interviewCount %></strong> <small>Interviews (1w)</small></li></a>
										<% } %>	
										</ul>
										</div>
			                    	</div>
								</div>
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	              <% if(strUserType != null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equals(IConstants.CEO) || strUserType.equals(IConstants.HOD) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { 			 
					if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0) { 
							String exceptionCount = (String) request.getAttribute("exceptionCount");				
							int totExceptionCount = uF.parseToInt(exceptionCount);
						%> 
	               <div class="box box-success">
			          
	                <div class="box-header with-border">
	                	<h3 class="box-title">Time Exceptions</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-green"><%=totExceptionCount %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div class="rosterweek"> 
							<div class="content1">
								<div class="holder">
									<div style="width:100%; float:left;">
									<ul class="site-stats">
							            <a href="Login.action?role=2&userscreen=teamExceptions"><li class="bg_lh" style="width: 90% !important;"><i class="fa fa-exclamation-circle"></i><strong><%=uF.parseToInt(exceptionCount) %></strong> <small>Time Exceptions</small></li></a>
							        </ul>
			                    	</div>
								</div>
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% }
					} %>
	               <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){ %> 
	               <div class="box box-warning">
			          
	                <div class="box-header with-border">
	                	<h3 class="box-title">My Team Ratings</h3>
	                  <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div class="holder">
	                		<center>
		                     <div>
		                     	<div id="rateYo" style="margin-top: 10px;margin-bottom: 10px;"></div>
		                     	
		                     	<%if(uF.parseToDouble((String) request.getAttribute("dblMyRatings")) >= 3.5) { %>
		                     		<script>
				                     	$(function () {
					                     	$("#rateYo").rateYo({
					                     		ratedFill: "#00a65a",
					                    	    starWidth: "40px",
					                    	    rating: <%=""+uF.parseToDouble((String) request.getAttribute("dblMyRatings")) %>,
					                    	    readOnly: true,
					                    	    spacing: "5px"
					                    	});
				                     	});
		                     		</script>
		                     	<% } else if (uF.parseToDouble((String) request.getAttribute("dblMyRatings")) < 3.5 && uF.parseToDouble((String) request.getAttribute("dblMyRatings")) >= 2.5) { %>
		                     		<script>
				                     	$(function () {
					                     	$("#rateYo").rateYo({
					                     		ratedFill: "#FFDA17",
					                    	    starWidth: "40px",
					                    	    rating: <%=""+uF.parseToDouble((String) request.getAttribute("dblMyRatings")) %>,
					                    	    readOnly: true,
					                    	    spacing: "5px"
					                    	});
				                     	});
		                     		</script>
		                     	<% } else if (uF.parseToDouble((String) request.getAttribute("dblMyRatings")) >= 0) { %>
		                     		<script>
				                     	$(function () {
					                     	$("#rateYo").rateYo({
					                     		ratedFill: "#dd4b39",
					                    	    starWidth: "40px",
					                    	    rating: <%=""+uF.parseToDouble((String) request.getAttribute("dblMyRatings")) %>,
					                    	    readOnly: true,
					                    	    spacing: "5px"
					                    	});
				                     	});
		                     		</script>
		                     	<% } %>	                     	
		                     	</div>                    	
	                   		</center>
                 		</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	              <div class="box box-danger">
            	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { 
                    Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
                          	if(hmEmp==null) hmEmp=new HashMap<String,String>();
                    	Map<String,String> empImageMap = (Map<String, String>) request.getAttribute("empImageMap");
                    	if(empImageMap==null) empImageMap=new HashMap<String,String>();
                    %> 
                <div class="box-header with-border">
                  <h3 class="box-title">My Team</h3>
                  <div class="box-tools pull-right">
                    <span class="label label-danger"><%=hmEmp.size() %> Members</span>
                    <button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i>
                    </button>
                    <button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i>
                    </button>
                  </div>
                </div>
                
                <div class="box-body no-padding" style="max-height: 350px;overflow-y:auto;">
                  <ul class="users-list clearfix">
                  	<%
                                        Iterator<String> it=hmEmp.keySet().iterator();
                                        int j=0;
                                        while(it.hasNext()) {
                                        	j++;
                                        	String empId=it.next();
                                        	String empName=hmEmp.get(empId);
                                        	if(j > 24) {
                                        		break;
                                        	}
                                        %>
					                    <li>
					                      <%if(docRetriveLocation==null) { %>
					                      	<img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=IConstants.DOCUMENT_LOCATION + empImageMap.get(empId.trim())%>" >
					                      <% } else { %>
					                      	<img class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empId.trim()+"/"+IConstants.I_60x60+"/"+empImageMap.get(empId.trim())%>" />
					                      <% } %>    
					                      <a class="users-list-name" href="javascript:void(0);" onclick="getEmpProfile('<%=empId %>');" title="<%=empName %>"><%=empName %></a>
					                    </li>
                    		<%  } %>
                              <% if(hmEmp == null || hmEmp.isEmpty()) { %>
                                    <div class="nodata msg">No team available.</div>
                             <% } %>
                  </ul>
                  
                </div>
                
                 <%if(hmEmp != null && hmEmp.size() > 24) { %>
                <div class="box-footer text-center">
                  <a href="javascript:void(0);" onclick="getTeamMembers();" class="uppercase">View All Users</a>
                </div>
                <%}
                 }  %>
               
            </div>
            	<%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
					//Map<String, String> hmEmp = (Map<String, String>) request.getAttribute("hmEmp");
					List<List<String>> managerGoalList = (List<List<String>>) request.getAttribute("managerGoalList");
					Map<String, List<List<String>>> hmTeamGoals = (Map<String, List<List<String>>>) request.getAttribute("hmTeamGoals");
				%>
	              <div class="box box-success">
	                <div class="box-header with-border">
	                  <h3 class="box-title">Manager Goals</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-green"><%=managerGoalList != null ? managerGoalList.size() : "0" %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="profilecontainer">	
							<div class="content1">
								<div style="float: left; width: 100%; max-height: 250px;">
							<%
								Map<String, String> hmTeamGoalAverage = (Map<String, String>) request.getAttribute("hmTeamGoalAverage");
								Map<String, Map<String, String>> hmTeamGoalCalDetailsManager = (Map<String, Map<String, String>>)request.getAttribute("hmTeamGoalCalDetailsManager");
									int teamGoalCnt = 0;
									for (i = 0; managerGoalList != null && i < managerGoalList.size(); i++) {
										if(i>4) {
											break;
										}
										List<String> mInnerList = managerGoalList.get(i);
										//String pClass= innerList.get(8);
										//Map<String, String> hmIndGoalCalDetailsParent = hmTeamGoalCalDetailsManager.get(innerList.get(0));
										String alltwoDeciTotProgressAvgManager = "0";
					 					String alltotal100Manager = "100";
					 					String strtwoDeciTotManager = "0";
					 					if(hmTeamGoalCalDetailsManager != null && !hmTeamGoalCalDetailsManager.isEmpty()) {
					 	 				Map<String, String> hmTeamGoalCalDetailsParentManager = hmTeamGoalCalDetailsManager.get(mInnerList.get(0));
					 						
					 						if(hmTeamGoalCalDetailsParentManager != null && !hmTeamGoalCalDetailsParentManager.isEmpty()) {
					 							alltwoDeciTotProgressAvgManager = hmTeamGoalCalDetailsParentManager.get(mInnerList.get(0)+"_PERCENT");
					 					 		alltotal100Manager = hmTeamGoalCalDetailsParentManager.get(mInnerList.get(0)+"_TOTAL");
					 					 		strtwoDeciTotManager = hmTeamGoalCalDetailsParentManager.get(mInnerList.get(0)+"_STR_PERCENT");	
					 						}
					 					}
									%>
									
									
									<table class="table table-condensed table_no_border table_no_bottom_margin">
					                <tbody>
					                <tr>
					                  <td width="60%"><%=mInnerList.get(1)%>
					                  	<div id="starPrimaryMG<%=mInnerList.get(0)%>"></div>
					                  	<script type="text/javascript">
					                  	$(function(){
					                  		$('#starPrimaryMG<%=mInnerList.get(0)%>').raty({
								        		readOnly: true,
								        		start: <%=hmTeamGoalAverage.get(mInnerList.get(0)) != null ? uF.parseToInt(hmTeamGoalAverage.get(mInnerList.get(0))) / 20 + "" : "0"%>,
								        		half: true,
								        		targetType: 'number'
											});
					                  	});
								        	
										</script>
					                  </td>
					                  <td>
					                      <%if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 33.33){ %>
					                      <div class="progress progress-xs"><div class="progress-bar progress-bar-danger" style="width: <%=strtwoDeciTotManager%>%;"></div></div><span class="badge bg-red"><%=strtwoDeciTotManager%>%</span>
					                      <%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 66.67){ %>
					                      <div class="progress progress-xs"><div class="progress-bar progress-bar-yellow" style="width: <%=strtwoDeciTotManager%>%;"></div></div><span class="badge progress-bar-yellow"><%=strtwoDeciTotManager%>%</span>
					                      <%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 66.67){ %>
					                      <div class="progress progress-xs"><div class="progress-bar progress-bar-green" style="width: <%=strtwoDeciTotManager%>%;"></div></div><span class="badge bg-green"><%=strtwoDeciTotManager%>%</span>
					                      <%} %>
					                  </td>
					                </tr>
					              </tbody>
					            </table>
									<%-- <ul>
										<li class="list">	
											<div style="float: left; width: 100%;">
												<div style="float: right; width: 50%;">
													<div style="float: right; min-height: 40px; padding-right: 30px; width: 80%;">
														<div class="anaAttrib1"><span style="margin-left:<%=uF.parseToInt(alltwoDeciTotProgressAvgManager) > 94 ? uF.parseToInt(alltwoDeciTotProgressAvgManager)-6 : uF.parseToInt(alltwoDeciTotProgressAvgManager)-3.5 %>%;"><%=strtwoDeciTotManager%>%</span></div>
														<div id="outbox">
														<%if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 33.33){ %> 
														<div id="redbox" style="width: <%=alltwoDeciTotProgressAvgManager %>%;"></div>
														<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 66.67){ %>
														<div id="yellowbox" style="width: <%=alltwoDeciTotProgressAvgManager %>%;"></div>
														<%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 66.67){ %>
														<div id="greenbox" style="width: <%=alltwoDeciTotProgressAvgManager %>%;"></div>
														<%} %>
														</div>
														<div class="anaAttrib1" style="float: left; width: 100%;"><span style="float: left; margin-left: -3.5%;">0%</span>
														<span style="float: right; margin-right: -9%;"><%=alltotal100Manager %>%</span></div>
													</div>
													
												</div>
											
												<div style="float: left; width: 49%">
														<span><%=mInnerList.get(1)%></span> <!-- style="font-weight: bold;" -->
														<div style="float: left; margin-top: 5px; width: 99%;">
														<div id="starPrimaryMG<%=mInnerList.get(0)%>"></div>
													</div>
												</div>
										</div>
									</li>
								</ul>	 --%>
								
								
								<%
							//Map<String, String> hmTeamGoalAverage = (Map<String, String>) request.getAttribute("hmTeamGoalAverage");
							Map<String, Map<String, String>> hmIndGoalCalDetailsTeam = (Map<String, Map<String, String>>)request.getAttribute("hmIndGoalCalDetailsTeam");
							List<List<String>> teamGoalList = hmTeamGoals.get(mInnerList.get(0));
									for (int t = 0; teamGoalList != null && t < teamGoalList.size(); t++) {
										if(t>4) {
											teamGoalCnt++;
										}
										List<String> innerList = teamGoalList.get(t);
										//String pClass= innerList.get(8);
										Map<String, String> hmIndGoalCalDetailsParent = hmIndGoalCalDetailsTeam.get(innerList.get(0));
										String alltwoDeciTotProgressAvgTeam = "0";
										String alltotal100Team = "100";
										String strtwoDeciTotTeam = "0";
										if (hmIndGoalCalDetailsParent != null && !hmIndGoalCalDetailsParent.isEmpty()) {
											alltwoDeciTotProgressAvgTeam = hmIndGoalCalDetailsParent.get(innerList.get(0) + "_PERCENT");
											alltotal100Team = hmIndGoalCalDetailsParent.get(innerList.get(0) + "_TOTAL");
											strtwoDeciTotTeam = hmIndGoalCalDetailsParent.get(innerList.get(0) + "_STR_PERCENT");
										}
									%>
									
									
									<%-- <ul style="margin-left: 40px;">
										<li class="list">
											<div style="float: left; width: 100%;">
												<div style="float: right; width: 50%;">
													<div style="float: right; min-height: 40px; padding-right: 30px; width: 80%;">
														<div class="anaAttrib1"><span style="margin-left:<%=uF.parseToDouble(alltwoDeciTotProgressAvgTeam) > 95 ? uF.parseToDouble(alltwoDeciTotProgressAvgTeam) - 6 : uF.parseToDouble(alltwoDeciTotProgressAvgTeam) - 3%>%;"><%=strtwoDeciTotTeam%>%</span></div>
														<div id="outbox">
														<% if (uF.parseToDouble(alltwoDeciTotProgressAvgTeam) < 33.33) { %>
														<div id="redbox" style="width: <%=alltwoDeciTotProgressAvgTeam%>%;"></div>
														<% } else if (uF.parseToDouble(alltwoDeciTotProgressAvgTeam) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgTeam) < 66.67) { %>
														<div id="yellowbox" style="width: <%=alltwoDeciTotProgressAvgTeam%>%;"></div>
														<% } else if (uF.parseToDouble(alltwoDeciTotProgressAvgTeam) >= 66.67) { %>
														<div id="greenbox" style="width: <%=alltwoDeciTotProgressAvgTeam%>%;"></div>
														<% } %>
														</div>
														<div class="anaAttrib1" style="float: left; width: 100%;"><span style="float: left; margin-left:-3%;">0%</span>
														<span style="float: right; margin-right:-10%;"><%=alltotal100Team%>%</span></div>
													</div>
												</div>
											
												<div style="float: left; width: 49%">
														<span><%=innerList.get(1)%> (Team Goal)</span> <!-- style="font-weight: bold;" -->
														<div style="float: left; margin-top: 5px; width: 99%;">
														<div id="starPrimaryMTG<%=innerList.get(0)%>"></div>
													</div>
												</div>
										</div>
									</li>
								</ul> --%>	
								<table class="table table-condensed table_no_border table_no_bottom_margin" style="margin-left: 20px;width: 94%;margin-top: 5px;">
					                <tbody>
					                <tr>
					                  <td width="58%"><%=innerList.get(1)%> (Team Goal)
					                  	<div id="starPrimaryMTG<%=innerList.get(0)%>"></div>
					                  	<script type="text/javascript">
					                  	$(function(){
								        	$('#starPrimaryMTG<%=innerList.get(0)%>').raty({
								        		readOnly: true,
								        		start: <%=hmTeamGoalAverage.get(innerList.get(0)) != null ? uF.parseToInt(hmTeamGoalAverage.get(innerList.get(0))) / 20 + "" : "0"%>,
								        		half: true,
								        		targetType: 'number'
													});
					                  	});
										</script>
					                  </td>
					                  <td>
					                      <%if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 33.33){ %>
					                      <div class="progress progress-xs"><div class="progress-bar progress-bar-danger" style="width: <%=strtwoDeciTotTeam%>%;"></div></div><span class="badge bg-red"><%=strtwoDeciTotTeam%>%</span>
					                      <%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 33.33 && uF.parseToDouble(alltwoDeciTotProgressAvgManager) < 66.67){ %>
					                      <div class="progress progress-xs"><div class="progress-bar progress-bar-yellow" style="width:<%=strtwoDeciTotTeam%>%;"></div></div><span class="badge progress-bar-yellow"><%=strtwoDeciTotTeam%>%</span>
					                      <%}else if(uF.parseToDouble(alltwoDeciTotProgressAvgManager) >= 66.67){ %>
					                      <div class="progress progress-xs"><div class="progress-bar progress-bar-green" style="width: <%=strtwoDeciTotTeam%>%;"></div></div><span class="badge bg-green"><%=strtwoDeciTotTeam%>%</span>
					                      <%} %>
					                  </td>
					                </tr>
					              </tbody>
					            </table>
								<%	}%>
								<hr/>
								<%	if(teamGoalList == null || teamGoalList.isEmpty() || teamGoalList.size() == 0) {
								%>
								<div class="nodata msg" style="float: left; width: 93%; margin-left: 40px;">
									<span>No team goals assigned.</span>
									</div>
								<% } %>
								
								
									<%
										}
									if(managerGoalList == null || managerGoalList.isEmpty() || managerGoalList.size()==0) {
								%>
									<div class="nodata msg" style="width: 93%">
										<span>No goals found.</span>
									</div>
								<% } %>
								
								<%if((managerGoalList != null && managerGoalList.size() > 5) || (teamGoalCnt > 0)) { %>
									<div class="viewmore clr">
							        	<a style="float: right;" href="Login.action?role=2&userscreen=managerGoals">
					           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="View Manager Goals...."/> --%>
					           				<i class="fa fa-forward" aria-hidden="true" title="View Manager Goals...."></i>
					           				
					           			</a>
					           		</div>
					           		<div class="clr"></div>
								<%} %>
								</div> 
								
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	              <% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
						  if(strBaseUserType != null && (strBaseUserType.equals(IConstants.MANAGER) || strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HRMANAGER) || strBaseUserType.equals(IConstants.ADMIN))) { 
							Map<String, String> hmTeamKRAData = (Map<String, String>) request.getAttribute("hmTeamKRAData");
								String strTotPercent = hmTeamKRAData.get("PERCENT");
								String strTotCount = hmTeamKRAData.get("COUNT");
								//System.out.println("strFulfilledPosition ===>> " + strFulfilledPosition);
								double dblAvgPercent = 0;
								if(uF.parseToInt(strTotCount) > 0) {
									dblAvgPercent = uF.parseToDouble(strTotPercent) / uF.parseToDouble(strTotCount);
								}
							%>
	              <div class="box box-warning">
	                <div class="box-header with-border">
	                	<h3 class="box-title">Team KRAs</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-yellow"><%=uF.showData(strTotCount, "0") %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div class="content1">
	                		<div class="progress" style="margin-left: 20px;margin-right: 20px;margin-top: 20px;">
	                			<% if(dblAvgPercent < 33.33) { %> 
	                				<div class="progress-bar progress-bar-red" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblAvgPercent) %>%;">
					                  <span><%=uF.formatIntoOneDecimal(dblAvgPercent) %>% Complete</span>
					                </div>
								<% } else if(dblAvgPercent >= 33.33 && dblAvgPercent < 66.67) { %>
									<div class="progress-bar progress-bar-yellow" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblAvgPercent) %>%;">
					                  <span><%=uF.formatIntoOneDecimal(dblAvgPercent) %>% Complete</span>
					                </div>
								<% } else if(dblAvgPercent >= 66.67) { %>
									<div class="progress-bar progress-bar-green" role="progressbar" aria-valuenow="40" aria-valuemin="0" aria-valuemax="100" style="width: <%=uF.formatIntoOneDecimal(dblAvgPercent) %>%;">
					                  <span><%=uF.formatIntoOneDecimal(dblAvgPercent) %>% Complete</span>
					                </div>
								<% } %>
				                
				            </div>
				            <div style="width: 100%; margin-left: 25px;margin-top: 0px;">
									<div style="float: left; width: 35%; margin: 3px;"><span style="float: left; width: 20px; height: 20px; background-color: #EEEEEE;"></span><span style="float: left; margin-left: 10px;">Target</span></div>
									<div style="float: left; width: 35%; margin: 3px;">
									<span style="float: left; width: 6%; height: 20px; background-color: #dd4b39;"></span>
									<span style="float: left; width: 16px; height: 20px; background-color: #00a65a;">
									<span style="float: left; width: 40%; height: 20px; background-color: #FFE96F;"></span>
									
									</span><span style="float: left; margin-left: 10px;">Achieved</span>
									</div>
								</div>

			                <div class="viewmore clr" style="float: right;"><a href="Login.action?role=2&userscreen=teamGoalsKRATarget">
		           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to KRAs.."/> --%>
		           				<i class="fa fa-forward" aria-hidden="true" title="Go to KRAs.."></i>
		           				
		           				
		           			</a></div>					
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% }} %>
	              <% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) { %>
					<% if(strBaseUserType != null && (strBaseUserType.equals(IConstants.MANAGER) || strBaseUserType.equals(IConstants.CEO) || strBaseUserType.equals(IConstants.HRMANAGER) || strBaseUserType.equals(IConstants.ADMIN))) { %>
		              <% String myTeamTargetsCnt = (String) request.getAttribute("myTeamTargetsCnt"); %>
		              <div class="box box-default">
		                <div class="box-header with-border">
		                  <h3 class="box-title">Team Targets</h3> 
		                  <div class="box-tools pull-right">
		                  	<span class="badge bg-blue"><%=myTeamTargetsCnt %></span>
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                  </div>
		                </div><!-- /.box-header -->
		                
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                	<%
									String myTeamTargetachievedPercent = (String) request.getAttribute("myTeamTargetachievedPercent");
								 	String myTeamTargetremainPercent = (String) request.getAttribute("myTeamTargetremainPercent");
								%>
								<script type="text/javascript">
								var chart1234;
								$(document).ready(function () {
								//$(function () { 
								  //  $('#containerforTargetDonutChart').highcharts({ 
								    	chart1234 = new Highcharts.Chart({
								        chart: {
								        	renderTo: 'myTeamTarget',
								        	type: 'pie', 
								            options3d: {
												enabled: true,
								                alpha: 180
								            }
								        }, 
								        title: {
								            text: ''
								        },
								        subtitle: {
								            text: ''
								        },
								        plotOptions: {
								            pie: {
								                innerSize: 130,
								                depth: 45,
								                allowPointSelect: true,
								                cursor: 'pointer',
								                dataLabels: {
								                    enabled: false
								                },
								                showInLegend: true
								            }
								        },
								        series: [{
								            name: 'Percentage',
								            data: [
								                ['Target Missed', <%=uF.showData(myTeamTargetremainPercent, "0")%>],
								                ['Target Achieved', <%=uF.showData(myTeamTargetachievedPercent, "0")%>] 
								            ]
								        }]
								    });
								}); 
								</script>
								
								<div id="myTeamTarget"></div>
									<div style="width:95%;">
										<jsp:include page="/jsp/chart/MyTargetDonutChart.jsp" />
									</div>
					                <div class="viewmore"><a href="Login.action?role=2&userscreen=teamGoalsKRATarget">
				           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Targets.."/> --%>
				           				<i class="fa fa-forward" aria-hidden="true" title="Go to Targets.."></i>
				           				
				           			</a></div>	
		                </div><!-- /.box-body -->
		              </div>
	              <% }} %>
	              <%
				 if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { 
					List<String> alLeaves = (List<String>)request.getAttribute("alLeaves");
					if(alLeaves==null)alLeaves=new ArrayList<String>();
					%>
	              <div class="box box-info">
	                <div class="box-header with-border">
	                	<h3 class="box-title">Up Coming Team Leaves</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-blue"><%=alLeaves.size() %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="clockcontainer">
								<div class="leaves-div" style="max-height: 300px; overflow-y: auto;">
								<%
								for(i=0; i< alLeaves.size(); i++) {
									if(i>9) {
										break;
									}
								%>
									<div class="issues"><%= alLeaves.get(i)%></div>
									<%
								}
								if(i == 0) {
								%>		
								<div class="nodata msg">No up coming team leaves.</div>
								<%-- <%} else { %>
				            		<div class="viewmore">
					            		<a href="Login.action?role=2&userscreen=leaveRequest">
					            			<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Up Coming Team Leaves.."/>
					            		</a>
				            		</div> --%>
								<%} %>				
								</div>
							</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	 		</section>
	 		<section class="col-lg-4 connectedSortable">
	              <div class="box box-danger">
	                <div class="box-header with-border">
	                	<h3 class="box-title">Position</h3>
	                  <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div class="rosterweek"> 
							<div class="content1">
								<div class="holder" style="overflow-x: auto;">
									<div id="chart" class="orgChart" style="float:left;width:100%;text-align: center;"></div>
			                    </div>
		                    </div>
						</div>
						<div class="viewmore clr" style="float: right;">
			                <a href="MyProfile.action?fromPage=MyDashboard">
		           				<i class="fa fa-forward" aria-hidden="true" title="Go to My Position.."></i>
		           			</a>
		           		</div>
	                </div><!-- /.box-body -->
	              </div>
	              <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){ %>
		              <div class="box box-warning">
		                <div class="box-header with-border">
		                	<h3 class="box-title">Certificates</h3>
		                  <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                  </div>
		                </div><!-- /.box-header -->
		                
		                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
		                	<div class="KPI"> 
								<div class="content1" style="max-height: 300px;">
									<div class="holder" id="idCertificates">
									<ul class="issuereasons" style="float:left; width: 100%; margin: 9px 0px;padding-left:10px;">
									<%	List<List<String>> recentAwardedEmpList = (List<List<String>>) request.getAttribute("recentAwardedEmpList");
											for(int x=0; recentAwardedEmpList!=null && x<recentAwardedEmpList.size(); x++) {
												if(x>9) {
													break;
												}
										%>
											<li style="float:left;list-style-type: none;"><%=recentAwardedEmpList.get(x) %></li>
										<% } if(recentAwardedEmpList==null || recentAwardedEmpList.isEmpty() || recentAwardedEmpList.size()==0) { %>
											<li style="float:left;" class="nodata msg"> You have not earned any certificate till date. </li>
										<% } %>
									</ul>
									<% if(recentAwardedEmpList != null && !recentAwardedEmpList.isEmpty() && recentAwardedEmpList.size()>0) { %>
									<div class="viewmore clr" style="float: right;">
						                <a href="MyHR.action?callFrom=LPDash">
					           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to My Certificates.."/> --%>
					           				<i class="fa fa-forward" aria-hidden="true" title="Go to My Certificates.."></i>
					           			</a>
				           			</div>
				           			<div class="clr"></div>
				           			<% } %>
			                    </div>
			                </div>
						</div>
		                </div><!-- /.box-body -->
		              </div>
		           <% } %>
		           <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_CAREER_DEV_PLANNING+"")>=0){ %>
			           <% List<String> myLearnings = (List<String>) request.getAttribute("myLearnings"); %>
			           <div class="box box-danger">
		                <div class="box-header with-border">
		                	<h3 class="box-title">My Learnings</h3>
		                  <div class="box-tools pull-right">
		                  	<span class="badge bg-red"><%=myLearnings != null ? myLearnings.size() : "0" %></span>
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                  </div>
		                </div><!-- /.box-header -->
		                
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                	 <div id="profilecontainer">
								<p class=""></p>
								<div class="content1">
					                <div style="width:95%; height: 350px; float:left">
										<jsp:include page="/jsp/chart/MyLearningDonutChart.jsp" />
					                </div>
					                
					                <div class="viewmore clr" style="float: right;">
					                <a href="MyHR.action?callFrom=LPDash">
				           				<%--<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to My Learning.."/> --%>
				           				<i class="fa fa-forward" aria-hidden="true" title="Go to My Learning.."></i>
				           			</a>
				           			</div>
				                </div>
							</div>
		                </div><!-- /.box-body -->
		              </div>
	              <% } %>
	              
	               
	               <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){ %>
		              <div class="box box-default">
		                <div class="box-header with-border">
		                	<h3 class="box-title">Leave Summary</h3>
		                  <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                  </div>
		                </div><!-- /.box-header -->
		                
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                	<div class="rosterweek">
			                    <div class="content1">
				                     <div class="holder">
				                            <div style="width:95%; height: 200px;float:left">
				                            	<jsp:include page="/jsp/chart/LeaveApprovalsBarChart.jsp" />
				                    		</div>
				                          <div class="viewmore clr" style="float: right;">
				                       <!-- ===start parvez date: 06-09-2022=== -->   	
				                          	<a href="MyTime.action?callFrom=MyDashLeaveSummary">Know More..</a>
				                       <!-- ===end parvez date: 06-09-2022=== -->   	
				                          </div>
				                          <div class="clr"></div> 
				                      </div>
			                      </div>
			                 </div>
		                </div><!-- /.box-body -->
		              </div>
	              <% } %>
	              <%-- <% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_COMPENSATION_MANAGEMENT+"")>=0) { %>
		              <div class="box box-success">
		                <div class="box-header with-border">
		                	<h3 class="box-title">My Growth</h3>
		                  <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                  </div>
		                </div><!-- /.box-header -->
		                
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                	<div class="KPI"> 
									<div class="content1">
										<div class="holder">		
										 	<div style="width:95%; height: 320px;float:left">
				                            	<jsp:include page="/jsp/chart/MyGrowthLineChart.jsp" />
				                    		</div>
				                    		
				                    		<div class="viewmore clr" style="float: right;">
					                          	<a href="MyPay.action">Know More..</a>
					                        </div>
					                        <div class="clr"></div> 
					                    </div>
				                    </div>
							</div>
		                </div><!-- /.box-body -->
		              </div>
	              <% } %> --%>
	 		</section>
	 	</div>
 	</section>
 	
 	<% } else { %>
 	
 	<section class="content">
	 	<div class="row jscroll">
	 		<section class="col-lg-4 connectedSortable">
	 		
	 			<div class="box box-widget widget-user">
			            <!-- Add the bg color to the header using any of the bg-* classes -->
			            <div class="widget-user-header bg-aqua-active" style="padding-top: 10px;">
			              <h3 class="widget-user-username" style="color: #fff;font-weight: 600; margin-top: 0px;"><span><%=uF.showData((String) hmEmpProfile.get("NAME"), "-")%></span>
			              	<span style="float: right;"><a href="MyProfile.action" title="Go to My FactSheet.."><i class="fa fa-address-card-o" style="color: #fff;"></i></a></span>
			              </h3>
			              <h5 class="widget-user-desc" style="margin-bottom: 10px;"><%=uF.showData((String) hmEmpProfile.get("EMPCODE"), "-")%></h5>
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
			                    <%-- <p class="description-text"><%=uF.showData((String) hmEmpProfile.get("ORG_NAME"), "-")%></p> --%>
			                    <%if(((String) hmEmpProfile.get("SUPERVISOR_NAME"))!=null) { %>
			                   	  <span class="description-text">You report to <strong><%=uF.showData((String) hmEmpProfile.get("SUPERVISOR_NAME"), "-")%></strong> </span>
			                  	<% } else { %>
			                  	  You don't have a reporting manager.
			                  	<% } %>
			                  </div>
			                  <!-- /.description-block -->
			                </div>
			              </div>
			              <!-- /.row -->
			            </div>
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
										<%-- 
										<%if(skillrate <=0) { %>
										 	<div>Not Rated</div>
										<%} else { %> --%>
										<div id="starSkills_<%=i %>" style="float:left; margin-left: 5px;"></div>
											<script type="text/javascript">
											        	$('#starSkills_<%=i %>').raty({
											        		readOnly: true,
											        		start: <%=skillrate %>,
											        		half: true,
											        		targetType: 'number'
											        	});
								        	</script>
										<%-- <% } %> --%>
										</div>
									
								<% }
								if(i==0){
									%>
									<div class="nodata msg" style="width: 93%">
									<span>No skills aligned.</span>
									</div>
								<%} %>
								</div>
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	 		</section>
	 		<section class="col-lg-4 connectedSortable" style="padding-left: 0px;padding-right: 0px;">
	 			<%-- <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0 && CF.isClockOnOff() && blnWebClockOnOff) { %>
		 			<div class="box box-primary">	                
		                <div class="box-body" style="padding: 5px; overflow-y: auto;background-color: #2A3B3F;background-image: url(images1/clockon_bg.png);background-position: right top;background-repeat: no-repeat;">
		                	<div id="involmentcontainer" style="padding: 10px;">
								<div id="clockONOFF" style="color: #fff;"></div>
							</div>
		                </div><!-- /.box-body -->
		              </div>
	              <% } %> --%>
	              
	              <% if(strUserType != null && (strUserType.equalsIgnoreCase(IConstants.MANAGER) || strUserType.equals(IConstants.CEO) || strUserType.equals(IConstants.HOD) || strUserType.equals(IConstants.HRMANAGER) || strUserType.equals(IConstants.ADMIN))) { 			 
					if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0) { 
							String exceptionCount = (String) request.getAttribute("exceptionCount");				
							int totExceptionCount = uF.parseToInt(exceptionCount);
						%> 
	               <div class="box box-success">
			          
	                <div class="box-header with-border">
	                	<h3 class="box-title">Time Exceptions</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-green"><%=totExceptionCount %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div class="rosterweek"> 
							<div class="content1">
								<div class="holder">
									<div style="width:100%; float:left;">
									<ul class="site-stats">
							            <a href="Login.action?role=2&userscreen=teamExceptions"><li class="bg_lh" style="width: 90% !important;"><i class="fa fa-exclamation-circle"></i><strong><%=uF.parseToInt(exceptionCount) %></strong> <small>Time Exceptions</small></li></a>
							        </ul>
			                    	</div>
								</div>
							</div>
						</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% }
					} %>

	              <%
				 if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0) { 
					List<String> alLeaves = (List<String>)request.getAttribute("alLeaves");
					if(alLeaves==null)alLeaves=new ArrayList<String>();
					%>
	              <div class="box box-info">
	                <div class="box-header with-border">
	                	<h3 class="box-title">Up Coming Team Leaves</h3>
	                  <div class="box-tools pull-right">
	                  	<span class="badge bg-blue"><%=alLeaves.size() %></span>
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div id="clockcontainer">
								<div class="content1" style="max-height: 300px; overflow-y: auto;">
								<%
								for(i=0; i< alLeaves.size(); i++) {
									if(i>9) {
										break;
									}
								%>
									<div class="issues"><%= alLeaves.get(i)%></div>
									<%
								}
								if(i == 0) {
								%>		
								<div class="nodata msg">No up coming team leaves.</div>
								<%-- <%} else { %>
				            		<div class="viewmore">
					            		<a href="Login.action?role=2&userscreen=leaveRequest">
					            			<img src="<%=request.getContextPath()%>/images1/icons/icons/forward_icon.png" title="Go to Up Coming Team Leaves.."/>
					            		</a>
				            		</div> --%>
								<%} %>				
								</div>
							</div>
	                </div><!-- /.box-body -->
	              </div>
	              <% } %>
	 		</section>
	 		<section class="col-lg-4 connectedSortable">
	              <div class="box box-danger">
	                <div class="box-header with-border">
	                	<h3 class="box-title">Position</h3>
	                  <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                  </div>
	                </div><!-- /.box-header -->
	                
	                <div class="box-body" style="padding: 5px; overflow-y: auto; max-height: 250px;">
	                	<div class="rosterweek"> 
							<div class="content1">
								<div class="holder" style="overflow-x: auto;">
									<div id="chart" class="orgChart" style="float:left;width:100%;text-align: center;"></div>
			                    </div>
		                    </div>
						</div>
						<div class="viewmore clr" style="float: right;">
			                <a href="MyProfile.action?fromPage=MyDashboard">
		           				<i class="fa fa-forward" aria-hidden="true" title="Go to My Position.."></i>
		           			</a>
		           		</div>
	                </div><!-- /.box-body -->
	                
	              </div>
	              
	              
	               
	               <%if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_LEAVE_MANAGEMENT+"")>=0){ %>
		              <div class="box box-default">
		                <div class="box-header with-border">
		                	<h3 class="box-title">Leave Summary</h3>
		                  <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                  </div>
		                </div><!-- /.box-header -->
		                
		                <div class="box-body" style="padding: 5px; overflow-y: auto;">
		                	<div class="rosterweek">
			                    <div class="content1">
				                     <div class="holder">
				                            <div style="width:95%; height: 200px;float:left">
				                            	<jsp:include page="/jsp/chart/LeaveApprovalsBarChart.jsp" />
				                    		</div>
				                          <div class="viewmore clr" style="float: right;">
				                          	<a href="MyPay.action?callFrom=MyDashLeaveSummary">Know More..</a>
				                          </div>
				                          <div class="clr"></div> 
				                      </div>
			                      </div>
			                 </div>
		                </div><!-- /.box-body -->
		              </div>
	              <% } %>
	 		</section>
	 	</div>
 	</section>
 <% } %>	

<script type="text/javascript" src="scripts/organisational/jquery.jOrgChart.js"></script>
<script type="text/javascript">
$("#org").jOrgChart({
	chartElement : '#chart',
	dragAndDrop  : false
});

$(document).ready(function() {
<% if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_TIME_AND_ATTENDANCE+"")>=0 && CF.isClockOnOff() && blnWebClockOnOff) { %>
		//alert("onload");
		setClockOnOff('onload', '');
	<% } %>
});


function setClockOnOff(type, strAction) {
	var strMode = '';
	var strMode = '';
	var strApproval = '';
	var isRosterDependant = '';
	var isRosterRequired = '';
	var isSingleButtonClockOnOff = '';
	if(type == 'onclick') {
		if(document.getElementById("strMode")) {
			strMode = document.getElementById("strMode").value;
		}
		if(document.getElementById("strPrevMode")) {
			strMode = document.getElementById("strPrevMode").value;
		}
		if(document.getElementById("strApproval")) {
			strApproval = document.getElementById("strApproval").value;
		}
		if(document.getElementById("isRosterDependant")) {
			isRosterDependant = document.getElementById("isRosterDependant").value;
		}
		if(document.getElementById("isRosterRequired")) {
			isRosterRequired = document.getElementById("isRosterRequired").value;
		}
		if(document.getElementById("isSingleButtonClockOnOff")) {
			isSingleButtonClockOnOff = document.getElementById("isSingleButtonClockOnOff").value;
		}
		var id=false;
		if(strAction == "CON") { 
			id = confirm('Are you sure you want to clock on?');	
		} else if(strAction == "COFF") {
			id = confirm('Are you sure you want to clock off?');
		}
		if(id) {
			//alert("1 load");
			fadeForm('frmClockEntries1');
			getContent('clockONOFF', 'GetClockEntryMessage.action?strAction='+strAction+'&strMode='+strMode+'&strPrevMode='+strPrevMode+'&strApproval='+strApproval
				+'&isRosterDependant='+isRosterDependant+'&isRosterRequired='+isRosterRequired+'&isSingleButtonClockOnOff='+isSingleButtonClockOnOff);
		}
	} else {
		//alert("2 load");
		getContent('clockONOFF', 'GetClockEntryMessage.action?strAction='+strAction+'&strMode='+strMode+'&strApproval='+strApproval);
	}
}

//]]> 

</script>

<div id="empProfilediv"></div>
<div id="teamMembersDiv"></div>
<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Candidate Information</h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
