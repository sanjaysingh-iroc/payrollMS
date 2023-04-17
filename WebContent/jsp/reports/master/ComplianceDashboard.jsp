<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>

<script type="text/javascript" src="scripts/charts/jquery.min.js"></script>
<script src="scripts/charts/highcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />

<style> 
 

#greenbox {
height: 11px;
background-color:#00a65a; /* the critical component */
}

#redbox {
height: 11px;
background-color:#E25948; /* the critical component */
}

#yellowbox {
height: 11px;
background-color:#f39c12; /* the critical component */
}

#outbox {
height: 11px;
width: 100%;
background-color:#D8D8D8; /* the critical component */
}

.anaAttrib1 {
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}
.site-stats li {
width: 29%;
}

.highcharts-legend-item text{
font-weight: 300 !important; 
}
</style>

<%UtilityFunctions uF = new UtilityFunctions(); 
	CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
	List alMonth = (List) request.getAttribute("alMonth");
	Map<String,String>tdsBarchart1= (Map<String,String>)request.getAttribute("tdsBarchart");
	StringBuilder sbMonthName=(StringBuilder)request.getAttribute("sbMonthName");
	//System.out.println("sbMonthName=="+sbMonthName);
 %>
	

<script>
$(document).ready(function() {
var chart = Highcharts.chart('containerForTDSProjection', {

    title: {
    	 text: 'TDS Projection Summary'
    },

    subtitle: {
        text: ''
    },
    yAxis: {
        min: 0,
        title: {
           text: 'Amount'
        }
     },
    xAxis: {
    	categories: [<%=(StringBuilder)request.getAttribute("sbMonthName")%>],
    	title: {
	            text: 'Months'
	         }
    },

    series: [{
        type: 'column',
        colorByPoint: true,
        data: [<%=(StringBuilder)request.getAttribute("sbTDSMonthAmountData")%>],
        showInLegend: false
    }]

});
});


<%-- var tdsBarChartv;

$(document).ready(function() {
	
	tdsBarChartv = new Highcharts.Chart({
   		
      chart: {
         renderTo: 'containerForTDSProjection',
        	type: 'column'
      },
      title: {
         text: 'TDS Projection Summary'
      },
     
      xAxis: {
    	     categories: [<%=(StringBuilder)request.getAttribute("sbMonthName")%>], 
/*     	    categories:['April','May','June','July','August','September','October','November','December','January','February','March'],
 */    	   
    	    labels: {
               rotation: -45,
               align: 'right',
               style: {
                   font: 'normal 10px Verdana, sans-serif'
               }
            },
           title: {
  	            text: 'Months'
  	         }
      }, 
      credits: {
       	enabled: false
   	  },
      yAxis: {
         min: 0,
         title: {
            text: 'Amount'
         }
      },
      plotOptions: {
         column: {
            pointPadding: 0.2,
            borderWidth: 0
         }
      },
      
      series: [<%=(StringBuilder)request.getAttribute("sbTDSMonthAmountData")%>]
/*       series: [{name:'April',data: [6868]},{name:'May',data: [6773]},{name:'June',data: [6773]},{name:'July',data: [6773]},{name:'August',data: [6773]},{name:'September',data: [6773]},{name:'October',data: [6773]},{name:'November',data: [6773]},{name:'December',data: [6773]},{name:'January',data: [6773]},{name:'February',data: [6773]},{name:'March',data: [6773]}]
 */   
	});
	
}); --%>

</script>

