


<%@ page import="java.util.*,com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" >

		$(document).ready(function() {
			$('#lt').dataTable({
				bJQueryUI : true,
				"sPaginationType" : "full_numbers",
				"aaSorting" : []
			})
		});
	

</script>
	
	<jsp:include page="../common/SubHeader.jsp">
		<jsp:param value="Candidate Database" name="title" />
	</jsp:include>
	
	
	<div class="leftbox reportWidth">

	

	<s:form name="frm_SearchCandidate" action="SearchCandidate" theme="simple">

	<div class="filter_div">
			<div class="filter_caption">Filter</div>
			
			<s:select theme="simple" name="f_org" listKey="orgId" cssStyle="float:left;margin-right: 10px;width:170px;"
                         listValue="orgName" headerKey="" headerValue="All Organisations"
                         list="organisationList" key="" onchange="frm_SearchCandidate.submit()" size="3"/>
                         
    <s:select theme="simple" name="f_wlocation" listKey="wLocationId" cssStyle="float:left;margin-right: 10px;width:170px;"
                         listValue="wLocationName" headerKey="" headerValue="All Locations"
                         list="workList" key="" size="3" />

 			<s:select list="eduList" theme="simple" listKey="eduName" listValue="eduName" name="strMinEducation" 
 			cssStyle="float:left;margin-right: 10px;width:170px;"
                      headerKey=""  headerValue="All Educations" multiple="true" size="3"></s:select>
					
		     
     		<s:select list="skillsList" theme="simple" listKey="skillsId" listValue="skillsName" name="strSkills" 
     		cssStyle="float:left;margin-right: 10px;width:170px;"
                             headerKey="" headerValue="All Skills" multiple="true" size="3"></s:select>
				
			<s:select theme="simple" name="strExperience" id="strExperience" 
			cssStyle="float:left;margin-right: 10px;width:170px;"
		      list="#{'1':'0 to 1 Year','2':'1 to 2 Years','3':'2 to 5 Years','4':'5 to 10 Years',
		      '5':'10+ Years'}" 
		      headerKey="0" headerValue="All Experience"  size="3"></s:select>

 			<s:submit cssClass="input_button" value="Search" align="center" /> 

		</div>   
	</s:form>
	<%if(request.getAttribute("recruitId")!=null &&  !request.getAttribute("recruitId").equals("")){ %>
	<a href="OpenJobReport.action">Go To Previous Page</a> 
	<%} %>
	<table class="display" id="lt">
		<thead>
			<tr>

				<th style="text-align: center;">Job Code (Designation)</th>
				<th style="text-align: center;">Candidate Name</th>
				<th style="text-align: center;">Education</th>
				<th style="text-align: center;">Experience</th>
				<th style="text-align: center;">Skills</th>
				<th style="text-align: center;">Email</th>
				<th style="text-align: center;">Factsheet</th>

			</tr>
		</thead>
		<tbody>
			<%
			UtilityFunctions uF=new UtilityFunctions();
         
			Map<String,String> hmCandidateEducation =(Map<String,String>)request.getAttribute("hmCandidateEducation");
			Map<String,String> hmCandidateSkill =(Map<String,String>)request.getAttribute("hmCandidateSkill");
			Map<String,String> hmCandidateExperience =(Map<String,String>)request.getAttribute("hmCandidateExperience");
			
				List<List<String>> alCandidateReport = (List<List<String>>)request.getAttribute("alCandidateReport");
                    if(alCandidateReport != null){
				for ( List<String>   alInner:alCandidateReport) {
			%>
			
			<tr>

		<td><%=alInner.get(1) %></td>
		<td><%=alInner.get(2) %></td>
		
		<td><%=uF.showData(hmCandidateEducation.get(alInner.get(0)),"")%></td>
		<td><%=uF.showData(hmCandidateExperience.get(alInner.get(0)),"")%></td>
		<td><%=uF.showData(hmCandidateSkill.get(alInner.get(0)),"")%></td>
			
		<td><%=alInner.get(3) %></td>
		<td><%=alInner.get(4) %></td>

		</tr>
		
		<%} } else if (alCandidateReport == null || alCandidateReport.size() == 0) {%>

			<tr>
				<td colspan="7"><div class="nodata msg">
						<span> No Candidate Available</span>
					</div></td>
			</tr>
			<%}%>
		
		</tbody>
		</table>
		
		
</div>

