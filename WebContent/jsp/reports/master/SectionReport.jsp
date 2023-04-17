<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

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


function addNewSection(financialYear, userscreen, navigationId, toPage) {  
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	var height = $(window).height()* 0.85;
	var width = $(window).width()* 0.55;
	$(".modal-dialog").css("height", height);
	$(".modal-dialog").css("width", width);
	$(".modal-dialog").css("max-height", height);
	$(".modal-dialog").css("max-width", width);
	$('.modal-title').html('Add New Section');
	$.ajax({
		url : 'AddSection.action?financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	}); 
}


	function editSection(sectionId, financialYear, userscreen, navigationId, toPage) {
		var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$("#modalInfo").show();
		var height = $(window).height()* 0.85;
		var width = $(window).width()* 0.55;
		$(".modal-dialog").css("height", height);
		$(".modal-dialog").css("width", width);
		$(".modal-dialog").css("max-height", height);
		$(".modal-dialog").css("max-width", width);
		$('.modal-title').html('Edit Section');
		$.ajax({
			url : 'AddSection.action?operation=E&sectionId='+sectionId+'&financialYear='+financialYear+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		}); 
	}
	
	function deleteSection(strAction) {
		if(confirm('Are you sure you wish to delete this Section?')) {
			$.ajax({
				type:'POST',
				url: strAction,
				cache : false,
				success:function(data) {
					$("#actionResult").html(data);
				},
				error: function(result){
					$.ajax({
						url: 'SectionReport.action',
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
		var form_data = $("#frm_SectionReport").serialize();
		$.ajax({
			type:'POST',
			url: 'SectionReport.action',
			data : form_data,
			cache : false,
			success:function(data) {
				$("#actionResult").html(data);
			}
		});
	}
	
	
	
			
</script>

</head>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strUserType = (String)session.getAttribute(IConstants.USERTYPE);
	
	String userscreen = (String)request.getAttribute("userscreen");
	String navigationId = (String)request.getAttribute("navigationId");
	String toPage = (String)request.getAttribute("toPage");
	
	/* if(strUserType!=null && (strUserType.equalsIgnoreCase(IConstants.ADMIN) || strUserType.equalsIgnoreCase(IConstants.CEO)
				|| strUserType.equalsIgnoreCase(IConstants.CFO) || strUserType.equalsIgnoreCase(IConstants.HRMANAGER))){ */
%>

<!-- Custom form for adding new records -->
<%-- <jsp:include page="../../master/AddSection.jsp" flush="true" /> --%>  
<%-- <%} %> --%>
 
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
				<s:form name="frm_SectionReport" action="SectionReport" id="frm_SectionReport" theme="simple">
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
	<% session.removeAttribute("MESSAGE"); %>
	
	<div class="col-md-12 col_no_padding">
		<p style="float: right; font-style: italic; font-size: 12px;">Last updated by <%=uF.showData((String)request.getAttribute("UPDATED_NAME"), "N/A") %> on <%=uF.showData((String)request.getAttribute("UPDATED_DATE"), "N/A") %></p>       
    </div>
    
	<div class="col-md-12 col_no_padding" style="margin: 0px 0px 10px;">
		<a href="javascript:void(0)"  onclick="addNewSection('<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Add New Section"><i class="fa fa-plus-circle" aria-hidden="true"></i> Add New Section</a>
	</div>
	

	<%-- <div class="filter_div">
		<div class="filter_caption">Filter</div>
		<s:form name="frm_SectionReport" action="SectionReport" theme="simple">
			<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" 
				headerKey="0" onchange="document.frm_SectionReport.submit();" list="financialYearList" key="" cssStyle="width:200px;"/>
		</s:form>
	</div> --%>

    
        
<!-- Place holder where add and delete buttons will be generated -->
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Section Code</th>
					<th style="text-align: left;">Section Description</th>
					<th style="text-align: left;">Section Exemption Limit</th>
					<th style="text-align: left;">Section Limit Type</th>
					<th style="text-align: left;">Under Chapter</th>
					<th style="text-align: left;">Slab Type</th>
					 <th style="text-align: left;" class="no-sort">Action</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			 <% for (int i=0; couterlist!=null && i<couterlist.size(); i++) { %>
			 <% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id = <%= cinnerlist.get(0) %> >
					<td><%=  cinnerlist.get(1) %></td>
					<td><%=  cinnerlist.get(2) %></td>
					<td><%=  cinnerlist.get(3) %></td>
					<td><%=  cinnerlist.get(4) %></td>
					<td><%=  cinnerlist.get(5) %></td>
					<td><%=  cinnerlist.get(6) %></td>
					<td>
						<a href="javascript:void(0)" style="float: left;" onclick="editSection('<%=cinnerlist.get(0) %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')" title="Edit Section"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
						<a href="javascript:void(0)"  onclick="deleteSection('AddSection.action?operation=D&sectionId=<%=cinnerlist.get(0) %>&financialYear=<%=(String)request.getAttribute("financialYear") %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>')" style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
					</td>
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