<script>
$(function() {
	$("body").on('click','#closeButton',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
	$("body").on('click','.close',function(){
		$(".modal-dialog").removeAttr('style');
		$(".modal-body").height(400);
		$("#modalInfo").hide();
	});
});

function viewStatutoryIdAndRegInfo(strOrgId, userscreen, navigationId, toPage) {
	//alert("hii editStatutoryIdAndRegInfo");
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update Statutory ID & Registration Information');
	$.ajax({
		url : 'UpdateStatutoryIdAndRegInfo.action?operation=E&ID='+strOrgId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&fromPage=CD',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

function viewStatutoryIdAndRegInfoLocation(strLocationId, userscreen, navigationId, toPage) {
	//alert("hii editStatutoryIdAndRegInfoLocation");
	var dialogEdit = '.modal-body';
	$(dialogEdit).empty();
	$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
	$("#modalInfo").show();
	$('.modal-title').html('Update Statutory ID & Registration Information of location');
	$.ajax({
		url : 'UpdateStatutoryIdLocation.action?operation=E&ID='+strLocationId+'&userscreen='+userscreen+'&navigationId='+navigationId+'&toPage='+toPage+'&fromPage=CD',
		cache : false,
		success : function(data) {
			$(dialogEdit).html(data);
		}
	});
}

</script>

<%
        String empApprovedCount= (String)request.getAttribute("empApprovedCount");
        String empUnApprovedCount=(String)request.getAttribute("empUnApprovedCount");
        String empPendingCount = (String)request.getAttribute("empPendingCount");
        
        Map<String,String>hmPtCurentYearamount= (Map<String,String>)request.getAttribute("hmPtCurentYearamount");
        Map<String,String>hmPtPrevYearamount= (Map<String,String>)request.getAttribute("hmPtPrevYearamount");

        
        Map<String,String>hmTdsCurrentYearamount= (Map<String,String>)request.getAttribute("hmTdsCurrentYearamount");
        Map<String,String>hmTdsPrevYearamount= (Map<String,String>)request.getAttribute("hmTdsPrevYearamount");
        
        Map<String,String>hmEpfcurrentYearamount= (Map<String,String>)request.getAttribute("hmEpfcurrentYearamount");
        Map<String,String>hmEpfPrevYearamount= (Map<String,String>)request.getAttribute("hmEpfPrevYearamount");

        Map<String,String>hmEsiCurrentYearamount= (Map<String,String>)request.getAttribute("hmEsiCurrentYearamount");
        Map<String,String>hmEsiPrevYearamount= (Map<String,String>)request.getAttribute("hmEsiPrevYearamount");
   
        Map<String,String>hmLwfCurrentYearamount= (Map<String,String>)request.getAttribute("hmLwfCurrentYearamount");
        Map<String,String>hmLwfPrevYearamount= (Map<String,String>)request.getAttribute("hmLwfPrevYearamount");
		Map hmChildNavL = (Map) session.getAttribute("hmChildNavL");
   
%>
<div class="leftbox reportWidth">
	<section class="content">
		<div class="row jscroll">
		
			 <section class="col-lg-4 connectedSortable paddingright5">
				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3 class="box-title">IT Declaration</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		                <!-- /.box-header -->
		               <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                	<div class="content1">
		                	<% if(empApprovedCount.equals(0) && empUnApprovedCount.equals(0) && empPendingCount.equals(0)){ %>
		                		<div style="padding: 20px;">No Data To Display</div>
		                	<%}else{ %>
		                	<div id="chartdiv"></div>
				             	<% int toIntempApprovedCount = uF.parseToInt(empApprovedCount);
				             		int toIntempUnApprovedCount = uF.parseToInt(empUnApprovedCount);
				             		int toIntempPendingCount = uF.parseToInt(empPendingCount);
				             	%>
				               <script>
				               var ITDeclaration = [];
				             
				               	   ITDeclaration.push({"name": "Approved","count": '<%=toIntempApprovedCount%>'});
				               	   ITDeclaration.push({"name": "UnApproved","count": '<%=toIntempUnApprovedCount%>'});
				               	   ITDeclaration.push({"name": "Pending","count": '<%=toIntempPendingCount%>'});
								
				                </script> 
								
								<script>
								
								var chart = AmCharts.makeChart("chartdiv", {
									  "type": "pie",
									  "startDuration": 0,
									   "theme": "light",
									  "addClassNames": true,
									  "legend":{
									   	"position":"right",
									    "marginRight":100,
									    "autoMargins":false
									  },
									  "labelsEnabled": false,
									  "innerRadius": "30%",
									  "defs": {
									    "filter": [{
									      "id": "shadow",
									      "width": "250%",
									      "height": "250%",
									      "feOffset": {
									        "result": "offOut",
									        "in": "SourceAlpha",
									        "dx": 0,
									        "dy": 0
									      },
									      "feGaussianBlur": {
									        "result": "blurOut",
									        "in": "offOut",
									        "stdDeviation": 5
									      },
									      "feBlend": {
									        "in": "SourceGraphic",
									        "in2": "blurOut",
									        "mode": "normal"
									      }
									    }]
									  },
									  "dataProvider": ITDeclaration,
									  "valueField": "count",
									  "titleField": "name",
									  "export": {
									    "enabled": true
									  }
									});
								</script>
							<% } %>
		                </div>
		               </div>
		                <!-- /.box-body -->
		            </div>
			</section>
			
<!-- ******************************************** for PT********************************* -->	
 		<section class="col-lg-4 connectedSortable paddingright5" >
			<div class="box box-info">		        
		         <div class="box-header with-border">
	                 <h3 class="box-title">PT</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
		        
               	 <!-- /.box-header -->
               	 
    	 <%if(hmPtCurentYearamount!=null && hmPtPrevYearamount!=null && hmPtCurentYearamount.size()>=0  && hmPtPrevYearamount.size()>=0){
               		double totalPtPrevamount=0.0,totalPtCurrentamount=0.0;
               		 	Iterator<String>it=hmPtCurentYearamount.keySet().iterator();
               		 	while(it.hasNext())
               		 	{
               		 		String keyid=it.next();
               		 		totalPtCurrentamount=uF.parseToDouble(hmPtCurentYearamount.get(keyid));
               		 	}
               		 
               		 	Iterator<String>it1=hmPtPrevYearamount.keySet().iterator();
               		 	while(it1.hasNext()){
               		 		String keyid1=it1.next();
               		 		totalPtPrevamount=uF.parseToDouble(hmPtPrevYearamount.get(keyid1));
							//System.out.println("======totalPrevamount==="+hmEpfPrevYearamount.get(keyid1));
               		 	}
						
						%>
               	 
               	 
                	<div class="box-body" style="padding: 5px; overflow-y: auto;">
	                	<div class="content1">
                    	<ul class="site-stats">
								<li class="bg_lh" style="width: 92%;">
								<% String sign="";
								    double amountPtDiff=totalPtCurrentamount - totalPtPrevamount;
									if(amountPtDiff >= 0){
									sign="+"; %>
									  <div class="col-md-2" >
									 	 <i class="fa fa-long-arrow-up" style="font-size:50px;color:green;padding-top:10px"></i> 	
									  </div>
									  
									<%}else{ sign="";%>
									 <div class="col-md-2" >
										 <i class="fa fa-long-arrow-down " style="font-size:50px;color:red;padding-top:10px"></i> 	
									 </div>
								  <%}%>
									<div class="col-md-2 paddingright5">
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),totalPtCurrentamount) %> </small> 
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),totalPtPrevamount) %> </small> 
										<small style="font-weight: bold"><%=sign%><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(amountPtDiff)) %> </small> 
								</div>
							</li>
                   		</ul>
                   		</div>
                	</div> 
               <!-- /.box-body -->
               <%} %>
               <!-- /.box-body -->
		     </div>
  		</section>
	
	
	<!-- ******************************************** for TDS********************************* -->	
 		<section class="col-lg-4 connectedSortable paddingright5">
  			<div class="box box-info">
		        <div class="box-header with-border">
	                 <h3 class="box-title">TDS</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
		       
               	 <!-- /.box-header -->
               	 <%if(hmTdsCurrentYearamount!=null && hmTdsPrevYearamount!=null && hmTdsCurrentYearamount.size()>=0  && hmTdsPrevYearamount.size()>=0){
               		double totalTdsPrevamount=0.0,totalTdsCurrentamount=0.0;
               		 	Iterator<String>it=hmTdsCurrentYearamount.keySet().iterator();
               		 	while(it.hasNext())
               		 	{
               		 		String keyid=it.next();
        					totalTdsCurrentamount=uF.parseToDouble(hmTdsCurrentYearamount.get(keyid));
               		 	}
               		 
               		 	Iterator<String>it1=hmTdsPrevYearamount.keySet().iterator();
               		 	while(it1.hasNext()){
               		 		String keyid1=it1.next();
               		 		totalTdsPrevamount=uF.parseToDouble(hmTdsPrevYearamount.get(keyid1));
               		 	}
						
						%>
               	 
                	<div class="box-body" style="padding: 5px; overflow-y: auto;">
	                	<div class="content1">
                    		<ul class="site-stats">
								<li class="bg_lh" style="width: 92%;">
								<% String sign="";
								    double amountTdsDiff=totalTdsCurrentamount - totalTdsPrevamount;
									if(amountTdsDiff >= 0){
									sign="+"; %>
									  <div class="col-md-2" >
									 	 <i class="fa fa-long-arrow-up" style="font-size:50px;color:green;padding-top:10px"></i> 	
									  </div>
									  
									<%}else{ sign="";%>
									 <div class="col-md-2" >
										 <i class="fa fa-long-arrow-down" style="font-size:50px;color:red;padding-top:10px"></i> 	
						               	 
									 </div>
								  <%}%>
									<div class="col-md-2 paddingright5">
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),totalTdsCurrentamount) %> </small> 
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),totalTdsPrevamount) %> </small> 
										<small style="font-weight: bold"><%=sign%><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(amountTdsDiff)) %> </small> 
								</div>
							</li>
                   		</ul>
                	</div> 
                </div>
               <!-- /.box-body -->
               <%} %>
		    </div>
  		</section>	
		
	<!-- ******************************************** for EPF********************************* -->	
 		<section class="col-lg-4 connectedSortable paddingright5">
  			<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3 class="box-title">EPF</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
               	 <!-- /.box-header -->
               	 <%if(hmEpfcurrentYearamount!=null && hmEpfPrevYearamount!=null && hmEpfcurrentYearamount.size()>=0 && hmEpfPrevYearamount.size()>=0){
               		double totalPrevamount=0.0,totalCurrentamount=0.0;
               		 	Iterator<String>it=hmEpfcurrentYearamount.keySet().iterator();
               		 	while(it.hasNext())
               		 	{
               		 		String keyid=it.next();
        					//System.out.println("======totalcurrentamount==="+hmEpfcurrentYearamount.get(keyid));
        					totalCurrentamount=uF.parseToDouble(hmEpfcurrentYearamount.get(keyid));
               		 	}
               		 
               		 	Iterator<String>it1=hmEpfPrevYearamount.keySet().iterator();
               		 	while(it1.hasNext()){
               		 		String keyid1=it1.next();
               		 		totalPrevamount=uF.parseToDouble(hmEpfPrevYearamount.get(keyid1));
							//System.out.println("======totalPrevamount==="+hmEpfPrevYearamount.get(keyid1));
               		 	}
						
						%>
               	 
               	 
                	<div class="box-body" style="padding: 5px; overflow-y: auto;">
	               		 <div class="content1">
                    	<ul class="site-stats">
								<li class="bg_lh" style="width: 92%;">
								<% String sign="";
								    double amountEpfDiff=totalCurrentamount - totalPrevamount;
									if(amountEpfDiff >= 0){
									sign="+"; %>
									  <div class="col-md-2" >
									 	 <i class="fa fa-long-arrow-up" style="font-size:50px;color:green;padding-top:10px"></i> 	
									  </div>
									  
									<%}else{ sign="";%>
									 <div class="col-md-2" >
										 <i class="fa fa-long-arrow-down" style="font-size:50px;color:red;padding-top:10px"></i> 	
									 </div>
								  <%}%>
									<div class="col-md-2 paddingright5">
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),totalCurrentamount) %> </small> 
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),totalPrevamount) %> </small> 
										<small style="font-weight: bold"><%=sign%><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(amountEpfDiff)) %> </small> 
								</div>
							</li>
                   		</ul>
                	</div> 
                	</div>
               <!-- /.box-body -->
               <%} %>
		     </div>
  		</section>
  		
  	<!-- ******************************************** for ESIC********************************* -->	
 		<section class="col-lg-4 connectedSortable paddingright5">
  			<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3 class="box-title">ESIC</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		          
               	<%if(hmEsiCurrentYearamount!=null && hmEsiPrevYearamount!=null && hmEsiCurrentYearamount.size()>=0 &&  hmEsiPrevYearamount.size()>=0){
               		double esiPrevamount=0.0,esiCurrentamount=0.0;
               		 	Iterator<String>ite=hmEsiCurrentYearamount.keySet().iterator();
               		 	while(ite.hasNext())
               		 	{
               		 		String keyid=ite.next();
        					//System.out.println("======totalcurrentamount==="+hmEpfcurrentYearamount.get(keyid));
        					esiCurrentamount=uF.parseToDouble(hmEsiCurrentYearamount.get(keyid));
               		 	}
               		 
               		 	Iterator<String>ite1=hmEsiPrevYearamount.keySet().iterator();
               		 	while(ite1.hasNext()){
               		 		String keyid1=ite1.next();
               		 		esiPrevamount=uF.parseToDouble(hmEsiPrevYearamount.get(keyid1));
							//System.out.println("======totalPrevamount==="+hmEpfPrevYearamount.get(keyid1));
               		 	}
						
						%>
               	 
               	 
                	<div class="box-body" style="padding: 5px; overflow-y: auto;">
	               		 <div class="content1">
                    	<ul class="site-stats">
								<li class="bg_lh" style="width: 92%;">
								<% String sign="";
								    double amountEsiDiff=esiCurrentamount - esiPrevamount;
									if(amountEsiDiff >= 0){
									sign="+"; %>
									  <div class="col-md-2" >
									 	 <i class="fa fa-long-arrow-up" style="font-size:50px;color:green;padding-top:10px"></i> 	
									  </div>
									  
									<%}else{ sign="";%>
									 <div class="col-md-2" >
										 <i class="fa fa-long-arrow-down" style="font-size:50px;color:red;padding-top:10px"></i> 	
									 </div>
								  <%}%>
									<div class="col-md-2 paddingright5">
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),esiCurrentamount) %> </small> 
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),esiPrevamount) %> </small> 
										<small style="font-weight: bold"><%=sign%><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(amountEsiDiff)) %> </small> 
								</div>
							</li>
                   		</ul>
                	</div> 
                	</div>
               <!-- /.box-body -->
               <%} %>
		     </div>
  		</section>
  		
  	<!-- ******************************************** for LWF********************************* -->	
 		<section class="col-lg-4 connectedSortable paddingright5">
  			<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3 class="box-title">LWF</h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
               	 <%if(hmLwfCurrentYearamount!=null && hmLwfPrevYearamount!=null && hmLwfCurrentYearamount.size()>=0 && hmLwfPrevYearamount.size()>=0){
               		double lwfPrevamount=0.0,lwfCurentamount=0.0;
               		 	Iterator<String>ite=hmLwfCurrentYearamount.keySet().iterator();
               		 	while(ite.hasNext())
               		 	{
               		 		String keyid=ite.next();
        					//System.out.println("======totalcurrentamount==="+hmEpfcurrentYearamount.get(keyid));
        					lwfCurentamount=uF.parseToDouble(hmLwfCurrentYearamount.get(keyid));
               		 	}
               		 
               		 	Iterator<String>ite1=hmLwfPrevYearamount.keySet().iterator();
               		 	while(ite1.hasNext()){
               		 		String keyid1=ite1.next();
               		 		lwfPrevamount=uF.parseToDouble(hmLwfPrevYearamount.get(keyid1));
							//System.out.println("======totalPrevamount==="+hmEpfPrevYearamount.get(keyid1));
               		 	}
						
						%>
               	 
               	 
                	<div class="box-body" style="padding: 5px; overflow-y: auto;">
	               		 <div class="content1">
                    	<ul class="site-stats">
								<li class="bg_lh" style="width: 92%;">
								<% String sign="";
								    double amountLwfDiff=lwfCurentamount - lwfPrevamount;
									if(amountLwfDiff >= 0){
									sign="+"; %>
									  <div class="col-md-2" >
									 	 <i class="fa fa-long-arrow-up" style="font-size:50px;color:green;padding-top:10px"></i> 	
									  </div>
									  
									<%}else{ sign="";%>
									 <div class="col-md-2" >
										 <i class="fa fa-long-arrow-down" style="font-size:50px;color:red;padding-top:10px"></i> 	
									 </div>
								  <%}%>
									<div class="col-md-2 paddingright5">
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),lwfCurentamount) %> </small> 
										<small style="font-weight: bold"><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),lwfPrevamount) %> </small> 
										<small style="font-weight: bold"><%=sign%><%=uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(amountLwfDiff)) %> </small> 
								</div>
							</li>
                   		</ul>
                	</div> 
                	</div>
               <!-- /.box-body -->
               <%} %>
		     </div>
		     
  		</section>
  		
  	<%String navigationId="131";
  		List alListC = (List) hmChildNavL.get(navigationId);
	 	if(alListC==null)alListC = new ArrayList();
	 	// System.out.println("alListC.size()"+alListC.size());
	 			Navigation navC1 = (Navigation)alListC.get(1);
	 			//System.out.println("navC"+navC);%> 
  				<section class="col-lg-6 connectedSortable paddingright5">
					<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC1.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						
						<div class="box-body" style="padding: 5px; max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
				   		 	<div style="clear: both; margin-left:0px" class="pagetitle">Professional Tax slabs for Males</div>
							 <div class="col-md-12 col_no_padding">
								 <table class="table table-bordered" id="lt">
									<thead>
										<tr>
											<th style="text-align: left;">Income From</th>
											<th style="text-align: left;">Income To</th>
											<th style="text-align: left;">Deduction per paycycle</th>
											<th style="text-align: left;">Deduction Amount</th>
											<th style="text-align: left;">State</th>
										</tr>
									</thead>
									<tbody>
									 <% 	List<List<String>> cOuterList = (List<List<String>>)request.getAttribute("reportListM");
										if(cOuterList == null) cOuterList = new ArrayList<List<String>>();
									 	for (int i=0; i<cOuterList.size(); i++) {
									 		List<String> cInnerList = (List<String>)cOuterList.get(i); 
									 %>
											<tr id = <%= cInnerList.get(0) %> >
												<td> <%= cInnerList.get(1) %></td>
												<td> <%= cInnerList.get(2) %></td>
												<td> <%= cInnerList.get(3) %></td>
												<td> <%= cInnerList.get(4) %></td>
												<td> <%= cInnerList.get(5) %></td>
											</tr>
										<% } 
									 	if(cOuterList.size() == 0) {
										%>
											<tr><td colspan="6" align="center">No data available in table</td></tr>
										<% } %> 
									</tbody>
								</table> 
							</div>
							<div style="clear: both; margin-left:0px" class="pagetitle">Professional Tax slabs for Females</div>
							<div class="col-md-12 col_no_padding">
								<table class="table table-bordered" id="lt">
									<thead>
										<tr>
											<th style="text-align: left;">Income From</th>
											<th style="text-align: left;">Income To</th>
											<th style="text-align: left;">Deduction per paycycle</th>
											<th style="text-align: left;">Deduction Amount</th>
											<th style="text-align: left;">State</th>
											
										</tr>
									</thead>
									<tbody>
									 <% 	cOuterList = (List<List<String>>)request.getAttribute("reportListF");
										if(cOuterList == null) cOuterList = new ArrayList<List<String>>();
										for (int i=0; i<cOuterList.size(); i++) { 
											List<String> cInnerList = (List<String>)cOuterList.get(i); 
									%>
											<tr id = <%= cInnerList.get(0) %> >
												<td> <%= cInnerList.get(1) %></td>
												<td> <%= cInnerList.get(2) %></td>
												<td> <%= cInnerList.get(3) %></td>
												<td> <%= cInnerList.get(4) %></td>
												<td> <%= cInnerList.get(5) %></td>
											</tr>
										<% }
										if(cOuterList.size() == 0) {
										%>
											<tr><td colspan="6" align="center">No data available in table</td></tr>
										<% } %> 
									</tbody>
								</table> 
							</div>
							</div>
						</div>
					</div>
				</section>
  		
  <!-- ********************************Deduction Report Tax************************** -->		
  		<%Navigation navC2 = (Navigation)alListC.get(2); %>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC2.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
		            
						<div class="box-body" style="padding: 5px;max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
							<div class="col-md-12 col_no_padding">
								<div style="clear: both; margin-left:0px" class="pagetitle">Income Tax slabs for Males</div>
									<table class="table table-bordered" id="lt">
										<thead>
											<tr>
												<th style="text-align: left;">Age From</th>
												<th style="text-align: left;">Age To</th>			
												<th style="text-align: left;">Income From</th>
												<th style="text-align: left;">Income To</th>
												<th style="text-align: left;">Deduction Amount</th>
												<th style="text-align: left;">Deduction Type</th>
												
											</tr>
										</thead>
										<tbody>
										 <% java.util.List cOuterListtax = (java.util.List)request.getAttribute("reportListMTax"); %>
										 <% for (int i=0; i<cOuterListtax.size(); i++) { %>
										 <% java.util.List cInnerListtax = (java.util.List)cOuterListtax.get(i); %>
											<tr id = <%= cInnerListtax.get(0) %> >
												<td><%= cInnerListtax.get(1) %></td>
												<td><%= cInnerListtax.get(2) %></td>
												<td><%= cInnerListtax.get(4) %></td>
												<td><%= cInnerListtax.get(5) %></td>
												<td><%= cInnerListtax.get(6) %></td>
												<td><%= cInnerListtax.get(7) %></td>
											</tr>
											<% } %> 
										</tbody>
									</table> 
								</div>
								<div class="col-md-12 col_no_padding">
								<div style="clear: both;margin-left:0px;margin-top: 20px;" class="pagetitle">Income Tax slabs for Females</div>
									<table class="table table-bordered" id="lt1">
										<thead>
											<tr>
												<th style="text-align: left;">Age From</th>
												<th style="text-align: left;">Age To</th>			
												<th style="text-align: left;">Income From</th>
												<th style="text-align: left;">Income To</th>
												<th style="text-align: left;">Deduction Amount</th>
												<th style="text-align: left;">Deduction Type</th>
											</tr>
										</thead>
										<tbody>
										 <% cOuterListtax = (java.util.List)request.getAttribute("reportListFTax"); %>
										 <% for (int i=0; i<cOuterListtax.size(); i++) { %>
										 <% java.util.List cInnerListtax = (java.util.List)cOuterListtax.get(i); %>
											<tr id = <%= cInnerListtax.get(0) %> >
												<td><%= cInnerListtax.get(1) %></td>
												<td><%= cInnerListtax.get(2) %></td>
												<td><%= cInnerListtax.get(4) %></td>
												<td><%= cInnerListtax.get(5) %></td>
												<td><%= cInnerListtax.get(6) %></td>
												<td><%= cInnerListtax.get(7) %></td>
											</tr>
											<% } %> 
									</tbody>
								</table> 
							</div>
						</div>
						</div>
					</div>
			</section>
			
