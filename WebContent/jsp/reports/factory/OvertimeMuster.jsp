<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="divResult">
<script type="text/javascript">
    /* function submitForm(){
    	document.frm_fromOvertimeMuster.exportType.value='';
    	document.frm_fromOvertimeMuster.submit();
    } */
    $(document).ready(function() {
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
		
		$("#f_strWLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter(); 
		
	});
    function getData(type) {
    	//alert("type ===>> " + type);
    	var org = document.getElementById("f_org").value;
    	var location = getSelectedValue("f_strWLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	var financialYear = document.getElementById("financialYear").value;
    	var strMonth = document.getElementById("strMonth").value;
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
			+'&financialYear='+financialYear+'&strMonth='+strMonth;
    	}
    	//alert("service ===>> " + service);
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'OvertimeMuster.action?f_org='+org+paramValues, 
    		data: $("#"+this.id).serialize(),
    		success: function(result){
            	//console.log(result);
            	$("#divResult").html(result);
       		}
    	});
    	/* window.location='MenuNavigationInner.action?NN='+NN+'&strOrg='+org+"&strLocation="+location+"&strDepartment="+department+"&strSbu="+service
    			+"&strCFYear="+financialYear+'&strMonth='+strMonth+'&toPage='+toPage; */
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
	    <div class="desgn" style="background:#f5f5f5; color:#232323;">
	        <div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE; margin-bottom: 10px;">
	            <div class="box-header with-border">
	                <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
	                <div class="content1">
	                    <s:form name="frm_fromOvertimeMuster" action="OvertimeMuster" theme="simple">
	                        <s:hidden name="exportType"></s:hidden>
	                        <div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="getData('1');" list="orgList" key=""/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
									</div>
									<div class="col-lg-3 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
		
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">SBU</p>
										<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
									</div>
								</div>
							</div>
	                        <br>
	                        <div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Financial Year</p>
										<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" 
	                                    onchange="getData('2');" list="financialYearList" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Month</p>
										<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" 
	                                    onchange="getData('2');" list="monthList" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="getData('2');"/>  
									</div>
								</div>
							</div>
	                    </s:form>
	                </div>
	            </div>
	            <!-- /.box-body -->
	        </div>
	    </div>
	    <br/>
	   <!--  <div class="col-md-2" style="margin: 0px 0px -15px 0px; text-align: right; float: right;">
			<a onclick="generatePaymentHeldPdfReport();" href="javascript:void(0)" class="fa fa-file-pdf-o" >&nbsp;&nbsp; </a>
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div> -->
		
		<div class="col-md-2 pull-right">
			<!-- <a onclick="generatePaymentHeldPdfReport();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-pdf-o" aria-hidden="true"></i></a> -->
			<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat; float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>
		</div>
		
	    
	    <display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt1">
	        <display:column style="text-align:center;" valign="top" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
	        <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
	        <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
	        <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
	        <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Work Location"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
	        <display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Standard Work Hours/ Days"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
	        <display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Overtime Work Hours/ Days"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
	        <display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Total Overtime Work in Days"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
	        <display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Earning during the month"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
	        <display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Total"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
	        <display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Date on which overtime paid"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
	        <display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Payment Mode"><%=((java.util.List) pageContext.getAttribute("lt1")).get(11)%></display:column>
	    </display:table>
	</div>
<!-- /.box-body -->
</div>