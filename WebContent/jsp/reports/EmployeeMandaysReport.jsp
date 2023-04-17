<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<div id="divResult">
<script type="text/javascript">
$(document).ready(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	$("#f_strWLocation").multiselect().multiselectfilter();
	$("#f_department").multiselect().multiselectfilter();
	$("#f_service").multiselect().multiselectfilter();
	$("#f_level").multiselect().multiselectfilter();
});

function submitForm(type){
	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var financialYear = document.getElementById("financialYear").value;
	var paramValues = "";
	if(type == '2') {
		paramValues = '&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
		+'&strLevel='+level+'&financialYear='+financialYear;
	}
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'EmployeeMandaysReport.action?f_org='+org+paramValues, 
		data: $("#"+this.id).serialize(),
		success: function(result) {
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
	/* document.frmEmployeeHours.submit(); */
}

function submitFormSelected(type, cnt){

	var org = document.getElementById("f_org").value;
	var location = getSelectedValue("f_strWLocation");
	var department = getSelectedValue("f_department");
	var service = getSelectedValue("f_service");
	var level = getSelectedValue("f_level");
	var financialYear = document.getElementById("financialYear").value;
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'EmployeeMandaysReport.action?f_org='+org+'&strLocation='+location+'&strDepartment='+department+'&strSbu='+service
			+'&strLevel='+level+'&financialYear='+financialYear, 
		data: $("#"+this.id).serialize(),
		success: function(result){
        	//console.log(result);
        	$("#divResult").html(result);
   		}
	});
	/* document.frmEmployeeHours.submit(); */
}


