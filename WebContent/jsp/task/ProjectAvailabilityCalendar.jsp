<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>


<div id="divResult">

<%String btnSubmit = (String)request.getAttribute("btnSubmit");
	if(btnSubmit == null || btnSubmit.equalsIgnoreCase("null") || btnSubmit.equals("")) {
%>
	<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"> </script> --%>
<% } %>

<script type="text/javascript">
$(document).ready(function() {
	$("body").on('click','#closeButton',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
    });
	$("body").on('click','.close',function() {
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});

	$('#lt').DataTable({
		dom: 'lBfrtip',
		buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
		]
	});
	
});


function viewWorkAllocation(emp_id, curr_date) {
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$(".modal-title").html('Task Summary');
	if($(window).width() >= 900) {
		$('.modal-dialog').width(900);
	}
	$.ajax({
		url : 'ProjectWorkAllocation.action?emp_id='+emp_id+'&curr_date='+curr_date,
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
} 


function submitForm(type) {
	//strProType f_org
	var data = "";
	if(type == '1') {
		var f_org = document.getElementById("f_org").value;
		var strProType = '';
		if(document.getElementById("strProType")) {
			strProType = document.getElementById("strProType").value;
		}
		data = '&f_org='+f_org+'&strProType='+strProType;
	} else if(type == '2') {
		data = $("#frmProjectAvailabilityCalendar").serialize();
	}
	//alert(data);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ResourceLoad.action?btnSubmit=Submit',
		data: data,
		success: function(result){
        	$("#divResult").html(result);
        	$("#f_strWLocation").multiselect().multiselectfilter();
        	$("#f_department").multiselect().multiselectfilter();
        	$("#f_service").multiselect().multiselectfilter();
   		}
	});
}
</script>


<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>

<script type="text/javascript">
$(function(){
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
});    
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	List alDates = (List) request.getAttribute("alDates");
	Boolean poFlag = (Boolean)request.getAttribute("poFlag");
%>

	<section class="content">
		<!-- title row -->
		<div class="row">
		<section class="content">
			<div class="col-lg-12 col-md-12 col-sm-12 box box-body">
				<s:form name="frmProjectAvailabilityCalendar" id="frmProjectAvailabilityCalendar" action="ResourceLoad" theme="simple">
					<s:hidden name="strType" id="strType" />
					<div class="box box-default collapsed-box">
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
									<% if(poFlag) { %>
										<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
											<p style="padding-left: 5px;">Project Type</p> 
											<s:select theme="simple" name="strProType" id="strProType" headerKey="1" headerValue="All Projects"
								                list="#{'2':'My Projects'}" onchange="submitForm('1');"/>
										</div>
									<%} %>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organisation</p> 
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" 
											onchange="submitForm('1');" list="organisationList" />
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">SBU</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"/>
									</div>
								</div>
							</div>
							
							<div class="row row_without_margin" style="margin-top: 10px;">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Month</p>
										<s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:110px !important;" listValue="monthName" headerKey="0" list="monthList" key="" />
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Year</p>
										<s:select theme="simple" name="strYear" listKey="yearsID" cssStyle="width:75px !important;" listValue="yearsName" headerKey="0" list="yearList" key="" />
						      		</div>
						      		
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
									</div>
									
								</div>
							</div>
							
						</div>
					</div>
				</s:form>
			

				<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt">
					<display:column style="text-align:left;" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
					<%
						for (int ii = 0; ii < alDates.size(); ii++) {
							int count = 1 + ii;
							String strDate = uF.getDateFormat((String) alDates.get(ii), IConstants.DATE_FORMAT, "dd");
					%>
						<display:column title="<%=strDate%>" > <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
					<% } %>
				</display:table>
			
				<div>
					<div style="width: 100%; float: left;margin:3px"><div style="width: 20px; height: 100%; text-align: center; background-color: red;float:left">&nbsp;</div><div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocated more than 5</div></div>
					<div style="width: 100%; float: left;margin:3px"><div style="width: 20px; height: 100%; text-align: center; background-color: yellow;float:left">&nbsp;</div><div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocated between 2 - 5</div></div>
					<div style="width: 100%; float: left;margin:3px"><div style="width: 20px; height: 100%; text-align: center; background-color: lightgreen;float:left">&nbsp;</div><div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocated less than 2</div></div>
				</div>
	
			</div>
			</section>
		</div>
	</section>
    
    
    <div class="modal" id="modalInfo" role="dialog">
	    <div class="modal-dialog">
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	                <h4 class="modal-title">-</h4>
	            </div>
	            <div class="modal-body" style="height:auto;overflow-y:auto;padding-left: 25px;">
	            </div>
	            <div class="modal-footer">
	                <button type="button" id="closeButton" class="btn btn-default" data-dismiss="modal">Close</button>
	            </div>
	        </div>
	    </div>
	</div>
	
	
</div>