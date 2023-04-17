<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
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
	});*/
	    
    $('#lt1').DataTable( {
        dom: 'lBfrtip',
           buttons: [
                  'copy',
		            {
		                extend: 'csv',
		                title: 'Workforce Report'
		                
		            },
		            {
		                extend: 'excel',
		                title: 'Workforce Report'
		    
		            },
		            {
		                extend: 'pdf',
		                title: 'Workforce Report'
		     
		            },
		            {
		                extend: 'print',
		                title: 'Workforce Report'
		                
		            }
        ]
    } );
	 
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
	$("#f_employeType").multiselect().multiselectfilter();
	$("#f_grade").multiselect().multiselectfilter();
	
	$( "#startDate" ).datepicker({format: 'dd/mm/yyyy'});
});
function submitForm(type) {
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var strGrade = getSelectedValue("f_grade");
	var strEmployeType = getSelectedValue("f_employeType");
	var startDate = document.getElementById("startDate").value;
	
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service+'&strLevel='+level+'&strGrade='+strGrade
		+'&strEmployeType='+strEmployeType+'&startDate='+startDate;
	}
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		url: 'AdultWorker.action?f_org='+org+paramValues, 
		success: function(result){
        	//console.log(result);
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

<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>

<%-- 	<jsp:include page="../../common/SubHeader.jsp">
    <jsp:param value="Adult Workers" name="title"/>
    </jsp:include> --%>

                <div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
                        <div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
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
                                    <div class="content1">
                                        <s:form name="frmAdultWorker" action="AdultWorker" theme="simple">
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
														<p style="padding-left: 5px;">Department</p>
                                                    	<s:select name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">SBU</p>
                                                    	<s:select name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" key="" multiple="true"/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Level</p>
                                                    	<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" multiple="true" list="levelList" key="" />
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Grade</p>
														<s:select theme="simple" name="f_grade" id="f_grade" list="gradeList" listKey="gradeId" listValue="gradeCode" key="" multiple="true"/>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">Employee Type</p>
														<s:select theme="simple" name="f_employeType" id="f_employeType" listKey="empTypeId" listValue="empTypeName" list="employementTypeList" key=""  multiple="true"  />
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">As of Date</p>
														<s:textfield name="startDate" id="startDate" cssStyle="width:90px !important;"></s:textfield>
													</div>
													<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
														<p style="padding-left: 5px;">&nbsp;</p>
                                                		<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
													</div>
												</div>
											</div><br>
                                        </s:form>
                                    </div>
                                </div>
                                <!-- /.box-body -->
                            </div>
                        </div>
                        <div>
                            <display:table name="reportList" cellspacing="1" class="table table-bordered" id="lt1" style="height:100px !important;"> <!-- requestURI="AdultWorker.action" width="100%" -->
					     		<%-- <display:setProperty name="export.excel.filename" value="AdultWorker.xls" />
								<display:setProperty name="export.xml.filename" value="AdultWorker.xml" />
								<display:setProperty name="export.csv.filename" value="AdultWorker.csv" /> --%>
								<%-- <display:column style="text-align:center;" valign="top" title="Sr.No"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column> --%>
								<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
								<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
								<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Date of Birth"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
								<display:column style="text-align:center;" valign="top" title="Age"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
								<display:column style="text-align:center;" valign="top" title="Gender"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
								<display:column style="text-align:left;" valign="top" title="Residential Address"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
								<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Father's/Husband Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
								<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Date of Joining"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
								<display:column style="text-align:left;" valign="top" title="Alphabet Assigned"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
								<display:column style="text-align:left;" valign="top" title="Designation"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
							</display:table>
                        </div>
                </div>
                <!-- /.box-body -->
</div>