<!--************************** Section Report*********************** -->			
		<%Navigation navC3 = (Navigation)alListC.get(3); %>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC3.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						
						<div class="box-body" style="padding: 5px; max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
							<div class="col-md-12 col_no_padding">
								<table class="table table-bordered" id="lt">
									<thead>
										<tr>
											<th style="text-align: left;">Section Code</th>
											<th style="text-align: left;">Section Description</th>
											<th style="text-align: left;">Section Exemption Limit</th>
											<th style="text-align: left;">Section Limit Type</th>
											<th style="text-align: left;">Under Section</th>
										</tr>
									</thead>
									<tbody>
									 <% java.util.List couterlistSection = (java.util.List)request.getAttribute("reportListSection"); %>
									 <% for (int i=0; couterlistSection!=null && i<couterlistSection.size(); i++) { %>
									 <% java.util.List cinnerlistsection = (java.util.List)couterlistSection.get(i); %>
										<tr id = <%= cinnerlistsection.get(0) %> >
											<td><%=  cinnerlistsection.get(1) %></td>
											<td><%=  cinnerlistsection.get(2) %></td>
											<td><%=  cinnerlistsection.get(3) %></td>
											<td><%=  cinnerlistsection.get(4) %></td>
											<td><%=  cinnerlistsection.get(5) %></td>
										</tr>
										<% } %> 
									</tbody>
								</table>
							</div>
							</div>
						</div>
					</div>
			</section>
			
			<%Navigation navC4 = (Navigation)alListC.get(4); %>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC4.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						
						<div class="box-body" style="padding: 5px; max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
							<div class="col-md-12 col_no_padding">
								<table class="table table-bordered" id="lt">
									<thead>
										<tr>
											<th style="text-align: left;">Exemption Name</th>
											<th style="text-align: left;">Exemption Salary Head</th>
											<th style="text-align: left;">Exemption Description</th>
											<th style="text-align: left;">Exemption Limit</th>
											<th style="text-align: left;">Under Section</th>
											<th style="text-align: left;">In Investment Form</th>
