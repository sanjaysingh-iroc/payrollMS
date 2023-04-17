<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
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

	$('#lt').DataTable({
		dom: 'lBfrtip',
		buttons: []
	});
	
});


function submitForm(type) {
	//strProType f_org
	var data = "";
	if(type == '1') {
		var f_org = document.getElementById("f_org").value;
		data = '&f_org='+f_org;
	} else if(type == '2') {
		data = $("#frm_Engagement").serialize();
	}
	//alert(data);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ProjectEngagementReport.action?btnSubmit=Submit',
		data: data,
		success: function(result){
        	$("#divResult").html(result);
        	$("#f_strWLocation").multiselect().multiselectfilter();
        	$("#f_department").multiselect().multiselectfilter();
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
	});    
</script>

<%
	UtilityFunctions uF = new UtilityFunctions();
	String strTitle = (String)request.getAttribute(IConstants.TITLE); 

	List<String> alDates = (List<String>)request.getAttribute("alDates");
	if(alDates == null) alDates = new ArrayList<String>();
	
	String paramSelection = (String) request.getAttribute("paramSelection");
	String strColumnTitle = "";
	if(paramSelection!=null && paramSelection.trim().equalsIgnoreCase("ORG")){
		strColumnTitle = "Organization";
	} else {
		strColumnTitle = "SBU";
	}
%>


<!-- Custom form for adding new records -->

<section class="content">
		<!-- title row -->
	<div class="row">
		<section class="content">
			<div class="col-lg-12 col-md-12 col-sm-12 box box-body">

				<s:form name="frm_Engagement" id="frm_Engagement" action="ProjectEngagementReport" theme="simple">
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
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Organisation</p> 
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" />
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" list="departmentList" listKey="deptId" listValue="deptName" multiple="true"/>
									</div>
								</div>
							</div>
							
							<div class="row row_without_margin" style="margin-top: 10px;">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Calendar Year</p>
										<s:select name="calendarYear" id="calendarYear" listKey="calendarYearId" listValue="calendarYearName" headerKey="0"  list="calendarYearList" key="" cssStyle="width:200px !important;" onchange="submitForm('2');"/>
						      		</div>
						      		
						      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Month</p>
										<s:select theme="simple" name="strMonth" listKey="monthId" cssStyle="width:110px !important;" listValue="monthName" headerKey="0" list="monthList" key="" />
									</div>
									
								</div>
							</div>
							<div class="row row_without_margin" style="margin-top: 10px;">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									&nbsp;<!-- <i class="fa fa-calendar"></i> -->
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								
						      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<s:radio name="paramSelection" id="paramSelection" list="#{'ORG':'By Organization','SBU':'By SBU'}" />
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<input type="button" name="btnSubmit" value="Submit" class="btn btn-primary" onclick="submitForm('2');"/>
									</div>
									
								</div>
							</div>
						</div>
					</div>
				</s:form>
				

				<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt">	
					<display:column style="text-align:left" nowrap="nowrap" title="<%=strColumnTitle %>" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
					<%
					for (int ii=0; ii<alDates.size(); ii++){
						int count = 1+ii;
						String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
					%>
						<display:column title="<%=strDate%>" > <%=((java.util.List) pageContext.getAttribute("lt")).get(count)%></display:column>
					<% } %>
				</display:table>
	
				<div>
					<div style="width: 100%; float: left;margin:3px"><div style="width: 20px; height: 100%; text-align: center; background-color: red;float:left">&nbsp;</div><div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocation ratio is less than 1:2.5</div></div>
					<div style="width: 100%; float: left;margin:3px"><div style="width: 20px; height: 100%; text-align: center; background-color: yellow;float:left">&nbsp;</div><div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocation ratio is between 1:2.5 - 1:1.25</div></div>
					<div style="width: 100%; float: left;margin:3px"><div style="width: 20px; height: 100%; text-align: center; background-color: lightgreen;float:left">&nbsp;</div><div style="font-size: 10px; float: left; padding-left: 5px;">Tasks allocation ratio is greater than 1:1.25</div></div>
				</div>
	
    		</div>
 		</section>
 	</div>
 </section>   

 </div>