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
				"order": [],
				"columnDefs": [ {
				      "targets"  : 'no-sort',
				      "orderable": false
				    }],
		        "dom": 'lBfrtip',
		        "buttons": [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Promotion Due Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Promotion Due Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Promotion Due Report'
		            },
		            {
		                extend: 'print',
		                title: 'Promotion Due Report'
		            }
		        ]
		    } );
		
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_designation").multiselect().multiselectfilter();
    	$("#f_employeType").multiselect().multiselectfilter();
    	
        $("#startDate").datepicker({
            format: 'dd/mm/yyyy',
            autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#endDate').datepicker('setStartDate', minDate);
        });
        
        $("#endDate").datepicker({
        	format: 'dd/mm/yyyy',
        	autoclose: true
        }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#startDate').datepicker('setEndDate', minDate);
        });
	});
	
    function submitForm(type) {
    	var org = document.getElementById("f_org").value;
    	var department = getSelectedValue("f_department");
    	var designation = getSelectedValue("f_designation");
    	var startDate = document.getElementById("startDate").value;
    	var endDate = document.getElementById("endDate").value;
    	var strEmployeType = getSelectedValue("f_employeType");
    	
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strDepartment='+department+'&strDesignation='+designation+'&startDate='+startDate+'&endDate='+endDate+'&strEmployeType='+strEmployeType;
    	}
    	//alert("service ===>> " + service); +'&startDate='+startDate
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'PromotionDueReport.action?f_org='+org+paramValues, 
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
<% UtilityFunctions uF = new UtilityFunctions(); %>

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
                                        <s:form name="frmPromotionDueReport" action="PromotionDueReport" theme="simple">
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
														<p style="padding-left: 5px;">From Date</p>
														<input type="text" name="startDate" id="startDate" placeholder="From Date" style="width:90px !important;" value="<%=uF.showData((String)request.getAttribute("startDate"), "") %>"/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">To Date</p>
														<input type="text" name="endDate" id="endDate" placeholder="To Date" style="width:90px !important;" value="<%=uF.showData((String)request.getAttribute("endDate"), "") %>"/>
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
					     		<display:setProperty name="export.excel.filename" value="PromotionDueReport.xls" />
								<display:setProperty name="export.xml.filename" value="PromotionDueReport.xml" />
								<display:setProperty name="export.csv.filename" value="PromotionDueReport.csv" />
								<%-- <display:column style="text-align:center;" title="Sr.No"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column> --%>
								<display:column style="text-align:center;" title="Employee Code" nowrap="nowrap" sort="false"><%=((Map) pageContext.getAttribute("lt1")).get("empCode")%></display:column>
								<display:column style="text-align:left;" title="Employee Name" nowrap="nowrap" sort="false"><%=((Map) pageContext.getAttribute("lt1")).get("empName")%></display:column>
								<display:column style="text-align:left;" title="Department" nowrap="nowrap" sort="false"><%=((Map) pageContext.getAttribute("lt1")).get("empDepartmentName")%></display:column>
								<display:column style="text-align:left;" title="Designation" nowrap="nowrap" sort="false"><%=((Map) pageContext.getAttribute("lt1")).get("empdesignation_name")%></display:column>
								<display:column style="text-align:center;" title="Joining Date" nowrap="nowrap" sort="false"><%=((Map) pageContext.getAttribute("lt1")).get("empjoining_date")%></display:column>
								<display:column style="text-align:center;" title="Promotion Date" nowrap="nowrap" sort="false"><%=((Map) pageContext.getAttribute("lt1")).get("promotion_date")%></display:column>
								<display:column style="text-align:right;" title="Salary Scale" nowrap="nowrap" sort="false"><%=((Map) pageContext.getAttribute("lt1")).get("salaryscale")%></display:column>
                            </display:table>
                        </div>
                </div>
                <!-- /.box-body -->
</div>