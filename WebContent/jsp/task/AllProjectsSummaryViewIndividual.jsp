<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
		

    
   
 <%
 Map hmDetails = (Map)request.getAttribute("hmDetails");
int val=0;
	for(int a=0;a<hmDetails.size();a++){
		
List alGanntChart = new ArrayList();
	alGanntChart=(List)hmDetails.get(a);

 %>	
 <link rel="stylesheet" href="<%=request.getContextPath() %>/css/gantt/style.css" type="text/css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/css/gantt/jsgantt.css"/>
<script language="javascript" src="<%=request.getContextPath() %>/js/gantt/jsgantt_1.js"></script>
<script>


   var g = new JSGantt.GanttChart('g',document.getElementById('GanttChartDIV<%=a%>'), 'day');

	g.setShowRes(1); // Show/Hide Responsible (0/1)
	g.setShowDur(0); // Show/Hide Duration (0/1)
	g.setShowComp(0); // Show/Hide % Complete(0/1)
   	g.setCaptionType('None');  // Set to Show Caption (None,Caption,Resource,Duration,Complete)
	g.setShowStartDate(0); // Show/Hide Start Date(0/1)
	g.setShowEndDate(0); // Show/Hide End Date(0/1)
	g.setDateInputFormat('dd/mm/yyyy')  // Set format of input dates ('mm/dd/yyyy', 'dd/mm/yyyy', 'yyyy-mm-dd')
	g.setDateDisplayFormat('dd/mm/yyyy') // Set format to display dates ('mm/dd/yyyy', 'dd/mm/yyyy', 'yyyy-mm-dd')
	g.setFormatArr("day","week","month","quarter") // Set format options (up to 4 : "minute","hour","day","week","month","quarter")

  //var gr = new Graphics();

  if( g ) {

	<%
	
	
// 	if(alGanntChart==null)alGanntChart=new ArrayList();
	for(int i=0; i<alGanntChart.size(); i++){
		List alInner = (List)alGanntChart.get(i);
		if(alInner==null)continue;
		%>
		g.AddTaskItem(new JSGantt.TaskItem(<%=alInner.get(0)%>,   '<%=alInner.get(1)%>',     '<%=alInner.get(2)%>', '<%=alInner.get(3)%>', '<%=alInner.get(4)%>', '<%=alInner.get(5)%>', '<%=alInner.get(6)%>', <%=alInner.get(7)%>, '<%=alInner.get(8)%>',     <%=alInner.get(9)%>, <%=alInner.get(10)%>, <%=alInner.get(11)%>, <%=alInner.get(12)%>, <%=alInner.get(13)%>, '<%=alInner.get(14)%>'));
		<%		
	}
	
	%>
	
    g.Draw();	
    g.DrawDependencies();

  }

  else

  {

    alert("not defined");

  }
  
</script>
    	
    	<div style="float:left; margin:20px 0px;max-width:98%">
<!--     		<div class="title"><h3>Gantt Chart</h3></div> -->
    		<%-- <%
				if (alGanntChart != null && alGanntChart.size() == 0) {
			%>
			<div class="msg nodata"><span>No task is
				assigned in this project.</span></div>
			<%
				}else{
			%>
        	<div style="position:relative;max-width:98%;overflow-y: hidden;float:left; position: relative;" class="gantt" id="GanttChartDIV"></div>
        	<%} 
    		
        	%> --%>
        	<div style="position:relative;max-width:98%;overflow-y: hidden;float:left; position: relative;" class="gantt" id="GanttChartDIV<%=a%>"></div>
    	</div>
    	<div class="clr"></div>
	<%
	
	} 
	%>
	
	