<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%> 

<% 
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	String strTitle = (String)request.getAttribute(IConstants.TITLE);
	
	String strOrg = (String)request.getAttribute("strOrg");
	String strLevel = (String)request.getAttribute("strLevel");
	String strSalaryHeadId = (String)request.getAttribute("strSalaryHeadId");
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
%> 
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

function submitForm(type) {
	var org='';
	var strLevel='';
	var strSalaryHeadId='';
	var level = document.getElementById("strLevel").value;
	var userscreen = document.getElementById("userscreen").value;
	var navigationId = document.getElementById("navigationId").value;
	var toPage = document.getElementById("toPage").value;
	if(type=='3') {
		org=document.getElementById("strOrg").value;
		strLevel = document.getElementById("strLevel").value;
		strSalaryHeadId = document.getElementById("strSalaryHeadId").value;
	} else if(type=='2') {
		org=document.getElementById("strOrg").value;
		strLevel = document.getElementById("strLevel").value;
	} else {
		org=document.getElementById("strOrg").value;		
	}
	
	window.location='MyDashboard.action?strOrg='+org+'&strLevel='+strLevel+'&strSalaryHeadId='+strSalaryHeadId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
	
}


function addNewCondition(strOrg, strLevel, strSalaryHeadId, userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('New Allowance Condition');
	$.ajax({
		url : 'AddAllowanceCondition.action?strOrg='+strOrg+'&strLevel='+strLevel+'&strSalaryHeadId='+strSalaryHeadId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editCondition(conditionId,strOrg, strLevel, strSalaryHeadId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Allowance Condition'); 
	$.ajax({
		url : 'AddAllowanceCondition.action?operation=E&ID='+conditionId+'&strOrg='+strOrg+'&strLevel='+strLevel+'&strSalaryHeadId='+strSalaryHeadId
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addPaymentLogic(strOrg, strLevel, strSalaryHeadId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('New Allowance Payment Logic'); 
	$.ajax({
		url : 'AddAllowancePaymentLogic.action?strOrg='+strOrg+'&strLevel='+strLevel+'&strSalaryHeadId='+strSalaryHeadId
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editPaymentLogic(paymentLogicId,strOrg, strLevel, strSalaryHeadId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Allowance Payment Logic'); 
	$.ajax({
		url : 'AddAllowancePaymentLogic.action?operation=E&ID='+paymentLogicId+'&strOrg='+strOrg+'&strLevel='+strLevel+'&strSalaryHeadId='+strSalaryHeadId
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function getPublishConditionORLogic(id, type, isPublish) {
	if(type == 'C') {
		getContent('publishUnpublishCSpan_'+id, 'AllowancePolicy.action?condiOrLogicId='+id+'&type='+type+'&operation=PUP&isPublish='+isPublish);
	} else if(type == 'PL') {
		getContent('publishUnpublishPLSpan_'+id, 'AllowancePolicy.action?condiOrLogicId='+id+'&type='+type+'&operation=PUP&isPublish='+isPublish);
	}
}

</script>
                        


	<div class="box-body">
	
		<div class="col-md-12" style="padding: 7px 0px;">
		<p style="background-color: #FFFF33; color: #777777; padding: 4px; border: 1px solid #cccccc;">
			<strong>Note:</strong>The system allows you to add Allowance based on the &#8216;Condition&#8217; and the &#8216;Payment Logic&#8217;, required to create an 
			Allowance Policy. Only once you Publish a Payment Logic, does an Allowance gets launched. The Allowance Policy can then be approved 
			from &#8216;Allowance&#8217; under the &#8216;Approve- pay&#8217; navigation, every time you would want to Pay-out. <br/><br/>
			Please note that the allowance&#39;s are paid as a part of the salary.
		</p>
	</div>  
	
			
		<div class="box box-default collapsed-box" style="clear: both;">
			<div class="box-header with-border">
			    <h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
			    <div class="box-tools pull-right">
			        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
			        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			    </div>
			</div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />

					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm('1');"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="submitForm('1');"></s:select>
								<% } %>
							</div>
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 12px;">Level</p>
								<s:select theme="simple" name="strLevel" id="strLevel" listKey="levelId" cssStyle="margin-left:10px" listValue="levelCodeName" 
									headerKey="" headerValue="Choose Level" onchange="submitForm('2');" list="levelList" key="" required="true" />
							</div>
							
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 12px;">Salary Head</p>
								<s:select theme="simple" name="strSalaryHeadId" id="strSalaryHeadId" listKey="salaryHeadId" cssStyle="margin-left:10px"
									listValue="salaryHeadName" headerKey="" headerValue="Choose Salary Head" list="salaryAllowanceHeadList" key="" required="true" onchange="submitForm('3');"/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
	<% session.setAttribute("MESSAGE", ""); %>
	

	<%if(uF.parseToInt(strSalaryHeadId)>0){ %>	
		<div style="float:left; width: 98%; margin:0px 0px 10px 0px"><strong><%=uF.showData((String)request.getAttribute("SALARY_HEAD_NAME"),"") %> policy for level - <%=uF.showData((String)request.getAttribute("LEVEL_NAME"),"") %></strong></div>
		<div style="float:left; width: 98%; margin:0px 0px 10px 0px;"> 
			<a href="javascript:void(0);" onclick="addNewCondition('<%=strOrg %>', '<%=strLevel %>', '<%=strSalaryHeadId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Condition</a>
		</div>
		
		<div style="float:left; width: 98%; margin:0px 0px 10px 0px;">
			<ul class="level_list">
				<%
					List<Map<String, String>> alCondition = (List<Map<String, String>>)request.getAttribute("alCondition");
					if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
					
					int cnt = 0;
					for(int i = 0; i < alCondition.size(); i++) {
						cnt++;
						Map<String,String> hmCondition = (Map<String,String>) alCondition.get(i);
						if(hmCondition == null) hmCondition = new HashMap<String, String>();
				%>
						<li>
							<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %> --%>
							<%-- <span id="publishUnpublishCSpan_<%=hmCondition.get("ALLOWANCE_CONDITION_ID") %>" style="margin-right: 5px;">
								<% if(uF.parseToBoolean(hmCondition.get("IS_PUBLISH"))) { %>
									<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this condition?'))getPublishConditionORLogic('<%=hmCondition.get("ALLOWANCE_CONDITION_ID") %>','C', 'F');">
										<img title="Published" src="images1/icons/icons/publish_icon_b.png">
									</a>
								<% } else { %>
									<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this condition?'))getPublishConditionORLogic('<%=hmCondition.get("ALLOWANCE_CONDITION_ID") %>','C', 'T');" >
										<img title="Waiting to be publish" src="images1/icons/icons/unpublish_icon_b.png">
									</a>
								<% } %>
							</span> --%>
								<a href="AddAllowanceCondition.action?operation=D&ID=<%=hmCondition.get("ALLOWANCE_CONDITION_ID") %>&strOrg=<%=strOrg %>&strLevel=<%=strLevel %>&strSalaryHeadId=<%=strSalaryHeadId %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>" onclick="return confirm('Are you sure you wish to delete this condition?')" style="color:rgb(233,0,0)"> <i class="fa fa-trash" aria-hidden="true"></i> </a> 
							<%-- <%} %>
							<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> --%>
								<a href="javascript:void(0)" onclick="editCondition('<%=hmCondition.get("ALLOWANCE_CONDITION_ID")%>', '<%=strOrg %>', '<%=strLevel %>', '<%=strSalaryHeadId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<%-- <%} %> --%> 
							Condition Name/ Slab: <strong><%=hmCondition.get("ALLOWANCE_CONDITION_SLAB") %></strong>,&nbsp;
							Condition Type: <strong><%=hmCondition.get("ALLOWANCE_CONDITION") %></strong>,&nbsp;
							<%
							if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_CUSTOM_FACTOR_ID) {
								String StrType= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "Percentage";
								String StrTypeStatus= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "%";
							%>
								Pay Type: <strong><%=StrType %></strong>&nbsp;
								<%-- <%=StrTypeStatus %>: <strong><%=hmCondition.get("CUSTOM_AMT_PERCENTAGE") %></strong> --%>
							<% } else { %>
							<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID) { %>Achieved % <% } %>
								Min: <strong><%=hmCondition.get("MIN_CONDITION") %></strong>&nbsp;-&nbsp;
								<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_GOAL_KRA_TARGET_ID) { %>Achieved % <% } %>
								Max: <strong><%=hmCondition.get("MAX_CONDITION") %></strong>,&nbsp;
								<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_HOURS_ID) { %>
								Calculate &nbsp;<strong><%=hmCondition.get("CALCULATE_FROM") %></strong>&nbsp;to actual amount
								<% } %>
								<% if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == IConstants.A_NO_OF_DAYS_ID) { %>
									,&nbsp;Add days from approve attendance: <strong><%=hmCondition.get("IS_ADD_ATTENDANCE") %></strong>
								<% } %>
							<%} %>
							<p style="font-size: 12px; padding-left: 66px; font-style: italic;">Last updated by <%=hmCondition.get("ADDED_BY") %> on <%=hmCondition.get("ENTRY_DATE") %></p>
						</li>
				<%	} if(cnt == 0) { %>
						<li><div class="filter"><div class="msg nodata"><span>No allowance condition has been set.</span></div></div></li>
				<%	} %>
			</ul>
		</div>
		
		
		<div style="float:left; width: 98%; margin:0px 0px 10px 0px;"> 
			<a href="javascript:void(0);" onclick="addPaymentLogic('<%=strOrg %>', '<%=strLevel %>', '<%=strSalaryHeadId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Payment Logic</a>
		</div>
		
		<div style="float:left; width: 98%; margin:0px 0px 10px 0px;">
			<ul class="level_list">
				<%
					List<Map<String, String>> alLogic = (List<Map<String, String>>)request.getAttribute("alLogic");
					if(alLogic == null) alLogic = new ArrayList<Map<String,String>>();
					
					cnt = 0;
					for(int i = 0; i < alLogic.size(); i++){
						cnt++;
						Map<String,String> hmLogic = (Map<String,String>) alLogic.get(i);
						if(hmLogic == null) hmLogic = new HashMap<String, String>();
				%>
						<li>
							<span id="publishUnpublishPLSpan_<%=hmLogic.get("PAYMENT_LOGIC_ID") %>" style="margin-right: 5px;">
								<% if(uF.parseToBoolean(hmLogic.get("IS_PUBLISH"))) { %>
									<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to unpublish this payment logic?'))getPublishConditionORLogic('<%=hmLogic.get("PAYMENT_LOGIC_ID") %>','PL', 'F');">
										<img title="Published" src="images1/icons/icons/publish_icon_b.png">
									</a>
								<% } else { %>
									<a href="javascript:void(0)" onclick="if(confirm('Are you sure, you want to publish this payment logic?'))getPublishConditionORLogic('<%=hmLogic.get("PAYMENT_LOGIC_ID") %>','PL', 'T');" >
										<img title="Waiting to be publish" src="images1/icons/icons/unpublish_icon_b.png">
									</a>
								<% } %>
							</span>
							<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %> --%>
								<a href="AddAllowancePaymentLogic.action?operation=D&ID=<%=hmLogic.get("PAYMENT_LOGIC_ID") %>&strOrg=<%=strOrg %>&strLevel=<%=strLevel %>&strSalaryHeadId=<%=strSalaryHeadId %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>" onclick="return confirm('Are you sure, you wish to delete this payment logic?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i> </a> 
							<%-- <%} %>
							<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %> --%>
								<a href="javascript:void(0)" onclick="editPaymentLogic('<%=hmLogic.get("PAYMENT_LOGIC_ID")%>', '<%=strOrg %>', '<%=strLevel %>', '<%=strSalaryHeadId %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<%-- <%} %> --%> 
							Payment Logic Name: <strong><%=hmLogic.get("PAYMENT_LOGIC_SLAB") %></strong>,&nbsp;
							Condition Name/ Slab: <strong><%=hmLogic.get("ALLOWANCE_CONDITION") %></strong>,&nbsp;
							Payment Logic: <strong><%=hmLogic.get("ALLOWANCE_PAYMENT_LOGIC") %></strong>
							<%if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_CUSTOM_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_X_ACHIEVED_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_HOUR_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_DAY_ID) { %>
								,&nbsp;Fixed Amount: <strong><%=hmLogic.get("FIXED_AMOUNT") %></strong>
								<%if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_HOUR_ID){ %>
									,&nbsp;Per Hour Amount: <strong><%=hmLogic.get("PER_HOUR_DAY_AMOUNT") %></strong>
								<%} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_AND_PER_DAY_ID){ %>
									,&nbsp;Per Day Amount: <strong><%=hmLogic.get("PER_HOUR_DAY_AMOUNT") %></strong>
								<%} %>
							<%} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_EQUAL_TO_SALARY_HEAD_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_DAYS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_HOURS_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_CUSTOM_ID || uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_SALARY_HEAD_X_ACHIEVED_ID) { %>
								,&nbsp;Salary Head: <strong><%=hmLogic.get("CAL_SALARY_HEAD_NAME") %></strong>
							<%} %>
							<%
							if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == IConstants.A_FIXED_ONLY_DEDUCTION_ID) {
							%>
								,&nbsp;Is Deduct Full Amount: <strong><%=hmLogic.get("IS_DEDUCT_FULL_AMOUNT") %></strong>
								<%if(!uF.parseToBoolean(hmLogic.get("IS_DEDUCT_FULL_AMOUNT"))) {%>
									,&nbsp;Fixed Amount: <strong><%=hmLogic.get("FIXED_AMOUNT") %></strong>
								<%} %>
							<%} %>
							,&nbsp;Effective From: <strong><%=hmLogic.get("EFFECTIVE_DATE") %></strong>
							<p style="font-size: 12px; padding-left: 66px; font-style: italic;">Last updated by <%=hmLogic.get("ADDED_BY") %> on <%=hmLogic.get("ENTRY_DATE") %></p>
						</li>
				<%	} if(cnt == 0){ %>
						<li><div class="filter"><div class="msg nodata"><span>No allowance payment logic has been set.</span></div></div></li>
				<%	} %>
			</ul>
		</div>
		
	<% } else { %>
		<div class="filter">
			<div class="msg nodata"><span>Please choose the Salary Head</span></div>
		</div>
	<% } %>
</div>


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