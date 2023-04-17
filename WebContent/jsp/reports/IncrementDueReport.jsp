<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<div id="divResult">

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
 
<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
		/* $('#lt1').DataTable({
			dom: 'lBfrtip',
	        buttons: [
				'copy', 'csv', 'excel', 'pdf', 'print'
	        ]
		}); */
		 $('#lt1').DataTable( {
		        dom: 'lBfrtip',
		        buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Increment Due Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Increment Due Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Increment Due Report'
		            },
		            {
		                extend: 'print',
		                title: 'Increment Due Report'
		            }
		        ]
		    } );
		
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_designation").multiselect().multiselectfilter();
    	$("#f_employeType").multiselect().multiselectfilter();
	});
	
    function submitForm(type) {
    	var org = document.getElementById("f_org").value;
    	var department = getSelectedValue("f_department");
    	var designation = getSelectedValue("f_designation");
    	var calendarYear = document.getElementById("calendarYear").value;
    	var strMonth = document.getElementById("strMonth").value;
    	var strEmployeType = getSelectedValue("f_employeType");
    	
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strDepartment='+department+'&strDesignation='+designation+'&calendarYear='+calendarYear+'&strMonth='+strMonth+'&strEmployeType='+strEmployeType;
    	}
    	//alert("service ===>> " + service); +'&startDate='+startDate
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'IncrementDueReport.action?f_org='+org+paramValues, 
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
    	for (var i = 0, j = 0; i < choice.options.length; i++) {
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

<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Work Force Joining Report" name="title"/>
    </jsp:include> --%>

	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
		<div class="desgn" style="margin-bottom: 5px; color:#232323;">
			<div class="box box-primary collapsed-box" style="border-top-color: #EEEEEE;">
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
						<s:form name="frmIncrementDueReport" action="IncrementDueReport" theme="simple">
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
										<p style="padding-left: 5px;">Department</p>
		                                              	<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
		
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Designation</p>
		                                              	<s:select theme="simple" name="f_designation" id="f_designation" listKey="desigId" listValue="desigCodeName" list="designationList" multiple="true" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Employee Type</p>
										<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
									</div>
								</div>
							</div><br>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Financial Year</p>
										<s:select label="Select Financial Year" name="calendarYear" id="calendarYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" list="financialYearList" key="" cssStyle="width:200px;"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Month</p>
										<s:select label="Select Month" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" headerKey="1" list="monthList" key="" cssStyle="width:200px;"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
									</div>
								</div>
							</div>
						</s:form>
					</div>
				</div>
                          <!-- /.box-body -->
			</div>
		</div>
			<div class="scroll" style="width:100%">
				<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt1">
		    		<display:setProperty name="export.excel.filename" value="IncrementDueReport.xls" />
					<display:setProperty name="export.xml.filename" value="IncrementDueReport.xml" />
					<display:setProperty name="export.csv.filename" value="IncrementDueReport.csv" />
					<%-- <display:column style="text-align:center;" valign="top" title="Sr.No"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column> --%>
					<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Employee Code"><%=((Map) pageContext.getAttribute("lt1")).get("empCode")%></display:column>
					<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((Map) pageContext.getAttribute("lt1")).get("empName")%></display:column>
					<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Department"><%=((Map) pageContext.getAttribute("lt1")).get("empDepartmentName")%></display:column>
					<display:column style="text-align:left;" valign="top" title="Designation"><%=((Map) pageContext.getAttribute("lt1")).get("empdesignationName")%></display:column>
					<display:column style="text-align:center;" valign="top" title="Joining Date"><%=((Map) pageContext.getAttribute("lt1")).get("empjoining_date")%></display:column>
					<display:column style="text-align:center;" valign="top" title="Increment Date"><%=((Map) pageContext.getAttribute("lt1")).get("increment_date")%></display:column>
				</display:table>
			</div>
	</div>
          <!-- /.box-body -->
</div>