<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g"%>

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Investment Report" name="title" />
</jsp:include> --%>


<g:compress>

<script>
  
function getEmpInvestmentDetails(emp_id, fy_from, fy_to) {

	var dialogEdit = '.modal-body'; 
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Investment details');
	$.ajax({
		url : "InvestmentDetails.action?emp_id="+emp_id+"&fy_from="+fy_from+"&fy_to="+fy_to,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>
</g:compress>


<script type="text/javascript">

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
		
		$("#f_strWLocation").multiselect().multiselectfilter();
		$("#f_department").multiselect().multiselectfilter();
		$("#f_service").multiselect().multiselectfilter();
		$("#f_level").multiselect().multiselectfilter();
		
		$('#lt1').DataTable({
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
		var f_strFinancialYear = document.getElementById("f_strFinancialYear").value;
		var location = getSelectedValue("f_strWLocation");
		var department = getSelectedValue("f_department");
		var service = getSelectedValue("f_service");
		var level = getSelectedValue("f_level");
		var paramValues = "";
		if(type == '2') {
			paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level
				+'&f_strFinancialYear='+f_strFinancialYear;
		}
		
		var action = 'InvestmentReport.action?f_org='+org+paramValues; 
		
		$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$.ajax({
			type : 'POST',
			url: action,
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

<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
	<div class="box box-default collapsed-box">
		<div class="box-header with-border">
   			<h3 class="box-title"><%=(String)request.getAttribute("selectedFilter") %></h3>
    		<div class="box-tools pull-right">
        		<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
        		<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
			</div>
		</div>
		<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
		<s:form name="frmAdditionalHours" action="InvestmentReport" theme="simple">
			<div class="row row_without_margin">
				<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
					<i class="fa fa-filter"></i>
				</div>
				<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Financial Year</p>
						<s:select theme="simple" name="f_strFinancialYear" id="f_strFinancialYear" listKey="financialYearId" listValue="financialYearName" list="financialYearList" key="" onchange="submitForm('2');"/>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Organisation</p>
						<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Location</p>
						<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true" />
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Department</p>
						<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" key="" multiple="true"></s:select>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Service</p>
						<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"></s:select>
					</div>
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Level</p>
						<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true" />
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
		<display:table name="alReport" cellspacing="1" class="table tabled-bordered" id="lt1">
			<display:column style="padding-left:10px" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
			<display:column style="padding-right:10px" nowrap="nowrap" title="Declared Amount"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
			<display:column style="padding-right:10px" nowrap="nowrap" title="Approved Amount"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
			<display:column  media="html" nowrap="nowrap" title="Details"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
		</display:table>
	</div>
<!-- /.box-body -->

<div class="modal" id="modalInfo" role="dialog">
	<div class="modal-dialog">
        <!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Candidate Information</h4>
			</div>
			<div class="modal-body" style="height:400px;overflow-y:auto;padding-left: 25px;"></div>
			<div class="modal-footer">
				<button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
			</div>
		</div>
	</div>
</div>
