<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

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
	
	function addAndEditOrientation(operation, orientId, userscreen, navigationId, toPage,strOrg) {
		var titl = 'New';
		if(operation == 'E') {
			titl = 'Edit';
		}
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html(titl+' Orientation');
		$.ajax({
			url : 'AddOrientation.action?operation='+operation+'&ID='+orientId+'&userscreen='+userscreen+'&navigationId='+navigationId
					+'&toPage='+toPage+'&strOrg='+strOrg,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}
	
</script> 

	<%
		UtilityFunctions uF = new UtilityFunctions();
	
		List<List<String>> outerList = (List<List<String>>)request.getAttribute("outerList");
		List<List<String>>  memberList = (List<List<String>>  )request.getAttribute("memberList");
		Map<String,String> mp = (Map<String,String>)request.getAttribute("mp");
		String strOrg = (String)request.getAttribute("strOrg");
		String userscreen = (String)request.getAttribute("userscreen");
		String navigationId = (String)request.getAttribute("navigationId");
		String toPage = (String)request.getAttribute("toPage");
		System.out.println("userscreen==>"+userscreen+"==>navigationId==>"+navigationId+"=>toPage=>"+toPage+"=>strOrg=>"+strOrg);
	%>

	<div class="box-body">
		<div class="col-md-12">
			<a href="javascript:void(0);" onclick="addAndEditOrientation('A', '', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>', '<%=strOrg %>');"> <i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Orientation </a>
		</div>
		
		<s:hidden name="operation" value="A"></s:hidden>
		<div class="col-md-12">
			<ul class="level_list">
			<%
				for(int i=0;outerList!=null && i<outerList.size();i++) {
					List<String> innerList = outerList.get(i);
					StringBuilder sb = null; 
					for(int j=0; memberList!=null && j<memberList.size(); j++) {
						List<String> memberInner = memberList.get(j);
						
						if(mp!=null && mp.get(memberInner.get(2)+"orientation"+innerList.get(0))!=null) {
							if(sb == null) {
								sb = new StringBuilder();
								sb.append(memberInner.get(1));	
							} else {
								sb.append(", "+memberInner.get(1));
							}
						}
					}
					
					if(sb == null) {
						sb = new StringBuilder();
					}
			%>
				<li>
					<% if(uF.parseToInt(innerList.get(0)) > 6) { %><!-- Created By Dattatray Date:08-09-21  -->
						<a href="AddOrientation.action?operation=D&ID=<%=innerList.get(0)%>&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>" onclick="return confirm('Are you sure you wish to delete this orientation?')" style="color:red;"><i class="fa fa-trash" aria-hidden="true"></i></a>
						<a href="javascript:void(0);" onclick="addAndEditOrientation('E', '<%=innerList.get(0)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i> </a> &nbsp;
					<% } %>
					<strong><%=innerList.get(1)%>&deg</strong> 
					<span class="addnew desgn"><%=sb.toString() %></span>
				       
				</li>
			<% } %>
		</ul>
				
    </div>
</div>

<div id="newOrientationDiv"></div>
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