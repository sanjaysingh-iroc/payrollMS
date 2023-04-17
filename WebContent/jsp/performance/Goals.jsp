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
	
	
	function goalChart(corpGoalID, managerGoalID, teamGoalID, goalID) {
		//alert("corpGoalID ===> "+corpGoalID+" managerGoalID ===> "+managerGoalID+" teamGoalID ===> "+teamGoalID+" goalID ===> "+goalID);
		removeLoadingDiv('the_div');
		
		var dialogEdit = '#chartGoal';
		var data1 = "<div id=\"the_div\"><div id=\"ajaxLoadImage\"></div></div>";
		dialogEdit = $(data1).appendTo('body'); 
		$(dialogEdit).dialog(
				{
					autoOpen : false,
					bgiframe : true,
					resizable : false,
					height : 600,
					width : 900,
					modal : true,
					title : 'Goal Chart',
					open : function() {
						var xhr = $.ajax({
							url : "GoalChartIndividualGoal.action?corpGoalID=" + corpGoalID + "&managerGoalID=" + managerGoalID +"&teamGoalID=" + teamGoalID 
							+"&goalID=" + goalID,
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
	
	
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Goals" name="title"/>
	
</jsp:include>
<%
	CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);		
	UtilityFunctions uF=new UtilityFunctions();
	
	String strSessionUserType=(String) session.getAttribute(IConstants.USERTYPE);
	String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
	
	List<String> empList=(List<String>)request.getAttribute("empList");
	Map<String, String> hmEmpCodeName =(Map<String, String>)request.getAttribute("hmEmpCodeName");
	Map<String, List<List<String>>> hmTeamGoal=(Map<String, List<List<String>>>)request.getAttribute("hmTeamGoal");
	Map<String, List<List<String>>> hmPersonalGoal=(Map<String, List<List<String>>>)request.getAttribute("hmPersonalGoal");
	Map<String, String> empImageMap=(Map<String, String>)request.getAttribute("empImageMap");
	Map<String, Map<String, String>> hmEmpGoalRating = (Map<String, Map<String, String>>) request.getAttribute("hmEmpGoalRating");
	
%>

<div style="float: left; width: 100%; margin-bottom: -7px; margin-left: 10px;">
		<%
		String dataType = (String) request.getAttribute("dataType");
		if(dataType != null && dataType.equals("L")) { %>
			<a href="Goals.action?dataType=L" class="all">Live</a>
			<a href="Goals.action?dataType=C" class="close_dull">Closed</a> 
		
		<% } else if(dataType != null && dataType.equals("C")) { %>
			<a href="Goals.action?dataType=L" class="all_dull">Live</a>
			<a href="Goals.action?dataType=C" class="close">Closed</a>
		<% } %>	 
	</div>

<div class="leftbox reportWidth">
	<% if(strSessionUserType!=null && (strSessionUserType.equals(IConstants.HRMANAGER) || strSessionUserType.equals(IConstants.ADMIN))){ %>
		<s:form name="frm_Search" action="Goals" theme="simple">
			<s:hidden name="dataType"></s:hidden>
			<div class="filter_div">
				<div class="filter_caption">Filter</div>
				<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;"
	                         listValue="orgName" onchange="window.location='GoalTarget.action?f_org='+this.value"  	
	                         list="organisationList" key="" value="strOrg"/>
	                         <!-- headerKey="" headerValue="All Organisations" -->
				<s:select theme="simple" name="f_Location" listKey="wLocationId"
	                         listValue="wLocationName" headerKey="" headerValue="All Locations"
	                         onchange="document.frm_Search.submit();" 		
	                         list="wLocationList" key=""/>
	                    
	            <s:select theme="simple" name="f_department" list="departmentList" listKey="deptId" 
	             			listValue="deptName" headerKey="" headerValue="All Departments" 
	             			onchange="document.frm_Search.submit();"/>
	             			
	            <s:select theme="simple" name="f_level" listKey="levelId" headerValue="All Levels"
		                     listValue="levelCodeName" headerKey="" 
		                     list="levelList" key="" onchange="document.frm_Search.submit();"/>
		                     
		        <s:select theme="simple" name="f_desig" list="desigList"
	                         listKey="desigId" id="desigIdV" listValue="desigCodeName"
	                         headerKey="" headerValue="All Designation" onchange="document.frm_Search.submit();"/>
			</div>
		</s:form>
	
	<!-- <div style="float: right; ">
		<a style="height: 1px; width: 2px;" href="javascript:void(0)" onclick="myTarget();" class="add_lvl" title="Add New Traget">Add New Target</a> 
	</div> -->
	<%} %>
<div style="margin: 10px 0px 0px 0px; float: left; width: 100%">

<% 	int i=0;
	for(;empList!=null && !empList.isEmpty() && i<empList.size();i++) {
		Map<String, String> hmGoalAverage = hmEmpGoalRating.get(empList.get(i));
		int targetSize=0;
		List<List<String>> goalOuterList=hmTeamGoal.get(empList.get(i));		
		if(goalOuterList!=null){
			targetSize+=goalOuterList.size();
		}
		List<List<String>> personalOuterList=hmPersonalGoal.get(empList.get(i));
		if(personalOuterList!=null){
			targetSize+=personalOuterList.size();
		}
		
		String empname=hmEmpCodeName.get(empList.get(i).trim());
		if(empList.get(i)!=null && strSessionEmpId!=null && empList.get(i).trim().equals(strSessionEmpId)){
			empname="Your";
		}
		String strImage =empImageMap.get(empList.get(i).trim());
		
%>		<div class="desgn" style="margin-bottom: 5px;">
			<p class="past heading_dash" style="text-align: left; padding-left: 35px;">
				<%-- <img height="20" width="20" border="0" style="float:left;margin-right:10px; border:1px solid #000;" data-original="<%=CF.getStrDocRetriveLocation()+strImage %>" src="userImages/avatar_photo.png" class="lazy"> --%>
				<img height="20" width="20" class="lazy img-circle" src="userImages/avatar_photo.png" data-original="<%=CF.getStrDocRetriveLocation() +IConstants.I_PEOPLE+"/"+IConstants.I_IMAGE+"/"+empList.get(i).trim()+"/"+IConstants.I_22x22+"/"+strImage%>" />
				<strong><%=empname %></strong>
				<span id="empStatus<%=empList.get(i) %>" style="float: right; margin-right: 1cm;">(<%=targetSize %>)</span>
			</p>
			<div class="content1">
				<ul> 
					<% 
						if(goalOuterList!=null && !goalOuterList.isEmpty()){
					%>				
					<li><p style="margin: 0px 0px 0px 15px"><strong>Team Goal:</strong></p>
						<ul>
						<%for(int j=0;j<goalOuterList.size();j++){
							List<String> innerList=goalOuterList.get(j);
						%>							
								<li style="width:100%">
										<div style="margin: 0px 0px 0px 15px">
										<span style="float: left;"><strong><%=j + 1%>.</strong>&nbsp;</span>
										<span style="float: left; width: 95%;"><%=innerList.get(2)%>&nbsp;&nbsp;&nbsp;
										<a href="javascript:void(0)" onclick="goalChart('<%=innerList.get(9)%>','<%=innerList.get(8)%>','<%=innerList.get(7)%>','<%=innerList.get(1)%>')" title="Goal Chart"><i class="fa fa-sitemap" aria-hidden="true"></i></a>
										</span>
											<%-- <strong><%=j + 1%>.</strong>&nbsp;<%=innerList.get(2)%> --%>		
											<%-- <span style="font-size:10px;color:#666"> - assigned by <%=innerList.get(6)%> due date <%=innerList.get(7)%> </span> --%>
											<input type="hidden" name="tgoalid" id="<%=i%>tgoalid<%=j%>" value="<%=innerList.get(1)%>" />
											<input type="hidden" name="tempid" id="<%=i%>tempid<%=j%>" value="<%=empList.get(i)%>" />
											<input type="hidden" name="mtype" id="<%=i%>mtype<%=j%>" value="<%=innerList.get(4)%>" />
			<script type="text/javascript">
		        $(function() {
		        	$('#starPrimaryTG<%=empList.get(i)%>_<%=innerList.get(1)%>').raty({
		        		readOnly: true,
		        		start: <%=hmGoalAverage.get(innerList.get(1)) != null ? uF.parseToInt(hmGoalAverage.get(innerList.get(1))) / 20 + "" : "0"%>,
		        		half: true,
		        		targetType: 'number'
							});
					});
		</script>
							<div style="float: right; margin-right : 10%; width:35%">
							<%-- <% if(hmGoalAverage.get(innerList.get(1)) == null) { %>
							Not Rated.
							<% } else { %> --%>
							<div id="starPrimaryTG<%=empList.get(i)%>_<%=innerList.get(1)%>"></div>
							<%-- <% } %> --%>
							</div>
											
											<%-- <div style="float: right; margin-right : 10%; width:35%">
												<table width="100%">
													<tr>
														<td width="40%"><strong><u>Target</u></strong></td>
														<td><strong><u>Actual</u></strong></td>
													</tr>
												</table>
										</div> --%>
										
										<p style="margin: 0px 0px 0px 14px; width: 50%; font-weight: normal; font-size: 10px;">
											(from <%=innerList.get(3)%> )
											<br/>
											<span style="font-size:10px;color:#666"> - assigned by <%=innerList.get(5)%> due date <%=innerList.get(6)%> </span>
										</p>
									</div>
									</li>							
						<%} %>
						</ul>
					</li>
					<%} %>
					
					
					
					<% 
						if(personalOuterList!=null && !personalOuterList.isEmpty()){
					%>				
					<li><p style="margin: 0px 0px 0px 15px"><strong>Personal Goals:</strong></p>
						<ul>
						<%for(int j=0;j<personalOuterList.size();j++) {
							List<String> innerList=personalOuterList.get(j);
						%>							
								<li style="width:100%">
									<div style="margin: 0px 0px 0px 15px">
									<span style="float: left;"><strong><%=j + 1%>.</strong>&nbsp;</span>
									<span style="float: left; width: 95%;"><%=innerList.get(2)%></span>
										<%-- <strong><%=j + 1%>.</strong>&nbsp;<%=innerList.get(2)%> --%>
										<%-- <span style="font-size:10px;color:#666"> - assigned by <%=innerList.get(6)%> due date <%=innerList.get(7)%> </span> --%>
										<input type="hidden" name="tgoalid" id="<%=i%>itgoalid<%=j%>" value="<%=innerList.get(1)%>" />
										<input type="hidden" name="tempid" id="<%=i%>itempid<%=j%>" value="<%=empList.get(i)%>" />
										<input type="hidden" name="mtype" id="<%=i%>imtype<%=j%>" value="<%=innerList.get(4)%>" />
			<script type="text/javascript">
		        $(function() {
		        	$('#starPrimaryPG<%=empList.get(i)%>_<%=innerList.get(1)%>').raty({
		        		readOnly: true,
		        		start: <%=hmGoalAverage.get(innerList.get(1)) != null ? uF.parseToInt(hmGoalAverage.get(innerList.get(1))) / 20 + "" : "0"%>,
		        		half: true,
		        		targetType: 'number'
							});
					});
		</script>
							<div style="float: right; margin-right : 10%; width:35%">
							<%-- <% if(hmGoalAverage.get(innerList.get(1)) == null) { %>
							Not Rated.
							<% } else { %> --%>
							<div id="starPrimaryPG<%=empList.get(i)%>_<%=innerList.get(1)%>"></div>
							<%-- <% } %> --%>
							</div>
										
										<%-- <div style="float: right; margin-right : 10%; width:35%">
											<table width="100%">
												<tr>
													<td width="40%"><strong><u>Target</u></strong></td>
													<td><strong><u>Actual</u></strong></td>
												</tr>
											</table>
									</div> --%>
									
									<p style="margin: 0px 0px 0px 14px; width: 50%; font-weight: normal; font-size: 10px;">
										(from <%=innerList.get(3)%>)
										<br/>
										<span style="font-size:10px;color:#666"> - assigned by <%=innerList.get(5)%> due date <%=innerList.get(6)%> </span>
									</p>
									</div>
								</li>							
						<% } %>
						</ul>
					</li>
					<% } %>
				</ul>
				<%if((goalOuterList == null || goalOuterList.isEmpty()) &&(personalOuterList == null || personalOuterList.isEmpty())){ %>
					<div class="nodata" style="border-radius: 4px 4px 4px 4px; padding: 10px; width: 97%; margin: 5px 5px 5px 5px">No Goal assigned to <%=empname %>.</div>
					<% } %>
			</div>
		</div>
	<%} %>
	
	<%if(i==0) { %>
	<div class="nodata msg">No Goal assigned.</div>
	<%} %>
	</div>
	
</div>

<div id="memDataDiv"></div>
<div id="MyPersonalTargetid"></div>
<div id="chartGoal"></div>

<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>