<!-- 											<th style="text-align: left;" class="no-sort">Action</th>
 -->										</tr>
									</thead>
									<tbody>
									<% java.util.List couterlistExcemption = (java.util.List)request.getAttribute("reportListExcemption"); %>
									 <% for (int i=0; couterlistExcemption!=null && i<couterlistExcemption.size(); i++) { 
										 java.util.List cinnerlistExcemption = (java.util.List)couterlistExcemption.get(i); %>
										<tr id = <%=cinnerlistExcemption.get(0) %> >
											<td><%=cinnerlistExcemption.get(1) %></td>
											<td><%=cinnerlistExcemption.get(2) %></td>
											<td><%=cinnerlistExcemption.get(3) %></td>
											<td><%=cinnerlistExcemption.get(4) %></td>
											<td><%=cinnerlistExcemption.get(5) %></td>
											<td><%=cinnerlistExcemption.get(6) %></td>
											<%-- <td>
												<a href="javascript:void(0)"  onclick="editExamption('<%=cinnerlist.get(0) %>', '<%=(String)request.getAttribute("financialYear") %>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>','ER')" title="Edit Exemption"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
												<a href="javascript:void(0)"  onclick="deleteExemption('AddExemption.action?operation=D&exemptionId=<%=cinnerlist.get(0) %>&userscreen=<%=userscreen%>&navigationId=<%=navigationId%>&toPage=<%=toPage%>&financialYear=<%=(String)request.getAttribute("financialYear") %>', '<%=(String)request.getAttribute("financialYear") %>')"  style="color:rgb(233,0,0)"><i class="fa fa-trash" aria-hidden="true"></i></a>
											</td> --%>
										</tr>
										<% } %> 
									</tbody>
								</table>
							</div>
						</div>
						</div>
					</div>
			</section>
	
 <!--***************************HRAReport*******************  -->		
			<%Navigation navC5 = (Navigation)alListC.get(5); %>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC5.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						<div class="box-body" style="padding: 5px; max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
						  <div class="col-md-12 col_no_padding">
							 <div class="pagetitle" style="margin:10px 0px 10px 0px">Previous Year HRA Calculation</div>
								<table width="100%" class="table table-bordered">
									<tr>
										<th class="alignCenter">FYI</th>
										<th class="alignCenter">Rent Paid - x % of salary</th>
										<th class="alignCenter">x % of salary in metro cities</th>
										<th class="alignCenter">x % of salary in other cities</th>
										<th class="alignCenter">Salary Heads</th>
									</tr>
									 <%
									 List alHRASettings = (List)request.getAttribute("alHRASettings");
									 if(alHRASettings==null) alHRASettings=new ArrayList();
									  for(int i=0; i<alHRASettings.size(); i++) {
										List alInner = (List)alHRASettings.get(i);
										 if(alInner==null)alInner=new ArrayList();
									 %>
										<tr>
											<td class="alignCenter"><%=uF.showData((String)alInner.get(0), "")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(1), "0")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(2), "0")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInner.get(3), "0")%></td>
											<td><%=uF.showData((String)alInner.get(4), "")%></td>
										</tr>
									<% } %> 
								</table>
							</div>
						</div>
						</div>
					</div>
			</section>
			
