<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="com.konnect.jpms.reports.ReportList"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
/* jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});	 */		
/* 
$(function() {
    $( "#strStartDate" ).datepicker({dateFormat: 'dd/mm/yy'});
    $( "#strEndDate" ).datepicker({dateFormat: 'dd/mm/yy'});
}); */

</script>


<%-- <link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script> --%>
<script type="text/javascript">
/* $(function(){
	$("#f_strWLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();
	$("#f_level").multiselect();
});  */   

$(document).ready(function() {
	$('#lt').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	}); 
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
});

function generateReportExcel(){
	window.location = "ExportExcelReport.action";
}

/* function submitForm(){
	document.frm_DepartmentwiseEmpReport.exportType.value='';
	document.frm_DepartmentwiseEmpReport.submit();
} */

function submitForm(type){
	$("input[name='exportType']").val('');
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var service = getSelectedValue("f_service");
	var paycycle =  getSelectedValue("paycycle");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_strWLocation='+location+'&f_service='+service
		+'&paycycle='+paycycle;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'DepartmentEmpwiseReport.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
}
</script>

<%-- <jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Department Employeewise Report" name="title"/>
</jsp:include> --%>

   
<div id="divResult" class="leftbox reportWidth">
  	<div class="desgn" style="margin-bottom: 5px;color:#232323;">
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
		
		<s:form name="frm_DepartmentwiseEmpReport" action="DepartmentEmpwiseReport" theme="simple">
			  <s:hidden name="exportType"></s:hidden>
              <div class="row row_without_margin">
				<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
					<i class="fa fa-filter"></i>
				</div>
				<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">	
				
		            <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                         <s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="orgList" key=""/>
					</div>
		         
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Location</p>
		                   <s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
					</div>
				
					<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">SBU</p>
	                    <s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
					</div>
					
			</div>
					
			</div><br>
				<div class="row row_without_margin">
					<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
						<i class="fa fa-calendar"></i>
					</div>
					
		      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">Paycycle</p>
					     <s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName" list="paycycleList" key=""/>
					 </div>   
		      		
		      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
						<p style="padding-left: 5px;">&nbsp;</p>
						<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
					</div>
		    
				
			</div>
		</s:form>
		</div>
                <!-- /.box-body -->
        </div>
        </div>
		<br/>
   <!-- <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
		<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
		<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat; /*float: right; */"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
   </div> -->
   
<div class="col-md-2 pull-right">
					
<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

</div>


   
 <display:table name="reportList" class="table table-bordered" id="lt">
	   <display:column style="align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	   <display:column style="align:left;" nowrap="nowrap" title="Total Employee" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	   <display:column style="align:left;" nowrap="nowrap" title="Gross Salary" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
	   <display:column style="align:left;" nowrap="nowrap" title="Averge" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
</display:table>
</div>
    
<!-- </div>	 -->

