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
function addProductionLine(strOrg,userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Production Line');
	$.ajax({
		url : 'AddProductionLine.action?strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editProductionLine(plId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Production Line');
	$.ajax({
		url : 'AddProductionLine.action?operation=E&ID='+plId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function addAlignSalaryHead(plId,strOrg,userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Align Salary Head');
	$.ajax({
		url : 'AddAlignSalaryHead.action?productionLineId='+plId+'&strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

function editAlignSalaryHead(alignHeadId,plId,strOrg,userscreen, navigationId, toPage) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Align Salary Head');
	$.ajax({
		url : 'AddAlignSalaryHead.action?operation=E&ID='+alignHeadId+'&productionLineId='+plId+'&strOrg='+strOrg+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 

</script>

		
<%
	UtilityFunctions uF = new UtilityFunctions();
	
	List<Map<String, String>> prodLineList = (List<Map<String, String>>)request.getAttribute("prodLineList"); 
	if(prodLineList == null) prodLineList = new ArrayList<Map<String, String>>();	
	Map<String, List<Map<String, String>>> hmProdSalaryHeads = (Map<String, List<Map<String, String>>>)request.getAttribute("hmProdSalaryHeads");
	if(hmProdSalaryHeads == null) hmProdSalaryHeads = new HashMap<String, List<Map<String, String>>>();
	
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
								<s:select list="orgList" name="strOrg" listKey="orgId" listValue="orgName" onchange="document.frm.submit();"></s:select>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
	<% session.setAttribute(IConstants.MESSAGE, ""); %>
		
	<div class="col-md-12">
		<a href="javascript:void(0);" onclick="addProductionLine('<%=(String)request.getAttribute("strOrg") %>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Production Line</a>
	</div>
	
	<div class="col-md-12">
		<ul class="level_list">
			<%		
				int nProdLineList = prodLineList.size(); 
				for(int i=0; i<prodLineList.size(); i++) {
					Map<String, String> hmInnerPL = prodLineList.get(i);
			%>
					<li>
						<a href="AddProductionLine.action?operation=D&strOrg=<%=hmInnerPL.get("PRODUCTION_LINE_ORG_ID") %>&ID=<%=hmInnerPL.get("PRODUCTION_LINE_ID")%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color: red;" onclick="return confirm('Are you sure you wish to delete this production line?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
						<a href="javascript:void(0);" onclick="editProductionLine('<%=hmInnerPL.get("PRODUCTION_LINE_ID")%>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						Code: <strong><%=uF.showData(hmInnerPL.get("PRODUCTION_LINE_CODE"),"")%></strong> &nbsp;&nbsp;
						Name: <strong><%=uF.showData(hmInnerPL.get("PRODUCTION_LINE_NAME"),"")%></strong>
						<ul style="width: 100%;">
							<li class="addnew desgn">
								<a href="javascript:void(0);" onclick="addAlignSalaryHead('<%=hmInnerPL.get("PRODUCTION_LINE_ID")%>','<%=hmInnerPL.get("PRODUCTION_LINE_ORG_ID") %>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Align Salary Head</a>
							</li>
							<%
							List<Map<String, String>> alPLHeadsList = hmProdSalaryHeads.get(hmInnerPL.get("PRODUCTION_LINE_ID"));
							if(alPLHeadsList == null) alPLHeadsList = new ArrayList<Map<String,String>>();
							for(int j=0; j<alPLHeadsList.size(); j++) {
								Map<String, String> hmPLHeads = alPLHeadsList.get(j);
							%>
								<li>
									<a href="AddAlignSalaryHead.action?operation=D&strOrg=<%=hmInnerPL.get("PRODUCTION_LINE_ORG_ID") %>&ID=<%=hmPLHeads.get("PRODUCTION_LINE_HEAD_ID")%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" style="color: red;" onclick="return confirm('Are you sure you wish to delete this production line heads?')"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
									<a href="javascript:void(0);" onclick="editAlignSalaryHead('<%=hmPLHeads.get("PRODUCTION_LINE_HEAD_ID")%>','<%=hmInnerPL.get("PRODUCTION_LINE_ID")%>','<%=hmInnerPL.get("PRODUCTION_LINE_ORG_ID") %>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
									Level: <strong><%=uF.showData(hmPLHeads.get("PRODUCTION_LINE_LEVEL_NAME"),"")%> [<%=uF.showData(hmPLHeads.get("PRODUCTION_LINE_LEVEL_CODE"),"")%>]</strong> &nbsp;&nbsp;
									Salary Heads: <strong><%=uF.showData(hmPLHeads.get("PRODUCTION_LINE_SALARY_HEAD"),"")%></strong>
								</li>
							<%} %>
						</ul> 						
					</li> 
					
			<%	} %> 			
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