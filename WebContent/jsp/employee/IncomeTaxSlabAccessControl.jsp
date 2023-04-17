<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element" %>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%> 
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<style>
.desgn { padding:0px ; border:1px solid #ccc;}
</style>

	<%
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, List<String>> hmEmpData = (Map<String, List<String>>) request.getAttribute("hmEmpData");
		if(hmEmpData == null) hmEmpData = new HashMap<String, List<String>>();
		Map<String, List<String>> hmEmpAccessData = (Map<String, List<String>>) request.getAttribute("hmEmpAccessData");
		if(hmEmpAccessData == null) hmEmpAccessData = new HashMap<String, List<String>>();
	%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script type="text/javascript" charset="utf-8">


<% if(hmEmpData != null && hmEmpData.size() > 0) { %>
$(document).ready( function () {
	$('#lt').DataTable({
		aLengthMenu: [
			[25, 50, 100, 200, -1],
			[25, 50, 100, 200, "All"]
		],
		iDisplayLength: -1,
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ],
        order: [],
		columnDefs: [ {
	      "targets"  : 'no-sort',
	      "orderable": false
	    }]
	});
	
	/* $('#lt').dataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
  	}); */
});
<% } %>

	function selectall(x, chBoxName, slabType) {
	 	var status = x.checked;
	 	
	 	if(slabType=='0') {
		 	<%
			Iterator<String> itt1 = hmEmpData.keySet().iterator();
				while(itt1.hasNext()) {
				String strEmpId = itt1.next();
			%>
			var empId = '<%=strEmpId %>';
			var strId = chBoxName+empId;
			if(document.getElementById(strId)) {
				if(status == 'false' || status == false) {
					document.getElementById('chboxStandardNew_'+empId).value = '1';
					document.getElementById('chboxNew_'+empId).checked = 'checked';
					document.getElementById('chboxStandard_'+empId).checked = '';
				} else {
					document.getElementById('chboxStandardNew_'+empId).value = '0';
					document.getElementById('chboxNew_'+empId).checked = '';
					document.getElementById('chboxAllNew').checked = '';
					document.getElementById('chboxStandard_'+empId).checked = 'checked';
				}
			}
			<% } %>
	 	} else if(slabType=='1') {
		 	<%
			Iterator<String> it1 = hmEmpData.keySet().iterator();
				while(it1.hasNext()) {
				String strEmpId = it1.next();
			%>
			var empId = '<%=strEmpId %>';
			var strId = chBoxName+empId;
			if(document.getElementById(strId)) {
				if(status == 'false' || status == false) {
					document.getElementById('chboxStandardNew_'+empId).value = '0';
					document.getElementById('chboxStandard_'+empId).checked = 'checked';
					document.getElementById('chboxNew_'+empId).checked = '';
				} else {
					document.getElementById('chboxStandardNew_'+empId).value = '1';
					document.getElementById('chboxStandard_'+empId).checked = '';
					document.getElementById('chboxAllStandard').checked = '';
					document.getElementById('chboxNew_'+empId).checked = 'checked';
				}
			}
			<% } %>
	 	} 
	}

	
	function selectOne(x, empId, slabType) {
	 	var status = x.checked;
	 	
		if(status == 'false' || status == false) {
			if(slabType=='0') {
				document.getElementById('chboxStandardNew_'+empId).value = '1';
				document.getElementById('chboxNew_'+empId).checked = 'checked';
			} else if(slabType=='1') {
				document.getElementById('chboxStandardNew_'+empId).value = '0';
				document.getElementById('chboxStandard_'+empId).checked = 'checked';
			}
			
		} else {
			if(slabType=='0') {
				document.getElementById('chboxStandardNew_'+empId).value = '0';
				document.getElementById('chboxNew_'+empId).checked = '';
			} else if(slabType=='1') {
				document.getElementById('chboxStandardNew_'+empId).value = '1';
				document.getElementById('chboxStandard_'+empId).checked = '';
			}
			
		}
	}
	
	
function exportpdf(){
  window.location="ExportExcelReport.action";
}



function submitForm() {
	document.frmIncomeTaxSlabAccessControl.submit();
}

</script>

<script type="text/javascript">
$(function(){
	$("#wLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#level").multiselect().multiselectfilter();
});
</script>

