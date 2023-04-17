<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<div id="divResult">

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
		
		
		 $('#lt1').DataTable( {
		        dom: 'lBfrtip',
		        buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Work Anniversary Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Work Anniversary Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Work Anniversary Report'
		            },
		            {
		                extend: 'print',
		                title: 'Work Anniversary Report'
		            }
		        ]
		    } );
		
	 	$("#f_department").multiselect().multiselectfilter();
	 	/* $("#f_designation").multiselect().multiselectfilter(); */
	 	$("#f_level").multiselect().multiselectfilter();
	 	$("#f_strWLocation").multiselect().multiselectfilter();
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
    	/* var designation = getSelectedValue("f_designation"); */
    	var location = getSelectedValue("f_strWLocation");
    	var level = getSelectedValue("f_level");
    	var startDate = document.getElementById("startDate").value;
    	var endDate = document.getElementById("endDate").value;
    	var strEmployeType = getSelectedValue("f_employeType");
    	
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation=' + location+'&strDepartment='+department+
    		'&strLevel=' + level+'&startDate='+startDate+'&endDate='+endDate+'&strEmployeType='+strEmployeType;
    	}
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'WorkAnniversaryReport.action?f_org='+org+paramValues, 
    		data: $("#"+this.id).serialize(),
    		success: function(result){
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
                    	<s:form name="frmWorkAnniversaryReport" action="WorkAnniversaryReport" theme="simple">
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
										<s:select theme="simple" name="f_strWLocation" id="f_strWLocation" listKey="wLocationId" listValue="wLocationName" multiple="true" list="wLocationList" key="" />
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
                                        <s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
									
									<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Designation</p>
                                        <s:select theme="simple" name="f_designation" id="f_designation" listKey="desigId" listValue="desigCodeName" list="designationList" multiple="true" key=""/>
									</div> --%>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key=""  onchange="getLevelwiseGrade();" />
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
														<%-- <s:textfield name="startDate" id="startDate" placeholder="From Date" cssStyle="width:90px !important;"></s:textfield>&nbsp;&nbsp;&nbsp; --%>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">To Date</p>
										<input type="text" name="endDate" id="endDate" placeholder="To Date" style="width:90px !important;" value="<%=uF.showData((String)request.getAttribute("endDate"), "") %>"/>
														<%-- <s:textfield name="endDate" id="endDate" placeholder="To Date" cssStyle="width:90px !important;"></s:textfield> --%>
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
        	<display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt1" style="overflow-x: auto;">
				<display:setProperty name="export.excel.filename" value="BirthdayReport.xls" />
				<display:setProperty name="export.xml.filename" value="BirthdayReport.xml" />
				<display:setProperty name="export.csv.filename" value="BirthdayReport.csv" />
				<%-- <display:column style="text-align:center;" valign="top" title="Sr.No"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column> --%>
				<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Employee Code"><%=((Map) pageContext.getAttribute("lt1")).get("empCode")%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((Map) pageContext.getAttribute("lt1")).get("empName")%></display:column>
				<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Department"><%=((Map) pageContext.getAttribute("lt1")).get("empDepartmentName")%></display:column>
				<display:column style="text-align:left;" valign="top" title="Designation"><%=((Map) pageContext.getAttribute("lt1")).get("empDesignation")%></display:column>
				<display:column style="text-align:left;" valign="top" title="Joining Date"><%=((Map) pageContext.getAttribute("lt1")).get("empJoiningDate")%></display:column>
				<display:column style="text-align:left;" valign="top" title="Anniversary Date"><%=((Map) pageContext.getAttribute("lt1")).get("empAnniversaryDate")%></display:column>
            </display:table>
        </div>
        
	</div>
</div>	 