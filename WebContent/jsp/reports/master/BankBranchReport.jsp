<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>

<style>
.level_list>li {
padding-top: 5px;
padding-bottom: 5px;
border-bottom: 1px solid rgb(240, 240, 240);
}
</style>

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

function addBank(userscreen, navigationId, toPage) { 
	
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Bank');
	$.ajax({
		url : 'AddBank1.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editBank(strBankId, userscreen, navigationId, toPage) { 
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Bank');
	$.ajax({ 
		url : 'AddBank1.action?operation=E&ID='+strBankId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function addBranch(strBankId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Branch');
	$.ajax({
		url : 'AddBranch.action?param='+strBankId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editBranch(strBankId, strBranchId, userscreen, navigationId, toPage) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Branch');
	$.ajax({
		url : 'AddBranch.action?param='+strBankId+'&operation=E&ID='+strBranchId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}
</script>


<div class="box-body">
<%
	UtilityFunctions uF = new UtilityFunctions();
	Map hmBankData = (Map)request.getAttribute("hmBankData"); 
	Map hmBankBranchData = (Map)request.getAttribute("hmBankBranchData");
	Map hmEmpCount = (Map)request.getAttribute("hmEmpCount");

	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
%>
			
<%=uF.showData((String)session.getAttribute(IConstants.MESSAGE),"") %>
<% session.setAttribute("MESSAGE", ""); %>

	<div class="col-md-12">
		<a href="javascript:void(0)" onclick="addBank('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Bank"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Bank</a>
	</div>
	
	<div class="col-md-12">
		<ul class="level_list">
		<% 
			Set setBank = hmBankData.keySet();
			Iterator it = setBank.iterator();
			
			while(it.hasNext()) {
				String strBankId = (String)it.next();
				List alBankData = (List)hmBankData.get(strBankId);
				if(alBankData == null) alBankData = new ArrayList();
					
					List alBankBranchData = (List)hmBankBranchData.get(strBankId);
					if(alBankBranchData == null)alBankBranchData = new ArrayList();
			%>
					<li>
					<a href="AddBank1.action?operation=D&ID=<%=strBankId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this bank?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
					 <a href="javascript:void(0)" onclick="editBank('<%=strBankId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> 
					 <strong><%=alBankData.get(2)%> [<%=alBankData.get(1)%>]</strong>
					<ul>
					<li class="addnew desgn">
						<a href="javascript:void(0)" onclick="addBranch('<%=strBankId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Branch</a>
					</li>
					
					<%
						for(int d=0; d<alBankBranchData.size(); d+=8) {
							String strBranchId = (String)alBankBranchData.get(d);
					%>
							<li style="margin: 5px 0px;"> 
							   <%if(!uF.parseToBoolean((String)alBankBranchData.get(d+7))) { %>
			                    	<a href="AddBranch.action?operation=D&ID=<%=strBranchId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this branch?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
			                    	<a href="javascript:void(0)" onclick="editBranch('<%=strBankId%>', '<%=strBranchId%>','<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
		                      <% } else {%>
		                    		<a href="#" title="Already in use!"><i class="fa fa-star" aria-hidden="true" style="color: #1ba904;"></i> </a>
		                      <% } %>		
			                     <strong>[<%=alBankBranchData.get(d+2)%>] <%=alBankBranchData.get(d+1)%>  <%=alBankBranchData.get(d+3)%> </strong> Acc/No - <%=alBankBranchData.get(d+6)%>  
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
