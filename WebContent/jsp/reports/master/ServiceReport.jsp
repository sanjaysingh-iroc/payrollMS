<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
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
function addSBU(userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add SBU');
	$.ajax({
		url : 'AddService.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editSBU(sbuId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit SBU');
	$.ajax({
		url : 'AddService.action?operation=E&ID='+sbuId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

</script>


<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="<%=IMessages.TViewService %>" name="title"/>
</jsp:include> --%>

		
<%
	UtilityFunctions uF = new UtilityFunctions();
	
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
	Map<String, List<List<String>>> hmSBUDataOrgwise = (Map<String, List<List<String>>>)request.getAttribute("hmSBUDataOrgwise"); 
	Map<String, String> hmOrgName = (Map<String, String>) request.getAttribute("hmOrgName");
	Map<String, String> hmSBUEmpCount = (Map<String, String>)request.getAttribute("hmSBUEmpCount");
	//out.println(hmOfficeTypeMap);
	
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
									<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } %>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<% session.setAttribute(IConstants.MESSAGE, ""); %>
	
<div class="col-md-12">
	<a href="javascript:void(0);" onclick="addSBU('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New SBU</a>
</div>

<div class="col-md-12">
         <ul>
		<% 
			session.setAttribute(IConstants.MESSAGE, "");
			Set<String> setServiceMap = hmSBUDataOrgwise.keySet();
			Iterator<String> it = setServiceMap.iterator();
			
			while(it.hasNext()) {
				String strOrgId = (String)it.next();
				String StrOrgName = hmOrgName.get(strOrgId);
			%>
			<li> 
			<strong><%=StrOrgName %> </strong>
			<ul class="level_list">
			<%		
				List<List<String>> sbuDataList = (List<List<String>>)hmSBUDataOrgwise.get(strOrgId);
					if(sbuDataList!= null && !sbuDataList.isEmpty()) {
						for(int i=0; i<sbuDataList.size(); i++) {
							List<String> alService = sbuDataList.get(i);
					%>
					
					<li>
					<%if(uF.parseToInt(hmSBUEmpCount.get(alService.get(0))) > 0) { 
					String strMsg = "Sorry! You have " + uF.parseToInt(hmSBUEmpCount.get(alService.get(0))) + " employees added with this SBU, therefore we cannot delete the SBU. To consider this option, please ensure that you have ZERO Employees added.";
					%>
						<a href="javascript:void(0);" onclick="alert('<%=strMsg %>')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
					<% } else { %>
						<a href="AddService.action?strOrg=<%=request.getAttribute("strOrg") %>&operation=D&ID=<%=alService.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color: red;" onclick="return confirm('Are you sure you wish to delete this SBU?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
					<% } %>
					<a href="javascript:void(0);" onclick="editSBU('<%=alService.get(0)%>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					
					Name: <strong><%=alService.get(2)%></strong>&nbsp;&nbsp;&nbsp;Code: <strong><%=alService.get(1)%></strong> &nbsp;&nbsp;&nbsp;  
					<p style="font-weight: normal; font-size: 10px;padding-left:40px"><%=alService.get(3)%></p>
					</li> 
		<% }	} %> 
			
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