<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%String fromPage = (String)request.getAttribute("fromPage"); %>
 <%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
	<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<%} %> 
<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>
<script type="text/javascript">
function generateReportExcel(){
	window.location="ExportExcelReport.action";
}



$(function() {
	$("#f_department").multiselect().multiselectfilter();
	$("#f_designation").multiselect().multiselectfilter();
	$("#strStartDate").datepicker({
        format: 'dd/mm/yyyy',
        autoclose: true
    }).on('changeDate', function (selected) {
        var minDate = new Date(selected.date.valueOf());
        $('#strEndDate').datepicker('setStartDate', minDate);
    });
    
    $("#strEndDate").datepicker({
    	format: 'dd/mm/yyyy',
    	autoclose: true
    }).on('changeDate', function (selected) {
            var minDate = new Date(selected.date.valueOf());
            $('#strStartDate').datepicker('setEndDate', minDate);
    });
    
     $("#lt").DataTable({
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
    
    $("#strEmpType").multiselect().multiselectfilter();
    
});

function getSelectedValue(selectId) {
	var choice = document.getElementById(selectId);
	var exportchoice = "";
	for ( var i = 0, j = 0; i < choice.options.length; i++) {
		var value = choice.options[i].value;
		if(choice.options[i].selected == true && value != "") {
			
			if (j == 0) {
				exportchoice = "," + choice.options[i].value + ",";
				j++;
			} else {
				exportchoice += choice.options[i].value + ",";
				j++;
			}
		}else if(choice.options[i].selected == true && value == ""){
			exportchoice = "";
			break;
		}
		
	}
	//alert("exportchoice==>"+exportchoice);
	return exportchoice;
}

function submitFilterForm(){
	
	var strStartDate = document.getElementById("strStartDate").value;
	var strEndDate = document.getElementById("strEndDate").value;
	var empDepart="";
	var empDesignation=""; 
	var empBloodGroup="";
	var empEmployeeType="";
	 if(document.getElementById("f_department")){
		 empDepart = getSelectedValue("f_department");
		} 
	 if(document.getElementById("f_designation")){
		 empDesignation = getSelectedValue("f_designation");
		}
	$.ajax({
		url : "RetirementReport.action?strStartDate="+strStartDate+"&strEndDate="+strEndDate+"&strDesignation="+empDesignation+"&strDepartment="+empDepart,
		type:"GET",
		cache : false,
		success : function(data) {
			$("#divResult").html(data);
		}
	});
}
</script>


	<%-- <script type="text/javascript" charset="utf-8">
				$(document).ready(function () {
					
						$('#lt').dataTable({ bJQueryUI: true, 
							  								
							"sPaginationType": "full_numbers",
							"aaSorting": [],
							"sDom": '<"H"lTf>rt<"F"ip>',
							oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
							aButtons: [
									"csv", "xls", {
										sExtends: "pdf",
										sPdfOrientation: "landscape"
										//sPdfMessage: "Your custom message would go here."
	 								}, "print" 
								]
							}
						});
				});
				
	</script> --%>

  <%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
<section class="content">
    <div class="row jscroll">
        <section class="col-lg-12 connectedSortable">
            <div class="box box-primary">
 <%} %>
                <div class="box-body" id="divResult" style="padding: 5px; overflow-y:auto;min-height:600px;">
                    <%-- <s:form action="BirthdayReport" name="formID" id="formID" > --%>
                    <div class="box box-default collapsed-box">
                        <div class="box-header with-border">
                            <h3 class="box-title">Filter</h3>
                            <div class="box-tools pull-right">
                                <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
                                <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
                            </div>
                        </div>
                        <div class="box-body" style="padding: 5px; overflow-y: auto;">
                            <div class="row row_without_margin">
                                <div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
                                    <i class="fa fa-filter" aria-hidden="true"></i>
                                </div>
                                <div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
                                    <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                       <p style="padding-left: 5px;">Department</p>
                                      <s:select theme="simple" name="f_department" id="f_department" listKey="deptId" listValue="deptName" multiple="true" list="departmentList" key="" /> 
                                    </div>
                                    
                                    <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                       <p style="padding-left: 5px;">Designation</p>
                                     <s:select theme="simple" name="f_designation" id="f_designation" listKey="desigId" listValue="desigName" multiple="true" list="designationList" key="" /> 
                                    </div>
                                    <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                      <p style="padding-left: 5px;">From Date</p>
                                        <s:textfield name="strStartDate" id="strStartDate"/>
                                    </div>
                                     <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
                                      <p style="padding-left: 5px;">To Date</p>
                                      <s:textfield name="strEndDate" id="strEndDate" />
                                    </div>
                                    <div class="col-lg-2 col-md-6 col-sm-1 autoWidth paddingleftright5">
                                        <p style="padding-left: 5px;">&nbsp;</p>
                                        <input type="button" name="submit" value="Submit" class="btn btn-primary" style="margin: 0px" onclick="submitFilterForm()"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                  
                    
                 <table class="table" id="lt">
                        <thead>
                        	<th>Sr No.</th>
                           
                            <th>Employee Code</th>
                            <th>Employee Name</th>
                            <th>Department</th>
                            <th>Designation</th>
                            <th>Age</th>
                            <th>Appointment Date</th>
                        </thead>
                        <tbody>
     
                            <% 
                            List<HashMap<String,String>> counerlist = ((ArrayList<HashMap<String,String>>)request.getAttribute("reportList"));
                               
                            if(counerlist!=null)
                            {
                            for (int i = 0; i < counerlist.size(); i++) {
                                %>
                            <tr>
                             	<td><%=i+1%></td>
                                <td><%=counerlist.get(i).get("empCode")%></td>
                                <td><%=counerlist.get(i).get("empName")%></td>
                                <td><%=counerlist.get(i).get("deptName")%></td>
                                <td><%=counerlist.get(i).get("designationName")%></td>
                                <td><%=counerlist.get(i).get("age")%></td>
                                <td><%=counerlist.get(i).get("joining_date")%></td>
                            </tr>
                            <% }  }%> 
                        </tbody>
                    </table>
			
			
                   
                </div>
   <%if(fromPage == null || fromPage.equals("") || fromPage.equalsIgnoreCase("null")) { %>
            </div>
        </section>
    </div>
</section>
<%}%>