<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">

function updateProfile(recruitid) {
	//alert("asdklfjklasdjf "+recruitid);
	var dialogEdit = '#UpdateJobProfile';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>')
			.appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : false,
		height : 650,
		width : 800,
		modal : true,
		title : 'Job Profile',
		open : function() {
			var xhr = $.ajax({
				//url : "ApplyLeavePopUp.action", 
				url : "UpdateJobProfilePopUp.action?recruitID="+recruitid+"&view=otherview",
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

function addcriteria(jobid){
	

}

 function checkingfunction(value){
	 
	 //alert(value+"pressed ");
 }
	
	</script>
	
<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Job Report" name="title" />
</jsp:include>


<div class="leftbox reportWidth">

	
	<table class="display tb_style" >
	<thead>
		<tr>
					
			<th style="text-align: center;">Job Id</th>
			<th style="text-align: center;">Job Description</th>		
			<th style="text-align: center;">Changes</th>
			
			
		</tr>
	</thead>
	
	<tbody>
	
	<% List<List<String>> alopenjobreport=(List)request.getAttribute("job_code_info");
	for(int i=0;alopenjobreport!=null && i<alopenjobreport.size();i++){	
		java.util.List alinner = (java.util.List)alopenjobreport.get(i);
	%>
	<tr>
	<td><a href="javascript:void(0)"
				onclick="updateProfile(<%=alinner.get(0)%>)"><%=alinner.get(1)%></a></td>
	<td><%=alinner.get(2)%></td>	
	<td><a href="javascript:void(0)" name="viewarrpoveclick"
				onclick="checkingfunction(this.name);">View & Approve</a> 
	&nbsp;&nbsp;
	<a href="javascript:void(0)" name="addclick"
				onclick="checkingfunction(this.name)" >Add Criteria/Panel</a> 
					
	&nbsp;&nbsp;
	<a href="javascript:void(0)" name="addclick"
				onclick="checkingfunction(this.name);" >Publish</a> 
	</td>
	</tr>
	<%} %>
	
	
	</tbody>
</table>
	

</div>
