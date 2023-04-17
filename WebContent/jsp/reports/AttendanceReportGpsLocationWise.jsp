<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<div id="divResult">
<%
	UtilityFunctions uF = new UtilityFunctions();
    CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions); 
%>
<script>

$(function () {
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

function submitForm(type){
	var paycycle = document.getElementById("paycycle").value;
	var f_org = document.getElementById("f_org").value;
	var location = document.getElementById("location").value;
	var strSelectedEmpId = document.getElementById("strSelectedEmpId").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&paycycle='+paycycle+'&location='+location+'&strSelectedEmpId='+strSelectedEmpId;
	}
	$.ajax({
		url : 'AttendanceReportGpsLocationWise.action?f_org='+f_org+paramValues,
		type:"GET",
		cache : false,
		success : function(data) {
			$("#divResult").html(data);
		}
	});
}
</script>

       <div class="box-body" style="padding: 5px; overflow-y:auto;min-height:600px;">
           <%-- <s:form action="BirthdayReport" name="formID" id="formID" > --%>
           <div class="box box-default collapsed-box">
               <div class="box-header with-border">
                   <h3 class="box-title" style="font-size: 14px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
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
                       <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Oganisation</p>
							<s:select theme="simple" name="f_org" id="f_org" listKey="orgId" listValue="orgName" list="orgList" key="" onchange="submitForm('1')"/>
						</div>
						
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Location</p>
							<s:select theme="simple" name="location" id="location" listKey="wLocationId" listValue="wLocationName" list="workLocationList" key="" onchange="submitForm('2')"/>
						</div>
						
						<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Paycycle</p>
							<s:select theme="simple" name="paycycle" id="paycycle" listKey="paycycleId" listValue="paycycleName"  list="payCycleList" key="" onchange="submitForm('2')"/>
						</div>
						
						 <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
							<p style="padding-left: 5px;">Employee List</p>
							<s:select theme="simple" name="strSelectedEmpId" id="strSelectedEmpId" headerKey="" headerValue="Select Employee" listKey="employeeId" listValue="employeeName" 
								list="empNamesList" key="" onchange="submitForm('2')"/>
						</div> 
                       
                    </div>
                </div>
            </div>    
            
            <div>
              <table class="table" id="lt">
                <thead align="center">
                	<th lign="center">Sr No.</th>
                    <th align="center">Employee Code</th>
                    <th align="center">Employee Name</th>
                    <th align="center">Date</th>
                     <th align="center">Roster Time</th>
                      <th align="center">Actual Time</th>
                     <th align="center">Type</th>
                    <th align="center">Location</th>
                    </tr>
                </thead>
                <tbody>

                    <% 
                   List<HashMap<String,String>> counerlist = ((ArrayList<HashMap<String,String>>)request.getAttribute("reportList"));                               
                   if(counerlist!=null)
                   {
                   for (int i = 0; i < counerlist.size(); i++) {
                       %>
                   <tr align="center">
                   	 <td><%=i+1%></td>
                       <td><%=counerlist.get(i).get("empCode")%></td>
                       <td><%=counerlist.get(i).get("empName")%></td>
                       <td><%=counerlist.get(i).get("date")%></td>
                       <td><%=counerlist.get(i).get("rostertime")%></td>
                       <td><%=counerlist.get(i).get("actualtime")%></td>
                       <td><%=counerlist.get(i).get("Type")%></td>
                       <td><%=counerlist.get(i).get("location")%></td>   
                   </tr>
                   <% }  }%>
               </tbody>
           </table>                    
           </div>                                  
       </div>
       
	</div>
