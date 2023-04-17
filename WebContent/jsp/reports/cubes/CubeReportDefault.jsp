<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>


<script>


	$(function() {
	    $( "#idTest" ).datepicker({dateFormat: 'dd/mm/yy'});
	});

function showCubeReport() {	
	dojo.event.topic.publish("showCubeReport");
}

function uncheckAll()
{
	var field = document.frmCubeReport.cubeReportSubMeasure;
	for (i = 0; i < field.length; i++)
		field[i].checked = false ;
	
	var field = document.frmCubeReport.cubeReportSubMeasureAll;
	for (i = 0; i < field.length; i++)
		field[i].checked = false ;
}
function checkAll(id, count, bool)
{
	if(bool){
		for (i = 0; i < count; i++){
			document.getElementById(id+""+i).checked = true ;
			i++;
		}
		document.getElementById(id+"A").checked = true ;
	}else{
		for (i = 0; i < count; i++){
			document.getElementById(id+""+i).checked = (document.getElementById(id+"A").checked) ;
			i++;
		}	
	}
	
		
}


</script>

<script type="text/javascript">
function collapsing_nav() {
	$("body").addClass("enhanced");
	$("#collapsing-nav > li:first").addClass("selected");
	$("#collapsing-nav > li").not(":first").find("ul").hide();
	$("#collapsing-nav > li span").click(function() {
		if ($(this).parent().find("ul").is(":hidden")) {
			$("#collapsing-nav ul:visible").slideUp("fast");
			$("#collapsing-nav > li").removeClass("selected");
			$(this).parent().addClass("selected");
			$(this).parent().find("ul").slideDown("fast");
		}
	});
}
$(collapsing_nav);
</script>
	
	
	<style type="text/css">
	ul#collapsing-nav li a {
		color: #4b7515;
		text-decoration: none;
	}

	ul#collapsing-nav li a:hover {
		color: #4b7515;
	}

	body.enhanced ul#collapsing-nav span {
		color: #4b7515;
		text-decoration: none;
	}

	body.enhanced ul#collapsing-nav span:hover {
		color: #f00;
		cursor: pointer;
	}

	body.enhanced ul#collapsing-nav li.selected span,
	body.enhanced ul#collapsing-nav li.selected span:hover {
		color: #000;
		cursor: default;
		text-decoration: none;
	}
</style>

<%
List alLeaveType = (List)request.getAttribute("alLeaveType");
List alWLocation = (List)request.getAttribute("alWLocation");
List alLevels = (List)request.getAttribute("alLevels");
List alSalaryHeads = (List)request.getAttribute("alSalaryHeads");


%>

<div class="pagetitle">
	Report Analyser
</div>
				
