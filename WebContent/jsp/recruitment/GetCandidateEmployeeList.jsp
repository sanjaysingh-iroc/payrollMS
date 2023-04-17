


<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="com.konnect.jpms.select.FillEmployee"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<script>

$(document).ready(function() {
	$('#lt2').DataTable({
		"order": [],
		"columnDefs": [ {
		      "targets"  : 'no-sort',
		      "orderable": false
		    }],
		'dom': 'lBfrtip',
        'buttons': []
  	});
	/* $('#lt').dataTable({
		bJQueryUI : true,
		"sPaginationType" : "full_numbers",
		"aaSorting" : []
	}) */
});
</script>
	
	<%	List<String> selectEmpIds = (List<String>) request.getAttribute("selectEmpIds"); %>
	
 <s:if test="viewSelection=='list'">	
	
		<table class="table" id="lt2">
			<thead>
				<tr>
					<th width="10%"><input onclick="checkUncheckValueInd();" type="checkbox" name="allEmpInd" id="allEmpInd"></th>
					<th align="center">Employee</th>
					<th align="center">Location</th>
					<!-- <th align="center">Factsheet</th> -->
				</tr>
			</thead>
				<%	
					List<String> listRoundId = (List<String>) request.getAttribute("listRoundId");
					String maxRountId = null;
					if(listRoundId != null){
						maxRountId = listRoundId.get(listRoundId.size()-1);
					}else{
						maxRountId = "1";
					}
				%>
			<tbody>	
				<%
				Map<String, String> sltEmpDtCmprHm = (Map<String, String>) request.getAttribute("sltEmpDtCmprHm");
				List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
				UtilityFunctions uF=new UtilityFunctions();
				Map<String,String> hmWlocation=(Map<String,String>)request.getAttribute("hmWlocation");	
					for(int i=0;empList!=null && i<empList.size();i++){
		
		 		 String empID=((FillEmployee)empList.get(i)).getEmployeeId();
		         String empName=((FillEmployee)empList.get(i)).getEmployeeCode();
		         String yesno="";
		         String a=sltEmpDtCmprHm.get(empID.trim());
		         if(a==null || a.equals("true")){
		        	 yesno = "yes";
		         }else{
		        	 yesno = "no";
		         }
              	 %>
				<tr>
					<td><input onclick="checkSelectEmp(this.checked,this.value,'<%=i %>','<%=maxRountId %>');"
						type="checkbox" name="strTrainerId" id="strTrainerId<%=i%>" value="<%=empID %>" />
					</td>
					<td nowrap="nowrap"><a href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empID %>');"><%=empName %></a></td>
					<td nowrap="nowrap"><%=uF.showData(hmWlocation.get(empID),"")%></td>
					<%-- <td><a class="factsheet" href="javascript: void(0)" onclick="openEmployeeProfilePopup('<%=empID %>');"></a></td> --%>
	
				</tr>
				<%} %>
				<%-- <%
				UtilityFunctions uF=new UtilityFunctions();
				Map<String,String> hmWlocation=(Map<String,String>)request.getAttribute("hmWlocation");	
				
						List<FillEmployee> empList = (List<FillEmployee>) request.getAttribute("empList");
						for (int i = 0; empList != null && i < empList.size(); i++) {
							

							String empID = ((FillEmployee) empList.get(i)).getEmployeeId();
							String empName = ((FillEmployee) empList.get(i))
									.getEmployeeCode();
					%>
				<tr>
					<td><input
						onclick="getContent('idEmployeeInfo', 'GetCandidateEmployeeList.action?chboxStatus='+this.checked+'&selectedEmp='+this.value+'&recruitmentID=<s:property value="recruitmentID"/>')"
						type="checkbox" name="strTrainerId" id="strTrainerId<%=i%>"
						value="<%=empID%>"
						<%if(selectEmpIds != null && selectEmpIds.contains(empID)){ %>
						checked="checked"
						<%} %>
						>
					</td>
					<td><%=empName%></td>
					<td><%=uF.showData(hmWlocation.get(empID),"")%></td>
					<td><a class="factsheet"
						href="MyProfile.action?empId=<%=empID%>"></a>
					</td>
	
				</tr>
				<%
				
						}%> --%>
				
				<%if(empList.size()==0){ %>
				<tr><td colspan="4">
				<div class="nodata msg" style="width: 85%">
				<span>No Employee within selection</span>
				</div></td></tr>
				<%} %>
			</tbody>
		</table>


			</s:if>
			<s:else>
			
			<div align="center" style="border: 1px solid rgb(204, 204, 204);"><b>Round Information</b></div>

			<%
			List<String> selectEmpNameList = (List<String>) request.getAttribute("selectEmpNameList");
				if (selectEmpNameList != null) {
			%>
			<table border="0" class="formcss" width="100%">
				<%
					for (int i = 0; i < selectEmpNameList.size(); i++) {
				%>
				<tr>
					<td nowrap="nowrap" style="font-weight: bold;">Round <%=i + 1%></td>
					<td align="left"><%=selectEmpNameList.get(i)%></td>
				</tr>
				<%}} else {%>
				<tr>
					<td colspan="2">
						<div class="nodata msg" style="width: 85%">
							<span>No Panel Added</span>
						</div>
					</td>
				</tr>
				<%}%>
			</table>
			
			
	</s:else>
	
<script>
	//$("img.lazy").lazyload({ threshold : 200,effect : "fadeIn",failure_limit : 10});
	$("img.lazy").lazyload({event : "sporty", threshold : 200,effect : "fadeIn",failure_limit : 10});

$(window).bind("load", function() {
    var timeout = setTimeout(function() { $("img.lazy").trigger("sporty") }, 1000);
});  
</script>