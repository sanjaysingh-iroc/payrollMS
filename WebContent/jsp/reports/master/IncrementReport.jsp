<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript">
function addIncrement(strOrg) { 
	
	removeLoadingDiv('the_div');
	var dialogEdit = '#addIncrement';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 400,
				width : 700,
				modal : true,
				title : 'Add New Increment',
				open : function() {
					var xhr = $.ajax({
						url : "AddIncrement.action?orgId="+strOrg, 
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

function editIncrement(strIncrementId) { 
	removeLoadingDiv('the_div');
	var dialogEdit = '#editIncrement';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog(
			{
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 400,
				width : 700,
				modal : true,
				title : 'Edit Increment',
				open : function() {
					var xhr = $.ajax({ 
						url : "AddIncrement.action?operation=E&ID="+strIncrementId, 
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



<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Increment Slabs" name="title"/>
</jsp:include>


<%
UtilityFunctions uF = new UtilityFunctions();
Map hmIncrementReport = (Map)request.getAttribute("hmIncrementReport"); 

//out.println(hmOfficeTypeMap);

%>

 

<div id="printDiv" class="leftbox reportWidth">

<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>

      
<div class="filter_div">
<div class="filter_caption">Select Organisation</div>
<s:form name="frm" action="IncrementReport" theme="simple">
	<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
</s:form>
</div>	 

		
<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))){ %> 		
<div style="float:left; margin:0px 0px 10px 0px"> 
	<%-- <a href="AddIncrement.action?orgId=<%=request.getAttribute("strOrg") %>" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })"> + Add New Slab</a> --%>
	<a href="javascript:void(0)" onclick="addIncrement('<%=(String)request.getAttribute("strOrg") %>')"> + Add New Increment Slab</a>
</div>
<%} %>  
<div class="clr"></div>


<div>
         <ul class="level_list">

		
		<% 
			Set setIncrementMap = hmIncrementReport.keySet();
			Iterator it = setIncrementMap.iterator();
			int count=0;
			while(it.hasNext()){
				String strIncrementId = (String)it.next();
				List alIncrement = (List)hmIncrementReport.get(strIncrementId);
				if(alIncrement==null)alIncrement=new ArrayList();
				count++;
					
					
					%>
					
					<li>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.DELETE_ACCESS))){ %>
					<a href="AddIncrement.action?orgId=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=strIncrementId%>" class="del" onclick="return confirm('Are you sure you wish to delete this slab?')"> - </a>
					<%} %>
					<%if(uF.parseToBoolean((String)request.getAttribute(IConstants.UPDATE_ACCESS))){ %>
					<%-- <a href="AddIncrement.action?operation=E&ID=<%=strIncrementId%>" class="edit_lvl" onclick="return hs.htmlExpand(this, { objectType: 'ajax',width:700 })">Edit</a> --%>
					<a href="javascript:void(0)" class="edit_lvl" onclick="editIncrement('<%=strIncrementId%>')">Edit</a> 
					<%} %> 
					Increment From: <strong><%=alIncrement.get(1) %></strong>&nbsp;&nbsp;&nbsp;
					Increment To: <strong><%=alIncrement.get(2) %></strong>&nbsp;&nbsp;&nbsp;
					Increment Amount: <strong><%=alIncrement.get(3) %></strong>&nbsp;&nbsp;&nbsp;
					Payable Month: <strong><%=alIncrement.get(4) %></strong>&nbsp;&nbsp;&nbsp;
					
					<p style="font-size: 10px; padding-left: 42px; font-style: italic;">	Last updated by <%=uF.showData((String)alIncrement.get(5), "N/A")%> on <%=uF.showData((String)alIncrement.get(6),"")%></p>
					
					</li> 
					
		<%
			}
		%>
		 
		 </ul>
         
     </div>	
		
</div>

<div id="addIncrement"></div>
<div id="editIncrement"></div>
