<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<style>
.level_list>li{
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
	
	function addDomain(userscreen, navigationId, toPage) { 
		
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Add New Domain');
		$.ajax({
			url : 'AddDomain.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage, 
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
	function editDomain(strDomainId, userscreen, navigationId, toPage) { 
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Edit Service');
		$.ajax({ 
			url : 'AddDomain.action?operation=E&ID='+strDomainId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
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
	
	Map hmProjectDomainMap = (java.util.Map)request.getAttribute("hmProjectDomainMap");
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
%>

<div class="box-body">
<%=uF.showData((String) session.getAttribute("MESSAGE"), "") %>
	
	<div class="col-md-12">
		<a href="javascript:void(0)" onclick="addDomain('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Domain</a>
	</div>
	
	<div class="col-md-12">
		<ul class="level_list">
			<%
			Set<String> setLevelMap = hmProjectDomainMap.keySet();
			Iterator<String> it = setLevelMap.iterator();
			
			while(it.hasNext()) {
				String strDomainId = (String)it.next();
				List<String> alLevel = (List<String>)hmProjectDomainMap.get(strDomainId);
				if(alLevel==null)alLevel = new ArrayList<String>();
			
			%>
				<li>
					<a href="AddDomain.action?operation=D&ID=<%=strDomainId%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" title="Delete Domain" onclick="return confirm('Are you sure you wish to delete this Domain?')" style="color: red;"> <i class="fa fa-trash" aria-hidden="true"></i> </a>
                    <a href="javascript:void(0)" class="edit_lvl" onclick="editDomain('<%=strDomainId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
                    <strong><%=alLevel.get(2)%> [<%=alLevel.get(1)%>]</strong>
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