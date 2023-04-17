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


function addDocumentCategory(userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Document Category');
	$.ajax({
		url : 'AddProjectCategory.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}


function editDocumentCategory(proCategoryId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Document Category');
	$.ajax({
		url : 'AddProjectCategory.action?operation=E&proCategoryId='+proCategoryId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
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

Map<String, String> hmOrgName = (Map<String, String>)request.getAttribute("hmOrgName");
Map<String, String> hmCategoryOrgName = (Map<String, String>)request.getAttribute("hmCategoryOrgName");

Map<String, List<Map<String, String>>> hmDocumentCategory = (Map<String, List<Map<String, String>>>)request.getAttribute("hmDocumentCategory");
if(hmDocumentCategory == null) hmDocumentCategory = new HashMap<String, List<Map<String, String>>>();

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
	

<%=uF.showData((String) session.getAttribute("MESSAGE"), "") %>
<% session.setAttribute("MESSAGE", ""); %>


	<!-- *************************************** Document Categories ********************** -->

    <!--  <div style="float: left; width: 100%; margin-left: 15px; margin-top: 15px; color: #346897; font-size: 16px; font-weight: bolder; text-shadow: 0 1px 2px #FFFFFF;">Document Categories</div> -->

	<div style="float:left; margin:15px 0px 0px 15px"> 
		<a href="javascript:void(0)" onclick="addDocumentCategory('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> + Add New Document Category</a>
	</div>

	<div style=";float:left; width:100%">
         <ul class="level_list">
		<% 
		if(hmDocumentCategory != null && !hmDocumentCategory.isEmpty()) {
			Iterator<String> it3 = hmDocumentCategory.keySet().iterator(); 
			while(it3.hasNext()) {
				String strOrgId = (String)it3.next();
				String StrOrgName = hmCategoryOrgName.get(strOrgId);
			%>
			<li> 
			<strong><%=StrOrgName %> </strong>
			<ul>
			<%		
			List<Map<String, String>> alList = (List<Map<String, String>>) hmDocumentCategory.get(strOrgId);
				for(int i = 0; alList != null && i < alList.size();i++) {
				Map<String, String> hmInner = alList.get(i);
					%>
					
				<li>
					<a href="AddProjectCategory.action?operation=D&proCategoryId=<%=hmInner.get("PROJECT_CATEGORY_ID")%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" class="fa fa-trash-o" style="color: red;" onclick="return confirm('Are you sure, you wish to delete this category?')">&nbsp;</a>
					<a href="javascript:void(0);" class="fa fa-edit" onclick="editDocumentCategory('<%=hmInner.get("PROJECT_CATEGORY_ID")%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')">&nbsp;</a>
					Category:&nbsp;<strong><%=hmInner.get("PROJECT_CATEGORY")%></strong>
					<%if(hmInner.get("PROJECT_DESCRIPTION")!=null && !hmInner.get("PROJECT_DESCRIPTION").trim().equals("")) { %>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						Description:&nbsp;<strong><%=hmInner.get("PROJECT_DESCRIPTION")%></strong>
					<% } %>
				</li> 
			<% } %> 
		</ul>
		</li>	
			<% } } %>
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
