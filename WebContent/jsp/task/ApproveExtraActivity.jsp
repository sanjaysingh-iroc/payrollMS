<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" src="<%=request.getContextPath()%>/scripts/jquery.ui.autocomplete.js"></script>

<script>



function SendProId(id)
{
		
	if(id!=null)
		{
	window.location ='ApproveExtraActivity.action?pro_id='+id ;
		}
} 
	function addProject() {

		var dialogEdit = '#addproject';

		$(dialogEdit).dialog({
			autoOpen : false,
			bgiframe : true,
			resizable : false,
			height : 650,
			width : 800,
			modal : true,
			title : 'Add New Project',
			open : function() {
				var xhr = $.ajax({
					url : "PreAddNewProjectPopup.action",
					cache : false,
					success : function(data) {
						$(dialogEdit).html(data);
					}
				});
				xhr = null;

			},
			overlay : {
				backgroundColor : '#000',
				opacity : 0.5
			}
		});

		$(dialogEdit).dialog('open');
	}
	
	
	$(function() {
		$("#task_date").datepicker({
			dateFormat : 'dd/mm/yy'
		});
	});
	
</script>

  <link href="<%=request.getContextPath() %>/css/autocomplete/jquery-ui.css" rel="stylesheet" type="text/css"/>
  <script src="<%=request.getContextPath() %>/scripts/autocomplete/jquery.min.js"></script>
  <script src="<%=request.getContextPath() %>/scripts/autocomplete/jquery-ui.min.js"></script> 
  
  <script>
  $(document).ready(function() {
    $("input#autocomplete").autocomplete({
    source: [<%=request.getAttribute("sbEmp")%>]
});
    jQuery("#formID").validationEngine();
  });
  </script>



<%
	List<Integer> taskindex = (List<Integer>) request
			.getAttribute("taskindex");
	Map<Integer, List<String>> taskmap = (Map<Integer, List<String>>) request
			.getAttribute("taskmap");
	List<Integer> eidlist = (List<Integer>) request
			.getAttribute("eidlist");
	List<String> enamelist = (List<String>) request
			.getAttribute("enamelist");
	
	UtilityFunctions uF = new UtilityFunctions();
	
%>
<style>
.tb_style tr td {
	padding: 5px;
	border: solid 1px #efefef;
}

.tb_style tr th {
	padding: 5px;
	border: solid 1px #efefef;
	background: #efefef
}
</style>


<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Team Activities" name="title" />
</jsp:include>

<div class="leftbox reportWidth">


	<div style="float: left; width: 98%" class="ui-widget filter_div">
	<div class="filter_caption">Filter</div>
	<s:form action="ApproveExtraActivity" id="formID" method="post" theme="simple">
	Select Date: 
	<s:textfield cssClass="validateRequired" label="Select Task Date" id="task_date" name="task_date" cssStyle="width:100px"></s:textfield>
	Select Name: 
	<s:textfield id="autocomplete" name="autocompleteEmpName" ></s:textfield>
	
	<s:select label="Select Type" name="isBillable" headerKey="" 
						headerValue="Select All" list="isBillableList" 
						key="" onchange="billableTest(this.value);"/>
	
	<s:select label="Select Project" name="pro_id" listKey="projectID" headerKey="" 
						headerValue="Select Project" listValue="projectName" list="projectdetailslist" 
						key=""/> 
	
	<s:submit value="Search" cssClass="input_button"></s:submit>						
							
	</s:form>	 					
					 
	</div>
	
	
	<div style="float: left; width: 100%; margin:10px 0px">
	
<form action="AddExtraActivity.action" method="post">

<table class="tb_style" style="width:50%" cellspacing="0">
	<%	
		Map hmActivities = (Map)request.getAttribute("hmActivities");
		Map hmActivitiesTotal = (Map)request.getAttribute("hmActivitiesTotal");
	
		Set set = hmActivities.keySet();
		Iterator it = set.iterator();
		int i=0;
		while(it.hasNext()){
		String strTaskDate = (String)it.next();
		List alActivitiesList = (List)hmActivities.get(strTaskDate);
		
		
	double totalHrs = uF.parseToDouble((String)request.getAttribute("totalHrs")); 
	%>
	
	
	
	<tr>
		<th>Activities for <%=strTaskDate %></th>
		<th>Time</th>
	</tr>
	
	<%
	
	for(i=0; alActivitiesList!=null && i<alActivitiesList.size(); i++){
		List alInner = (List)alActivitiesList.get(i);
		if(alInner==null)alInner = new ArrayList();
				
	%>
	
	<tr>
		<td><%=alInner.get(0) %></td>
		<td><%=alInner.get(1) %></td>
	</tr>
	
	<%}%>
	<%	 if(i!=0){	%>
	<tr>
		<td align="right"><b>Total</b></td>
		<td><b><%=uF.showData((String)hmActivitiesTotal.get(strTaskDate), "0")%></b></td>
	</tr>
	<%
	}
		}
	%>
	<%
	if(i==0){
		%>
		<tr><td colspan="2"><div class="msg nodata" style="width:96%"><span>No activities found.</span></div></td></tr>
		
		<%
	}
		
	%>
	
	</table>
	
	

	
	 
	</form>
	
	</div>
	
	
</div>




<div id="addproject"></div>