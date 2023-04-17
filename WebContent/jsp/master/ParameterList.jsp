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
function addParameter(userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Parameter');
	$.ajax({
		url : 'AddEditDeleteParameter.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editParameter(parameterId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Parameter');
	$.ajax({
		url : 'AddEditDeleteParameter.action?operation=E&parameterId='+parameterId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
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
	
	Map<String, List<List<String>>> hmParametersOrgwise = (Map<String, List<List<String>>>)request.getAttribute("hmParametersOrgwise");
	if(hmParametersOrgwise==null) hmParametersOrgwise = new HashMap<String, List<List<String>>>();
	
	Map<String, String> hmOrgName = (Map<String, String>) request.getAttribute("hmOrgName");
	Map<String, String> hmClassStudentCount = (Map<String, String>)request.getAttribute("hmClassStudentCount");
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
	<a href="javascript:void(0);" onclick="addParameter('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Parameter</a>
</div>

<div class="col-md-12">
         <ul>
		<% 
			session.setAttribute(IConstants.MESSAGE, "");
			Iterator<String> it = hmParametersOrgwise.keySet().iterator();
			while(it.hasNext()) {
				String strOrgId = (String)it.next();
				String StrOrgName = hmOrgName.get(strOrgId);
			%>
		<li> 
			<strong><%=StrOrgName %> </strong>
			<ul class="level_list">
			<%		
				List<List<String>> parameterList = (List<List<String>>)hmParametersOrgwise.get(strOrgId);
					if(parameterList!= null && !parameterList.isEmpty()) {
						for(int i=0; i<parameterList.size(); i++) {
							List<String> innerList = parameterList.get(i);
					%>
					
					<li>
					<%if(hmClassStudentCount !=null && uF.parseToInt(hmClassStudentCount.get(innerList.get(0))) > 0) { 
					String strMsg = "Sorry! You have " + uF.parseToInt(hmClassStudentCount.get(innerList.get(0))) + " students added with this parameter, therefore we cannot delete the parameter. To consider this option, please ensure that you have ZERO students added.";
					%>
						<a href="javascript:void(0);" onclick="alert('<%=strMsg %>')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
					<% } else { %>
						<a href="AddEditDeleteParameter.action?strOrg=<%=request.getAttribute("strOrg") %>&operation=D&parameterId=<%=innerList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color: red;" onclick="return confirm('Are you sure you wish to delete this parameter?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
					<% } %>
					<a href="javascript:void(0);" onclick="editParameter('<%=innerList.get(0)%>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					
					Parameter Name: <strong><%=innerList.get(1)%></strong><%-- &nbsp;&nbsp;&nbsp;Code: <strong><%=innerList.get(1)%></strong> &nbsp;&nbsp;&nbsp; --%>  
					<p style="font-weight: normal; font-size: 10px;padding-left:55px"><%=innerList.get(2)%></p>
					 
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