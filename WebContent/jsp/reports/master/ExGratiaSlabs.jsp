<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		$('#lt').DataTable({
			"order": [],
			"columnDefs": [ {
			      "targets"  : 'no-sort',
			      "orderable": false
			    }],
			'dom': 'lBfrtip',
	        'buttons': [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
	  	});
	});
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
function addExGratia(userscreen, navigationId, toPage, toTab) { 

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add Ex Gratia Slab');
	$.ajax({
		url : 'AddExGratiaSlab.action?userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&toTab='+toTab,  
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function editExGratia(gratiaSlabId, userscreen, navigationId, toPage, toTab) {

	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Ex Gratia Slab');
	$.ajax({
		url : 'AddExGratiaSlab.action?operation=E&ID='+gratiaSlabId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&toTab='+toTab,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>

<%
	UtilityFunctions uF=new UtilityFunctions();
	
	List<Map<String,String>> gratiaSlabList = (List<Map<String,String>>) request.getAttribute("gratiaSlabList");
	if(gratiaSlabList==null) gratiaSlabList = new ArrayList<Map<String,String>>();
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	String toTab = (String)request.getAttribute("toTab");
	
%>
						
	<div class="box box-none nav-tabs-custom">
		<ul class="nav nav-tabs">
			<%
				String strLabel = "";
				if(toTab == null || toTab.trim().equals("") || toTab.trim().equalsIgnoreCase("NULL") || toTab.trim().equalsIgnoreCase("EGS")) { 
			%> 
				<li class="active"><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGS&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Slab</a></li>
				<li><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGP&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Policy</a></li>
			<% } else if(toTab != null && toTab.trim().equalsIgnoreCase("EGP")) {%>
				<li><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGS&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Slab</a></li>
				<li class="active"><a href="javascript:void(0)" onclick="window.location='MyDashboard.action?toTab=EGP&userscreen=<%=userscreen %>&navigationId=<%=navigationId %>&toPage=<%=toPage %>'" data-toggle="tab">Ex Gratia Policy</a></li>
			<% } %>	
		</ul>
		<%=uF.showData((String) session.getAttribute("MESSAGE"), "") %>
		<% session.setAttribute("MESSAGE", ""); %>
		<div style="float:left; margin:10px 0px 0px 0px; width: 50%;"> 
			<a href="javascript:void(0)" onclick="addExGratia('<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>', '<%=toTab %>');"><i class="fa fa-plus-circle" aria-hidden="true"></i>Add New Ex Gratia Slab</a>
		</div>

	<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
		<table class="table table-bordered" id="lt" style="width:100%">
			<thead>
				<tr>
					<th style="text-align: left;">Slabs</th>
					<th style="text-align: left;">From</th>
					<th style="text-align: left;">To</th>
					<th style="text-align: left;">Percentage</th>
					<th style="text-align: left;">Action</th>
				</tr>
			</thead>
			<tbody>
				<%	for(int i=0;gratiaSlabList!=null && i<gratiaSlabList.size();i++) { 
					Map<String,String> hmInner = (Map<String,String>) gratiaSlabList.get(i);
				%>
				<tr>
					<td><%=uF.showData(hmInner.get("EX_GRATIA_SLAB"),"") %></td>
					<td><%=uF.showData(hmInner.get("SLAB_FROM"),"") %></td>
					<td><%=uF.showData(hmInner.get("SLAB_TO"),"") %></td>
					<td><%=uF.showData(hmInner.get("SLAB_PERCENTAGE"),"") %></td>
					<td>
                    <a href="AddExGratiaSlab.action?operation=D&ID=<%=hmInner.get("GRATIA_SLAB_ID")%>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>" title="Delete Gratia Slab"  onclick="return confirm('Are you sure you wish to delete this slab?')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
                    <a href="javascript:void(0);" onclick="editExGratia('<%=hmInner.get("GRATIA_SLAB_ID")%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>', '<%=toTab %>');" title="Edit Gratia Slab"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a> 
					</td>
				</tr>
				<% } %>
			</tbody>
		</table>
	</div>
	<div style="margin:10px 0px 0px 0px ;float:left; width:100%">
		<p style="font-weight:bold">Calculation</p>
		<p style="padding: 4px;">
			<strong>Steps</strong><br/>
			1. TotalExGratiaAmount = (Net Profit * slab percentage)/100<br/>
			2. TotalEmployeeBasicDA (for all salaries paid in financial year) = Total no of employees * (basic+DA)<br/>
			3. EmployeeExGratia = (TotalExGratiaAmount / TotalEmployeeBasicDA) * 100<br/>
			4. IndividualEmployeeBasicDA (for all salaries paid in financial year) = (basic+DA)<br/>
			5. EmployeeExGratiaAmount = (IndividualEmployeeBasicDA * EmployeeExGratia) /100.			
		</p>
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

