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
	
	$('#lt').DataTable();
	
});




function updateRetirementPolicy(strOrgId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update Retirement Policy');
	//var strOrgId = document.getElementById("strOrg").value;
	$.ajax({
		url : 'AddUpdateRetirementPolicy.action?operation=E&strOrgId='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId
			+'&toPage='+toPage, 
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
	
	Map<String, Map<String, String>> hmOrg = (Map<String, Map<String, String>>) request.getAttribute("hmOrg");
	if(hmOrg == null) hmOrg = new HashMap<String, Map<String, String>>();

	
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
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
								<% } else { %>
									<s:select list="orgList" name="strOrg" id="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
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
         <ul class="level_list">   
         <p style="text-align:left;"><b>Retirement Policy</b></p>      
   
		<% 
			Iterator<String> it = hmOrg.keySet().iterator();
			while(it.hasNext()) {
				String strOrgId = (String)it.next();
				Map<String, String> hmInner = (Map<String, String>)hmOrg.get(strOrgId);
			%>
				<li> 
					<strong>
						<a href="javascript:void(0)" class="edit_lvl" onclick="updateRetirementPolicy('<%=hmInner.get("ORG_ID") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Payroll Setting"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						<%=uF.showData(hmInner.get("ORG_NAME"),"") %> [<%=uF.showData(hmInner.get("ORG_CODE"),"") %>] 
					</strong>
					<ul>
						<li>
		                    <p>
								Retirement Age: <strong><%=uF.showData(hmInner.get("ORG_RETIREMENT_AGE"),"") %> </strong>&nbsp;&nbsp;&nbsp;
		                    </p> 
		                   
						</li>
					</ul>
				</li>	
			 <%} %>  
			 
		 </ul>
     </div>	
</div>

<div class="modal" id="modalInfo" role="dialog">
    <div class="modal-dialog">
        <!-- Modal content-->
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
            </div>
            <div class="modal-footer">
                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
