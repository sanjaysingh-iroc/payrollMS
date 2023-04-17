
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib uri="http://displaytag.sf.net/" prefix="display"%>

<script type="text/javascript" charset="utf-8">
	$(function(){
		var usertype = "<%=((String) session.getAttribute(IConstants.USERTYPE))%>";
		if (usertype == '<%=IConstants.ADMIN%>' 
				|| usertype == '<%=IConstants.CEO%>' 
				|| usertype == '<%=IConstants.CFO%>'
				|| usertype == '<%=IConstants.ACCOUNTANT%>'
				|| usertype == '<%=IConstants.HRMANAGER%>'
				|| usertype == '<%=IConstants.MANAGER%>' ) {
				
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
		} else {
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
		}
	});
	
	
	function submitForm(type){
		var org = document.getElementById("f_org").value;
		var f_strWLocation = document.getElementById("f_strWLocation").value;
		var f_department = document.getElementById("f_department").value;
		var f_level = document.getElementById("f_level").value;
		var paramValues = "";
		if(type == '2') {
			paramValues = '&f_strWLocation='+f_strWLocation+'&f_department='+f_department+'&f_level='+f_level;
		}
		//alert("service ===>> " + service);
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: 'ServiceTaxDependency.action?f_org='+org+paramValues,
			data: $("#"+this.id).serialize(),
			success: function(result){
	        	$("#divResult").html(result);
	   		}
		});
	}
	
</script>

		<!-- <div class="box-header with-border">
			<h3 class="box-title">Service Tax Dependency</h3>
		</div> -->
		
		<!-- /.box-header -->
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->

		<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
		<s:form name="frmLiveEmployee" action="ServiceTaxDependency" theme="simple">
			<div class="row row_without_margin">
				<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
					<i class="fa fa-filter"></i>
				</div>
				<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Organization</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key=""/>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Department</p>
						<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" headerKey="0" headerValue="All Departments"></s:select>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Level</p>
						<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" headerValue="All Levels" listValue="levelCodeName" headerKey="0" list="levelList" key="" required="true" />
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">&nbsp;</p>
						<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitForm('2');" />
					</div>
				</div>
			</div>
		</s:form>
		</div>
	<!-- /.box-body -->
	</div>
				
		<div class="clr margintop20"></div>
		<table class="table table-bordered" id="lt">
			<thead>
				<tr>
					<th style="text-align: left;">Emp Code</th>
					<th style="text-align: left;">Emp Name</th>
					<th style="text-align: left;">Contact No</th>
					<th style="text-align: left;">Department</th>
					<th style="text-align: left;">Emp Type</th>
					<th style="text-align: left;">Service Tax Dependent</th>
				</tr>
			</thead>
			<tbody>
				<%java.util.List couterlist = (java.util.List) request.getAttribute("reportList");%>
				<%for (int i = 0; i < couterlist.size(); i++) { %>
				<%java.util.List cinnerlist = (java.util.List) couterlist.get(i);%>
				<tr id=<%=cinnerlist.get(0)%>>
					<td class="read_only"><%=cinnerlist.get(1)%></td>
					<td><%=cinnerlist.get(2)%></td>
					<td><%=cinnerlist.get(3)%></td>
					<td><%=cinnerlist.get(4)%></td>
					<td><%=cinnerlist.get(5)%></td>
					<td><%=cinnerlist.get(6)%></td>
				</tr>
				<% } %>
			</tbody>
		</table>
	</div>
	<!-- /.box-body -->



