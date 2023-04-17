<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%
Map<String, String> hmDesignationDetails = (Map)request.getAttribute("hmDesignationDetails");
if(hmDesignationDetails==null)hmDesignationDetails = new HashMap<String, String>();
%>	
				
<table class="table table-bordered" style="width:100%">

	<tr>	
		<th width="20%" class="alignRight padR" valign="top">Designation</th>
		<td width="80%">
		<%= hmDesignationDetails.get("DESIG_NAME")%>
		<p class="desc"><%= hmDesignationDetails.get("DESIG_DESCRIPTION")%></p>
		</td>
	</tr>
	<tr>	
		<th class="alignRight padR">Grade</th>
		<td><%= hmDesignationDetails.get("GRADE_NAME")%></td>
	</tr>
	<tr>	
		<th class="alignRight padR">Level</th>
		<td><%= hmDesignationDetails.get("LEVEL_NAME")%></td>
	</tr>

	<%-- <tr>	
		<th class="alignRight padR" valign="top">Attributes</th>
		<td><%= hmDesignationDetails.get("ATTRIBUTES")%></td>
	</tr> --%>
	
	<tr>	
		<th class="alignRight padR">Job Description</th>
		<td><%= hmDesignationDetails.get("JOB_DESCRIPTION")%></td>
	</tr>
	
	<tr>	
		<th class="alignRight padR">Profile</th>
		<td><%= hmDesignationDetails.get("JOB_PROFILE")%></td>
	</tr>
	
	<tr>	
		<th class="alignRight padR">Ideal Candidate</th>
		<td><%= hmDesignationDetails.get("IDEAL_CANDIDATE")%></td>
	</tr>	
</table>				