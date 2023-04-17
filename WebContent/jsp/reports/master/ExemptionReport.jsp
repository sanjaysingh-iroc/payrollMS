<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<style>
.label {
color: #000;
font-size: 14px;
font-weight: 400;
}
</style>  
<script type="text/javascript" charset="utf-8">
$(function(){
	$("body").on('click','#closeButton',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
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

function addNewExamption(financialYear, userscreen, navigationId, toPage, from) {  
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Add New Exemption');
	$.ajax({
		url : 'AddExemption.action?financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&fromPage='+from,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}


function editExamption(exemptionId, financialYear, userscreen, navigationId, toPage, from) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Edit Exemption');
	$.ajax({
		url : 'AddExemption.action?operation=E&exemptionId='+exemptionId+'&financialYear='+financialYear+'&userscreen='+userscreen
				+'&navigationId='+navigationId+'&toPage='+toPage+'&fromPage='+from,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}

function deleteExemption(strAction, financialYear) {
	if(confirm('Are you sure you wish to delete this Exemption?')) {
		$.ajax({
			type : 'POST',
			url  : strAction+"&fromPage=ER",
			success:function(result){
				$("#actionResult").html(result);
			},
			error: function(result){
				$.ajax({
					url: 'ExemptionReport.action?financialYear='+financialYear,
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
		var financialYear = document.getElementById("financialYear").value;
		var userscreen = document.getElementById("userscreen").value;
		var navigationId = document.getElementById("navigationId").value;
		var toPage = document.getElementById("toPage").value;
		var strAction = "ExemptionReport.action?financialYear="+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
		
		$.ajax({
			type : 'POST',
			url  : strAction,
			success:function(result){
				$("#actionResult").html(result);
			}
		});
		//window.location='MyDashboard.action?strCFYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage;
	}
	
	</script>

	</head>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	String strFinancialYearStarts = (String)request.getAttribute("strFinancialYearStart");
	String strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
	String strTitle = "Exemptions for financial year "+strFinancialYearStarts+" to "+strFinancialYearEnd;
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
	/* if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){ */
%>
<%-- <jsp:include page="../../master/AddExemption.jsp" flush="true" />  
<%} %> --%>

 
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
				<s:form name="frmExemption" action="MyDashboard" theme="simple">
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
								<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" 
									onchange="getData('0');" list="financialYearList" key="" />
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
		
	<%=uF.showData((String) session.getAttribute("MESSAGE"), "")%>
	<% session.removeAttribute("MESSAGE"); %>
	
	<div class="col-md-12 col_no_padding">
		<p style="float: right; font-style: italic; font-size: 12px;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>       
    </div>
    
	<div class="col-md-12 col_no_padding" style="margin: 0px 0px 10px;">
		<a href="javascript:void(0)"  onclick="addNewExamption('<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>','ER')" title="Add New Exemption"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Exemption</a>
	</div>
	     
<!-- Place holder where add and delete buttons will be generated -->
	<div class="col-md-12 col_no_padding">
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Exemption Name</th>
					<th style="text-align: left;">Exemption Salary Head</th>
					<th style="text-align: left;">Exemption Description</th>
					<th style="text-align: left;">Exemption Limit</th>
					<th style="text-align: left;">Under Section</th>
					<th style="text-align: left;">In Investment Form</th>
					<th style="text-align: left;">Slab Type</th>
					<th style="text-align: left;" class="no-sort">Action</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { 
				 java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id = <%=cinnerlist.get(0) %> >
					<td><%=cinnerlist.get(1) %></td>
					<td><%=cinnerlist.get(2) %></td>
					<td><%=cinnerlist.get(3) %></td>
					<td><%=cinnerlist.get(4) %></td>
					<td><%=cinnerlist.get(5) %></td>
					<td><%=cinnerlist.get(6) %></td>
					<td><%=cinnerlist.get(7) %></td>
					<td>
						<a href="javascript:void(0)" style="float: left;" onclick="editExamption('<%=cinnerlist.get(0) %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>','ER')" title="Edit Exemption"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						<a href="javascript:void(0)"  onclick="deleteExemption('AddExemption.action?operation=D&exemptionId=<%=cinnerlist.get(0) %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>&financialYear=<%=(String)request.getAttribute("financialYear") %>', '<%=(String)request.getAttribute("financialYear") %>')"  style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
					</td>
				</tr>
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
