<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script>

function addAttribute(elementID,levelid) {
	var dialogEdit = '#addAttribute';
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : true,
				height : 300,
				width : 600,
				modal : true,
				title : 'Add New Attribute',
				open : function() {
					var xhr = $.ajax({
						url : "AddAppraisalAttributeForLevel.action?operation=A&elementid=" + elementID+"&strLevel="+levelid,
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

function addLeveltoAttribute(attributeID) {
	var dialogEdit = '#addLeveltoAttribute';
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : true,
				height : 300,
				width : 600,
				modal : true,
				title : 'Add Level to Attribute',
				open : function() {
					var xhr = $.ajax({
						url : "AddAppraisalAttributeForLevel.action?operation=A&attributeid=" + attributeID,
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


function editAppraisal(id,elementID,levelid) {
	var dialogEdit = '#editAttribute';
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : true,
				height : 300,
				width : 600,
				modal : true,
				title : 'Edit Attribute',
				open : function() {
					var xhr = $.ajax({
						url : "AddAppraisalAttributeForLevel.action?operation=E&arribute_level_id=" + id+"&elementid="+elementID+"&strLevel="+levelid+"&type=level",
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
		<jsp:param value="Attribute Details" name="title" />
	</jsp:include>
	<%
		List<List<String>> levelList = (List<List<String>>) request
				.getAttribute("levelList");
		Map<String, List<List<String>>> mp = (Map<String, List<List<String>>>) request
				.getAttribute("mp");
		List<List<String>> elementOuterList = (List<List<String>>) request
				.getAttribute("elementOuterList");
		Map<String,List<List<String>>> hmAttributeLevel=(Map<String,List<List<String>>>)request.getAttribute("hmAttributeLevel");
		Map<String, List<String>> hmAttributeDetails =(Map<String, List<String>>)request.getAttribute("hmAttributeDetails");
		
		
		%>

	<div id="printDiv" class="leftbox reportWidth">
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
		<s:form name="frm" action="AppraisalAttributeDetails" theme="simple">
			<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
			<s:hidden name="type" value="level"></s:hidden>
			<%-- <s:select theme="simple" name="strLocation" listKey="wLocationId" listValue="wLocationName" list="workList" onchange="document.frm.submit();"/> --%>
		</s:form>
		</div>
	<div style="float: right;"><a	href="javascript:void(0)" onclick="window.location='AppraisalAttributeDetails.action?type='">view Attribute wise</a></div>
	<ul class="level_list" style="width: 100%;">
			<%
				for (int i = 0; elementOuterList != null
						&& !elementOuterList.isEmpty()
						&& i < elementOuterList.size(); i++) {
					List<String> elementList = elementOuterList.get(i);
			%>
			<li class="addnew desgn" style="width: 100%;"><strong><%=elementList.get(1)%></strong>
				<ul style="width: 100%;">
					<%-- <li class="addnew desgn">
					 <a	href="javascript:void(0)" onclick="addAttribute('<%=elementList.get(0)%>');">+ Add new Attribute</a>
					</li> --%>

					<%
							for (int j = 0; levelList != null && j < levelList.size(); j++) {
								List<String> levelInnerList = levelList.get(j);
					%>
				<li class="addnew desgn" style="width: 100%;">
					<%-- <a href="AddAppraisalAttribute.action?operation=D&ID=<%=attributeList.get(0)%>" class="del"> Delete Attribute</a> 
					<a href="javascript:void(0)" onclick="editAppraisal(<%=attributeList.get(0)%>);" class="edit_lvl">Edit Attribute</a> --%>
					<strong><%=levelInnerList.get(1)%></strong>
					<ul>
						<li class="addnew desgn"><a href="javascript:void(0)" onclick="addAttribute('<%=elementList.get(0)%>','<%=levelInnerList.get(0)%>');">+ Align to new Attribute</a>
						</li>
						
						<%
						
						List<List<String>> attributeOuterList = hmAttributeLevel.get(levelInnerList.get(0)+elementList.get(0));
							for (int k = 0; attributeOuterList != null && k < attributeOuterList.size(); k++) {
								List<String> attributeList = attributeOuterList.get(k);
					%>
				<li class="addnew desgn" style="width: 100%;">
				<%-- AddAppraisalAttribute.action?operation=D&ID=<%=attributeList.get(0)%> --%>
					<%-- <a href="AddAppraisalAttributeForLevel.action?operation=D&arribute_level_id=<%=attributeList.get(0)%>&elementid=<%=elementList.get(0)%>&strLevel=<%=levelInnerList.get(0)%>&type=level" class="del"> Delete Attribute</a> --%>
					<a href="AddAppraisalAttributeForLevel.action?operation=D&arribute_level_id=<%=attributeList.get(0)%>&elementid=<%=elementList.get(0)%>&strLevel=<%=levelInnerList.get(0)%>&type=level" onclick="return confirm('Are you sure you wish to delete this Attribute?')"  class="del"> Delete Attribute</a> 
					<a href="javascript:void(0)" onclick="editAppraisal('<%=attributeList.get(0)%>','<%=elementList.get(0)%>','<%=levelInnerList.get(0)%>');" class="edit_lvl">Edit Attribute</a> 
					<p><strong><%=attributeList.get(2) %></strong>  with the threshold limit of <strong><%=attributeList.get(3) %></strong></p>
					<%-- <% List<String> attributeDetails=hmAttributeDetails.get(4);
						if(attributeDetails==null) attributeDetails=new ArrayList<String>();
							String attributeinfo="";
							if(attributeDetails.get(4)!=null){
								if(attributeDetails.get(4).equals("1")){
									attributeinfo="Punctuality KPI";
								}else if(attributeDetails.get(4).equals("2")){
									attributeinfo="Attendance KPI";
								}else if(attributeDetails.get(4).equals("3")){
									attributeinfo="Efforts KPI";
								}else if(attributeDetails.get(4).equals("4")){
									attributeinfo="Work Performance KPI";
								}else if(attributeDetails.get(4).equals("5")){
									attributeinfo="Quality of Work KPI";
								} 
							}
						%>
					<p style="font-weight: normal; font-size: 10px;margin-left: 40px;">
						<%=attributeDetails.get(5)!=null ? attributeDetails.get(5) : "" %>
						&nbsp;
						(<strong><%=attributeinfo %></strong>)
					</p> --%>
					
					
				</li>
				<%
					}
				%>
						
					</ul>
				</li>
				<%
					}
				%>
				</ul>
				</li>
			<%
				}
			%>
		</ul>
	
	
	</div>

	
	<div id="addAttribute"></div>
	<div id="editAttribute"></div>
	<div id="addLeveltoAttribute"></div>