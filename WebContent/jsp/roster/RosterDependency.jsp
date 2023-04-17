<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://displaytag.sf.net/" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<% 
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String) request.getAttribute(IConstants.TITLE);
%>

<script type="text/javascript" charset="utf-8"> 
$(document).ready( function () {
	var usertype = "<%= ((String)session.getAttribute(IConstants.USERTYPE)) %>";
	if (usertype != '<%=IConstants.EMPLOYEE%>') {
				
	} else {
		
	}

});

function submitForm(){
	document.frm_RosterDependancy.submit();
}

</script>

<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	
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


function submitForm(type){
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'RosterDependency.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}


function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		if (choice.options[i].selected == true) {
			if (j == 0) {
				exportchoice = choice.options[i].value;
				j++;
			} else {
				exportchoice += "," + choice.options[i].value;
				j++;
			}
		}
	}
	return exportchoice;
}


</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="<%=strTitle %>" name="title"/>
</jsp:include> --%>
 
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="box box-primary" style="border-top-color: #EEEEEE;">  <!-- collapsed-box -->
           <%-- <div class="box-header with-border">
               <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
               <div class="box-tools pull-right">
                   <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                   <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
               </div>
           </div> --%>
           <!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<s:form name="frm_RosterDependancy" id="frm_RosterDependancy" action="RosterDependency" theme="simple">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter" aria-hidden="true"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Location</p>
								<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Department</p>
								<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">SBU</p>
								<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Level</p>
								<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
							</div>
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">&nbsp;</p>
								<input type="button" name="Submit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
		</div>
	
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Emp Code</th>
					<th style="text-align: left;">Emp Name</th>
					<th style="text-align: left;">Contact No</th>
					<th style="text-align: left;">Department</th>
					<th style="text-align: left;">Emp Type</th>
					<th style="text-align: left;">Roster Validations</th>
					<th style="text-align: left;" class="no-sort">Facts</th>
				</tr>
			</thead>
			<tbody>
			<% java.util.List couterlist = (java.util.List)request.getAttribute("reportList"); %>
			<% for (int i=0; i<couterlist.size(); i++) { %>
			<% java.util.List cinnerlist = (java.util.List)couterlist.get(i); %>
				<tr id = <%=cinnerlist.get(0) %> >
					<td class="read_only"><%=cinnerlist.get(1) %></td>
					<td><%=cinnerlist.get(2) %></td>
					<td><%=cinnerlist.get(3) %></td>
					<td><%=cinnerlist.get(4) %></td>
					<td><%=cinnerlist.get(5) %></td>
					<td><%=cinnerlist.get(6) %></td>
					<td><%=cinnerlist.get(7) %></td>
				</tr>
			<% } %>
			</tbody>
		</table>
	</div>
	<!-- /.box-body -->