<!--******************************EPF Setting************************* -->
		<%Navigation navC6 = (Navigation)alListC.get(6); %>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC6.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						
						<div class="box-body" style="padding: 5px;max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
						   <div class="col-md-12 col_no_padding">
						   <%String currency = (String)request.getAttribute("currency");
						   		List alEPFSettings = (List)request.getAttribute("alEPFSettings");
						   		if(alEPFSettings==null)alEPFSettings=new ArrayList();
						   %>
						   
								<div class="pagetitle" style="margin:35px 0px 10px 0px">Previous Year EPF Calculation</div>
								<table  class="table table-bordered">
									<tr>
										<th class="alignCenter" valign="top" rowspan="2">FYI</th>
										<th class="alignCenter" colspan="4">EPF</th>
										<th class="alignCenter" colspan="2">EPS</th>
										<th class="alignCenter" colspan="2">EDLI</th>
										<th class="alignCenter" colspan="2">Admin</th>
										<th class="alignCenter" valign="top" rowspan="2">Salary Heads</th>
									</tr>
									
									<tr>
										<th class="alignCenter">Employee<br/>(%)</th>
										<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
										<th class="alignCenter">Employer<br/>(%)</th>
										<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
										<th class="alignCenter">Employer<br/>(%)</th>
										<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
										<th class="alignCenter">Employer<br/>(%)</th>
										<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
										<th class="alignCenter">EPF (Employer)<br/>(%)</th>
										<th class="alignCenter">EDLI (Employer)<br/>(%)</th>
									</tr>
										<%
											for(int i=0; i<alEPFSettings.size(); i++){ 
												List alInnerEPF = (List)alEPFSettings.get(i);
												if(alInnerEPF==null)alInnerEPF=new ArrayList();
										%>
										<tr>
											<td class="alignCenter"><%=uF.showData((String)alInnerEPF.get(0), "")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(1), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(2), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(3), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(4), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(5), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(6), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(7), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(8), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(9), "0")%></td>
											<td class="alignRight"><%=uF.showData((String)alInnerEPF.get(10), "0")%></td>
											<td class=""><%=uF.showData((String)alInnerEPF.get(11), "")%></td>
										</tr>
								<div class="box-body" >	<%}%>
								</table>
							</div>
						</div>
						</div>
					</div>
			</section>
		
