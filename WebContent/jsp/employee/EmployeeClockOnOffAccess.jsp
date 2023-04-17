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
		
	});
<% } %>

	function selectall(x, chBoxName) {
	 	var status = x.checked;
	 	<%
		Iterator<String> it1 = hmEmpData.keySet().iterator();
			while(it1.hasNext()) {
			String strEmpId = it1.next();
		%>
		var strId = chBoxName+<%=strEmpId %>;
		if(document.getElementById(strId)) {
			if(status == 'false' || status == false) {
				document.getElementById(strId).checked = '';
				document.getElementById(strId).value = '0';
			} else {
				document.getElementById(strId).checked = 'checked';
				document.getElementById(strId).value = '1';
			}
		}
		<% } %>
	}

	
	function selectOne(x, chBoxName) {
	 	var status = x.checked;
	 	
		if(document.getElementById(chBoxName)) {
			if(status == 'false' || status == false) {
				document.getElementById(chBoxName).value = '0';
			} else {
				document.getElementById(chBoxName).value = '1';
			}
		}
	}
	
	
function exportpdf(){
  window.location="ExportExcelReport.action";
}



function submitForm() {
	document.frmClockOnOffAccess.submit();
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
				<s:form theme="simple" action="ClockOnOffAccessControl" method="post" name="frmClockOnOffAccess" id="frmClockOnOffAccess">
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
							<p style="padding-left: 5px;">Organization</p>
								<s:select theme="simple" name="f_org" id="f_org" listKey="orgId"
                                    listValue="orgName" onchange="document.frmClockOnOffAccess.submit();" list="organisationList" key="" />
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
	boolean isWebAccess = (Boolean) request.getAttribute("isWebAccess");
	boolean isMobAccess = (Boolean) request.getAttribute("isMobAccess");
	boolean isBioAccess = (Boolean) request.getAttribute("isBioAccess");
	
	if(hmEmpData != null && hmEmpData.size() > 0) { %>
		<s:form theme="simple" action="ClockOnOffAccessControl" method="post">
		<div style="float: left; width:96%; margin: 10px 0px; text-align: right;">
			<s:submit name="btnUpdate" value="Update" cssClass="btn btn-primary" cssStyle="margin:0px" />
		</div>
		
		<table id="lt" class="table table-bordered" style="width:100%;margin-top: 10px; clear:both;">
		<thead>
			<tr>
				<th class="alignCenter" nowrap>Employee Code</th>
				<th class="alignCenter" nowrap>Employee Name</th>
				<th class="alignCenter" nowrap>Web<br/><input type="checkbox" onclick="selectall(this,'chboxWeb_')" checked="checked"/></th>
				<th class="alignCenter" nowrap>Mobile<br/><input type="checkbox" onclick="selectall(this,'chboxMob_')" checked="checked"/></th>
				<th class="alignCenter" nowrap>Bio-Matric<br/><input type="checkbox" onclick="selectall(this,'chboxBio_')" checked="checked"/></th>
			</tr>
		</thead>

		<tbody>
			<%
				Iterator<String> it = hmEmpData.keySet().iterator();
					while(it.hasNext()) {
					String strEmpId = it.next();						
					List<String> innerList = hmEmpData.get(strEmpId);
					List<String> innerListAccess = hmEmpAccessData.get(strEmpId);
					String strWebCheck = "";
					String strMobCheck = "";
					String strBioCheck = "";
					if(innerListAccess != null && uF.parseToBoolean(innerListAccess.get(0))) {
						strWebCheck = "checked";
					}
					if(innerListAccess != null && uF.parseToBoolean(innerListAccess.get(1))) {
						strMobCheck = "checked";
					}
					if(innerListAccess != null && uF.parseToBoolean(innerListAccess.get(2))) {
						strBioCheck = "checked";
					}
			%>
			<tr>
				<td class=" alignLeft" nowrap>
					<%=innerList.get(1) %>
					<input type="hidden" name="hideEmpIds" value="<%=innerList.get(0) %>" />
				</td>
				<td class=" alignLeft" nowrap><%=innerList.get(2) %></td>
				<td class="alignCenter"><input type="checkbox" name="chboxWeb_<%=innerList.get(0) %>" id="chboxWeb_<%=innerList.get(0) %>" style="width:10px; height:10px" value="<%=strWebCheck.equals("checked") ? "1" : "0" %>" <%=strWebCheck %> onclick="selectOne(this,'chboxWeb_<%=innerList.get(0) %>')" /></td>
				<td class="alignCenter"><input type="checkbox" name="chboxMob_<%=innerList.get(0) %>" id="chboxMob_<%=innerList.get(0) %>" style="width:10px; height:10px" value="<%=strMobCheck.equals("checked") ? "1" : "0" %>" <%=strMobCheck %> onclick="selectOne(this,'chboxMob_<%=innerList.get(0) %>')"/></td>
				<td class="alignCenter"><input type="checkbox" name="chboxBio_<%=innerList.get(0) %>" id="chboxBio_<%=innerList.get(0) %>" style="width:10px; height:10px" value="<%=strBioCheck.equals("checked") ? "1" : "0" %>" <%=strBioCheck %> onclick="selectOne(this,'chboxBio_<%=innerList.get(0) %>')"/></td>
			</tr>
			<% } %>
					
			<% if (hmEmpData == null || hmEmpData.size() == 0) { %>
				<tr><td colspan="10" class="msg nodata"><span>No employee found for payroll</span></td></tr>
			<% } %>
		</tbody>
	</table>
	</s:form>
	<% } else { %>
		<div class="msg nodata"><span>No employee found for payroll</span></div>
	<% } %>
	</div>
	</div>
	</div>
	</div>

        </section>
    </div>
</section>