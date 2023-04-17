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
		 $('#lt').DataTable( {
		        dom: 'lBfrtip',
		        buttons: [
		            'copy',
		            {
		                extend: 'csv',
		                title: 'Exception Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Exception Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Exception Report'
		            },
		            {
		                extend: 'print',
		                title: 'Exception Report'
		            }
		        ]
		    } );
		
		$( "#strStartDate" ).datepicker({format: 'dd/mm/yyyy'});
	    $( "#strEndDate" ).datepicker({format: 'dd/mm/yyyy'});
	    
		$("#f_wLocation").multiselect().multiselectfilter();
    	$("#f_department").multiselect().multiselectfilter();
    	$("#f_service").multiselect().multiselectfilter();
    	$("#f_level").multiselect().multiselectfilter();
	});
	
    function submitForm(type) {
    	var org = document.getElementById("f_org").value;
    	var location = getSelectedValue("f_wLocation");
    	var department = getSelectedValue("f_department");
    	var service = getSelectedValue("f_service");
    	var level = getSelectedValue("f_level");
    	var strStartDate = document.getElementById("strStartDate").value;
    	var strEndDate = document.getElementById("strEndDate").value;
    	
    	var paramValues = "";
    	if(type == '2') {
    		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strService='+service+'&strLevel='+level
    		+'&strStartDate='+strStartDate+'&strEndDate='+strEndDate;
    	}
    	//alert("service ===>> " + service); +'&startDate='+startDate
    	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
    	$.ajax({
    		type : 'POST',
    		url: 'ExceptionReport.action?f_org='+org+paramValues, 
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
						<s:form name="frmExceptionReport" action="ExceptionReport" theme="simple">
							<s:hidden name="exportType"></s:hidden>
							<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-filter"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Status</p>
										<s:select theme="simple" name="exceptionStatus" cssStyle="width:92px;" list="#{'0':'All','1':'Approved', '2':'Pending'}" onchange="submitForm('2');"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px; margin-bottom: 8px;">Organization</p>
										<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" onchange="submitForm('1');" list="organisationList" key=""/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Location</p>
										<s:select theme="simple" name="f_wLocation" id="f_wLocation" listKey="wLocationId" listValue="wLocationName" list="wLocationList" key="" multiple="true"/>
									</div>
									
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Department</p>
										<s:select theme="simple" name="f_department" id="f_department" listKey="deptId" listValue="deptName" list="departmentList" key="" multiple="true"/>
									</div>
		
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Service</p>
										<s:select theme="simple" name="f_service" id="f_service" list="serviceList" listKey="serviceId" listValue="serviceName" multiple="true"></s:select>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Level</p>
										<s:select theme="simple" name="f_level" id="f_level" listKey="levelId" listValue="levelCodeName" list="levelList" key="" multiple="true"/>
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
										<s:textfield name="strStartDate" id="strStartDate" cssStyle="width:100px !important;" readonly="true"></s:textfield>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-right: 5px;">To Date</p>
					    				<s:textfield name="strEndDate" id="strEndDate" cssStyle="width: 100px !important;" readonly="true"></s:textfield>
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
			
				<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt">
				    <display:column style="align:left;" nowrap="nowrap" title="Employee Code" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(0)%></display:column>
					<display:column style="align:left;" nowrap="nowrap" title="Employee Name" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(1)%></display:column>
					<display:column style="align:left;" nowrap="nowrap" title="Department" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(2)%></display:column>
					<display:column style="align:left;" nowrap="nowrap" title="Location" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(11)%></display:column>				
					<display:column style="align:left;" nowrap="nowrap" title="Date" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(3)%></display:column>
					<display:column style="align:left;" nowrap="nowrap" title="Plan Roster Details" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(4)%></display:column>
					<display:column style="align:left;" nowrap="nowrap" title="Plan Roster Hrs" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(5)%></display:column>
					<display:column style="align:left;" nowrap="nowrap" title="Actual Attendance" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(6)%></display:column>
					<display:column style="align:left;" nowrap="nowrap" title="Status" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(7)%></display:column>		
					<display:column style="align:left;" nowrap="nowrap" title="Exception Type" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(8)%></display:column>	
					<display:column style="align:left;" nowrap="nowrap" title="Actual Worked Hours" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(9)%></display:column>					
					<display:column style="align:left;" nowrap="nowrap" title="Is Short Working Hrs" sort="false"><%=((java.util.List) pageContext.getAttribute("lt")).get(10)%></display:column>	
					
				</display:table>
			
			</div>
	</div>
          <!-- /.box-body -->
</div>