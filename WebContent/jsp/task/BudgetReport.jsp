<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<div id="divResult">

<script type="text/javascript" charset="utf-8">

$(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
}); 
	
		/* $(document).ready(function() {
			$("#f_strWLocation").multiselect().multiselectfilter();
			$("#f_department").multiselect().multiselectfilter();
			$("#f_service").multiselect().multiselectfilter();
			$("#f_level").multiselect().multiselectfilter();
		}); */
	
		function submitForm(type) {
			
			document.frm_BudgetReport.exportType.value='';
			var org = document.getElementById("f_org").value;
			/* var strMonth = document.getElementById("strMonth").value; */
			var financialYear = document.getElementById("financialYear").value;
			var location = getSelectedValue("f_strWLocation");
			var department = getSelectedValue("f_department");
			var service = getSelectedValue("f_service");
			var level = getSelectedValue("f_level");
			var paramValues = "";
			if(type == '2') {
				/* paramValues = '&strLocation='+location+'&strMonth='+strMonth+'&financialYear='+financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level; */
				paramValues = '&strLocation='+location+'&financialYear='+financialYear+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level;
			}
			//alert("paramValues ===>> " + paramValues);
			$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
			$.ajax({
				type : 'POST',
				url: 'BudgetReport.action?f_org='+org+paramValues,
				data: $("#"+this.id).serialize(),
				success: function(result){
		        	//console.log(result);
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
		
		function generateReportExcel(){
			document.frm_BudgetReport.exportType.value='excel';
			document.frm_BudgetReport.submit();
		}
	
	    
	</script>
	
<script type="text/javascript">
	$(function(){
		
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
	});    
</script>
	
	<%
		List alOuter = (List)request.getAttribute("alOuter");
	List headerList = (List)request.getAttribute("headerList");
	%>
	
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="desgn" style="margin-bottom: 5px; color:#232323;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-top: 10px;">
				<div class="box-header with-border">
					<h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
					<div class="box-tools pull-right">
						<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
						<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
					<s:form name="frm_BudgetReport" id="frm_BudgetReport" action="BudgetReport" theme="simple" method="post">
						<s:hidden name="exportType"></s:hidden>
						<div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
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
									<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Service</p>
									<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
								</div>
								
							</div>
						</div><br>
						 <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" />
					      		</div>
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Month</p>
									<s:select theme="simple" name="strMonth" id="strMonth" headerKey="0" headerValue="Select Month" listKey="monthId" listValue="monthName" list="monthList" key="" />
								</div> --%>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
					</s:form>
				</div>
	          <!-- /.box-body -->
			</div>
		</div>
		
		<div class="box-body">
			<div class="col-md-2 pull-right">
					<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
			</div>
		</div>
		
		<%-- <div class="box-body" style="padding: 5px; overflow-y: auto;">
			<table cellspacing="1" class="table table-bordered" id=lt1>
				<!-- <tr rowspan="3">
					<th class="alignLeft" >Partner</th>
				</tr> -->
				<tr>
					<th class="alignLeft" rowspan="3" >Partner</th>
					<th class="alignLeft" >Last Year</th>
					<th class="alignLeft" >Current Year</th>
					<th class="alignLeft" >Current Year</th>
					<th class="alignLeft" >Next Year</th>
					<th class="alignLeft" >Next Year</th>
					<th class="alignLeft" >Gap</th>
				</tr>
				<tr>
					<!-- <th></th> -->
					<th class="alignLeft" >Actual</th>
					<th class="alignLeft" >Budget</th>
					<th class="alignLeft" >Commitment</th>
					<th class="alignLeft" >Budget</th>
					<th class="alignLeft" >Commitment</th>
					<th class="alignLeft" ></th>
				</tr>
				<tr>
					<!-- <th></th> -->
					<th class="alignLeft" >2020-21</th>
					<th class="alignLeft" ><%=(String)strYears.get(0)%></th>
					<th class="alignLeft" ><%=(String)strYears.get(0)%></th>
					<th class="alignLeft" ><%=(String)strYears.get(1)%></th>
					<th class="alignLeft" ><%=(String)strYears.get(1)%></th>
					<th class="alignLeft" ></th>
				</tr>
				
				<%for(int i=0; i<alOuter.size(); i++){ %>
					<tr>
					<%
						List innerList = (List)alOuter.get(i);
						
					 for(int j=0; j<innerList.size(); i++){
					%>
						<td class="alignLeft"><%=innerList.get(0) %></td>
						<td class="alignRight"></td>
						<td class="alignRight"><%=innerList.get(1) %></td>
						<td class="alignRight"><%=innerList.get(2) %></td>
						<td class="alignRight"><%=innerList.get(3) %></td>
						<td class="alignRight"><%=innerList.get(4) %></td>
						<td class="alignRight"></td>
					<%} %>
					</tr>
				<%} %>
			</table>
		</div> --%>
		
		<display:table name="alOuter" cellspacing="1" class="table table-bordered" id="lt1">
        	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Partner"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="<%=(String)headerList.get(0)%>" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="<%=(String)headerList.get(1)%>" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="<%=(String)headerList.get(2)%>" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="<%=(String)headerList.get(3)%>" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="<%=(String)headerList.get(4)%>" ><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
        	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Gap"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
        	
        </display:table>
	
	</div>
</div>