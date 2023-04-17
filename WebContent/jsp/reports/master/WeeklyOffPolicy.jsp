<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript">
$(function() {
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

function getData(type){
	
	var org='';
	var location='';
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	if(type=='2'){
		org=document.getElementById("strOrg").value;
		location=document.getElementById("strLocation").value;
	}else{
		org=document.getElementById("strOrg").value;
		
	}
	
	window.location='MyDashboard.action?strOrg='+org+'&strLocation='+location+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
}
 
function editWeeklyOff(strOfficeId, userscreen, navigationId, toPage) {

	var org = document.getElementById("strOrg").value;
	var location = document.getElementById("strLocation").value;
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Weekly Off');
	$.ajax({
		url : 'AddWeeklyOff.action?operation=E&ID='+strOfficeId+'&strOrg='+org+'&strLocation='+location+'&userscreen='+userscreen
				+'&navigationId='+navigationId+'&toPage='+toPage, 
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

</script>

 
<%
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	Map<String, Map<String, String>> hmWorkLocation = (Map<String, Map<String, String>>) request.getAttribute("hmWorkLocation");
	if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String, String>>();

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
%>

	<div class="box-body">
			
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm_RosterPolicyReport" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select theme="simple" list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData('1');"/>
								<% } else { %>
									<s:select theme="simple" list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="getData('1');"/>
								<% } %>
							</div>
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="strLocation" id="strLocation" listKey="wLocationId" listValue="wLocationName" list="workList" onchange="getData('2');"/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>
	
	
	<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
         <ul class="level_list">
		<% 
			Iterator<String> it = hmWorkLocation.keySet().iterator();
			while(it.hasNext()) {
				String strWLocationId = (String)it.next();
				Map<String, String> hmWLocation = (Map<String, String>)hmWorkLocation.get(strWLocationId);
			%>
			<li> 
				<strong><%=uF.showData(hmWLocation.get("WL_NAME"),"") %> </strong>
				<ul>
					<li>
	                    <a href="javascript:void(0)" style="float: left; margin-right: 7px;" onclick="editWeeklyOff('<%=hmWLocation.get("WL_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Weekly OFF"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
	                    <% int cnt=0;
	                    	if(hmWLocation.get("WL_WEEKLY_OFF_1")!=null && !hmWLocation.get("WL_WEEKLY_OFF_1").equals("")){
								cnt++;
	                    %>
		                    <span>
			                     <%=cnt %>) Weekly Off: <strong><%=uF.showData(hmWLocation.get("WL_WEEKLY_OFF_TYPE_1"),"") %> </strong>&nbsp;&nbsp;&nbsp;
			                     Day: <strong><%=uF.showData(hmWLocation.get("WL_WEEKLY_OFF_1"),"") %> </strong>&nbsp;&nbsp;&nbsp;
			                     Weeks: <strong><%=uF.showData(hmWLocation.get("WL_WEEK_No_1"),"") %> </strong>&nbsp;&nbsp;&nbsp;
		                    </span> 
		                    <%}
		                    	if(hmWLocation.get("WL_WEEKLY_OFF_2")!=null && !hmWLocation.get("WL_WEEKLY_OFF_2").equals("")){
									cnt++;
		                    %>
		                    <span style="padding-left: 20px;">
			                     <%=cnt %>) Weekly Off: <strong><%=uF.showData(hmWLocation.get("WL_WEEKLY_OFF_TYPE_2"),"") %> </strong>&nbsp;&nbsp;&nbsp;
			                     Day: <strong><%=uF.showData(hmWLocation.get("WL_WEEKLY_OFF_2"),"") %> </strong>&nbsp;&nbsp;&nbsp;
			                     Weeks: <strong><%=uF.showData(hmWLocation.get("WL_WEEK_No_2"),"") %> </strong>&nbsp;&nbsp;&nbsp;
		                    </span> 
		                    <%}
		                    	if(hmWLocation.get("WL_WEEKLY_OFF_3")!=null && !hmWLocation.get("WL_WEEKLY_OFF_3").equals("")){
									cnt++;
		                    %>
		                    <span style="padding-left: 42px;">
			                     <%=cnt %>) Weekly Off: <strong><%=uF.showData(hmWLocation.get("WL_WEEKLY_OFF_TYPE_3"),"") %> </strong>&nbsp;&nbsp;&nbsp;
			                     Day: <strong><%=uF.showData(hmWLocation.get("WL_WEEKLY_OFF_3"),"") %> </strong>&nbsp;&nbsp;&nbsp;
			                     Weeks: <strong><%=uF.showData(hmWLocation.get("WL_WEEK_No_3"),"") %> </strong>&nbsp;&nbsp;&nbsp;
		                    </span> 
		                    <%} %>
					</li>
				</ul>
			</li>	
		<% } %>
		</ul>
     </div>	
</div>


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