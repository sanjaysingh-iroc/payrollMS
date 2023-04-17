<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<link rel="stylesheet" type="text/css" href="css/select/jquery.multiselect.css" />
<script type="text/javascript" src="scripts/select/jquery.multiselect.js"></script>

<%-- <script type="text/javascript" src="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
 <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="https://cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>  --%>
 
 <script type="text/javascript" src="DataTableJs/jquery.dataTables.min.js"></script>
 <script type="text/javascript" src="DataTableJs/dataTables.buttons.min.js"></script>
 <script type="text/javascript" src="DataTableJs/jszip.min.js"></script>
 <script type="text/javascript" src="DataTableJs/pdfmake.min.js"></script>
 <script type="text/javascript" src="DataTableJs/vfs_fonts.js"></script>
 <script type="text/javascript" src="DataTableJs/buttons.html5.min.js"></script> 
 <script type="text/javascript" src="DataTableJs/buttons.print.min.js"></script> 
 <script type="text/javascript" src="js_bootstrap/datatables/dataTables.bootstrap.js"></script> 
 
<script type="text/javascript">

$(document).ready(function() {
	$('#lt').DataTable({
		dom: 'lBfrtip',
        /* buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ] */
        buttons: [
                  'copy',
                  {
                      extend: 'csv',
                      title: 'Overtime Register Report'
                  },
                  {
                      extend: 'excel',
                      title: 'Overtime Register Report'
                  },
                  {
                      extend: 'pdf',
                      title: 'Overtime Register Report'
                  },
                  {
                      extend: 'print',
                      title: 'Overtime Register Report'
                  }
              ]
	}); 
	$("#f_wLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
});

/* $(function(){
	$("#f_wLocation").multiselect();
	$("#f_department").multiselect();
	$("#f_service").multiselect();    
});   */  
</script> 

<script type="text/javascript">
jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});

function checkUncheckValue() { 
	var allOt=document.getElementById("allOt");		
	var strOt = document.getElementsByName('ot');

	if(allOt.checked==true){
		 for(var i=0;i<strOt.length;i++){
			 strOt[i].checked = true;			  
		 }
	}else{		
		 for(var i=0;i<strOt.length;i++){
			 strOt[i].checked = false;			 
		 }		 
	}	 
}

</script>


<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

String  strYear = (String)request.getAttribute("strYear");
String  strMonth = (String)request.getAttribute("strMonth");

List<String> alDates = (List<String>)request.getAttribute("alDates"); 
 
%>
    

<script type="text/javascript" charset="utf-8">

function submitForm(type){
	$("input[name='exportType']").val('');
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_wLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var paycycle = getSelectedValue("paycycle");
	var empContractor = getSelectedValue("empContractor");
	var paramValues = "";
	if(type == '2') {
		paramValues = '&f_wLocation='+location+'&f_department='+department+'&f_service='+service
		+'&empContractor='+empContractor+'&paycycle='+paycycle;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'OTRegister.action?f_org='+org+paramValues,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
}


function loadMore(pageNumber, minLimit) {
	document.frm_OTRegister.pageNumber.value = pageNumber;
	document.frm_OTRegister.minLimit.value = minLimit;
	document.frm_OTRegister.submit();
}

function selectall(x,strEmpId){
	var  status=x.checked; 
	var  arr= document.getElementsByName(strEmpId);
	for(i=0;i<arr.length;i++){ 
  		arr[i].checked=status;
 	}
	if(x.checked == true){
		document.getElementById("unSendSpan").style.display = 'none';
		document.getElementById("sendSpan").style.display = 'block';
	} else {
		document.getElementById("unSendSpan").style.display = 'block';
		document.getElementById("sendSpan").style.display = 'none';
	}
}

function checkAll(){
	var sendAll = document.getElementById("sendAll");		
	var strEmpIds = document.getElementsByName('strEmpIds');
	var cnt = 0;
	var chkCnt = 0;
	for(var i=0;i<strEmpIds.length;i++) {
		cnt++;
		 if(strEmpIds[i].checked) {
			 chkCnt++;
		 }
	 }
	if(parseFloat(chkCnt) > 0) {
		document.getElementById("unSendSpan").style.display = 'none';
		document.getElementById("sendSpan").style.display = 'block';
	} else {
		document.getElementById("unSendSpan").style.display = 'block';
		document.getElementById("sendSpan").style.display = 'none';
	}
	
	if(cnt == chkCnt) {
		sendAll.checked = true;
	} else {
		sendAll.checked = false;
	}
}

function generateReportExcel(){
	window.location = "ExportExcelReport.action";
}

</script>

<%-- <jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="OverTime Register Report" name="title"/>
</jsp:include>  --%>

<div id="divResult" class="leftbox reportWidth">
<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
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
                    <s:form name="frm_OTRegister" action="OTRegister" theme="simple" method="post">
                   		<s:hidden name="pageNumber" id="pageNumber" />
	    				<s:hidden name="minLimit" id="minLimit" />
                        <s:hidden name="exportType"></s:hidden>
                        <div class="row row_without_margin">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-filter"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
                                	<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Location</p>
                                	<s:select name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
                               		<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
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
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Paycycle</p>
										<s:select name="paycycle" id='paycycle' listKey="paycycleId" listValue="paycycleName" headerKey="0"
										headerValue="Select Paycycle" list="paycycleList" key="" onchange="document.frm_OTRegister.submit('2');"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px; margin-left: 8px;">Employee/Contractor:</p>
									<s:select name="empContractor" id="empContractor" cssClass="validateRequired" headerKey="1" headerValue="Employee" 
										list="#{'2': 'Contractor'}" onchange="document.frm_OTRegister.submit('2');" cssStyle="margin-left: 10px;"/> <!-- checkEmpORContractorCode -->
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
								</div>
							</div>
						</div>
                    </s:form>
                </div>
                <!-- /.box-body -->
            </div>
        </div>
		<br>
  
    <div class="col-md-2" style="margin: 0px 0px 10px 0px; float: right;">
		<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
		
		<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
	</div>
   
 <display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt">
   
    <display:column style="align:left;" nowrap="nowrap" title="Employee Id" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Name of workman" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Father's / Husband's name " sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Sex" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Employee Id" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Designation/ nature of employment" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Date on which overtime worked " sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Total overtime worked" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Normal rates of wages" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Overtime rate of wages" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Overtimes earnings" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>
	<display:column style="align:left;" nowrap="nowrap" title="Date on which overtime wages paid" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>
	<display:column style="align:center;" nowrap="nowrap" title="Remarks " sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(12)%></display:column>
	
</display:table>
</div>
   
</div>	