function getCheckedValue(checkId) {
    var radioObj = document.getElementsByName(checkId);
    var radioLength = radioObj.length;
	for(var i = 0; i < radioLength; i++) {
		if(radioObj[i].checked) {
			return radioObj[i].value;
		}
	}
	
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

<script type="text/javascript" src="scripts/chart/jquery.min.js"></script>
<script type="text/javascript" src="scripts/chart/highcharts.js"></script>

<%
	UtilityFunctions uF = new UtilityFunctions();
    Map hmActual = (Map) request.getAttribute("hmActual");
    Map hmRoster = (Map) request.getAttribute("hmRoster");
    List alId = (List) request.getAttribute("alId");
    List alPayCycles = (List) request.getAttribute("alPayCycles");
    List alSubTitle = (List) request.getAttribute("alSubTitle");
    
    Map hmTotalA = (Map) request.getAttribute("hmTotalA");
    Map hmTotalR = (Map) request.getAttribute("hmTotalR");
    Map hmTotalV = (Map) request.getAttribute("hmTotalV");
    
    String currentPaycycle = (String) request.getAttribute("currentPaycycle");
	List<String> alPaycycleNo = (List<String>)request.getAttribute("alPaycycleNo");
	if(alPaycycleNo == null) alPaycycleNo = new ArrayList<String>();
	
    %>
<script type="text/javascript">
    function generateReportPdf(){
        alert("pdf generation");
    
     }
    
     function generateReportExcel(){
     	 alert("Excel  generation");
     	
     	
     }
    var chartRoster;
    var chartActual;
    
    $(document).ready(function() {
    	
    	chartActual = new Highcharts.Chart({
       		
          chart: {
             renderTo: 'container_Actual',
            	defaultSeriesType: 'column'
          },
          title: {
             text: 'Actual Hours'
          },
          xAxis: {
             categories: [<%=(String)request.getAttribute("sbActualPC")%>],
             labels: {
                 rotation: -45,
                 align: 'right',
                 style: {
                     font: 'normal 10px Verdana, sans-serif'
                 }
              },
             title: {
    	            text: 'Pay Cycles'
    	         }
          },
          credits: {
           	enabled: false
       	  },
          yAxis: {
             min: 0,
             title: {
                text: 'Resource Efforts'
             }
          },
          plotOptions: {
             column: {
                pointPadding: 0.2,
                borderWidth: 0
             }
          },
         series: [<%=request.getAttribute("sbActualHours")%>]
       });
    	
    	
    	chartRoster = new Highcharts.Chart({
       		
    	      chart: {
    	         renderTo: 'container_Roster',
    	        	defaultSeriesType: 'column'
    	      },
    	      title: {
    	         text: 'Roster Hours'
    	      },
    	      credits: {
    	         	enabled: false
    	      },
    	      xAxis: {
    	         categories: [<%=(String)request.getAttribute("sbActualPC")%>],
    	         title: {
    		            text: 'Pay Cycles'
    		         }
    	      },
    	      yAxis: {
    	         min: 0,
    	         title: {
    	            text: 'Resource Efforts'
    	         }
    	      },
    	      plotOptions: {
    	      	column: {
    	            pointPadding: 0.2,
    	            borderWidth: 0
    	         }
    	      },
    	     series: [<%=request.getAttribute("sbRosterHours")%>]
    	   });
    	
    	
    });
</script>
<%
    String strSubTitle = null;
    String strTitle = "";
    String strP = (String)request.getParameter("paramSelection");
    if(strP!=null && strP.equalsIgnoreCase("WLH")) {
    	strSubTitle="By Location";
    	strTitle = "Location";
    } else if(strP!=null && strP.equalsIgnoreCase("SH")) {
    	strSubTitle="By Service";
    	strTitle = "Service";
    } else if(strP!=null && strP.equalsIgnoreCase("UTH")) {
    	strSubTitle="By Usertypes";
    	strTitle = "Usertypes";
    } else if(strP!=null && strP.equalsIgnoreCase("DH")) {
    	strSubTitle="By Departments";
    	strTitle = "Departments";
    } else {
    	strSubTitle="By Employee";
    	strTitle = "Employee Name";
    }
    %>
    
<%--  <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="Actual Effort" name="title"/>
    </jsp:include> --%>
    
	<div class="box-body" style="padding: 5px; overflow-y: auto; min-height: 600px;">
       <div class="desgn" style="margin-bottom: 5px;background:#f5f5f5; color:#232323;">
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
                       <s:form name="frmEmployeeMandaysReport" action="EmployeeMandaysReport" theme="simple">
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
										<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('2');" list="financialYearList" key=""/>
									</div>
									<%-- <div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">Month</p>
										<s:select theme="simple" name="strMonth" id="strMonth" listKey="monthId" listValue="monthName" list="monthList" key=""  onchange="submitForm('2');"/>
									</div>
									<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
										<p style="padding-left: 5px;">&nbsp;</p>
										<input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>
									</div> --%>
								</div>
							</div>
							<br>
							<div class="row row_without_margin">
								<div class="col-lg-12 col-md-12 col-sm-12 paddingleft0 paddingright0">
                                   <s:radio name="paramSelection" id="paramSelection" cssStyle="margin: 0px 5px;" list="#{'WLH':'By Location','SH':'By Service','DH':'By Department','UTH':'By UserType','EH':'By Employee'}" />
                                   <input type="button" name="Submit" value="Submit" class="btn btn-primary" style="margin:0px" onclick="submitForm('2');"/>  
                               </div>
							</div>
                       </s:form>
                   </div>
               </div>
               <!-- /.box-body -->
			</div>
		</div>
       
       <span style="font-weight:bold">[<%=strSubTitle%>]</span>
       <div class="clr margintop20">
           <!-- decorator="org.displaytag.decorator.TotalTableDecorator" -->
           <display:table name="alReport" class="table table-bordered overflowtable" id="lt1">
               <display:setProperty name="export.pdf" value="true" />
               	
               <display:column style="text-align:left" nowrap="nowrap" title="<%=strTitle %>" sort="false"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
               <%
                   for (int ii=alPayCycles.size()-1; ii>=0; ii--) {
                   	int count = 1+(ii * 3);
				%>
               <display:column style="width:120px;text-align:center" nowrap="nowrap" title="Actual" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(0+count)%></display:column>
               <display:column style="width:120px;text-align:center" nowrap="nowrap" title="Roster" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(1+count)%></display:column>
               <display:column style="width:120px;text-align:center" nowrap="nowrap" title="Var" > <%=((java.util.List) pageContext.getAttribute("lt1")).get(2+count)%></display:column>
               <% } %>
               <display:footer>
                   <tr>
                       <th colspan="1" style="text-align:center">Total</th>
                       <%
                           for (int ii=alPayCycles.size()-1; ii>=0; ii--) {
                           	int count = ii-1;
						%>
                       <th colspan="1" style="text-align:center"><%=hmTotalA.get(ii+"") %></th>
                       <th colspan="1" style="text-align:center"><%=hmTotalR.get(ii+"") %></th>
                       <th colspan="1" style="text-align:center"><%=hmTotalV.get(ii+"") %></th>
                       <% } %>
                   </tr>
               </display:footer>
           </display:table>
       </div>
       <script>
               		 head = '<tr><th colspan="1"></th>';
               			</script>
                      
                           <%
                               for (int ii=alPayCycles.size()-1; ii>=0; ii--) {
                               	int count = 1+ii;
							%>
							<script>
			               	   head = head + '<th colspan="3">'+'<%=(String)alPayCycles.get(ii)%>'+'</th>';
			               </script>
                           <% } %>
                    <script>
	               	   head = head+'</tr>';
	               	   
	               		$("thead").prepend(head);
		               	
	                </script>
		<%if(strP!=null && (strP.equalsIgnoreCase("WLH") || strP.equalsIgnoreCase("UTH") || strP.equalsIgnoreCase("SH") || strP.equalsIgnoreCase("DH"))) { %>
			<div class="chartholder">
				<div style="float: right; text-decoration: underline;">Displaying only last 6 paycycles</div>
				<div id="container_Actual" style="height: 300px; width:45%; float:left; "></div>
				<div id="container_Roster" style="height: 300px; width:45%; float:left;  "></div>
			</div>
		<% } %>
	</div>
	<!-- /.box-body -->

</div>