<div class="leftbox reportWidth">				
	
	<s:form id="frmCubeReport" name="frmCubeReport" theme="simple" action="GenerateCubeReport">
	
	<div style="margin: 0pt 30px 5px 0pt; width: 100%; text-align: right; float: right;">
	<!-- <input type="radio" name="reportType" value="C" style="width:0px;height:0px"> Charts | --> 
	<input type="radio" name="reportType" value="T" style="width:0px;height:0px" checked>Tables
	</div>
	<div style="border:solid 0px #CCCCCC; width:310px;height:auto;margin-bottom:20px; float:left">
				
	<div  class="param1" >
		
				
	<ul id="collapsing-nav">
		<li><span><a href="javascript:void(0)" onclick="uncheckAll();"><input style="width:0px;height:0px" type="radio" name="cubeReportMeasure" value="Time" checked />Time</a></span>
			<ul>
				<li><input style="width:0px;height:0px" type="checkbox" name="cubeReportSubMeasure" value="A" checked/>Actual</li>
				<li><input style="width:0px;height:0px" type="checkbox" name="cubeReportSubMeasure" value="R" checked/>Roster</li>
			</ul>
				
		</li>
		
		<li><span><a href="javascript:void(0)" onclick="uncheckAll();checkAll('Leave_',<%=alLeaveType.size()%>, true);"><input style="width:0px;height:0px" type="radio" name="cubeReportMeasure" value="Leaves" />Leaves</a></span>
			<ul>
				<li><input id="Leave_A" style="width:0px;height:0px" type="checkbox" name="cubeReportSubMeasureAll" onclick="checkAll('Leave_',<%=alLeaveType.size()%>, false);" />All</li>
				<%for(int i=0; i<alLeaveType.size(); i++){ %>
				<li><input id="Leave_<%=i%>" style="width:0px;height:0px" type="checkbox" name="cubeReportSubMeasure" value="<%=(String)alLeaveType.get(i++)%>"/><%=(String)alLeaveType.get(i)%></li>
				<%}%>
			</ul>
				
		</li>
		<li><span><a href="javascript:void(0)"  onclick="uncheckAll();checkAll('Salary_',<%=alSalaryHeads.size()%>, true);" ><input style="width:0px;height:0px" type="radio" name="cubeReportMeasure" value="Salary"/>Salary</a></span>
			<ul>
				<li><input id="Salary_A" style="width:0px;height:0px" type="checkbox" name="cubeReportSubMeasureAll" onclick="checkAll('Salary_',<%=alSalaryHeads.size()%>, false);" />All</li>
				<%for(int i=0; i<alSalaryHeads.size(); i++){ %>
				<li><input id="Salary_<%=i%>" style="width:0px;height:0px" type="checkbox" name="cubeReportSubMeasure" value="<%=(String)alSalaryHeads.get(i++)%>"/><%=(String)alSalaryHeads.get(i)%></li>
				<%}%>
			</ul>
				
		</li>
		
	</ul>
	
	</div>
	
	
	
	<div class="param2" >
		
			
			<table style="width:300px;" cellpadding="5" cellspacing="0">
				<tr style="background:#EFEFEF">
					<td style="color:#4b7515"><b>Parameters</b></td>
					<td></td>
					<td></td>
				</tr>
				<tr>
					<td colspan="2"><strong>Company Driven</strong></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td>Company</td>
					<td><input id="Location_A" style="width:0px;height:0px" type="checkbox" onclick="checkAll('Location_',<%=alWLocation.size()%>);" checked />All</td>
				</tr>
				
				<%for(int i=0; i<alWLocation.size(); i++){ %>
				<tr>
					<td></td>
					<td></td>
					<td><input id="Location_<%=i%>" style="width:0px;height:0px" type="checkbox" name="cubeReportParaC" value="<%=(String)alWLocation.get(i++)%>" checked/><%=(String)alWLocation.get(i)%></td>
				</tr>
				<%}%>
				
				<tr>
					<td colspan="2"><strong>Employee Driven</strong></td>
					<td></td>
				</tr>
				<tr>
					<td></td>
					<td>Levels</td>
					<td><input id="Level_A" style="width:0px;height:0px" type="checkbox" onclick="checkAll('Level_',<%=alLevels.size()%>);" checked />All</td>
				</tr>
				
				<%for(int i=0; i<alLevels.size(); i++){ %>
				<tr>
					<td></td>
					<td></td>
					<td><input id="Level_<%=i%>" style="width:0px;height:0px" type="checkbox" name="cubeReportParaE" value="<%=(String)alLevels.get(i++)%>" checked/><%=(String)alLevels.get(i)%></td>
				</tr>
				<%}%>
				
			</table>
	
	</div>
	
	
	<div style="border:solid 0px #ccc; width:300px;height:auto;margin-bottom:20px; float:left">
			<table>
				<tr>
					<td colspan="3">
					
					<input type="button" class="input_button" onclick="javascript:showCubeReport()" value="Get Report"/>
					
				</tr>
			</table>
	</div>
	
	</div>
	
	</s:form>
	
	<div class="report_genarator" >
	
	<s:url id="cubeReportUrl" action="GenerateCubeReport"  /><sx:div href="%{cubeReportUrl}" listenTopics="showCubeReport" formId="frmCubeReport" showLoadingText="">
	</sx:div>
	
	</div>
	
</div>
