<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%-- <script type="text/javascript" src="scripts/charts/jquery.min.js"></script> --%>
<script src="scripts/charts/highcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>

<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />

<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>

<script type="text/javascript" charset="utf-8">

$(function () {
	$('#ltp').DataTable({ 
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

function submitForm(type){
	var financialYear = document.getElementById("financialYear").value;
	//alert("service ===>> " + service);
	$("#divResult").html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$.ajax({
		type : 'POST',
		url: 'ViewPaySlips.action?financialYear='+financialYear,
		data: $("#"+this.id).serialize(),
		success: function(result){
        	$("#divResult").html(result);
   		}
	});
}

</script>  


<script type="text/javascript">
//alert("hiii");
    var chart;
    
    $(document).ready(function() {
    	//alert("hii");
    	
//*****************PAYROLL***********************    	
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'containerForPayroll',
                type: 'spline'
            },
            title: {
                text: ''
            },
            
            xAxis: {
                type: 'linear',
                 categories: <%=(StringBuilder)request.getAttribute("sbMonth")%> ,
            title: {
	            text: 'Months'
	         }
            },
            yAxis: {
                title: {
                    text: 'Rs.'
                },
                min: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
            	formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                     this.x +', '+ this.y ;
            	}
            },
            
            series: [{
                name: 'Salary',
                data: <%= (StringBuilder)request.getAttribute("sbPayrollAmount") %> 
            }]
        });
        
        
   //**********************************REIMBURSE*********************     
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'containerForReimbursement',
                type: 'spline'
            },
            title: {
                text: ''
            },
            
            xAxis: {
                type: 'linear',
                 categories: <%=(StringBuilder)request.getAttribute("sbMonth")%> ,
            title: {
	            text: 'Months'
	         }
            },
            yAxis: {
                title: {
                    text: 'Rs.'
                },
                min: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
            	formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                     this.x +', '+ this.y ;
            	}
            },
            
            series: [{
                name: 'Reimbursement',
                data: <%= (StringBuilder)request.getAttribute("sbReimburse") %> 
            }]
        });
        
    //******************************************REIMBURSE CTC*****************
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'containerForReimbursementCTC',
                type: 'spline'
            },
            title: {
                text: ''
            },
            
            xAxis: {
                type: 'linear',
                 categories: <%=(StringBuilder)request.getAttribute("sbMonth")%> ,
                 title: {
     	            text: 'Months'
     	         }
            },
            yAxis: {
                title: {
                    text: 'Rs.'
                },
                min: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
            	formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                     this.x +', '+ this.y ;
            	}
            },
            
            series: [{
                name: 'CTC Reimbursement',
                data: <%= (StringBuilder)request.getAttribute("sbReimburseCTC") %> 
            }]
        });
    
 //***********************************CTC VARIABLE********************       
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'containerForCTC',
                type: 'spline'
            },
            title: {
                text: ''
            },
            
            xAxis: {
                type: 'linear',
                 categories: <%=(StringBuilder)request.getAttribute("sbMonth")%> ,
                 title: {
     	            text: 'Months'
     	         }
            },
            yAxis: {
                title: {
                    text: 'Rs.'
                },
                min: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
            	formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                     this.x +', '+ this.y ;
            	}
            },
            
            series: [{
                name: 'CTC Variable',
                data: <%= (StringBuilder)request.getAttribute("sbCTC") %> 
            }]
        });
 //**********************PERK ***************************
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'containerForPerk',
                type: 'spline'
            },
            title: {
                text: ''
            },
            
            xAxis: {
                type: 'linear',
                 categories: <%=(StringBuilder)request.getAttribute("sbMonth")%> ,
                 title: {
     	            text: 'Months'
     	         }
            },
            yAxis: {
                title: {
                    text: 'Rs.'
                },
                min: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
            	formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                     this.x +', '+ this.y ;
            	}
            },
            series: [{
                name: 'Perk',
                data: <%= (StringBuilder)request.getAttribute("sbPerk") %> 
            }]
        });       
 
 //************************GRATUTY********************
        chart = new Highcharts.Chart({
            chart: {
                renderTo: 'containerForGratuity',
                type: 'spline'
            },
            title: {
                text: ''
            },
            
            xAxis: {
                type: 'linear',
                 categories: <%=(StringBuilder)request.getAttribute("sbMonth")%> ,
                 title: {
     	            text: 'Months'
     	         }
            },
            yAxis: {
                title: {
                    text: 'Rs.'
                },
                min: 0,
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            },
            tooltip: {
            	formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                     this.x +', '+ this.y ;
            	}
            },
            
            series: [{
                name: 'Gratuity',
                data:[]
            }]
        });
 
    });
    
  //alert("hii2");
  
</script> 
    
