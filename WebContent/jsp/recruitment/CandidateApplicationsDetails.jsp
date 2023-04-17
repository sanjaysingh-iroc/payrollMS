<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script type="text/javascript" src="scripts/customAjax.js"></script>

<% 
UtilityFunctions uF = new UtilityFunctions();
//List<List<String>> candiAppDetailsList = (List<List<String>>)request.getAttribute("candiAppDetailsList"); 
List<List<String>> candiJobCodesList = (List<List<String>>)request.getAttribute("candiJobCodesList");
Map<String, String> hmjobCodeChangeDate = (Map<String, String>) request.getAttribute("hmjobCodeChangeDate");
Map<String, List<List<String>>> hmCandiAppActivityDetail = (Map<String, List<List<String>>>) request.getAttribute("hmCandiAppActivityDetail");
String type = (String) request.getAttribute("type");

%>


<script>
	$(function() {
		$("#jobCode").multiselect({
			noneSelectedText: 'None selected'
		}).multiselectfilter();
		getJobCodeInfo();
		$("#frmAddCandidateApplicationsDetails_submit").click(function(){
			$("#jobCode").prop('required',true);
		});
	});

	function getJobCodeInfo() {
		//alert(value);
		var candidateId = document.getElementById("candidateId").value;
		var recruitid = getSelectedValue("jobCode");
		//alert("recruitid ===>> " + recruitid);
		
		 var action = 'GetJobCodeDetails.action?recruitid=' + recruitid+'&candidateId=' + candidateId;
		 getContent("jobCodeInfoDiv", action);
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
	
	
	/* $("#frmAddCandidateApplicationsDetails").submit(function(event){
  		event.preventDefault();
  		var form_data = $("#frmAddCandidateApplicationsDetails").serialize();
		$.ajax({
			type :'POST',
			url  :'AddCandidateApplicationsDetails.action',
			data :form_data+"&submit=Add",
			cache:true,
			success : function(result) {
				$("#subSubDivResult").html(result);
			}
		});
  	}); */
	
  	
  	<%if(type != null && type.equals("IFrame")) { %>
    $("#frmAddCandidateApplicationsDetails").submit(function(event){
  	  var recruitId = document.getElementById("recruitId").value;
  	  
		event.preventDefault();
		var form_data = $("#frmAddCandidateApplicationsDetails").serialize();
		$.ajax({
			type :'POST',
			url  :'AddCandidateApplicationsDetails.action',
			data :form_data+"&submit=Add", 
			cache:true/* ,
			success : function(result) {
				$("#subSubDivResult").html(result);
			} */
		});
		
		$.ajax({
			url: 'Applications.action?recruitId='+recruitId,
			cache: true,
			success: function(result){
				$("#subSubDivResult").html(result);
	   		}
		});
	});
<% } %>

</script>
 
<div>  
	<% if(candiJobCodesList != null && !candiJobCodesList.isEmpty()) { %>
		<table id="lt" class="table table-bordered" style="width:100%; font-size: 12px;">
		<thead>
		<tr>
		<th style="text-align: center;">Job Code</th>
		<th style="text-align: center;">Status</th>
		<th style="text-align: center;">Date</th>
		<th style="text-align: center;">Rating/Other</th>
		<th style="text-align: center;">Comments</th>
		</tr>
		</thead>
		<tbody>
		
		<% 
		//System.out.println("candiJobCodesList ----------> " +candiJobCodesList);
		for(int i=0; candiJobCodesList != null && !candiJobCodesList.isEmpty() && i < candiJobCodesList.size(); i++){
			List<String> innerList = candiJobCodesList.get(i);
			//System.out.println("hmCandiAppActivityDetail ----------> " +hmCandiAppActivityDetail);
			List<List<String>> activityList = hmCandiAppActivityDetail.get(innerList.get(0));
			int rowSpn = 1;
			for(int a=0; activityList != null && !activityList.isEmpty() && a < activityList.size(); a++) {
				List<String> activityInnerList = activityList.get(a);
				if(!activityInnerList.isEmpty() && activityInnerList.size() >= 4 && !activityInnerList.get(0).equals("")) {
					rowSpn++;
				} 
			}
		%>
		
		<tr>
		<td valign="top" rowspan="<%=rowSpn %>"><%=innerList.get(1) %></td>
		<% if(i>0){ %>
			<td>Job Code Changed</td>
			<td><%=hmjobCodeChangeDate != null ? hmjobCodeChangeDate.get(innerList.get(0)) : "-"%></td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		<% } else {%>

		<% } %>
	</tr>
			
		<% 
			for(int j=0; activityList != null && !activityList.isEmpty() && j < activityList.size(); j++) {
				List<String> activityInnerList = activityList.get(j);
			if(!activityInnerList.isEmpty() && activityInnerList.size() >= 4 && !activityInnerList.get(0).equals("")){
				if(activityInnerList.get(4) != null && activityInnerList.get(4).equals("Yes")){
		%>
			<script type="text/javascript">
           	  $(function() {
				$('#starPrimary_'+'<%=innerList.get(0)+"_"+i+"_"+j%>').raty({
                    readOnly: true,
                    start:	<%=activityInnerList.get(2) %> ,
                    half: true,
                    targetType: 'number'
				 });
                });
          </script>
          <% } %>
			<tr>
				<%-- <td><%=innerList.get(1) %></td> --%>
				<td><%=uF.showData(activityInnerList.get(0), "") %></td>
				<td><%=uF.showData(activityInnerList.get(1),"") %></td>
				<td>
				<%if(activityInnerList.get(4) != null && activityInnerList.get(4).equals("Yes")) { %>
				<div id="starPrimary_<%=innerList.get(0)+"_"+i+"_"+j %>" style="width: 100%;"></div>
				<% } else { %>
				<%=uF.showData(activityInnerList.get(2), "") %>
				<% } %>
				</td>
				<td><%=uF.showData(activityInnerList.get(3), "") %></td>
			</tr>
		<% } } }%>
		</tbody>
		
		</table>
		<% } %>
		
		<% if(candiJobCodesList == null || candiJobCodesList.isEmpty() || candiJobCodesList.size()==0) { %>
		<div class="nodata msg"><span>No data available. </span></div>
		<% } %>
		
		<s:form id="frmAddCandidateApplicationsDetails" name="frmAddCandidateApplicationsDetails" method="POST" theme="simple" target="_parent" action="AddCandidateApplicationsDetails">
			<s:hidden name="candidateId" id="candidateId"/>
			<s:hidden name="recruitId" id="recruitId"/>
			<s:hidden name="type" id="type"/>
			<div style="width: 100%; margin-bottom: 18px; margin-top: 18px;">
			<span>Change the candidate to any job code:<sup>*</sup> &nbsp;&nbsp;</span>
			 <span> <select name="jobCode" id="jobCode" class="validateRequired" multiple="multiple" size="4" onchange="getJobCodeInfo();">
			<%=request.getAttribute("option") %>
			</select> &nbsp;&nbsp; </span>
			<span><s:submit name="submit" value="Add" cssClass="btn btn-primary"></s:submit></span>
			<!-- Started By Dattatray Date:12-10-21 -->
			<%
				System.out.println("ddd = ---"+(String)request.getAttribute("candiFinalStatus"));
				System.out.println("ddd1 = ---"+(String)request.getAttribute("isRejected"));
				if(uF.parseToBoolean((String)request.getAttribute("candiFinalStatus")) && uF.parseToBoolean((String)request.getAttribute("isRejected"))){
			%>
				<span><input type="submit" value="Reject" class="btn btn-primary" name="Reject" onclick="rejectCandidate('-1');"></span><!--  Created By Dattatray Date:08-10-21 -->
			<%
				}
			%>
			<!-- Ended By Dattatray Date:12-10-21  -->
			</div>
			<tr>
			
	</tr>
		</s:form>
		
	<div id="jobCodeInfoDiv"></div>
</div>

<!--  Started By Dattatray Date:08-10-21 -->
<script type="text/javascript">
function rejectCandidate(reject) {
	 
	  $("#frmAddCandidateApplicationsDetails").submit(function(event){
		  event.preventDefault();
		  if(confirm("Are you sure, you want to this candidate?")){
	  	  var recruitId = document.getElementById("recruitId").value;
			var form_data = $("#frmAddCandidateApplicationsDetails").serialize();
			$.ajax({
				type :'POST',
				url  :'AddCandidateApplicationsDetails.action',
				data :form_data+'&rejectStatus='+reject,
				cache:true,
				success: function(result){
					window.location="CandidateReport.action";
		   		}
			});	
		  }	
		}); 
		
		
 }    
</script>
<!--  Ended By Dattatray Date:08-10-21 -->