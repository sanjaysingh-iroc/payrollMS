<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

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


function addTaxHead(userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Tax Head');
	$.ajax({
		url : 'AddTaxHead.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editTaxHead(strTaxHeadId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Tax Head');
	$.ajax({
		url : 'AddTaxHead.action?operation=E&taxHeadId='+strTaxHeadId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addBillingHead(userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Billing Head');
	$.ajax({
		url : 'AddBillingHead.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editBillingHead(billingHeadId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Billing Head');
	$.ajax({
		url : 'AddBillingHead.action?operation=E&billingHeadId='+billingHeadId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addCostCalculation(userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Cost Calculation');
	$.ajax({
		url : 'AddCostCalculation.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editCostCalculation(costCalId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Cost Calculation');
	$.ajax({
		url : 'AddCostCalculation.action?operation=E&costCalId='+costCalId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addForcedTask(userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Set Forced Task Setting');
	$.ajax({
		url : 'SetTaskSubtaskType.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editForcedTask(taskTypeId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update Forced Task Setting');
	$.ajax({
		url : 'SetTaskSubtaskType.action?operation=E&taskTypeId='+taskTypeId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>

<% 
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
Map<String, List<List<String>>> hmTaxHead = (Map<String, List<List<String>>>)request.getAttribute("hmTaxHead");
Map<String, List<List<String>>> hmTaxHeadHistory = (Map<String, List<List<String>>>)request.getAttribute("hmTaxHeadHistory");

Map<String, List<List<String>>> hmBillingHead = (Map<String, List<List<String>>>)request.getAttribute("hmBillingHead");

Map<String, List<String>> hmOrgCostCalData = (Map<String, List<String>>)request.getAttribute("hmOrgCostCalData");

Map<String, List<String>> hmOrgForcedTask = (Map<String, List<String>>)request.getAttribute("hmOrgForcedTask");

Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
Map<String, String> hmBillOrgName = (Map<String, String>)request.getAttribute("hmBillOrgName");

String userscreen = (String)request.getAttribute("userscreen");
String navigationId = (String)request.getAttribute("navigationId");
String toPage = (String)request.getAttribute("toPage");

/* Map<String, List<Map<String, String>>> hmProjectCategory = (Map<String, List<Map<String, String>>>)request.getAttribute("hmProjectCategory");
if(hmProjectCategory == null) hmProjectCategory = new HashMap<String, List<Map<String, String>>>(); */

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
				<s:form name="frm" action="MyDashboard" theme="simple">
					<s:hidden name="userscreen" />
					<s:hidden name="navigationId" />
					<s:hidden name="toPage" />
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
								<s:select list="orgList" name="strOrg" headerKey="" headerValue="All Organisation" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
<% session.setAttribute("MESSAGE", ""); %>

	<div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Tax Setting</div>

	<div style="float:left; margin:15px 0px 0px 15px"> 
		<a href="javascript:void(0)" onclick="addTaxHead('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Tax</a>
	</div>

	<div style=";float:left; width:100%">
         <ul class="level_list">
		<% 
			Set<String> set = hmTaxHead.keySet();
			Iterator<String> it = set.iterator();
			
			while(it.hasNext()) {
				String strOrgId = (String)it.next();
				String StrOrgName = hmOrgName.get(strOrgId);
			%>
			<li> 
			<strong><%=StrOrgName %> </strong>
			<ul>
			<%		
				List<List<String>> taxHeadList = (List<List<String>>)hmTaxHead.get(strOrgId);
					if(taxHeadList!= null && !taxHeadList.isEmpty()) {
						for(int i=0; i<taxHeadList.size(); i++) {
							List<String> innerList = taxHeadList.get(i);
					%>
					
					<li>
					<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<%if(uF.parseToInt(hmSBUEmpCount.get(innerList.get(0))) > 0) { 
					String strMsg = "Sorry! You have " + uF.parseToInt(hmSBUEmpCount.get(innerList.get(0))) + " employees added with this SBU, therefore we cannot delete the SBU. To consider this option, please ensure that you have ZERO Employees added.";
					%>
						<a href="javascript:void(0);" class="del" onclick="alert('<%=strMsg %>')"> - </a>
					<% } else { %> --%>
						<a href="AddTaxHead.action?operation=D&taxHeadId=<%=innerList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" class="fa fa-trash-o" style="color: red;" onclick="return confirm('Are you sure, you wish to delete this tax head?')">&nbsp;</a>
					<%-- <% } %>
					
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>  --%>
					<a href="javascript:void(0);" class="fa fa-edit" onclick="editTaxHead('<%=innerList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a>
					<%-- <%} %> --%> 
					
					Tax Name: <strong><%=innerList.get(1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Tax Percentage: <strong><%=innerList.get(2)%> %</strong>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Deduction Type: <strong><%=innerList.get(3)%></strong>  
					</li> 
			<% }
					}
			%> 
		</ul>
		
		<ul><li>
			<table class="tb_style" style="width: 90%;">
			<tr>
				<th>Sr. No</th>
				<th>Effective Date</th>
				<th>Head Name</th>
				<th>Head Percentage</th>
				<th>Deduction Type</th>
			</tr>
			<%  List<List<String>> taxHeadHistoryList = (List<List<String>>)hmTaxHeadHistory.get(strOrgId);
					if(taxHeadHistoryList!= null && !taxHeadHistoryList.isEmpty()) {
						for(int i=0; i<taxHeadHistoryList.size(); i++) {
							List<String> innerList = taxHeadHistoryList.get(i);%>
			<tr>
				<td ><%=i+1 %></td>
				<td><%=innerList.get(5) %></td>
				<td><%=innerList.get(2) %></td>
				<td><%=innerList.get(3) %></td>
				<td><%=innerList.get(4) %></td>
			</tr>
			
			<% } } %>
			</table>
		</li></ul>
			
		</li>	
			<% } %>
		 </ul>
     </div>
     
     
     
     <div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Billing Setting</div>

	<div style="float:left; margin:15px 0px 0px 15px"> 
		<a href="javascript:void(0)" onclick="addBillingHead('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Billing Head</a>
	</div>

	<div style=";float:left; width:100%">
         <ul class="level_list">
		<% 
			Set<String> set1 = hmBillingHead.keySet();
			Iterator<String> it1 = set1.iterator();
			
			while(it1.hasNext()) {
				String strOrgId = (String)it1.next();
				String StrOrgName = hmBillOrgName.get(strOrgId);
			%>
			<li> 
			<strong><%=StrOrgName %> </strong>
			<ul>
			<%		
				List<List<String>> billingHeadList = (List<List<String>>)hmBillingHead.get(strOrgId);
					if(billingHeadList!= null && !billingHeadList.isEmpty()) {
						for(int i=0; i<billingHeadList.size(); i++) {
							List<String> innerList = billingHeadList.get(i);
					%>
					
					<li>
					<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<%if(uF.parseToInt(hmSBUEmpCount.get(innerList.get(0))) > 0) { 
					String strMsg = "Sorry! You have " + uF.parseToInt(hmSBUEmpCount.get(innerList.get(0))) + " employees added with this SBU, therefore we cannot delete the SBU. To consider this option, please ensure that you have ZERO Employees added.";
					%>
						<a href="javascript:void(0);" class="del" onclick="alert('<%=strMsg %>')"> - </a>
					<% } else { %> --%>
						<a href="AddBillingHead.action?operation=D&billingHeadId=<%=innerList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" class="fa fa-trash-o" style="color: red;" onclick="return confirm('Are you sure, you wish to delete this billing head?')">&nbsp;</a>
					<%-- <% } %>
					
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>  --%>
					<a href="javascript:void(0);" class="fa fa-edit" onclick="editBillingHead('<%=innerList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a>
					<%-- <%} %> --%> 
					
					Billing Head: <strong><%=innerList.get(1)%></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Billing Data Type: <strong><%=innerList.get(2)%> </strong>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Other Variable: <strong><%=innerList.get(3)%> </strong>  
					</li> 
			<% }
					}
			%> 
		</ul>
		</li>	
			<% } %>
		 </ul>
     </div>
     
    
<!-- *************************************** Actual Cost Calculation ********************** -->     
     <div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Actual Cost Calculation</div>

	<div style="float:left; margin:15px 0px 0px 15px"> 
		<a href="javascript:void(0)" onclick="addCostCalculation('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Organisation Cost Calculation</a>
	</div>

	<div style=";float:left; width:100%">
         <ul class="level_list">
		<%
		if(hmOrgCostCalData != null && !hmOrgCostCalData.isEmpty()) {
			Set<String> set2 = hmOrgCostCalData.keySet();
			Iterator<String> it2 = set2.iterator();
			
			while(it2.hasNext()) {
				String strOrgId = (String)it2.next();
				List<String> innerList = hmOrgCostCalData.get(strOrgId);
			%>
			<li>
			<strong><%=innerList.get(1) %> </strong>
			<ul>
				<li>
				<a href="AddCostCalculation.action?operation=D&costCalId=<%=innerList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" class="fa fa-trash-o" style="color: red;" onclick="return confirm('Are you sure, you wish to delete this calculation setting?')">&nbsp;</a>
				<a href="javascript:void(0);" class="fa fa-edit" onclick="editCostCalculation('<%=innerList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a>
				<%-- <strong><%=innerList.get(1)%></strong> --%>
				<%=innerList.get(2)%>
				<% if(uF.parseToInt(innerList.get(3)) == 3) { %>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Days:</b> <%=innerList.get(4)%>
				<% } %>
				</li> 
		</ul>
		</li>
			<% } 
			} %>
		 </ul>
     </div>
     
     
     <!-- *************************************** Project Category ********************** -->     
    <%--  <div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Project Category</div>

	<div style="float:left; margin:15px 0px 0px 15px"> 
		<a href="javascript:void(0)" onclick="addProjectCategory()"> + Add New Project Category</a>
	</div>

	<div style=";float:left; width:100%">
         <ul class="level_list">
		<%
		if(hmProjectCategory != null && !hmProjectCategory.isEmpty()) {
			Iterator it3 = hmProjectCategory.keySet().iterator(); 
			while(it3.hasNext()) {
				String strOrgId = (String)it3.next();
				List<Map<String, String>> alList = (List<Map<String, String>>) hmProjectCategory.get(strOrgId);
				for(int i = 0; alList != null && i < alList.size();i++){
				Map<String, String> hmInner = alList.get(i);
			%>
			<li>
			<strong><%=hmInner.get("ORG_NAME") %> </strong>
			<ul>
				<li>
					<p>
						<a href="AddProjectCategory.action?operation=D&proCategoryId=<%=hmInner.get("PROJECT_CATEGORY_ID")%>" class="del" onclick="return confirm('Are you sure, you wish to delete this project category?')"> - </a>
						<a href="javascript:void(0);" class="edit_lvl" onclick="editProjectCategory('<%=hmInner.get("PROJECT_CATEGORY_ID")%>')">Edit</a>
						Project Category:&nbsp;<%=hmInner.get("PROJECT_CATEGORY")%>
					</p>
					<%if(hmInner.get("PROJECT_DESCRIPTION")!=null && !hmInner.get("PROJECT_DESCRIPTION").trim().equals("")){ %>
						<p>
							Project Category Description:&nbsp;<%=hmInner.get("PROJECT_DESCRIPTION")%>
						</p>
					<%} %>
				</li> 
		</ul>
		</li>
			<%		}
				} 
			} %>
		 </ul>
     </div> --%>
     
     
     
<!-- *************************************** Forced Task Setting ********************** -->     
     <div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Forced Task Setting</div>

	<div style="float:left; margin:15px 0px 0px 15px"> 
		<a href="javascript:void(0)" onclick="addForcedTask('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Organisation Forced Task</a>
	</div>

	<div style=";float:left; width:100%">
         <ul class="level_list">
		<%
		if(hmOrgForcedTask != null && !hmOrgForcedTask.isEmpty()) {
			Set<String> set2 = hmOrgForcedTask.keySet();
			Iterator<String> it2 = set2.iterator();
			
			while(it2.hasNext()) {
				String strOrgId = (String)it2.next();
				List<String> innerList = hmOrgForcedTask.get(strOrgId);
			%>
			<li>
				<a href="SetTaskSubtaskType.action?operation=D&taskTypeId=<%=innerList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" class="fa fa-trash-o" style="color: red;" onclick="return confirm('Are you sure, you wish to delete this task approval setting?')">&nbsp;</a>
				<a href="javascript:void(0);" class="fa fa-edit" onclick="editForcedTask('<%=innerList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a>
				<strong><%=innerList.get(1) %> </strong>
				<ul style="padding-left: 65px;">
					<li>
						<input type="checkbox" name="forcedTask" <%=uF.parseToBoolean(innerList.get(2)) ? "checked" : "" %> disabled="disabled"> Forced Task Assigned
					</li>
					<li>
						<input type="checkbox" name="forcedTask" <%=uF.parseToBoolean(innerList.get(3)) ? "checked" : "" %> disabled="disabled"> Task Request Auto Approved
					</li> 
				</ul>
			</li>
			<% } 
			} %>
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
