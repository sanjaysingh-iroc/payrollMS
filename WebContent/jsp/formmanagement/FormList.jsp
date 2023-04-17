<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
	Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
	String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);

	String strTitle = (String) request.getAttribute(IConstants.TITLE);
	List<Map<String, String>> alReport = (List<Map<String, String>>) request.getAttribute("alReport");
	if (alReport == null) alReport = new ArrayList<Map<String, String>>();
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script>
$(document).ready(function(){
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
	
function formPreview(formId) {
	var dialogEdit = '.modal-body';
	 $(dialogEdit).empty();
	 $(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	 $("#modalInfo").show();
	 $(".modal-title").html('Form Preview');
	 
	 $.ajax({
			url : "FormPreview.action?formId="+formId,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
</script>

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

	<%=uF.showData((String) session.getAttribute(IConstants.MESSAGE), "")%>
	<% session.setAttribute("MESSAGE", ""); %>

	<a href="javascript:void(0);" onclick="window.location='AddForm.action?strOrg='+document.frm.strOrg.options[document.frm.strOrg.selectedIndex].value+'&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Form</a>

	<div class="box-body">
		<ul class="level_list">
			<% 
				for(int i = 0; i < alReport.size(); i++){
					Map<String, String> hmInner = (Map<String, String>) alReport.get(i);
			%>
					<li>
						<a title="Delete" href="AddForm.action?operation=D&formId=<%=hmInner.get("FORM_ID")%>&strOrg=<%=request.getAttribute("strOrg") %>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you want to delete this form?')" style="color: red;"><i class="fa fa-trash" aria-hidden="true"></i></a> 
						<a style="float: left;" title="View and Edit" href="FormSummary.action?formId=<%=hmInner.get("FORM_ID")%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						<a href="javascript: void(0)" style="float: left;" onclick="formPreview('<%=hmInner.get("FORM_ID")%>');" title="Preview"><i class="fa fa-list-alt" aria-hidden="true"></i></a>
						
						Form Title: <strong><%=uF.showData(hmInner.get("FORM_NAME"),"")%></strong>&nbsp;&nbsp;&nbsp; 
						Created By: <strong><%=uF.showData(hmInner.get("FORM_ADDED_BY"),"")%></strong> &nbsp;&nbsp;&nbsp; 
						Updated On: <strong><%=uF.showData(hmInner.get("FORM_ENTRY_DATE"),"")%></strong> &nbsp;&nbsp;&nbsp; 
						Node aligned: <strong><%=uF.showData(hmInner.get("FORM_NODE"),"")%></strong>
					</li>
			<%
				}
			%>
		</ul>
	</div>
</div>

<div id="formPreviewDiv"></div>
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
