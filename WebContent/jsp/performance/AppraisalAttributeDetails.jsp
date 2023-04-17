<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

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

function addAttribute(elementID, userscreen, navigationId, toPage) {

	var strOrg ="";
	if(document.getElementById("strOrg")){
		strOrg =document.getElementById("strOrg").value;
	};
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Attribute');
	$.ajax({
		url : 'AddAppraisalAttribute.action?operation=A&elementid='+elementID+'&strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editAttribute(attributeID, elementid, userscreen, navigationId, toPage) {
	
	var strOrg ="";
	if(document.getElementById("strOrg")){
		strOrg =document.getElementById("strOrg").value;
	};
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Attribute');
	$.ajax({
		url : 'AddAppraisalAttribute.action?operation=E&elementid='+ elementid+'&attributeid='+attributeID+'&strOrg='+strOrg
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function addLeveltoAttribute(attributeID, elementid, userscreen, navigationId, toPage) {
	var orgId ="";
	if(document.getElementById("strOrg")){
		orgId =document.getElementById("strOrg").value;
	};
	var strOrg ="";
	if(document.getElementById("strOrg")){
		strOrg =document.getElementById("strOrg").value;
	};
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Level to Attribute');
	$.ajax({
		url : 'AddAppraisalLevelForAttribute.action?operation=A&attributeid='+attributeID+'&elementid='+elementid+'&orgId='+orgId+'&strOrg='+strOrg
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editAppraisalLevel(id, elementID, levelid, userscreen, navigationId, toPage) {
	var strOrg ="";
	if(document.getElementById("strOrg")){
		strOrg =document.getElementById("strOrg").value;
	};
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Attribute');
	$.ajax({
		url : 'AddAppraisalLevelForAttribute.action?operation=E&arribute_level_id='+id+'&elementid='+elementID+'&strLevel='+levelid+'&strOrg='+strOrg
				+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	   	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	   	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	   
		List<List<String>> levelList = (List<List<String>>) request.getAttribute("levelList");
		Map<String, List<List<String>>> mp = (Map<String, List<List<String>>>) request.getAttribute("mp");
		List<List<String>> elementOuterList = (List<List<String>>) request.getAttribute("elementOuterList");
		Map<String,List<List<String>>> hmAttributeLevel=(Map<String,List<List<String>>>)request.getAttribute("hmAttributeLevel");
		
		String userscreen = (String)request.getAttribute("userscreen");
		String navigationId = (String)request.getAttribute("navigationId");
		String toPage = (String)request.getAttribute("toPage");
		%>

	<div class="box-body">
			
	<%-- 	<div class="box box-default collapsed-box">
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
					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Organisation</p>
								<% if(uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER)) && hmFeatureUserTypeId.get(IConstants.F_LOGIN_USER_ORG_IN_FILTER).contains(strUsertypeId)) { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } %>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div> --%>
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		
	<%-- <div id="printDiv" class="leftbox reportWidth">
	<div class="filter_div">
		<div class="filter_caption">Filter</div>
		<s:form name="frm" action="AppraisalAttributeDetails" theme="simple">
		<div style="margin-left: 10px;">
			<p style="padding-left: 5px;display: inline;">Organisation</p>
			<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
		</div>
		</s:form>
	</div> --%>

		<div class="col-md-12">
			<ul class="level_list" style="width: 100%;">
				<%
					for (int i=0; elementOuterList != null && !elementOuterList.isEmpty() && i < elementOuterList.size(); i++) {
						List<String> elementList = elementOuterList.get(i);
				%>
				<li class="addnew desgn" style="width: 100%;"><strong><%=elementList.get(1)%></strong>
					<ul style="width: 100%;">
						<li class="addnew desgn">
							<a	href="javascript:void(0)" onclick="addAttribute('<%=elementList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add new Attribute</a>
						</li>
						<%
							List<List<String>> outerList = mp.get(elementList.get(0));
							for (int j = 0; outerList != null && j < outerList.size(); j++) {
								List<String> attributeList = outerList.get(j);
						%>
						<li class="addnew desgn" style="width: 100%;">
						
						<%
						// Started By Dattatray Date:08-09-21 Note : checked condition
						if(!uF.parseToBoolean(hmFeatureStatus.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE)) || hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS")==null || !hmFeatureUserTypeId.get(IConstants.F_INSTANCE_NAMEWISE_FEATURE+"_USER_IDS").contains(IConstants.QULOI) || uF.parseToInt(attributeList.get(0))>1) {
						%>
							<a href="AddAppraisalAttribute.action?operation=D&elementid=<%=elementList.get(0)%>&attributeid=<%=attributeList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete attribute?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
						<% } // End By Dattatray Date:08-09-21 Note : checked condition%>	
							<a href="javascript:void(0)" onclick="editAttribute('<%=attributeList.get(0)%>','<%=elementList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<%
								String attributeinfo="No KPI Aligned";
								if(attributeList.get(4)!=null){
									if(attributeList.get(4).equals("1")){
										attributeinfo="Punctuality KPI";
									}else if(attributeList.get(4).equals("2")){
										attributeinfo="Attendance KPI";
									}else if(attributeList.get(4).equals("3")){
										attributeinfo="Efforts KPI";
									}else if(attributeList.get(4).equals("4")){
										attributeinfo="Work Performance KPI";
									}else if(attributeList.get(4).equals("5")){
										attributeinfo="Quality of Work KPI";
									} 
								}
							%>
						
						<strong><%=attributeList.get(1)%></strong> - <%=attributeinfo %>
						
						<p style="font-weight: normal; font-size: 10px;margin-left: 40px; line-height: 14px; width: 81%;">
							<%=attributeList.get(5)!=null ? attributeList.get(5) : "" %>
						</p>
						<ul>
							<li class="addnew desgn"><a href="javascript:void(0)" onclick="addLeveltoAttribute('<%=attributeList.get(0)%>','<%=elementList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-plus-circle" aria-hidden="true"></i> Align to new Level</a></li> 
							<%
							List<List<String>> LevelOuterList = hmAttributeLevel.get(attributeList.get(0)+elementList.get(0));
							for (int k = 0; LevelOuterList != null && k < LevelOuterList.size(); k++) {
								List<String> LevelList = LevelOuterList.get(k);
						%>
					<li class="addnew desgn" style="width: 100%;">
						<a href="AddAppraisalLevelForAttribute.action?operation=D&arribute_level_id=<%=LevelList.get(0)%>&elementid=<%=elementList.get(0)%>&strLevel=<%=LevelList.get(1)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this level?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
						<a href="javascript:void(0)" onclick="editAppraisalLevel('<%=LevelList.get(0)%>','<%=elementList.get(0)%>','<%=LevelList.get(1)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');" ><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> 
						<span><strong><%=LevelList.get(2) %></strong> with the threshold limit of <strong><%=LevelList.get(3) %></strong></span>
					</li>
					<% } %>
					</ul>
				</li>
				<% } %>
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
	