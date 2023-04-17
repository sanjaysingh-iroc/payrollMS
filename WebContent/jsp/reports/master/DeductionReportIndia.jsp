<%@page import="bsh.util.Util"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<style>
.label{
color: #000;
font-size: 14px;
font-weight: 400;
}
</style>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script type="text/javascript" charset="utf-8">

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


function addNewPTSlab(financialYear, userscreen, navigationId, toPage) {  
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New PT Slab');
	$.ajax({
		url : 'AddDeductionIndia.action?financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}

function deletePTSlab(strAction) {
	if(confirm('Are you sure you wish to delete this professional tax slab?')) {
		$.ajax({
			url : strAction,
			cache : false,
			success : function(result) {
				$("#actionResult").html(result);
			},
			error: function(result){
				$.ajax({
					url: 'DeductionReportIndia.action',
					cache: true,
					success: function(result){
						$("#actionResult").html(result);
			   		}
				});
			}
		}); 
	
	}
}

function editPTSlab(deductionId, financialYear, userscreen, navigationId, toPage) {  
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit PT Slab');
	$.ajax({
		url : 'AddDeductionIndia.action?operation=E&deductionId='+deductionId+'&financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}

$(document).ready(function () {
	$('#lt').DataTable();
});
			
	</script>
</head>

<!-- Custom form for adding new records -->

 <%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
 
 	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
 
	
 %>
 
<%-- <jsp:include page="../../policies/AddDeductionIndia.jsp" flush="true" /> --%>


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
				<s:form name="frmTaxDeduction" id="frmTaxDeduction" action="DeductionReportIndia" theme="simple">
					<s:hidden name="userscreen" id="userscreen" />
					<s:hidden name="navigationId" id="navigationId" />
					<s:hidden name="toPage" id="toPage" />

					<div style="float: left; width: 99%; margin-left: 10px;">
						<div style="float: left; margin-right: 5px;">
							<i class="fa fa-filter"></i>
						</div>
						<div style="float: left; width: 75%;">
							<div style="float: left; margin-left: 10px;">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select label="Select Financial Year" name="financialYear" id = "financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" 
									onchange="submitForm();" list="financialYearList" key="" />
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String) request.getAttribute("MESSAGE"), "")%>
	
	<div class="col-md-12 col_no_padding">
		<p style="float: right; font-style: italic; font-size: 12px;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>       
    </div>
	<div class="col-md-12 col_no_padding" style="margin-bottom: 10px;">
		<a href="javascript:void(0)"  onclick="addNewPTSlab('<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New PT Slab"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New PT Slab</a>
	</div>
	
	<div style="clear: both; margin-left:0px" class="pagetitle">Professional Tax slabs for Males</div>
	<div class="col-md-12 col_no_padding">
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Income From</th>
					<th style="text-align: left;">Income To</th>
					<th style="text-align: left;">Deduction per paycycle</th>
					<th style="text-align: left;">Deduction Amount</th>
					<th style="text-align: left;">State</th>
					<th style="text-align: left;">Actions</th>
					<!-- <th style="text-align: left;">Financial year end</th> -->
				</tr>
			</thead>
			<tbody>
			<% 	List<List<String>> cOuterList = (List<List<String>>)request.getAttribute("reportListM");
				if(cOuterList == null) cOuterList = new ArrayList<List<String>>();
			 	for (int i=0; i<cOuterList.size(); i++) {
			 		List<String> cInnerList = (List<String>)cOuterList.get(i); 
			 %>
					<tr id = <%= cInnerList.get(0) %> >
						<td> <%= cInnerList.get(1) %></td>
						<td> <%= cInnerList.get(2) %></td>
						<td> <%= cInnerList.get(3) %></td>
						<td> <%= cInnerList.get(4) %></td>
						<td> <%= cInnerList.get(5) %></td>
						<td> 
							<a href="javascript:void(0)"  onclick="editPTSlab('<%=cInnerList.get(0) %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit PT Slab"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<a href="javascript:void(0)"  onclick="deletePTSlab('AddDeductionIndia.action?operation=D&deductionId=<%=cInnerList.get(0) %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>')"  style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
						</td>
					</tr>
				<% } 
			 	if(cOuterList.size() == 0) {
				%>
					<tr><td colspan="6" align="center">No data available in table</td></tr>
				<% } %>
			</tbody>
		</table> 
	</div>
	
	<div style="clear: both; margin-left:0px" class="pagetitle">Professional Tax slabs for Females</div>
	<div class="col-md-12 col_no_padding">
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Income From</th>
					<th style="text-align: left;">Income To</th>
					<th style="text-align: left;">Deduction per paycycle</th>
					<th style="text-align: left;">Deduction Amount</th>
					<th style="text-align: left;">State</th>
					<th style="text-align: left;">Actions</th>
				</tr>
			</thead>
			<tbody>
			<% 	cOuterList = (List<List<String>>)request.getAttribute("reportListF");
				if(cOuterList == null) cOuterList = new ArrayList<List<String>>();
				for (int i=0; i<cOuterList.size(); i++) { 
					List<String> cInnerList = (List<String>)cOuterList.get(i); 
			%>
					<tr id = <%= cInnerList.get(0) %> >
						<td> <%= cInnerList.get(1) %></td>
						<td> <%= cInnerList.get(2) %></td>
						<td> <%= cInnerList.get(3) %></td>
						<td> <%= cInnerList.get(4) %></td>
						<td> <%= cInnerList.get(5) %></td>
						<td> 
							<a href="javascript:void(0)"  onclick="editPTSlab('<%=cInnerList.get(0) %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit PT Slab"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
							<a href="javascript:void(0)"  onclick="deletePTSlab('AddDeductionIndia.action?operation=D&deductionId=<%=cInnerList.get(0) %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>')"  style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
						</td>
					</tr>
				<% }
				if(cOuterList.size() == 0) {
				%>
					<tr><td colspan="6" align="center">No data available in table</td></tr>
				<% } %>
			</tbody>
		</table> 
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

<script>

function submitForm() {
	 var form_data = $("#frmTaxDeduction").serialize();
	// console.log("form_data==>"+form_data);
	 $.ajax({
		type : 'POST',
		url  : 'DeductionReportIndia.action',
		data : form_data,
		success:function(result){
			$("#actionResult").html(result);
		}
	}); 
}

   
</script>
