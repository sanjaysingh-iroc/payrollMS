<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script src="scripts/customAjax.js"></script>
<script>
	
	
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
	
	function addDept(userscreen, navigationId, toPage) {  
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Add Department');
		$.ajax({
			url : 'AddDepartment.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	
	
	function editDept(orgid, deptID, userscreen, navigationId, toPage) { 
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html( 'Edit Department');
		$.ajax({
			url : 'AddDepartment.action?orgId='+orgid+'&operation=E&ID='+deptID+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	} 
 
</script>

<style>
.ul_class li{ 
    /* margin: 0px 0px 10px 100px; */
    padding-left: 15px; 
} 
</style> 

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Departments" name="title"/>
</jsp:include> --%>
 
	<div class="box-body">
			
	<% 	UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmFeatureStatus = (Map<String, String>)request.getAttribute("hmFeatureStatus");
		Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
		String strUsertypeId = (String)session.getAttribute(IConstants.BASEUSERTYPEID);
	
		String userscreen = (String)request.getAttribute("userscreen");
		String navigationId = (String)request.getAttribute("navigationId");
		String toPage = (String)request.getAttribute("toPage");
	%>
		
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
			
		<%-- <%if(uF.parseToBoolean((String)request.getAttribute(IConstants.ADD_ACCESS))) { %> --%> 
			<div class="col-md-12">
				<div style="margin: 0px 0px 10px 0px;"> 
					<a href="javascript:void(0);" onclick="addDept('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle"></i>Add New Department</a>
				</div>
			</div>
		<%-- <% } %> --%>
	
			<div class="col-md-12">
				<%=request.getAttribute("departList") %>
		    </div>	
	</div>

	
	<div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">Department Information</h4>
	            </div>
	            <div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
	<script>
			$(function() {
			  $(document.body).on('click', '#btnAddNewRowOk', function() {
				  $("#strOrg").prop('required',true);
				  $("#formAddNewRow_deptCode").prop('required',true);
				  $("#formAddNewRow_deptName").prop('required',true);
			  });
			});
		</script>