<!--********************************ESIS Setting***************************  -->		
			<%Navigation navC7 = (Navigation)alListC.get(7); %>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC7.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						
						<% LinkedHashMap hmESISettings = (LinkedHashMap)request.getAttribute("hmESISettings");
							if(hmESISettings==null)hmESISettings=new LinkedHashMap();
						%>
						<div class="box-body" style="padding: 5px; max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
							<div class="col-md-12 col_no_padding">
								<div class="pagetitle" style="margin:10px 0px 10px 0px;clear: both;padding-top: 20px;">Previous Year ESI Calculation</div>
								<table width="100%" class="table table-bordered">
								
									<tr>
										<th valign="top" rowspan="2">Financial Year</th>
										<th valign="top" rowspan="2">Level</th>
										<th class="alignCenter" colspan="3">ESI</th>
										<th class="alignCenter" valign="top" rowspan="2">Salary Heads to be considered</th>
										<th class="alignCenter" valign="top" rowspan="2">Salary Heads to be eligible</th>
									</tr>
									
									<tr>
										<th class="alignCenter">Employee<br/>(%)</th>
										<th class="alignCenter">Employer<br/>(%)</th>
										<th class="alignCenter">Max Limit<br/>(<%=uF.showData(currency, "") %>)</th>
									</tr>
							
									<%
									Set set = hmESISettings.keySet();
									Iterator it = set.iterator();
									while(it.hasNext()){
										String strStateId = (String)it.next();
										List alEPFSettingss = (List)hmESISettings.get(strStateId);
										if(alEPFSettingss==null)alEPFSettingss=new ArrayList();
									%>
									<tr>
										<th colspan="5" class="alignLeft"><%= strStateId%></th>
									</tr>
								
									<%
									for(int i=0; i<alEPFSettingss.size(); i++){
										List alInnerESIS = (List)alEPFSettingss.get(i);
										if(alInnerESIS==null)alInnerESIS=new ArrayList();
									%>
										<tr>
											<td class="alignCenter"><%=uF.showData((String)alInnerESIS.get(0), "")%></td>
											<td class="alignCenter"><%=uF.showData((String)alInnerESIS.get(6), "")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInnerESIS.get(1), "0")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInnerESIS.get(2), "0")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInnerESIS.get(3), "0")%></td>
											<td><%=uF.showData((String)alInnerESIS.get(4), "-")%></td>
											<td><%=uF.showData((String)alInnerESIS.get(5), "-")%></td>
										</tr>
										
									<% } } %>
								</table>
							</div>
						</div>
						</div>
					</div>
			</section>

