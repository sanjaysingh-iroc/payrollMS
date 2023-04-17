<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<style>
.label {
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

	$(document).ready( function () {
		$('#lt1').DataTable();
		
	});
	
			
	function addNewTaxDeductionSlab(financialYear, userscreen, navigationId, toPage) {  
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Add New Tax Deduction Slab');
		$.ajax({
			url : 'AddDeductionTax.action?financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
	}


	function editNewTaxDeductionSlab(deductionId, financialYear, userscreen, navigationId, toPage) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		$('.modal-title').html('Edit Tax Deduction Slab');
		$.ajax({
			url : 'AddDeductionTax.action?operation=E&deductionId='+deductionId+'&financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
	}
			
	function deleteDRTax(strAction) {
		if(confirm('Are you sure you wish to delete this Tax Deduction Slab?')) {
			$.ajax({
				url : strAction,
				cache : false,
				success : function(data) {
					$("#actionResult").html(data);
				},
				error: function(result){
					$.ajax({
						url: 'DeductionReportTax.action',
						cache: true,
						success: function(result){
							$("#actionResult").html(result);
				   		}
					});
				}
			}); 
		}
	}	
	
	function getData(type) {
		var strOrg='';
		var strLevel='';
		/* var financialYear = document.getElementById("financialYear").value;
		var userscreen = document.getElementById("userscreen").value;
		var navigationId = document.getElementById("navigationId").value;
		var toPage = document.getElementById("toPage").value; */
		var form_data = $("#frmTaxDeduction").serialize();
		$.ajax({
			url   : 'DeductionReportTax.action',
			data  :form_data,
			cache : false,
			success : function(data) {
				$("#actionResult").html(data);
			}
		}); 
		//window.location='MyDashboard.action?strCFYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
	}
</script>


<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	
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
				<s:form name="frmTaxDeduction" id = "frmTaxDeduction" action="DeductionReportTax" theme="simple">
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
								<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" 
									headerKey="0" onchange="getData('0');" list="financialYearList" key=""/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
	<% session.setAttribute("MESSAGE", ""); %>
	
	<div class="col-md-12 col_no_padding">
		<p style="float: right; font-style: italic; font-size: 12px;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>       
    </div>
    
	<div class="col-md-12 col_no_padding" style="margin: 0px 0px 10px;">
		<a href="javascript:void(0)"  onclick="addNewTaxDeductionSlab('<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Tax Deduction Slab"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Tax Deduction Slab</a>
	</div>
	
	
<%-- <div id="printDiv" class="left reportWidth">
<p style="float: right; font-style: italic; font-size: 10px;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>
 <div>   
    
    
    <s:form name="frmTaxDeduction" action="DeductionReportTax" theme="simple" cssStyle="float:left">
    <s:hidden name="T" value="M"/>
    <s:hidden name="S" value="M"/>
    
    <label class="label">Select Financial Year</label>
	<s:select label="Select Financial Year" name="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" 
		onchange="document.frmTaxDeduction.submit();" list="financialYearList" key="" />
		
	</s:form> --%>
    
    
<!-- Place holder where add and delete buttons will be generated -->


	<div style="clear: both; margin-left:0px" class="pagetitle">Income Tax slabs for Males</div>

	<table class="table table-bordered" id="lt">
		<thead>
			<tr>
				<th style="text-align: left;">Age From</th>
				<th style="text-align: left;">Age To</th>			
				<th style="text-align: left;">Income From</th>
				<th style="text-align: left;">Income To</th>
				<th style="text-align: left;">Deduction Amount</th>
				<th style="text-align: left;">Deduction Type</th>
				<th style="text-align: left;">Slab Type</th>
				
				<th style="text-align: left;">Action</th>
				<!-- <th style="text-align: left;">Financial Year Start</th>
				<th style="text-align: left;">Financial Year End</th> -->
			</tr>
		</thead>
		<tbody>
		<% java.util.List cOuterList = (java.util.List)request.getAttribute("reportListM"); 
		System.out.println("reportListF:"+cOuterList);

		%>
		 <% for (int i=0; i<cOuterList.size(); i++) { %>
		 <% java.util.List cInnerList = (java.util.List)cOuterList.get(i); %>
			<tr id = <%= cInnerList.get(0) %> >
				<td><%= cInnerList.get(1) %></td>
				<td><%= cInnerList.get(2) %></td>
				<td><%= cInnerList.get(4) %></td>
				<td><%= cInnerList.get(5) %></td>
				<td><%= cInnerList.get(6) %></td>
				<td><%= cInnerList.get(7) %></td>
				<td><%= cInnerList.get(10) %></td>
				
				<td>
					<a href="javascript:void(0)"  onclick="editNewTaxDeductionSlab('<%=cInnerList.get(0) %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Tax Deduction Slab"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					<a href="javascript:void(0)"  onclick="deleteDRTax('AddDeductionTax.action?operation=D&deductionId=<%=cInnerList.get(0) %>&financialYear=<%=(String)request.getAttribute("financialYear") %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
				</td>
				<%-- <td><%= cInnerList.get(8) %></td>
				<td><%= cInnerList.get(9) %></td> --%>
			</tr>
			<% } %>
		</tbody>
	</table> 

	<!-- Place holder where add and delete buttons will be generated -->
	<!-- <div class="add_delete_toolbar2"></div> -->
	<div style="clear: both;margin-left:0px;margin-top: 20px;" class="pagetitle">Income Tax slabs for Females</div>
	<table class="table table-bordered" id="lt1">
		<thead>
			<tr>
				<th style="text-align: left;">Age From</th>
				<th style="text-align: left;">Age To</th>			
				<th style="text-align: left;">Income From</th>
				<th style="text-align: left;">Income To</th>
				<th style="text-align: left;">Deduction Amount</th>
				<th style="text-align: left;">Deduction Type</th>
				<th style="text-align: left;">Slab Type</th>
				<th style="text-align: left;">Action</th>
				<!-- <th style="text-align: left;">Financial Year Start</th>
				<th style="text-align: left;">Financial Year End</th> -->
			</tr>
		</thead>
		<tbody>
		<% cOuterList = (java.util.List)request.getAttribute("reportListF"); 
		System.out.println("reportListF:"+cOuterList);
		%>
		 <% for (int i=0; i<cOuterList.size(); i++) { %>
		 <% java.util.List cInnerList = (java.util.List)cOuterList.get(i); %>
			<tr id = <%= cInnerList.get(0) %> >
				<td><%= cInnerList.get(1) %></td>
				<td><%= cInnerList.get(2) %></td>
				<td><%= cInnerList.get(4) %></td>
				<td><%= cInnerList.get(5) %></td>
				<td><%= cInnerList.get(6) %></td>
				<td><%= cInnerList.get(7) %></td>
				<td><%= cInnerList.get(10) %></td>
				
				<td>
					<a href="javascript:void(0)"  onclick="editNewTaxDeductionSlab('<%=cInnerList.get(0) %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Tax Deduction Slab"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
					<a href="javascript:void(0)"  onclick="deleteDRTax('AddDeductionTax.action?operation=D&deductionId=<%=cInnerList.get(0) %>&financialYear=<%=(String)request.getAttribute("financialYear") %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
				</td>
				<%-- <td><%= cInnerList.get(8) %></td>
				<td><%= cInnerList.get(9) %></td> --%>
			</tr>
			<% } %>
		</tbody>
	</table> 
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