<section class="content">
   <div class="row jscroll">
     <section class="col-lg-12 connectedSortable">
        <div class="box box-primary" style="border-top: 0px;">
           <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
             <div id="printDiv" class="leftbox reportWidth">		
				<s:form theme="simple" action="IncomeTaxSlabAccessControl" method="post" name="frmIncomeTaxSlabAccessControl" id="frmIncomeTaxSlabAccessControl">
					<div class="box box-default collapsed-box" style="margin-top: 10px;">
		                <div class="box-header with-border">
		                    <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
		                    <div class="box-tools pull-right">
		                        <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
		                        <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                    </div>
		                </div>
			                <!-- /.box-header -->
	                <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
	                    <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
					<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Financial Year</p>
							<s:select theme="simple" name="f_strFinancialYear" id="f_strFinancialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key=""/>
						</div>
						
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
                                    listValue="orgName" onchange="document.frmIncomeTaxSlabAccessControl.submit();" list="organisationList" key="" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Location</p>
								<s:select theme="simple" name="wLocation" id="wLocation" listKey="wLocationId" 
	                                listValue="wLocationName" multiple="true" list="wLocationList" key="" />
						</div>
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Department</p>
							<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName"
	                           multiple="true"></s:select>
						</div>
					
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Service</p>
							<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId"
	                           listValue="serviceName" multiple="true"></s:select>
						</div>
						
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Level</p>
							<s:select theme="simple" name="level" id="level" listKey="levelId"
	                           listValue="levelCodeName" multiple="true" list="levelList" key="" />
						</div>
													
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">&nbsp;</p>
							<s:submit value="Submit" cssClass="btn btn-primary"/>
						</div>
											
					</div>
				</div>
         		</div>
      		 </div>
		</s:form>
	
					
	<div style="width:100%;float:left;">
	<% 
	if(hmEmpData != null && hmEmpData.size() > 0) { %>
		<s:form theme="simple" action="IncomeTaxSlabAccessControl" method="post">
		<div style="float: left; width:96%; margin: 10px 0px; text-align: right;">
			<s:submit name="btnUpdate" value="Update" cssClass="btn btn-primary" cssStyle="margin:0px" />
			<s:hidden name="f_strFinancialYear" />
		</div>
		
		<table id="lt" class="table table-bordered" style="width:100%;margin-top: 10px; clear:both;">
		<thead>
			<tr>
				<th nowrap>Employee Code</th>
				<th nowrap>Employee Name</th>
				<th class="alignCenter" nowrap>Standard<br/><input type="checkbox" id="chboxAllStandard" onclick="selectall(this, 'chboxStandard_', '0')"/></th>
				<th class="alignCenter" nowrap>New<br/><input type="checkbox" id="chboxAllNew" onclick="selectall(this, 'chboxNew_', '1')"/></th>
			</tr>
		</thead>

		<tbody>
			<%
				Iterator<String> it = hmEmpData.keySet().iterator();
					while(it.hasNext()) {
					String strEmpId = it.next();						
					List<String> innerList = hmEmpData.get(strEmpId);
					List<String> innerListAccess = hmEmpAccessData.get(strEmpId);
					String strStandardCheck = "";
					String strNewCheck = "";
					String strStandardNewVal = "0";
					if(innerListAccess != null && uF.parseToInt(innerListAccess.get(0))==0) {
						strStandardCheck = "checked";
						strStandardNewVal = "0";
					}
					if(innerListAccess != null && uF.parseToInt(innerListAccess.get(0))==1) {
						strNewCheck = "checked";
						strStandardNewVal = "1";
					}
			%>
			<tr>
				<td class=" alignLeft" nowrap>
					<%=innerList.get(1) %>
					<input type="hidden" name="hideEmpIds" value="<%=innerList.get(0) %>" />
					<input type="hidden" name="chboxStandardNew_<%=innerList.get(0) %>" id="chboxStandardNew_<%=innerList.get(0) %>" value="<%=strStandardNewVal %>" />
				</td>
				<td class=" alignLeft" nowrap><%=innerList.get(2) %></td>
				<td class="alignCenter"><%-- <input type="radio" id="chboxStandard_<%=innerList.get(0) %>" name="chboxStandardNew_<%=innerList.get(0) %>" value="0" <%=strStandardCheck %> onclick="selectOne(this,'chboxStandard_<%=innerList.get(0) %>')" /> --%>
					<input type="checkbox" name="chboxStandard_<%=innerList.get(0) %>" id="chboxStandard_<%=innerList.get(0) %>" style="width:10px; height:10px" <%=strStandardCheck %> onclick="selectOne(this, '<%=innerList.get(0) %>', '0')" />
				</td>
				<td class="alignCenter"><%-- <input type="radio" id="chboxNew_<%=innerList.get(0) %>" name="chboxStandardNew_<%=innerList.get(0) %>" value="1" <%=strNewCheck %> onclick="selectOne(this,'chboxNew_<%=innerList.get(0) %>')" /> --%>
					<input type="checkbox" name="chboxNew_<%=innerList.get(0) %>" id="chboxNew_<%=innerList.get(0) %>" style="width:10px; height:10px" <%=strNewCheck %> onclick="selectOne(this, '<%=innerList.get(0) %>', '1')" />
				</td>
			</tr>
			<% } %>
					
			<% if (hmEmpData == null || hmEmpData.size() == 0) { %>
				<tr><td colspan="10" class="msg nodata"><span>No employee found</span></td></tr>
			<% } %>
		</tbody>
	</table>
	</s:form>
	<% } else { %>
		<div class="msg nodata"><span>No employee found</span></div>
	<% } %>
	</div>
	</div>
	</div>
	</div>

        </section>
    </div>
</section>