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
		 $('#lt1').DataTable({
			 dom: 'lBfrtip',
		        
		     buttons: [
		               'copy',
		            {
		                extend: 'csv',
		                title: 'Employee Joining Report'
		            },
		            {
		                extend: 'excel',
		                title: 'Employee Joining Report'
		            },
		            {
		                extend: 'pdf',
		                title: 'Employee Joining Report'
		            },
		            {
		                extend: 'print',
		                title: 'Employee Joining Report'
		            }
		        ]
		 });
		 $("#f_strWLocation").multiselect().multiselectfilter();
		 $("#f_department").multiselect().multiselectfilter();
		 $("#f_service").multiselect().multiselectfilter();
		 $("#f_level").multiselect().multiselectfilter();
		 $("#f_employeType").multiselect().multiselectfilter();
		 $("#f_grade").multiselect().multiselectfilter();
		 $("#f_status").multiselect().multiselectfilter();
		 
		 $("#endDate").datepicker({
	    	format: 'dd/mm/yyyy'
	    });
	  });	
	 
	 function submitForm(type){
	    document.frmEmployeeJoiningReport.exportType.value='';
	    var org = document.getElementById("f_org").value;
	    var location = getSelectedValue("f_strWLocation");
	    var department = getSelectedValue("f_department");
	    var service = getSelectedValue("f_service");
	    var level = getSelectedValue("f_level");
	    //var startDate = document.getElementById("startDate").value;
	    var endDate = document.getElementById("endDate").value;
	    var strGrade = getSelectedValue("f_grade");
	    var strEmployeType = getSelectedValue("f_employeType");
	    var status = "";
	    if (document.getElementById("f_status")) {
			status = getSelectedValue("f_status");
		}
	    
	    var paramValues = "";
	    if(type == '2') {
	    	paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
				+'&strLevel='+level+'&endDate='+endDate+'&strGrade='+strGrade+'&strEmployeType='+strEmployeType+'&strStatus=' + status;
	    }
	    //alert("service ===>> " + service); +'&startDate='+startDate
	    $("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	    $.ajax({
	    	type : 'POST',
	    	url: 'EmployeeJoiningReport.action?f_org='+org+paramValues, 
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
                		<s:form name="frmEmployeeJoiningReport" action="EmployeeJoiningReport" theme="simple">
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
										<p style="padding-left: 5px;">Status</p>
										<s:select theme="simple" name="f_status" id="f_status" listKey="statusId" listValue="statusName" list="empStatusList" key="" multiple="true" />
									</div>
								</div>
                			</div><br>
                			<div class="row row_without_margin">
								<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
									<i class="fa fa-calendar"></i>
								</div>
								<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">As of Date</p>
										<s:textfield name="endDate" id="endDate" cssStyle="width:90px !important;"></s:textfield>
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
	 		</div>
	 	</div>
	 	<div class="scroll" style="width:100%">
	 		<display:table name="reportList" cellspacing="1" class="table table-bordered overflowtable" id="lt1">
            	<display:setProperty name="export.excel.filename" value="EmployeeJoiningReport.xls" />
				<display:setProperty name="export.xml.filename" value="EmployeeJoiningReport.xml" />
				<display:setProperty name="export.csv.filename" value="EmployeeJoiningReport.csv" />
				<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Sr.No"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
				<display:column style="text-align:center;" valign="top" nowrap="nowrap" title="Employee Code"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Employee Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Organization"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Work Location"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="SBU"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Level"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Designation"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Reporting Manager"><%=((java.util.List) pageContext.getAttribute("lt1")).get(9)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="HOD"><%=((java.util.List) pageContext.getAttribute("lt1")).get(10)%></display:column>
								
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Mobile Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(11)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Personal Email ID"><%=((java.util.List) pageContext.getAttribute("lt1")).get(12)%></display:column>
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Official Email ID"><%=((java.util.List) pageContext.getAttribute("lt1")).get(13)%></display:column>
								
				<display:column style="text-align:left;" valign="top" title="Status"><%=((java.util.List) pageContext.getAttribute("lt1")).get(14)%></display:column>
				<display:column style="text-align:center;" valign="top" title="Joining date"><%=((java.util.List) pageContext.getAttribute("lt1")).get(15)%></display:column>
								
				<display:column style="text-align:center;" valign="top" title="Years Of Experience before joining "><%=((java.util.List) pageContext.getAttribute("lt1")).get(16)%></display:column>
								
				<display:column style="text-align:center;" valign="top" title="Date of Birth"><%=((java.util.List) pageContext.getAttribute("lt1")).get(17)%></display:column>
				<display:column style="text-align:center;" nowrap="nowrap" valign="top" title="Age"><%=((java.util.List) pageContext.getAttribute("lt1")).get(18)%></display:column>
				<display:column style="text-align:center;" valign="top" title="Gender"><%=((java.util.List) pageContext.getAttribute("lt1")).get(19)%></display:column>
								
				<display:column style="text-align:left;" valign="top" title="Residential Address"><%=((java.util.List) pageContext.getAttribute("lt1")).get(20)%></display:column>
							
				<display:column style="text-align:left;" valign="top" title="Permanent Address"><%=((java.util.List) pageContext.getAttribute("lt1")).get(21)%></display:column>
				<display:column style="text-align:left;" valign="top" title="Marital Status"><%=((java.util.List) pageContext.getAttribute("lt1")).get(22)%></display:column>
								
				<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Father's/Husband Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(23)%></display:column>
                           
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Father/Husband Mobile number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(24)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Skills"><%=((java.util.List) pageContext.getAttribute("lt1")).get(35)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Education"><%=((java.util.List) pageContext.getAttribute("lt1")).get(36)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Languages"><%=((java.util.List) pageContext.getAttribute("lt1")).get(37)%></display:column>
                
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="PAN Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(25)%></display:column>
                           	
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Bank A/c Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(26)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Bank Branch Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(27)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="PF A/C Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(28)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="UID Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(29)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="UAN Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(30)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="ESIC Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(31)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Bio-Matric Machine Id"><%=((java.util.List) pageContext.getAttribute("lt1")).get(32)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Previous Companies"><%=((java.util.List) pageContext.getAttribute("lt1")).get(33)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Reference By"><%=((java.util.List) pageContext.getAttribute("lt1")).get(34)%></display:column>
                <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Documents"><%=((java.util.List) pageContext.getAttribute("lt1")).get(38)%></display:column>
                <%-- <display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Documents"><%=((java.util.List) pageContext.getAttribute("lt1")).get(38)%> <%=((java.util.List) pageContext.getAttribute("lt1")).get(39)%></display:column> --%>
                           		
             </display:table>
	 	</div>
	 </div>
 
</div>