<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
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
 
function addEducation(userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body'; 
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Education');
	$.ajax({
		url : 'AddEducation.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editEducation(educationId, userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Education');
	$.ajax({
		url : 'AddEducation.action?operation=E&ID='+educationId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
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
	
	Map<String, List<List<String>>> hmEducationOrgwise = (Map<String, List<List<String>>>) request.getAttribute("hmEducationOrgwise");
	Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
	
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
		
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
		<% session.setAttribute("MESSAGE", ""); %>
		
	<div class="col-md-12">
		<a href="javascript:void(0);" onclick="addEducation('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Education</a>
	</div>

	<div class="col-md-12">
		 <ul>
		<% 
		Set<String> setEducationMap = hmEducationOrgwise.keySet();
		Iterator<String> it = setEducationMap.iterator();
		
		while(it.hasNext()) {
			String strOrgId = (String)it.next();
			List<List<String>> educationList = (List<List<String>>)hmEducationOrgwise.get(strOrgId);
			%>
			<li> 
			<strong><%=hmOrgName.get(strOrgId) %> </strong>
				<ul class="level_list">
				<%		
				for(int i=0; educationList != null && !educationList.isEmpty() && i<educationList.size(); i++) {
					List<String> alInner = (List<String>)educationList.get(i);
				%>
					<li>
					<a href="AddEducation.action?strOrg=<%=strOrgId %>&operation=D&ID=<%=alInner.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this education?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
					<a href="javascript:void(0);" onclick="editEducation('<%=alInner.get(0)%>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					<strong><%=alInner.get(1)%></strong> - <span style="font-size:12px"><%=alInner.get(2)%></span>
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
