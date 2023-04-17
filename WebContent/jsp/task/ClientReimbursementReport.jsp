<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
function generateSalaryExcel(){
	window.location="ExportExcelReport.action";
} 


$(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
    $("#strStartDate").datepicker({format: 'dd/mm/yyyy'});
    $("#strEndDate").datepicker({format: 'dd/mm/yyyy'});
    
   /*  var value = document.getElementById("selectOne").value;
    checkSelectType(value); */
});


function checkSelectType(value) {
	
	//fromToDIV financialYearDIV monthDIV paycycleDIV
	if(value == '1') {
		document.getElementById("fromToDIV").style.display = 'block';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
		document.getElementById("paycycleDIV").style.display = 'none';
	} else if(value == '2') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'block';
		document.getElementById("monthDIV").style.display = 'none';
		document.getElementById("paycycleDIV").style.display = 'none';
	} else if(value == '3') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'block';
		document.getElementById("paycycleDIV").style.display = 'none';
	} else if(value == '4') {
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
		document.getElementById("paycycleDIV").style.display = 'block';
	} else {
		document.getElementById("strStartDate").value = '';
		document.getElementById("strEndDate").value = '';
		document.getElementById("fromToDIV").style.display = 'none';
		document.getElementById("financialYearDIV").style.display = 'none';
		document.getElementById("monthDIV").style.display = 'none';
		document.getElementById("paycycleDIV").style.display = 'none';
	}
}


function submitForm(type){
	var data = "";
	if(type == '1') {
		var f_org = document.getElementById("f_org").value;
		data = '&f_org='+f_org;
	} else if(type == '2') {
		data = $("#frmClientReimbursementReport").serialize();
	}
	$("#actionResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ClientReimbursementReport.action',
		data: data,
		success: function(result){
        	$("#actionResult").html(result);
        	$("#f_strWLocation").multiselect().multiselectfilter();
        	$("#f_department").multiselect().multiselectfilter();
        	$("#f_service").multiselect().multiselectfilter();
        	$("#f_level").multiselect().multiselectfilter();
        	$("#f_client").multiselect().multiselectfilter();
   		}
	});
}

</script>


<script type="text/javascript">
$(function(){
	
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	/* $("#f_project_service").multiselect().multiselectfilter(); */
	$("#f_client").multiselect().multiselectfilter();
});    
</script>


<%
UtilityFunctions uF = new UtilityFunctions();

List<List<String>> reportList = (List<List<String>>)request.getAttribute("reportList"); 
List<List<DataStyle>> reportListExport =(List<List<DataStyle>>)request.getAttribute("reportListExport");
session.setAttribute("reportListExport",reportListExport);
%> 

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
                    <s:form name="frmClientReimbursementReport" id="frmClientReimbursementReport" action="ClientReimbursementReport" theme="simple">
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
                                	<s:select name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Department</p>
                               		<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">SBU</p>
                                	<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Level</p>
									<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" multiple="true"/>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Client</p>
									<s:select name="f_client" id="f_client" listKey="clientId" listValue="clientName" list="clientList" key="" multiple="true" />
			             		</div>
							</div>
						</div>
						
						<div class="row row_without_margin" style="margin-top: 10px;">
							<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
								<i class="fa fa-calendar"></i>
							</div>
							<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
								<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">Select Period</p>
									<s:select theme="simple" name="selectOne" id="selectOne" cssStyle="float:left; margin-right: 10px;" headerKey="" 
										headerValue="Select Period" list="#{'1':'From-To', '2':'Financial Year', '3':'Month', '4':'Paycycle'}" onchange="checkSelectType(this.value);"/> <!--   -->
								</div> --%>
								
								<%-- <div id="fromToDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
									<p style="padding-left: 5px;">&nbsp;</p>
									<input type="text" name="strStartDate" id="strStartDate" placeholder="From Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strStartDate"), "") %>"/>
									<input type="text" name="strEndDate" id="strEndDate" placeholder="To Date" style="width:85px !important;" value="<%=uF.showData((String)request.getAttribute("strEndDate"), "") %>"/>
					      		</div>
					      		
					      		<div id="financialYearDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
									<p style="padding-left: 5px;">Financial Year</p>
									<s:select name="financialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" />
					      		</div>
					      		
					      		<div id="monthDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
									<p style="padding-left: 5px;">Financial Year &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Month</p>
									<s:select name="monthFinancialYear" listKey="financialYearId" listValue="financialYearName" headerValue="Select Financial Year" list="financialYearList" /> 
									<s:select name="strMonth" cssStyle="margin-left: 7px; width: 100px !important;" listKey="monthId" listValue="monthName" list="monthList" />	
					      		</div>
					      		
					      		<div id="paycycleDIV" class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5" style="display: none;">
									<p style="padding-left: 5px;">Paycycle</p>
									<s:select name="paycycle" listKey="paycycleId" listValue="paycycleName" headerValue="Select Paycycle" list="paycycleList" />
					      		</div> --%>
					      		
					      		<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-left: 5px;">From Date</p>
									<s:textfield name="strStartDate" id="strStartDate" cssStyle="width:100px !important;" readonly="true"></s:textfield>
								</div>
								<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
									<p style="padding-right: 5px;">To Date</p>
						    		<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
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
        
        
    	<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt1">
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
<!-- ===start parvez date: 26-01-2022=== -->			
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Designation"><%=((java.util.List) pageContext.getAttribute("lt1")).get(13)%></display:column>
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Date"><%=((java.util.List) pageContext.getAttribute("lt1")).get(14)%></display:column>
<!-- ===end parvez date: 26-01-2022=== -->				
			<display:column style="text-align:left; width: 18%;" valign="top" title="Client"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
<!-- ===start parvez date: 26-01-2022=== -->			
			<display:column style="text-align:left; width: 18%;" valign="top" title="Project"><%=((java.util.List) pageContext.getAttribute("lt1")).get(15)%></display:column>
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Amount"><%=((java.util.List) pageContext.getAttribute("lt1")).get(12)%></display:column>
			<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Chargeable Y/N"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
<!-- ===end parvez date: 26-01-2022=== -->				
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Reimbursement Type"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="if Travel- mode of Travel"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>	
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="No. of Persons"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>	
				
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Place From"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>	
			<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Place To"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>	
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Days"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>	
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Km/ Day"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>	
			<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Rate/ Km"><%=((java.util.List) pageContext.getAttribute("lt1")).get(11)%></display:column>	
			
				 <!-- sort="true" -->
		</display:table>

	</div>
