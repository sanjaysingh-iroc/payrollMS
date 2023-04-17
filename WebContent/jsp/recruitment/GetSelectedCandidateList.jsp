<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script>

$(document).ready(function() {
	$('#lt').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"aaSorting" : []
	})
});
</script>

<%
		List<List<String>> allCandidateList = (List<List<String>>)request.getAttribute("allCandidateList");
		//List<FillEmployee> selectCandidateIds = (List<FillEmployee>) request.getAttribute("selectCandidateIds");
		//Map<String, String> sltEmpDtCmprHm = (Map<String, String>) request.getAttribute("sltEmpDtCmprHm");
		%>
	<s:hidden name="recruitId" id="recruitId" ></s:hidden>
<div style="float: left; width: 100%;">
		<div style="width: 20%;">
		<a href="javascript:void(0)" onclick="addCandidateMode('<%=request.getAttribute("recruitId") %>')"><input type="button" class="btn btn-primary" value="Add New Candidate"> </a>
	</div>		
	</div>
	
	<div style="float: left; width: 52.5%; font-weight: bold;">New Applications</div>
	<div style="float: left; width: 45%; font-weight: bold;">Shortlisted / Rejected Applications</div>
	
	<div id="empidlist" style="overflow-y: auto; height: 200px; margin-top: 20px;border: 2px solid #F1F1F1; width: 50%;float:left;">

 		<table class="table table-bordered">
				<tr>
					<th nowrap="nowrap" width="27%">Approve | Reject</th>
					<th width="60%" align="center">Name</th>
					<!-- <th width="18%" align="center">Education</th> -->
					<th width="13%" align="center">Compentency</th>
				</tr>
				<%
				UtilityFunctions uF=new UtilityFunctions();
				//Map<String,String> hmWlocation=(Map<String,String>)request.getAttribute("hmWlocation");	
					for(int i=0; allCandidateList !=null && i<allCandidateList.size();i++){
					List<String> innerList = allCandidateList.get(i);
		 		 String empID=innerList.get(0);
		         String empName=innerList.get(1);
		         String education=innerList.get(2);
		         String jobCode=innerList.get(3);
		         String skills=innerList.get(4);
		         String candiExp=innerList.get(5);
		         String strStars = innerList.get(6);
		         String yesno="";
              	 %>
       	<script type="text/javascript">
           	  $(function() {
				$('#starPrimary'+'<%=empID%>').raty({
                    readOnly: true,
                    start:	<%=strStars %> ,
                    half: true,
                    targetType: 'number'
				 });
                });
          </script>
				<tr >
					<td nowrap="nowrap" style="vertical-align: text-top;text-align: center;">
					<input type="checkbox" name="strCandiShortlist" id="strCandiShortlist<%=i%>" 
					onclick="checkAdd(this.checked,this.value,'<s:property value="recruitId"/>','strCandiShortlist<%=i%>');"
					value="<%=empID %>">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="checkbox" name="strCandiReject" id="strCandiReject<%=i%>" 
						onclick="checkAdd1(this.checked,this.value,'<s:property value="recruitId"/>','strCandiReject<%=i%>');"
						value="<%=empID %>">
						
					</td>
					<td>
					<a href="javascript: void(0)" onclick="openCandidateProfilePopup('<%=empID%>','')"><%=empName %></a><br/>
					<%-- <a href="CandidateMyProfile.action?CandID=<%=empID %>"><%=empName %></a> --%>
					<div style="width: 100%; font-size: 11px; margin-top: -2px"> Edu:- <%=uF.showData(education,"Not specified")%><br/></div>
					<div style="width: 100%; font-size: 11px; margin-top: -5px">Skills:- <%=uF.showData(skills,"Not specified")%><br/></div>
					<div style="width: 100%; font-size: 11px; margin-top: -5px">Exp:- <%=uF.showData(candiExp,"Not specified")%></div></td>
					<!-- <td></td> --> 
					<td style="vertical-align: text-top;"><div style="width: 100%; float: left;"> <%=strStars %>/5</div>
					<div id="starPrimary<%=empID%>" style="width: 100%;"></div></td>
	
				</tr>
				<%} if(allCandidateList==null || allCandidateList.isEmpty() || allCandidateList.equals("")){%>
				<tr><td colspan="4">
				<div class="nodata msg" style="width: 95%">
				<span>No Employee within selection</span>
				</div></td></tr>
				<%} %>
			</table>  
			
		</div>
				
		<div id="idEmployeeInfo" style="border: 2px solid rgb(204, 204, 204); float: left; left: 70%; top: 46px;
		width: 46%; overflow-y: auto; padding: 4px; margin-top: 20px; height: 190px; margin-left: 20px;">
		
		<div align="center" style="width:50%;float:left;border-bottom:1px solid black;"><b>Shortlisted </b></div>
		<div align="center" style="width:50%;float:left;border-bottom:1px solid black;"><b>Rejected</b></div>

			<%	List<String> selectCandidateList = (List<String>) request.getAttribute("selectCandidateList");
				List<String> rejectCandidateList = (List<String>) request.getAttribute("rejectCandidateList");
				
				List<String> selectCandidateIds = (List<String>) request.getAttribute("selectCandidateIds");
				List<String> rejectCandidateIds = (List<String>) request.getAttribute("rejectCandidateIds");
			%>
			<table border="0" width="48%" style="float:left">
				<%  if (selectCandidateList != null) {
					for (int i = 0; i < selectCandidateList.size(); i++) {
				%>
				<tr>
					<%-- <td nowrap="nowrap" style="font-weight: bold; vertical-align: text-top;"><%=i + 1%>.&nbsp;</td> --%>
					<td nowrap="nowrap" align="left"><strong><%=i + 1%>.</strong>&nbsp;<%=selectCandidateList.get(i)%>
					<a href="javascript: void(0)" onclick="checkReset('<%=selectCandidateIds.get(i)%>','<s:property value="recruitId"/>');" title="Remove Shortlisted Candidate">
					<img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
					</td>
				</tr>
				<%}
				} else {%>
				<tr>
					<td>
						<div class="nodata msg" style="width: 90%">
							<span>No Candidate shortlisted</span>
						</div>
					</td>
				</tr>
				<%}%>
			</table>
			
			
			<table border="0" width="50%" style="float:left">
			<%
			if (rejectCandidateList != null) {
					for (int i = 0; i < rejectCandidateList.size(); i++) {
				%>
				<tr>
					<%-- <td nowrap="nowrap" style="font-weight: bold; vertical-align: text-top;"><%=i + 1%>.&nbsp;</td> --%>
					<td nowrap="nowrap" align="left"><strong><%=i + 1%>.</strong>&nbsp;<%=rejectCandidateList.get(i)%>
					<a href="javascript: void(0)" onclick="checkReset('<%=rejectCandidateIds.get(i)%>','<s:property value="recruitId"/>');" title="Remove Rejected Candidate">
					<img border="0" style="width: 12px; height: 12px;" src="<%=request.getContextPath()%>/images1/arrow_reset1.png"/></a>
					</td>
				</tr>
				<%}
				} else {%>
				<tr>
					<td>
						<div class="nodata msg" style="width: 90%">
							<span>No Candidate rejected</span>
						</div>
					</td>
				</tr>
				<%}%>
			</table>
	</div>