<%String strTitle = ((session.getAttribute(IConstants.USERTYPE)!=null && !((String)session.getAttribute(IConstants.USERTYPE)).equalsIgnoreCase(IConstants.EMPLOYEE)) ? "Staff Compensation": "My Compensation"); %>
<%-- <jsp:include page="../common/SubHeader.jsp">
    <jsp:param value="<%=strTitle %>" name="title"/>
    </jsp:include> --%>
    
	<div class="box-body" style="padding: 5px; overflow-y: auto;min-height:600px;">
		<div class="box box-default collapsed-box">
			<div class="box-header with-border">
				<h3 class="box-title" style="font-size: 14px;padding-right: 10px;"><%=(String)request.getAttribute("selectedFilter") %></h3>
				<div class="box-tools pull-right">
					<button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-plus"></i></button>
					<button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
				</div>
			</div>
			<!-- /.box-header -->
			<div class="box-body" style="padding: 5px; overflow-y: auto; display:none;">
				<s:form name="frm" action="ViewPaySlips" theme="simple">
					<div class="row row_without_margin">
						<div class="col-lg-1 col-md-1 col-sm-12 autoWidth paddingright0">
							<i class="fa fa-filter"></i>
						</div>
						<div class="col-lg-11 col-md-11 col-sm-12 paddingleft0 paddingright0">
							<div class="col-lg-2 col-md-6 col-sm-12 autoWidth paddingleftright5">
								<p style="padding-left: 5px;">Financial Year</p>
								<s:select label="Select Financial Year" name="financialYear" id="financialYear" listKey="financialYearId" listValue="financialYearName" headerKey="0" onchange="submitForm('1');" list="financialYearList" key=""/>
							</div>
						</div>
					</div>
				</s:form>
			</div>
			<!-- /.box-body -->
		</div>


		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">Payslips</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 420px; min-height: 420px; overflow-y: auto;">
	                <div class="content1">
						<table class="table table-bordered">
							<thead>
								<tr>
									<th>Month</th>
									<th>Salary</th>	
								</tr>
							<tbody>
								<% java.util.List couterlist1 = (java.util.List)request.getAttribute("reportList"); %>
								<% for (int i=0; couterlist1!=null && i<couterlist1.size(); i++) { 
									java.util.List cinnerlist1 = (java.util.List)couterlist1.get(i); %>
								<tr>
									<td><%= cinnerlist1.get(0) %> </td>
									<td><%= cinnerlist1.get(1) %> </td>	
							 	</tr>
							 	<%}%>
							</tbody>
						</table>			
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>	

		 <section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">Salary</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 420px; min-height: 420px; overflow-y: auto;">
	                <div class="content1">
						<div id="containerForPayroll" style="height: 370px; width:100%; margin:10px 0 0 0px;">&nbsp;</div>
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>	 


		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">Reimbursement</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 420px; min-height: 420px; overflow-y: auto;">
	                <div class="content1">
						<div id="containerForReimbursement" style="height: 370px; width:100%; margin:10px 0 0 0px;">&nbsp;</div>
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>	

		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">Reimbursement Part Of CTC</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 420px; min-height: 420px; overflow-y: auto;">
	                <div class="content1">
						<div id="containerForReimbursementCTC" style="height: 370px; width:100%; margin:10px 0 0 0px;">&nbsp;</div>
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>	

		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">CTC Variable</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 420px; min-height: 420px; overflow-y: auto;">
	                <div class="content1">
						<div id="containerForCTC" style="height: 370px; width:100%; margin:10px 0 0 0px;">&nbsp;</div>
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>
		
		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">Perk</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 420px; min-height: 420px; overflow-y: auto;">
	                <div class="content1">
						<div id="containerForPerk" style="height: 370px; width:100%; margin:10px 0 0 0px;">&nbsp;</div>
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>
		
		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">Gratuity</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 420px; min-height: 420px; overflow-y: auto;">
	                <div class="content1">
						<div id="containerForGratuity" style="height: 370px; width:100%; margin:10px 0 0 0px;">&nbsp;</div>
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>
		
		<%--<div class="clr margintop20">
			 <table class="table table-hover table-bordered" id="ltp">
				<thead>
					<tr>
						<th>Month</th>
						<th>Payroll Amount</th>
						<th>Reimbursement</th>
						<th>Reimbursement Part Of CTC</th>
						<th>CTC Variable</th>
						<th>Perk</th>
						<th>Gratuity</th>
					</tr>
				</thead>
				 <tbody>
					<% java.util.List couterlist2 = (java.util.List)request.getAttribute("reportList"); %>
					<% for (int i=0; couterlist2!=null && i<couterlist2.size(); i++) { 
						java.util.List cinnerlist2 = (java.util.List)couterlist2.get(i); %>
					<tr>
						<td><%= cinnerlist2.get(0) %> </td>
						<td><%= cinnerlist2.get(1) %> </td>
						<td><%= cinnerlist2.get(2) %> </td>
						<td><%= cinnerlist2.get(3) %> </td>
						<td><%= cinnerlist2.get(4) %> </td>
						<td><%= cinnerlist2.get(5) %> </td>
						<td>&nbsp;</td>
					</tr>
					<% } %>
				</tbody> 
			</table>
		</div>
			<% if(couterlist2.size() != 0){%>
				<div class="custom-legends">
					<div class="custom-legend approved"><div class="legend-info">Approved</div></div>
					<div class="custom-legend act_now"><div class="legend-info">Yet to be paid</div></div>
				</div>
			<% } %>  --%>
	
			
	</div>
	<!-- /.box-body -->