<!--*****************************LWF Setting*******************  -->			
			<%Navigation navC8 = (Navigation)alListC.get(8);%>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC8.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						
						<div class="box-body" style="padding: 5px;max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
							<div class="col-md-12 col_no_padding">
								<div style="margin:35px 0px 10px 0px"><span class="pagetitle">Previous Year LWF Calculation</span></div>
									<table class="table table-bordered">
									
										<tr>
											<th valign="top" rowspan="2" class="alignCenter">FYI</th>
											<th colspan="4" class="alignCenter">LWF</th>
											<th valign="top" rowspan="2" class="alignCenter">Salary Heads</th>
											<th valign="top" rowspan="2" class="alignCenter">Payable Months</th>
										</tr>
										
										<tr>
											<th class="alignCenter">Employee</th>
											<th class="alignCenter">Employer</th>
											<th class="alignCenter">Min Limit</th>
											<th class="alignCenter">Max Limit</th>
										</tr>
						
										<%
										LinkedHashMap hmESISettingsLWF = (LinkedHashMap)request.getAttribute("hmESISettingsLWF");
										if(hmESISettingsLWF==null)hmESISettingsLWF=new LinkedHashMap();
										
										Set setL = hmESISettingsLWF.keySet();
										Iterator itL = setL.iterator();
										while(itL.hasNext()) {
											String strStateId1 = (String)itL.next();
											List alEPFSettingsLWF = (List)hmESISettingsLWF.get(strStateId1);
											if(alEPFSettingsLWF==null)alEPFSettingsLWF = new ArrayList();
										%>
										<tr>
											<th colspan="7" class="alignLeft"><%=strStateId1 %></th>
										</tr>
										
										<%
										for(int i=0; i<alEPFSettingsLWF.size(); i++) {
											List alInnerLWF = (List)alEPFSettingsLWF.get(i);
											if(alInnerLWF==null)alInnerLWF=new ArrayList();
										%>
										<tr>
											<td class="alignCenter"><%=uF.showData((String)alInnerLWF.get(0), "")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInnerLWF.get(1), "0")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInnerLWF.get(2), "0")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInnerLWF.get(3), "0")%></td>
											<td class="alignRight padRight20"><%=uF.showData((String)alInnerLWF.get(4), "0")%></td>
											<td class=""><%=uF.showData((String)alInnerLWF.get(5), "0")%></td>
											<td class=""><%=uF.showData((String)alInnerLWF.get(6), "0")%></td>
										</tr>
											
									<% } } %>
								</table>
							</div>
						</div>
						</div>
					</div>
			</section>
		
	<!-- *************************Other Setting********************** -->		
			<%Navigation navC9 = (Navigation)alListC.get(9);
				LinkedHashMap hmMiscSettings = (LinkedHashMap)request.getAttribute("hmMiscSettings");
				if(hmMiscSettings==null)hmMiscSettings=new LinkedHashMap();%>
  			<section class="col-lg-6 connectedSortable paddingright5">
  				<div class="box box-info">		        
			         <div class="box-header with-border">
		                 <h3><%=navC9.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
						
					<div class="box-body" style="padding: 5px; max-height: 400px; min-height: 400px; overflow-y: auto;">
	               		 <div class="content1">
							<div class="col-md-12 col_no_padding">
							  <div class="pagetitle" style="margin:35px 0px 10px 0px">Miscellaneous Tax Calculations</div>
								<table class="table table-bordered">
								
									<tr>
										<th>FYI</th>
										<th>Flat TDS<br/>(%)</th>
										<th>Service Tax<br/>(%)</th>
										<th>Swachha Bharat Cess<br/>(%)</th>
										<th>Krishi Kalyan Cess<br/>(%)</th>
										<th>CGST<br/>(%)</th>
										<th>SGST<br/>(%)</th>
										<th>Standard Cess<br/>(%)</th>
										<th>Education Cess<br/>(%)</th>
									</tr>
											
									<%
									Set setm = hmMiscSettings.keySet();
									Iterator itm = setm.iterator();
									while(itm.hasNext()){
										String strStateIdm = (String)itm.next();
										//System.out.println("strStateId=="+strStateIdm);
										List alEPFSettingsm = (List)hmMiscSettings.get(strStateIdm);
										if(alEPFSettingsm==null)alEPFSettingsm=new ArrayList();
									%>
										<tr>
											<th colspan="6" class="alignLeft"><%= strStateIdm%></th>
										</tr>
										
										<%
										for(int i=0; i<alEPFSettingsm.size(); i++){
											List alInnerm = (List)alEPFSettingsm.get(i);
											if(alInnerm==null)alInnerm=new ArrayList();
										%>
											<tr>
												<td class="alignCenter"><%=uF.showData((String)alInnerm.get(0), "")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(1), "0")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(2), "0")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(7), "0")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(8), "0")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(9), "0")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(10), "0")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(3), "0")%></td>
													<td class="alignRight padRight20"><%=uF.showData((String)alInnerm.get(4), "0")%></td>
											</tr>
									<% } } %>
								</table>
						
						
							<div class="pagetitle" style="margin:35px 0px 10px 0px">Tax Rebate Under Section 87 A (Less)</div>
								<table class="table table-bordered">
									<tr>
										<th class="alignCenter">FYI</th>
										<th class="alignCenter">Maximum Taxable Income</th>
										<th class="alignCenter">Rebate Amount</th>
									</tr>
											
									<%
									Set setm1 = hmMiscSettings.keySet();
									Iterator itm1 = setm1.iterator();
									while(itm1.hasNext()) {
										String strStateIdm1 = (String)itm1.next();
										List alEPFSettingsm1 = (List)hmMiscSettings.get(strStateIdm1);
										if(alEPFSettingsm1==null) alEPFSettingsm1 = new ArrayList();
									%>
										<tr>
											<th colspan="3" class="alignLeft"><%= strStateIdm1%></th>
										</tr>
										<%
										for(int i=0; i<alEPFSettingsm1.size(); i++){
											List alInnerm1 = (List)alEPFSettingsm1.get(i);
											if(alInnerm1==null)alInnerm1=new ArrayList();
										%>
											<tr>
												<td class="alignCenter"><%=uF.showData((String)alInnerm1.get(0), "")%></td>
												<td class="alignRight padRight20"><%=uF.showData((String)alInnerm1.get(5), "0")%></td>
												<td class="alignRight padRight20"><%=uF.showData((String)alInnerm1.get(6), "0")%></td>
											</tr>
									<% } } %>
								</table>
							</div>
						</div>
					 </div>
					</div>
				</section>	
				
	<!--****************all Statutary ids listed********************  -->	
			
		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">		        
			         <div class="box-header with-border">
			         <%Navigation navC0 = (Navigation)alListC.get(0); %>	
		                <h3><%=navC0.getStrLabel()%></h3>
		                <div class="box-tools pull-right">
		                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
		                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
		                </div>
		            </div>
			
						
				<div class="box-body" style="padding: 5px;max-height: 400px; min-height: 400px; overflow-y: auto;">
	                <div class="content1">
					<div class="col-md-12 col_no_padding">
					 <%Map hmOfficeTypeMap = (Map)request.getAttribute("hmOfficeTypeMap"); 
					 Map hmOfficeLocationMap = (Map)request.getAttribute("hmOfficeLocationMap");
					 Map<String, String> hmEmpCount = (Map<String, String>)request.getAttribute("hmEmpCount");
					 Map<String, String> hmOrgEmpCount = (Map<String, String>) request.getAttribute("hmOrgEmpCount");
					 Map hmOrganistaionMap = (Map)request.getAttribute("hmOrganistaionMap");
				     String userscreen = (String)request.getAttribute("userscreen");
					 String navigationId1 = (String)request.getAttribute("navigationId");
					 String toPage = (String)request.getAttribute("toPage"); 
 					%>
 						<div>
					         <ul class="level_list">
							<% 
								Set setOrganisationMap = hmOrganistaionMap.keySet();
								Iterator itOrg = setOrganisationMap.iterator();
								
								while(itOrg.hasNext()){
									String strOrgId = (String)itOrg.next();
									List alOrg = (List)hmOrganistaionMap.get(strOrgId);
									if(alOrg==null)alOrg=new ArrayList();
										
									List alOfficeType = (List)hmOfficeTypeMap.get(strOrgId);
									if(alOfficeType==null)alOfficeType=new ArrayList();
									%>
										
										<li>
											 <a href="javascript:void(0)" onclick="viewStatutoryIdAndRegInfo('<%=strOrgId%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
 											 <strong><%=alOrg.get(2)%> [<%=alOrg.get(1)%>]</strong>
											<ul>
										<%
											for(int d=0; d<alOfficeType.size(); d+=4){
											String strOfficeTypeId = (String)alOfficeType.get(d);
					
											List alOfficeLocation = (List)hmOfficeLocationMap.get(strOfficeTypeId);
											if(alOfficeLocation==null)alOfficeLocation=new ArrayList();
											//System.out.println("alOfficeLocation in jsp===>"+alOfficeLocation);
										%>
										
											<% for(int g=0; g<alOfficeLocation.size(); g+=20) { %>
												
												<li>
													<a href="javascript:void(0)" onclick="viewStatutoryIdAndRegInfoLocation('<%=alOfficeLocation.get(g)%>', '<%=userscreen %>', '<%=navigationId %>', '<%=toPage %>')"><i class="fa fa-pencil-square-o" aria-hidden="true"></i></a>
 						                            <strong><%=alOfficeLocation.get(g+1)%>, <%=alOfficeLocation.get(g+7)%>, <%=alOfficeLocation.get(g+2)%></strong>
						                             <span class="user_no"> : <%=uF.showData(hmEmpCount.get(alOfficeLocation.get(g)), "0") %> </span>
					                            </li>	
											<% } %>
									<% } %>		
					                 	</ul>
					                 </li>
								<% } %>
							 </ul>
					     </div>	
					 </div>
					 </div>
				  </div>
			  </div>
		 </section>
		
		
		<section  class="col-lg-6 connectedSortable paddingright5">
			<div class="box box-info">
	            <div class="box-header with-border">
	                <h3 class="box-title">TDS Projection Summary</h3>
	                <div class="box-tools pull-right">
	                    <button class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
	                    <button class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
	                </div>
	            </div>
	            <!-- /.box-header -->
	            <div class="box-body" style="padding: 5px; max-height: 300px; min-height: 300px; overflow-y: auto;">
	                <div class="content1">
						<div id="containerForTDSProjection" style="height: 250px; width:95%; margin:10px 0 0 0px;">&nbsp;</div>
					</div>
	            </div>
	            <!-- /.box-body -->
	        </div>
		</section>
	 
	 </div>
  </section>
</div>


<div id="editStatIdRegInfoDIV"></div>
<div id="editStatIdRegInfoLocationDIV"></div>