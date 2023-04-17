<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<% 
		List<String> alBSCDetails = (List<String>) request.getAttribute("alBSCDetails");
	  	String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
	    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
%>

	<div class="col-md-12 col_no_padding">
      	<div class="box box-none">
               
          	<%
          		UtilityFunctions uF = new UtilityFunctions();
     	 	 	String strBscId = (String)request.getParameter("strBscId");
          		if(uF.parseToInt(strBscId)>0) {
          			String bscName = alBSCDetails.get(0);
          	%>
			<div class="box-header with-border">
				<h3 class="box-title" style="width: 92%;">Bsc Details of <strong><%=bscName %></strong></h3>
  				<%  if(strUserType != null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
					<a href="javascript:void(0)" onclick="editBSC('<%=strBscId %>', 'E')" title="Edit Bsc"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i> </a>
					<a href="javascript:void(0);" title="Delete Bsc" onclick="deleteBSC('<%=strBscId%>', 'D')"><i class="fa fa-trash" aria-hidden="true"></i> </a>
				<% } %>
           </div>
			<div class="box-body" style="padding: 5px; overflow-y: auto;">
				<% if(alBSCDetails!=null) { %>
					<table class="table table_no_border">
						<tr>
							<th class="txtlabel alignRight" style="width: 15%;">BSC Name:</th>
							<td><%=alBSCDetails.get(0) %></td>
						</tr>
						<tr>
							<th class="txtlabel alignRight">Vision:</th>
							<td><%=alBSCDetails.get(1) %></td>
						</tr>
						<tr>
							<th class="txtlabel alignRight">Mission:</th>
							<td><%=alBSCDetails.get(2) %></td>
						</tr>
						
						<%
						Map<String, List<String>> hmBSCPerspectives = (Map<String, List<String>>) request.getAttribute("hmBSCPerspectives");
						if(hmBSCPerspectives==null) hmBSCPerspectives = new HashMap<String, List<String>>();
					   	Iterator<String> it = hmBSCPerspectives.keySet().iterator();
						while(it.hasNext()) {
			               	String perspectiveId = it.next();
			               	List<String> innerList = hmBSCPerspectives.get(perspectiveId);
					%>
						<tr>
							<th class="txtlabel alignRight">Perspective:</th>
							<td><span style="float: left;"> <%=innerList.get(0)%></span> &nbsp;&nbsp; <span style="float: left; margin-left: 10px; background-color: <%=innerList.get(3) %>" title="Perspective Color"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></td>
						</tr>
						<tr>
						<th class="txtlabel alignRight">Weightage(%):</th>
							<td><%=innerList.get(1) %></td>
						</tr>
						<tr>
						<th class="txtlabel alignRight">Description:</th>
							<td><%=uF.showData(innerList.get(2), "") %>
							</td>
						</tr>
					<% } %>
			
					</table>
				<% } %>
				
				
			
			</div> 
			<% } else { %>
				<div class="nodata msg">No data available.</div>
			<% } %>
		</div>  
              
	</div>
	    

	<script type="text/javascript" charset="utf-8">

	
	function deleteBSC(bscId, strOperation) {
		if(confirm('Are you sure, you want to delete this Bsc?')) {
			$.ajax({ 
				type : 'POST',
				url: 'GoalKRABsc.action?strBscId='+bscId+'&operation='+strOperation,
				cache : false,
				success: function(result){
					//alert(11);
					getBSCView('BscView','BSC','','','','','','');
				},
				error: function(result){
					getBSCView('BscView','BSC','','','','','','');
				}
			});
		}
	}
	
	
 	function editBSC(bscId, strOperation) {
	 	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Edit BSC');
		$("#modalInfo").show();
		$(".modal-dialog").width(200);
		$(".modal-dialog").height(300);
		//$('.modal-body').height(300);
		$.ajax({
			type: 'POST',
			url: 'GoalKRABsc.action?strBscId='+bscId+'&operation='+strOperation,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
 	}
 
 </script>	






<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<% 
    Map<String, String> hmBscName = (Map<String, String>)request.getAttribute("BscNames");
	if(hmBscName == null) hmBscName = new HashMap<String, String>();
	  String strUserType=(String) session.getAttribute(IConstants.USERTYPE);
	    String strUserTypeId=(String) session.getAttribute(IConstants.USERTYPEID);
	    String strSessionEmpId=(String) session.getAttribute(IConstants.EMPID);
	    String strBaseUserType=(String) session.getAttribute(IConstants.BASEUSERTYPE);
	    String strBaseUserTypeId=(String) session.getAttribute(IConstants.BASEUSERTYPEID);
%>

<script src="scripts/D3/amcharts/amcharts.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/serial.js" type="text/javascript"></script> 
<script src="scripts/D3/amcharts/pie.js" type="text/javascript"></script>
<script src="scripts/D3/amcharts/themes/light.js" type="text/javascript"></script>
<link href="js/jvectormap/jquery-jvectormap-1.2.2.css" rel='stylesheet' />

	<script type="text/javascript">
	var bscs = [];
	</script>
	
 		<div class="col-md-12 col_no_padding">
        	<div class="box box-none">
                 
               	<%
               		UtilityFunctions uF = new UtilityFunctions();
          	 	 	String strBscId = (String)request.getParameter("strBscId");
               		String bscName = hmBscName.get(strBscId);
               		if(uF.parseToInt(strBscId)>0) {
               	%>
                <div class="box-header with-border">
                <div >
                  	<h3 class="box-title" style="width: 92%;">Bsc Details of <strong><%=bscName %></strong></h3>
	   				<script>
	   					bscs.push({name: '<%=bscName%>'});
	   				</script>
	   				
	   				<%  if(strUserType != null && (strUserType.equals(IConstants.ADMIN) || strUserType.equals(IConstants.HRMANAGER))) { %>
						<a href="javascript:void(0)" onclick="editBSC('<%=strBscId %>', 'E')" title="Edit Bsc"> <i class="fa fa-pencil-square-o" aria-hidden="true"></i> </a>
						<a href="javascript:void(0);" title="Delete Bsc" onclick="deleteBSC('<%=strBscId%>', 'D')"><i class="fa fa-trash" aria-hidden="true"></i> </a>
	                <% } else { %>
						<a href="javascript:void(0)" onclick="alert('Employee has already updated the Goal. You can not delete this Bsc.');"><i class="fa fa-trash" aria-hidden="true"></i> </a>
					<% } %>
	   			</div>	
	            </div>
            	   <div class="box-body" style="padding: 5px; overflow-y: auto;">
	                    <div class="content1" style="padding: 5px;">
							<div id="chartTargetDonutdiv" style="width:100%; height:300px;"></div>
							<script>
					            var chart5;
					        	var legend5;
					       	 var chartData5 = [<%=request.getAttribute("sbTotalBscs")%>];
					            AmCharts.ready(function () {
					                // PIE CHART
					                chart5 = new AmCharts.AmPieChart(AmCharts.themes.light);
			
					                // title of the chart
					               // chart.addTitle("Collections", 14);
					                chart5.allLabels=[{
				                        "text": "Total",
				                        "align": "center",
				                        "bold": true,
				                        "y": 100
				                    },{
				                        "text": "BSC",
				                        "align": "center",
				                        "bold": true,
				                        "size": 16,
				                        "y": 130
				                    }];
					                
					                chart5.theme="none";
					                chart5.dataProvider = chartData5;
					                chart5.titleField = "BSCs";
					                chart5.valueField = "targetAmt";
					                chart5.sequencedAnimation = true;
					              	chart5.startEffect = "elastic";
					                chart5.innerRadius = "60%";
					                chart5.radius= "42%",
					                chart5.startDuration = 2;
					                chart5.labelRadius = -100; 
					                chart5.labelText = "";
					                chart5.balloonText = "[[title]]<br><span style='font-size:14px'><b>[[value]]</b> ([[percents]]%)</span>";
					 
					                // LEGEND
					                legend5 = new AmCharts.AmLegend();
					                legend5.align = "center";
					                legend5.markerType = "circle";
					                chart5.addLegend(legend5);
					                
					                // WRITE
					                chart5.write("chartTargetDonutdiv");
					            });
					        </script>
					</div>
				</div> 
				<% } else { %>
					<div class="nodata msg">No data available.</div>
				<% } %>
			</div>  
		              
			</div>
	    

	<script type="text/javascript" charset="utf-8">

	
	function deleteBSC(bscId, strOperation) {
		if(confirm('Are you sure, you want to delete this Bsc?')) {
			$.ajax({ 
				type : 'POST',
				url: 'GoalKRABsc.action?strBscId='+bscId+'&operation='+strOperation,
				cache : false,
				success: function(result){
					//alert(11);
					getBSCView('BscView','BSC','','','','','','');
				},
				error: function(result){
					getBSCView('BscView','BSC','','','','','','');
				}
			});
		}
	}
	
	
 	function editBSC(bscId, strOperation) {
	 	var dialogEdit = '.modal-body';
		$(dialogEdit).empty();
		$(dialogEdit).html('<div id="the_div"><div id="ajaxLoadImage"></div></div>');
		$('.modal-title').html('Edit BSC');
		$("#modalInfo").show();
		$(".modal-dialog").width(200);
		$(".modal-dialog").height(300);
		//$('.modal-body').height(300);
		$.ajax({
			type: 'POST',
			url: 'GoalKRABsc.action?strBscId='+bscId+'&operation='+strOperation,
			cache : false,
			success : function(data) {
				$(dialogEdit).html(data);
			}
		});
 	}
 
 </script>	


</html>

 --%>