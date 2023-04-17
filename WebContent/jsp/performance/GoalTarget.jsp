<%@page import="java.util.ArrayList"%>
<%@page import="com.konnect.jpms.util.IMessages"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.CommonFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<style>
.desgn { padding:0px ; border:solid 1px #ccc;}
</style>

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

.emps {
text-align:center;
font-size: 26px;
color: #3F82BF;/* none repeat scroll 0 0 #3F82BF */
font-family: digital;
font-weight: bold;
}

.anaAttrib1 {
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}
</style>

<script type="text/javascript">
	jQuery(document).ready(function() {

		jQuery(".content1").hide();
		//toggle the componenet with class msg_body
		jQuery(".heading_dash").click(function() {
			jQuery(this).next(".content1").slideToggle(500);
			$(this).toggleClass("filter_close");
		});
	});
	
	
	function updatePersonalTagetRemark(targetID) {
		removeLoadingDiv('the_div');
		var dialogEdit = '#targetRemarkDiv';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : true,
					height : 250,
					width : 500,
					modal : true,
					title : 'Add Remark',
					open : function() {
						var xhr = $.ajax({
							url : "UpdateTargetRemark.action?targetID=" + targetID +"&form=GTarget",
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
	} 
	
	
	function updateIndTarget(id, val, measuretype, amount, dayHrs){
		//alert("measuretype ===> "+measuretype +" amount ===> "+amount +"dayHrs ===> "+dayHrs);
		if(confirm('Are you sure you wish to update target?')){
			var tgoalid =document.getElementById(id+"itgoalid"+val).value;
			var mtype=document.getElementById(id+"imtype"+val).value;
			var empid=document.getElementById(id+"itempid"+val).value;
			var emptarget= "";
			//alert(imtype);
			if(mtype=='Effort'){
				var mDays=document.getElementById(id+"imDays"+val).value;
				var msHrs=document.getElementById(id+"imsHrs"+val).value;
				if(mDays==''){
					mDays='0';
				}
				if(msHrs==''){
					msHrs='0';
				}
				emptarget=mDays+'.'+msHrs;
			}else{			
				emptarget=document.getElementById(id+"iemptarget"+val).value;
			}
		//alert("emptarget ===> "+emptarget);
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
        } else {
                var xhr = $.ajax({
                        url : 'UpdateTarget.action?tgoalid='+tgoalid + '&type=type&emptarget='+emptarget +'&empid=' + empid 
                        		+ '&measuretype='+measuretype + '&amount='+amount + '&dayHrs='+dayHrs + '&typeas=GoalTarget',
                        cache : false,
                        success : function(data) {
                        	//alert("Data ===> "+data.length);
                        	if(data == ""){
                        	}else if(data.length > 1){
                        		var allData = data.split("::::");
                                document.getElementById(id+"itargetStatusDiv"+val).innerHTML = allData[1];
                                document.getElementById(id+"iProBarDiv"+val).innerHTML = allData[2];
                                if(allData[1] == "Target Updated"){
                                	document.getElementById(id+"iremarkSpan"+val).style.display = "block";
                                }
                        	}
                        }
                });
        	}
		}  
	}
	
	
	function updateTarget(id, val, measuretype, amount, dayHrs){
		//alert("measuretype ===> "+measuretype +" amount ===> "+amount +"dayHrs ===> "+dayHrs);
		if(confirm('Are you sure you wish to update target?')){
			var tgoalid=document.getElementById(id+"tgoalid"+val).value;
			var mtype=document.getElementById(id+"mtype"+val).value;
			var empid=document.getElementById(id+"tempid"+val).value;
			var emptarget= "";
			if(mtype=='Effort'){
				var mDays=document.getElementById(id+"mDays"+val).value;
				var msHrs=document.getElementById(id+"msHrs"+val).value;
				if(mDays==''){
					mDays='0';
				}
				if(msHrs==''){
					msHrs='0';
				}
				emptarget = mDays+'.'+msHrs;
			}else{			
				emptarget = document.getElementById(id+"emptarget"+val).value;
			}
		//alert("emptarget ===> "+emptarget);
        xmlhttp = GetXmlHttpObject();
        if (xmlhttp == null) {
                alert("Browser does not support HTTP Request");
                return;
        } else {
                var xhr = $.ajax({
                        url : 'UpdateTarget.action?tgoalid='+tgoalid + '&type=type&emptarget='+emptarget +'&empid=' + empid 
                        		+ '&measuretype='+measuretype + '&amount='+amount + '&dayHrs='+dayHrs + '&typeas=GoalTarget',
                        cache : false,
                        success : function(data) {
                        	//alert("Data ===> "+data.length);
                        	if(data == ""){
                        	}else if(data.length > 1){
                        		var allData = data.split("::::");
                                document.getElementById(id+"targetStatusDiv"+val).innerHTML = allData[1];
                                document.getElementById(id+"ProBarDiv"+val).innerHTML = allData[2];
                                if(allData[1] == "Target Updated"){
                                	document.getElementById(id+"remarkSpan"+val).style.display = "block";
                                }
                        	}
                        }
                });
        	}
		}  
	}
	
	
 function GetXmlHttpObject() {
        if (window.XMLHttpRequest) {
                // code for IE7+, Firefox, Chrome, Opera, Safari
                return new XMLHttpRequest();
        }
        if (window.ActiveXObject) {
                // code for IE6, IE5
                return new ActiveXObject("Microsoft.XMLHTTP");
        }
        return null;
	}
	
	
	/* function updateIndTarget(id,val){
		if(confirm('Are you sure you wish to update target?')){
			var itgoalid =document.getElementById(id+"itgoalid"+val).value;
			var imtype=document.getElementById(id+"imtype"+val).value;
			var iempid=document.getElementById(id+"itempid"+val).value;
			//alert(imtype);
			if(imtype=='Effort'){
				var imDays=document.getElementById(id+"imDays"+val).value;
				var imsHrs=document.getElementById(id+"imsHrs"+val).value;
				if(imDays==''){
					imDays='0';
				}
				if(imsHrs==''){
					imsHrs='0';
				}
				var itarget=imDays+'.'+imsHrs;
				getContent(id+'itargetStatusDiv'+val, "UpdateTarget.action?tgoalid="+itgoalid+"&type=type&emptarget="+itarget+"&val="+val
						+"&empid=" + iempid + "&typeas=GoalTarget");
			}else{			
				var iemptarget=document.getElementById(id+"iemptarget"+val).value;
				getContent(id+'itargetStatusDiv'+val, "UpdateTarget.action?tgoalid="+itgoalid+"&type=type&emptarget="+iemptarget+"&val="+val
						+"&empid=" + iempid + "&typeas=GoalTarget");
			}
		}
	} */
	
	
	/* function updateTarget(id,val){
		if(confirm('Are you sure you wish to update target?')){
			var tgoalid=document.getElementById(id+"tgoalid"+val).value;
			var mtype=document.getElementById(id+"mtype"+val).value;
			var empid=document.getElementById(id+"tempid"+val).value;
			if(mtype=='Effort'){
				var mDays=document.getElementById(id+"mDays"+val).value;
				var msHrs=document.getElementById(id+"msHrs"+val).value;
				if(mDays==''){
					mDays='0';
				}
				if(msHrs==''){
					msHrs='0';
				}
				var target=mDays+'.'+msHrs;
				getContent(id+'targetStatusDiv'+val, "UpdateTarget.action?tgoalid="+tgoalid+"&type=type&emptarget="+target+"&val="+val
						+"&empid=" + empid + "&typeas=GoalTarget");
			}else{			
				var emptarget=document.getElementById(id+"emptarget"+val).value;
				getContent(id+'targetStatusDiv'+val, "UpdateTarget.action?tgoalid="+tgoalid+"&type=type&emptarget="+emptarget+"&val="+val
						+"&empid=" + empid + "&typeas=GoalTarget");
			}
		}
	} */
	
	
	function getMemberData(empId, goalid, type, assignedTarget, measureType) {
		removeLoadingDiv('the_div');
				var dialogEdit = '#memDataDiv';
				dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
				$(dialogEdit).dialog(
						{
							autoOpen : false,
							bgiframe : true,
							resizable : true,
							height : 600,
							width : 700,
							modal : true,
							title : 'Target Status',
							open : function() {
								var xhr = $.ajax({
									url : "TargetStatus.action?goalid=" + goalid + "&empid=" + empId + "&type=" + type + "&assignedTarget=" 
											+ assignedTarget + "&measureType=" + measureType,
									cache : false,
									success : function(data) {
										$(dialogEdit).html(data);
									}
								});
								xhr = null;
							},
							overlay : {
								backgroundColor : '#000',
								opacity : 0.5
							}
						});
				$(dialogEdit).dialog('open');

			} 
	
	function myTarget() { 
		removeLoadingDiv('the_div');
		var strID = '';
		if(document.getElementById("f_org")){
			strID = getSelectedValue("f_org"); 
	    }
		//alert("strID == "+strID);
		var dialogEdit = '#MyPersonalTargetid';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 700,
					width : 1100,
					modal : true,
					title : 'Individual Target',
					open : function() {
						var xhr = $.ajax({
							url : "MyGoalPopUp.action?operation=A&type=type&typeas=target&strID="+strID,
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
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

	
	function closeGoal(goalId, type) {
		//alert("openQuestionBank id "+ id)
		var pageTitle = 'Close Goal';
		if(type=='view') {
			pageTitle = 'Close Goal Reason';
		}
		removeLoadingDiv('the_div');
		 var dialogEdit = '#closeGoalDiv';
		 dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
				$(dialogEdit).dialog(
					{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 250,
					width : 360,
					modal : true,
					title : ''+pageTitle,
					open : function() {
						var xhr = $.ajax({
							url : "CloseGoalTargetKRA.action?goalId="+goalId+"&fromPage=GoalTarget",
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
			$(dialogEdit).dialog('open');
		}
	
	
	
	/* function editGoal(goalID, type, score,goalname,goalParentID) { 

		removeLoadingDiv('the_div');
		
		var dialogEdit = '#EditGoal1';
		var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
		dialogEdit = $(data1).appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 700,
					width : 1100,
					modal : true,
					title : 'Edit '+ goalname +' Goal',
					open : function() {
						var xhr = $.ajax({
							url : "EditGoalPopUp.action?goalid=" + goalID+ "&goaltype=" + type +"&score="+score +"&fromPage=GoalTarget",
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;

					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});

		$(dialogEdit).dialog('open');
	} */
	
	function editGoal(goalid) { 

		removeLoadingDiv('the_div');
		
		var dialogEdit = '#IndiTargetId';
		dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 700,
					width : 1100,
					modal : true,
					title : 'Update Individual Target',
					open : function() {
						var xhr = $.ajax({
							url : "EditMyPersonalGoalPopUp.action?operation=E&type=type&goalid="+goalid+"&typeas=target",
							cache : false,
							success : function(data) {
								$(dialogEdit).html(data);
							}
						});
						xhr = null;
					},
					overlay : {
						backgroundColor : '#000',
						opacity : 0.5
					}
				});
		$(dialogEdit).dialog('open');
	}
	
	function submitForm(){
		document.frm_GoalTarget.proPage.value = '';
		document.frm_GoalTarget.minLimit.value = '';
		document.frm_GoalTarget.submit();
	}

	function loadMore(proPage, minLimit) {
		document.frm_GoalTarget.proPage.value = proPage;
		document.frm_GoalTarget.minLimit.value = minLimit;
		document.frm_GoalTarget.submit();
	}
	
</script>
<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>
<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_level").multiselect();
});    
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Targets" name="title"/>
	
</jsp:include>
<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);		
	UtilityFunctions uF=new UtilityFunctions();
	String docRetriveLocation = (String) request.getAttribute("DOC_RETRIVE_LOCATION");
	
	String strSessionUserType=(String) session.getAttribute(IConstants.USERTYPE);
	String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
	
	String sbData = (String) request.getAttribute("sbData");
	String strSearchJob = (String) request.getAttribute("strSearchJob");
	String proCount = (String)request.getAttribute("proCount");
	
	List<String> empList = (List<String>)request.getAttribute("empList");
	Map<String, String> hmEmpCodeName = (Map<String, String>)request.getAttribute("hmEmpCodeName");
	Map<String, List<List<String>>> hmGoalTarget = (Map<String, List<List<String>>>)request.getAttribute("hmGoalTarget");
	Map<String, List<List<String>>> hmIndividualTarget = (Map<String, List<List<String>>>)request.getAttribute("hmIndividualTarget");
	Map<String, String> hmTargetValue = (Map<String, String>)request.getAttribute("hmTargetValue");
	Map<String, String> hmTargetID = (Map<String, String>)request.getAttribute("hmTargetID");
	Map<String, String> hmTargetRemark = (Map<String, String>)request.getAttribute("hmTargetRemark");
	Map<String, String> empImageMap = (Map<String, String>)request.getAttribute("empImageMap");
	Map<String, String> hmUpdateBy = (Map<String, String>)request.getAttribute("hmUpdateBy");
	Map<String, String> hmTargetAverage = (Map<String, String>) request.getAttribute("hmTargetAverage");
	
	List<String> alCheckList = (List<String>) request.getAttribute("alCheckList"); ;
	if (alCheckList == null) alCheckList = new ArrayList<String>();
	
	String strCurrency = (String) request.getAttribute("strCurrency"); 
%>

<div style="float: left; width: 100%; margin-bottom: -7px; margin-left: 10px;"> 
		<%
		String dataType = (String) request.getAttribute("dataType");
		if(dataType != null && dataType.equals("L")) { %>
			<a href="GoalTarget.action?dataType=L" class="all">Live</a>
			<a href="GoalTarget.action?dataType=C" class="close_dull">Closed</a> 
		<% } else if(dataType != null && dataType.equals("C")) { %>
			<a href="GoalTarget.action?dataType=L" class="all_dull">Live</a>
			<a href="GoalTarget.action?dataType=C" class="close">Closed</a>
		<% } %>	 
	</div>
		
<div class="leftbox reportWidth">
	<% if(strSessionUserType!=null && (strSessionUserType.equals(IConstants.HRMANAGER) || strSessionUserType.equals(IConstants.ADMIN))){ %>
	<s:form name="frm_GoalTarget" action="GoalTarget" theme="simple">
		<div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
			<p class="past heading_dash" style="text-align: left; font-size: 11px; font-weight: normal; padding-left: 35px; height: auto;">
				<%=(String)request.getAttribute("selectedFilter") %>
			</p>
			<div class="content1" style="height: 170px;">
				<s:hidden name="dataType"></s:hidden>
				<s:hidden name="proPage" id="proPage" />
    			<s:hidden name="minLimit" id="minLimit" />
				<div style="float: left; width: 100%;">
					<div style="float: left; margin-top: 10px;">
						<i class="fa fa-filter"></i>
					</div>
					
					<div style="float: left; margin-top: 8px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Organization</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName"
			                         onchange="submitForm();" 
			                         list="orgList" key=""   cssStyle="width:200px;"/>
		            </div>
		           
		            <div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Location</p>
						<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName"
									list="wLocationList" key=""  cssStyle="width:200px;" multiple="true"/>
					</div>
					<div style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
						<p style="padding-left: 5px;">Department</p>
						<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" 
									list="departmentList" key=""  cssStyle="width:200px;" multiple="true"/>
					</div>		
					<div
						style="float: left; margin-top: 10px; margin-left: 10px; width: auto;">
						<p style="padding-left: 5px;">Level</p>
						<s:select theme="simple" name="f_level" id="f_level"listKey="levelId"
							cssStyle="float:left;margin-right: 10px;width:200px;" listValue="levelCodeName" multiple="true" list="levelList" key="" />
					</div>			
					<div id="submitDIV" style="float: left; margin-top: 10px; margin-left: 10px; width: 215px;">
							<p style="padding-left: 5px;">&nbsp;</p>
							<input type="button" name="Submit" value="Submit" class="input_button" style="margin:0px" onclick="submitForm();"/>
			      	</div>
				</div>
			</div>	
		</div>	
		<div style="float:left; font-size:12px; line-height:22px; width:514px; margin-left: 350px;">
	           <span style="float:left;display:block; width:78px;">Search: </span>
	           <div style="border:solid 1px #68AC3B; margin:0px 0px 0px -26px; float:left; -moz-border-radius: 3px;	-webkit-border-radius: 3px;	border-radius: 3px;">
		            <div style="float:left">
		            	<input type="text" id="strSearchJob" name="strSearchJob" style="margin-left: 0px; border:0px solid #ccc; width:282px; box-shadow:0px 0px 0px #ccc" value="<%=uF.showData(strSearchJob,"") %>"/> 
		          	</div>
		         	 <div style="float:right">
		            	<input type="button" value="Search" class="input_search" onclick="submitForm();"/>
		            </div>
	       		</div>
	       </div>
	       <script>
			$( "#strSearchJob" ).autocomplete({
				source: [ <%=uF.showData(sbData,"") %> ]
			});
		</script>
	</s:form>
	
	<div style="float: right; ">
		<a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myTarget();" class="add_lvl" title="Add New Traget">Add New Target</a> 
	</div>
	<%} %>
<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">

<% 	int i=0;
	for(;empList!=null && !empList.isEmpty() && i<empList.size();i++){
		int targetSize=0;
		List<List<String>> goalOuterList=hmGoalTarget.get(empList.get(i));		
		if(goalOuterList!=null) {
			targetSize+=goalOuterList.size();
		}
		List<List<String>> individualOuterList=hmIndividualTarget.get(empList.get(i));
		if(individualOuterList!=null) {
			targetSize+=individualOuterList.size();
		}
		
		String empname=hmEmpCodeName.get(empList.get(i).trim());
		if(empList.get(i)!=null && strSessionEmpId!=null && empList.get(i).trim().equals(strSessionEmpId)) {
			empname="Your";
		}
		String strImage = uF.showData(empImageMap.get(empList.get(i).trim()), "avatar_photo.png");
		if(empList.get(i)!=null && strSessionEmpId!=null && empList.get(i).trim().equals(strSessionEmpId)) {
			//System.out.println("strImage ======> " + strImage);
		}
		
%>		<div class="desgn" style="margin-bottom: 5px;">
			<p class="past heading_dash" style="text-align: left; padding-left: 35px;">
				<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=docRetriveLocation +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+strImage%>" />	
					<%-- <img height="20" width="20" border="0" style="float:left;margin-right:10px; border:1px solid #000;" data-original="<%=CF.getStrDocRetriveLocation()+strImage %>" src="userImages/avatar_photo.png" class="lazy"> --%>
				<strong><%=empname %></strong>
				<span id="empStatus<%=empList.get(i) %>" style="float: right; margin-right: 1cm;">(<%=targetSize %>)</span>
			</p>
			<div class="content1">
				<ul> 
					<% 
						
						if(goalOuterList!=null && !goalOuterList.isEmpty()) {
					%>				
					<li><p style="margin: 0px 0px 0px 15px"><strong>Goal Target :</strong></p>
						<ul>
						<%for(int j=0;j<goalOuterList.size();j++) {
							List<String> innerList=goalOuterList.get(j);
							String target=hmTargetValue.get(empList.get(i)+"_"+innerList.get(1));
							String targetID=hmTargetID.get(empList.get(i)+"_"+innerList.get(1));
							String targetRemark=hmTargetRemark.get(empList.get(i)+"_"+innerList.get(1));
							String assignedTarget = "", measureType="";
							String pClass = innerList.get(14);
						%>							
								<li style="width:100%">
										<div style="margin: 0px 0px 0px 15px">
										<span class="<%=pClass %>" style="float: left;"><strong><%=j + 1%>.</strong>&nbsp;</span>
										<span class="<%=pClass %>" style="float: left; width: 95%;"><%=innerList.get(3)%></span>
											<%-- <strong><%=j + 1%>.</strong>&nbsp;<%=innerList.get(2)%> --%>		
											<%-- <span style="font-size:10px;color:#666"> - assigned by <%=innerList.get(6)%> due date <%=innerList.get(7)%> </span> --%>
											<input type="hidden" name="tgoalid" id="<%=i%>tgoalid<%=j%>" value="<%=innerList.get(1)%>" />
											<input type="hidden" name="tempid" id="<%=i%>tempid<%=j%>" value="<%=empList.get(i)%>" />
											<input type="hidden" name="mtype" id="<%=i%>mtype<%=j%>" value="<%=innerList.get(4)%>" />
											<div style="float: right; margin-right : 10%; width:35%">
												<table width="100%">
													<tr>
														<td width="40%"><strong><u>Target</u></strong></td>
														<td><strong><u>Actual</u></strong></td>
													</tr>
													<tr>
														<td nowrap="nowrap">
														<%if(innerList.get(4)!=null && innerList.get(4).equals("Effort")) {
															measureType= "effort";
															assignedTarget = innerList.get(5);
															%>
																<%=innerList.get(5) %>
															<% } else {
																measureType= "amount";
																assignedTarget = innerList.get(5);
															%>
															<%if(innerList.get(4)!=null && innerList.get(4).equals("Amount")) { %>
																<%=uF.showData(strCurrency,"")%>&nbsp;
															<% } %>	
																<%-- <%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(innerList.get(5)))%> --%>
																<%=""+uF.parseToDouble(innerList.get(5))%>
															<%if(innerList.get(4)!=null && innerList.get(4).equals("Percentage")) { %>
																&nbsp;%
															<% } %>		
															<% } %>
														</td>
														<td>
															<div id="<%=i%>spanid<%=j%>" style="float: left; text-align: center;">
													<%if(innerList.get(4)!=null && innerList.get(4).equals("Effort")){
														String t=""+uF.parseToDouble(target);
														String days="0";
														String hours="0";
														if(t.contains(".")){
															t=t.replace(".","_");
															String[] temp=t.split("_");
															days=temp[0];
															hours=temp[1];
														}
														if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)){
													%>
														<%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
														<%}else{ %>
															&nbsp;<input type="text" name="mDays" id="<%=i%>mDays<%=j%>" style="width: 20px; text-align: right;" value="<%=days %>"/>&nbsp;Days&nbsp; 
															<input type="text" name="mHrs" style="width: 20px; text-align: right;" id="<%=i%>msHrs<%=j%>" value="<%=hours %>"/>&nbsp;Hrs
													<%}
													}else{ 
														if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)){
														%>
														<%if(innerList.get(4)!=null && innerList.get(4).equals("Amount")) { %>
															<%=uF.showData(strCurrency,"")%>&nbsp;
														<% } %>
														<%=target!=null ? uF.getAmountInCrAndLksFormat(uF.parseToDouble(target)) : "0" %>
														<%if(innerList.get(4)!=null && innerList.get(4).equals("Percentage")) { %>
															&nbsp;%
														<% } %>
														<%}else{ %>
															<%if(innerList.get(4)!=null && innerList.get(4).equals("Amount")) { %>
																<%=uF.showData(strCurrency,"")%>&nbsp;
															<% } %>
															<input style="width:65px; text-align: right;" type="text" name="emptarget" id="<%=i%>emptarget<%=j%>" value="<%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %>" />
															<%if(innerList.get(4)!=null && innerList.get(4).equals("Percentage")) { %>
																&nbsp;%
															<% } %>															
													<%}
													}%>
														
														<%if(strSessionEmpId!=null && !empList.get(i).equals(strSessionEmpId)){
														%>
													<%-- <input type="button" name="approve" value="Update" class="input_button" onclick="updateTarget('<%=j%>');"/> --%>
													<a href="javascript:void(0);" onclick="updateTarget('<%=i%>','<%=j%>','<%=innerList.get(4)%>','<%=innerList.get(5)%>','<%=innerList.get(8)%>');"><img src="images1/edit.png" title="Update"></a>
													<%
													String disp = "none";
													if(targetRemark == null || targetRemark.equals("")){ 
														disp = "block";
													}
													%>
													<span id="<%=i%>remarkSpan<%=j%>" style="display: <%=disp %>; float: right; margin: 2px;">
														<a href="javascript:void(0);" onclick="getMemberData('<%=empList.get(i)%>', '<%=innerList.get(1)%>', 'remark','<%=assignedTarget %>','<%=measureType %>');"><img src="images1/pen.png" title="Add Remark"></a> <%-- updatePersonalTagetRemark('<%=targetID %>'); --%>
													</span>	
													<%} %>
													<br/><span id="<%=i%>targetStatusDiv<%=j%>"></span>	
													</div>
														</td>
													</tr>
													<%if(hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1))!=null){ %>
													<tr>
														<td colspan="2">
														<a href="javascript:void(0)" onclick="getMemberData('<%=empList.get(i)%>', '<%=innerList.get(1)%>', 'status','<%=assignedTarget %>','<%=measureType %>');" style="font-size:10px;">Last updated by <%=hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1))!=null ? hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1)) : "" %></a>
														</td>
													</tr>
													<%}else{ %>
													<tr>
														<td colspan="2">
														<a>Not updated yet.</a>
														</td>
													</tr>
													<%} %>
												</table>
												<%-- <div style="float:left;width:100%"><strong><u>Target</u></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strong><u>Actual</u></strong></div>
												<div style="float:left;width:45%">
													<%=innerList.get(4)!=null && innerList.get(4).equals("Effort")? "" : uF.showData(strCurrency,"")%>&nbsp;<%=innerList.get(5)%>
													
													<div id="<%=i%>spanid<%=j%>" style="float: right; text-align: center;">
													<%if(innerList.get(4)!=null && innerList.get(4).equals("Effort")){
														String t=""+uF.parseToDouble(target);
														String days="0";
														String hours="0";
														if(t.contains(".")){
															t=t.replace(".","_");
															String[] temp=t.split("_");
															days=temp[0];
															hours=temp[1];
														}
														if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)){
													%>
														<%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
														<%}else{ %>
															&nbsp;<input type="text" name="mDays" id="<%=i%>mDays<%=j%>" style="width: 40px;" value="<%=days %>"/>&nbsp;Days&nbsp; 
															<input type="text" name="mHrs" style="width: 40px;" id="<%=i%>msHrs<%=j%>" value="<%=hours %>"/>&nbsp;Hrs
													<%}
													}else{ 
														if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)){
														%>
														<%=target!=null ? target : "0" %>
														<%}else{ %>
															<input style="width:65px" type="text" name="emptarget" id="<%=i%>emptarget<%=j%>" value="<%=target!=null ? target : "0" %>" />															
													<%} 
													}%>
														
														<%if(strSessionEmpId!=null && !empList.get(i).equals(strSessionEmpId)){
														%>
													<input type="button" name="approve" value="Update" class="input_button" onclick="updateTarget('<%=j%>');"/>
													<a href="javascript:void(0);" onclick="updateTarget('<%=i%>','<%=j%>');">
														<img src="images1/add_level.png" title="Add">
													</a>
													<%} %>
														
													</div>
													
												</div>
												<div style="float:left;width:100%">
												<a href="javascript:void(0)" onclick="getMemberData('<%=empList.get(i)%>', '<%=innerList.get(1)%>')">Last updated by <%=hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1))!=null ? hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1)) : "" %></a>
												</div>
											</div> --%>
											
											<script type="text/javascript">
										        $(function() {
										        	$('#starPrimaryGT<%=empList.get(i)+'_'+innerList.get(1)%>').raty({
										        		readOnly: true,
										        		start: <%=hmTargetAverage.get(empList.get(i)+"_"+innerList.get(1)) != null ? uF.parseToInt(hmTargetAverage.get(empList.get(i)+"_"+innerList.get(1))) / 20 + "" : "0"%>,
										        		half: true,
										        		targetType: 'number'
															});
													});
											</script>
											<div style="float: left; background-repeat: no-repeat; background-position: right top; margin-top: 10px;">
											Rating:
											<div id="starPrimaryGT<%=empList.get(i)+"_"+innerList.get(1)%>"></div>
											</div>
											
										</div>
										<p class="<%=pClass %>" style="margin: 0px 0px 0px 14px; width: 50%; font-weight: normal; font-size: 10px;">
											(obj: <%=innerList.get(2)%>)
											<br/>
											- assigned by <%=innerList.get(6)%> due date <%=innerList.get(7)%>
											<%-- <span style="font-size:10px;color:#666"> - assigned by <%=innerList.get(6)%> due date <%=innerList.get(7)%> </span> --%>
										</p>
										<%  
											String twoDeciTotProgressAvg = "0";
											String twoDeciTot = "0";
											String total="100";
											double totalTarget=0;
											if(innerList.get(4)!=null && !innerList.get(4).equals("Effort")){
												if(uF.parseToDouble(innerList.get(5)) == 0){
													totalTarget=100;
												}else{
													totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(innerList.get(5)))*100;
												}
												twoDeciTot=""+Math.round(totalTarget);
											}else{
												
												
												String t=""+uF.parseToDouble(target);
												String days="0";
												String hours="0";
												if(t.contains(".")){
													t=t.replace(".","_");
													String[] temp=t.split("_");
													days=temp[0];
													hours=temp[1];
												}	
												String t1=""+uF.parseToDouble(innerList.get(8));
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
												
												
												/* totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(innerList.get(8)))*100;
												twoDeciTot=uF.formatIntoTwoDecimal(totalTarget); */
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
										
										<div id="<%=i %>ProBarDiv<%=j %>" style="padding:10px;min-height:40px; width: 25%;margin: 0px 0px 0px 17px;">
					
									<!-- <div style="width: 100%;"> -->
									<div class="anaAttrib1"><span style="margin-left:<%=uF.parseToDouble(twoDeciTotProgressAvg) > 97 ? uF.parseToDouble(twoDeciTotProgressAvg)-6 : uF.parseToDouble(twoDeciTotProgressAvg)-3 %>%;"><%=twoDeciTot%>%</span></div>
										<div id="outbox">
										<%if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33){ %>
										<div id="redbox" style="width: <%=twoDeciTotProgressAvg %>%;"></div>
										<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67){ %>
										<div id="yellowbox" style="width: <%=twoDeciTotProgressAvg%>%;"></div>
										<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67){ %>
										<div id="greenbox" style="width: <%=twoDeciTotProgressAvg%>%;"></div>
										<%} %>
										</div>
										<div class="anaAttrib1" style="float: left; width: 100%;"><span style="float: left; margin-left: -3%;">0%</span>
										 <%-- <span style="margin-left:64px;"><%=twoDeciTot%>%</span> --%>
										<span style="float: right; margin-right: -6%;"><%=total %>%</span></div>
										<span style="color: #808080;">Slow</span>
										<span style="margin-left:70px; color: #808080;">Steady</span>
										<span style="float: right; color: #808080;">Momentum</span>
									<!-- </div> -->
									</div>
									</div>
									</li>							
						<%} %>
						</ul>
					</li>
					<%} %>
					
					
					
					<% 
						if(individualOuterList!=null && !individualOuterList.isEmpty()) {
					%>				
					<li><p style="margin: 0px 0px 0px 15px"><strong>Individual Target :</strong></p>
						<ul>
						<%for(int j=0;j<individualOuterList.size();j++) {
							List<String> innerList=individualOuterList.get(j);
							String target=hmTargetValue.get(empList.get(i)+"_"+innerList.get(1));
							String targetID=hmTargetID.get(empList.get(i)+"_"+innerList.get(1));
							String targetRemark=hmTargetRemark.get(empList.get(i)+"_"+innerList.get(1));
							String assignedTarget = "", measureType="";
							String pClass = innerList.get(14);
							boolean checkFlag = false;
							if(alCheckList != null && alCheckList.contains(innerList.get(1))){
								checkFlag =true;
							}
						%>							
								<li style="width:100%">
										<div style="margin: 0px 0px 0px 15px">
										<span class="<%=pClass %>" style="float: left;"><strong><%=j + 1%>.</strong>&nbsp;</span>
										<span class="<%=pClass %>" style="float: left; width: 95%;"><%=innerList.get(3)%>
											<span style="float: right; margin-right: 1cm;"> 
											<a href="javascript:void(0)" class="edit_lvl" onclick="editGoal('<%=innerList.get(1)%>')" title="Edit Individual Target">Edit</a> &nbsp;
											<% if (!checkFlag){ %>
												<a href="RemoveEmpFromGoal.action?goalId=<%=innerList.get(1)%>&empId=<%=empList.get(i).trim() %>&from=Target" class="del" title="Delete Individual Target" onclick="return confirm('Are you sure you wish to delete this Target?')"> - </a>
											<%} else { %>
												<a href="javascript:void(0)" class="del" title="Delete Individual Target" onclick="alert('Employee has already updated the target. You can not delete this target.');"> - </a>
											<% } %>
											<%if(!uF.parseToBoolean(innerList.get(9))) { %>
												<a href="javascript:void(0);" onclick="closeGoal('<%=innerList.get(1)%>','close');" title="Close Goal"><i class="fa fa-times-circle-o" aria-hidden="true"></i></a>
											<% } else { %>
												<a href="javascript:void(0);" onclick="closeGoal('<%=innerList.get(1)%>','view');" title="Close Goal Reason" ><i class="fa fa-comment-o" aria-hidden="true"></i></a>
											<% } %>
											</span>
										</span>
											<input type="hidden" name="tgoalid" id="<%=i%>itgoalid<%=j%>" value="<%=innerList.get(1)%>" />
											<input type="hidden" name="tempid" id="<%=i%>itempid<%=j%>" value="<%=empList.get(i)%>" />
											<input type="hidden" name="mtype" id="<%=i%>imtype<%=j%>" value="<%=innerList.get(4)%>" />
											<div style="float: right; margin-right : 10%; width:35%">
												<table width="100%">
													<tr>
														<td width="40%"><strong><u>Target</u></strong></td>
														<td><strong><u>Actual</u></strong></td>
													</tr>
													<tr>
														<td nowrap="nowrap">
															<%if(innerList.get(4)!=null && innerList.get(4).equals("Effort")) {
																measureType = "effort";
																assignedTarget = innerList.get(5);
															%>
																<%=innerList.get(5) %>
															<%} else {
																measureType = "amount";
																assignedTarget = innerList.get(5);
															%>
															<%if(innerList.get(4)!=null && innerList.get(4).equals("Amount")) { %>
																<%=uF.showData(strCurrency,"")%>&nbsp;
															<% } %>
																<%=uF.getAmountInCrAndLksFormat(uF.parseToDouble(innerList.get(5)))%>
																<%-- <%=""+uF.parseToDouble(innerList.get(5))%> --%>
															<%if(innerList.get(4)!=null && innerList.get(4).equals("Percentage")) { %>
																&nbsp;%
															<% } %>	
															<% } %>
														</td>
														<td>
															<div id="<%=i%>spanid<%=j%>" style="float: left; text-align: center;">
													<%if(innerList.get(4)!=null && innerList.get(4).equals("Effort")) {
														String t=""+uF.parseToDouble(target);
														String days="0";
														String hours="0";
														if(t.contains(".")){
															t=t.replace(".","_");
															String[] temp=t.split("_");
															days=temp[0];
															hours=temp[1];
														}
														if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)) {
													%>
														<%=days %>&nbsp;Days&nbsp;<%=hours %>&nbsp;Hrs
														<% } else { %>
															&nbsp;<input type="text" name="mDays" id="<%=i%>imDays<%=j%>" style="width: 20px; text-align: right;" value="<%=days %>"/>&nbsp;Days&nbsp; 
															<input type="text" name="mHrs" style="width: 20px; text-align: right;" id="<%=i%>imsHrs<%=j%>" value="<%=hours %>"/>&nbsp;Hrs
													<% }
													} else { 
														if(strSessionEmpId!=null && empList.get(i).equals(strSessionEmpId)) {
														%>
														<%if(innerList.get(4)!=null && innerList.get(4).equals("Amount")) { %>
															<%=uF.showData(strCurrency,"")%>&nbsp;
														<% } %>
														<%=target!=null ? uF.getAmountInCrAndLksFormat(uF.parseToDouble(target)) : "0" %>
														<%if(innerList.get(4)!=null && innerList.get(4).equals("Percentage")) { %>
															&nbsp;%
														<% } %>	
														<%} else { %>
														<%if(innerList.get(4)!=null && innerList.get(4).equals("Amount")) { %>
															<%=uF.showData(strCurrency,"")%>&nbsp;
														<% } %>
															<input style="width:65px; text-align: right;" type="text" name="emptarget" id="<%=i%>iemptarget<%=j%>" value="<%=target!=null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(target)) : "0" %>" />
														<%if(innerList.get(4)!=null && innerList.get(4).equals("Percentage")) { %>
															&nbsp;%
														<% } %>																
													<% }
													} %>
														
														<%if(strSessionEmpId!=null && !empList.get(i).equals(strSessionEmpId)) {
														%>
													<a href="javascript:void(0);" onclick="updateIndTarget('<%=i%>','<%=j%>','<%=innerList.get(4)%>','<%=innerList.get(5)%>','<%=innerList.get(8)%>');"><img src="images1/edit.png" title="Update"></a>
													<%
													String disp = "none";
													if(targetRemark == null || targetRemark.equals("")) { 
														disp = "block";
													}
													%>
													<span id="<%=i%>iremarkSpan<%=j%>" style="display: <%=disp %>; float: right; margin: 2px;">
														<a href="javascript:void(0);" onclick="getMemberData('<%=empList.get(i)%>', '<%=innerList.get(1)%>', 'remark', '<%=assignedTarget %>','<%=measureType %>');"><img src="images1/pen.png" title="Add Remark"></a> <%--  "updatePersonalTagetRemark('<%=targetID %>');" --%>
													</span>	
													<%} %>
													<br/><span id="<%=i%>itargetStatusDiv<%=j%>"></span>
													</div>
														</td>
													</tr>
													<%if(hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1))!=null) { %>
													<tr>
														<td colspan="2">
														<a href="javascript:void(0)" onclick="getMemberData('<%=empList.get(i)%>', '<%=innerList.get(1)%>','status', '<%=assignedTarget %>','<%=measureType %>');" style="font-size:10px;">Last updated by <%=hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1))!=null ? hmUpdateBy.get(empList.get(i)+"_"+innerList.get(1)) : "" %></a>
														</td>
													</tr>
													<%} else { %>
													<tr>
														<td colspan="2">
														<a>Not updated yet.</a>
														</td>
													</tr>
													<% } %>
													
												</table>
												
												<script type="text/javascript">
										        $(function() {
										        	$('#starPrimaryIT<%=empList.get(i)+'_'+innerList.get(1)%>').raty({
										        		readOnly: true,
										        		start: <%=hmTargetAverage.get(empList.get(i)+"_"+innerList.get(1)) != null ? uF.parseToInt(hmTargetAverage.get(empList.get(i)+"_"+innerList.get(1))) / 20 + "" : "0"%>,
										        		half: true,
										        		targetType: 'number'
															});
													});
											</script>
											<div style="float: left; background-repeat: no-repeat; background-position: right top; margin-top: 10px;">
											Rating:
											<div id="starPrimaryIT<%=empList.get(i)+"_"+innerList.get(1)%>"></div>
											</div>
											
										</div>
										<p class="<%=pClass %>" style="margin: 0px 0px 0px 14px; width: 50%; font-weight: normal; font-size: 10px;">
											(obj: <%=innerList.get(2)%>)
											<br/>
											- assigned by <%=innerList.get(6)%> due date <%=innerList.get(7)%> 
											<%-- <span style="font-size:10px;color:#666"> - assigned by <%=innerList.get(6)%> due date <%=innerList.get(7)%> </span> --%>
										</p>
										<%  
											String twoDeciTotProgressAvg = "0";
											String twoDeciTot = "0";
											String total="100";
											double totalTarget=0;
											if(innerList.get(4)!=null && !innerList.get(4).equals("Effort")) {
												//System.out.println("uF.parseToInt(innerList.get(5)) ===> "+uF.parseToDouble(innerList.get(5)));
												if(uF.parseToDouble(innerList.get(5)) == 0) {
													totalTarget=100;
												} else {
													totalTarget = (uF.parseToDouble(target)/uF.parseToDouble(innerList.get(5)))*100;
												}
												twoDeciTot = ""+Math.round(totalTarget);
											} else {
												
												String t=""+uF.parseToDouble(target);
												String days="0";
												String hours="0";
												if(t.contains(".")) {
													t=t.replace(".","_");
													String[] temp=t.split("_");
													days=temp[0];
													hours=temp[1];
												}	
												String t1=""+uF.parseToDouble(innerList.get(8));
												String targetDays = "0";
												String targetHrs = "0";
												if(t1.contains(".")) {
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
												
												
												/* totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(innerList.get(8)))*100;
												twoDeciTot=uF.formatIntoTwoDecimal(totalTarget); */
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
										
										<div id="<%=i %>iProBarDiv<%=j %>" style="padding:10px;min-height:40px; width: 25%;margin: 0px 0px 0px 17px;">
					
									<!-- <div style="width: 100%;"> -->
									<div class="anaAttrib1"><span style="margin-left:<%=uF.parseToDouble(twoDeciTotProgressAvg) > 97 ? uF.parseToDouble(twoDeciTotProgressAvg)-6 : uF.parseToDouble(twoDeciTotProgressAvg)-3 %>%;"><%=twoDeciTot%>%</span></div>
										<div id="outbox">
										<%if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33){ %>
										<div id="redbox" style="width: <%=twoDeciTotProgressAvg %>%;"></div>
										<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67){ %>
										<div id="yellowbox" style="width: <%=twoDeciTotProgressAvg%>%;"></div>
										<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67){ %>
										<div id="greenbox" style="width: <%=twoDeciTotProgressAvg%>%;"></div>
										<%} %>
										</div>
										<div class="anaAttrib1" style="float: left; width: 100%;"><span style="float: left; margin-left: -3%;">0%</span>
										<%-- <span style="margin-left:64px;"><%=twoDeciTot%>%</span> --%>
										<span style="float: right; margin-right: -6%;"><%=total %>%</span></div>
										<span style="color: #808080;">Slow</span>
										<span style="margin-left:70px; color: #808080;">Steady</span>
										<span style="float: right; color: #808080;">Momentum</span>
										
									<!-- </div> -->
									</div>
									</div>
									</li>							
						<% } %>
						</ul>
					</li>
					<% } %>
				</ul>
				<%if((goalOuterList == null || goalOuterList.isEmpty()) &&(individualOuterList == null || individualOuterList.isEmpty())){ %>
					<div class="nodata" style="border-radius: 4px 4px 4px 4px; padding: 10px; width: 97%; margin: 5px 5px 5px 5px">No Target assigned to <%=empname %>.</div>
					<%} %>
			</div>
		</div>
	<%} %>
	
	<%if(i==0) { %>
		<div class="nodata msg">No Target assigned.</div>
	<%} else { %>
			<div style="text-align: center; float: left; width: 100%;">
		
			<% int intproCnt = uF.parseToInt(proCount);
				int pageCnt = 0;
				int minLimit = 0;
				i=1;
				for(; i<=intproCnt; i++) {
						minLimit = pageCnt * 10;
						pageCnt++;
			%>
			<% if(i ==1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				if(uF.parseToInt(strPgCnt) > 1) {
					 strPgCnt = (uF.parseToInt(strPgCnt)-1) + "";
					 strMinLimit = (uF.parseToInt(strMinLimit)-10) + "";
				}
				if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) {
					strPgCnt = "1";
				}
			%>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) > 1) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');">
					<%="< Prev" %></a>
				<% } else { %>
					<b><%="< Prev" %></b>
				<% } %>
				</span>
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))-3) > 1) { %>
					<b>...</b>
				<% } %>
			
			<% } %>
			
			<% if(i > 1 && i < intproCnt) { %>
			<% if(pageCnt >= (uF.parseToInt((String)request.getAttribute("proPage"))-2) && pageCnt <= (uF.parseToInt((String)request.getAttribute("proPage"))+2)) { %>
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(((String)request.getAttribute("proPage") == null && pageCnt == 1) || uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
			<% } %>
			<% } %>
			
			<% if(i == intproCnt && intproCnt > 1) {
				String strPgCnt = (String)request.getAttribute("proPage");
				String strMinLimit = (String)request.getAttribute("minLimit");
				 strPgCnt = (uF.parseToInt(strPgCnt)+1) + "";
				 strMinLimit = (uF.parseToInt(strMinLimit)+10) + "";
				 if(strMinLimit == null) {
					strMinLimit = "0";
				}
				if(strPgCnt == null) { 
					strPgCnt = "1";
				}
				%>
				<% if((uF.parseToInt((String)request.getAttribute("proPage"))+3) < intproCnt) { %>
					<b>...</b>
				<% } %>
			
				<span><a href="javascript:void(0);" onclick="loadMore('<%=pageCnt %>','<%=minLimit %>');"
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) == pageCnt) { %>
				style="color: black;"
				<% } %>
				><%=pageCnt %></a></span>
				<span style="color: lightgray;">
				<% if(uF.parseToInt((String)request.getAttribute("proPage")) < pageCnt) { %>
					<a href="javascript:void(0);" onclick="loadMore('<%=strPgCnt %>','<%=strMinLimit %>');"><%="Next >" %></a>
				<% } else { %>
					<b><%="Next >" %></b>
				<% } %>
				</span>
			<% } %>
			<%} %>
			
			</div>
		<%} %>
	</div>
	
</div>

<div id="memDataDiv"></div>
<div id="MyPersonalTargetid"></div>
<div id="closeGoalDiv"></div>
<div id="IndiTargetId"></div>

<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>
