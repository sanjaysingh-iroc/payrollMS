<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">

$(function() {
	$('#lt1').DataTable({
		dom: 'lBfrtip',
        buttons: [
			'copy', 'csv', 'excel', 'pdf', 'print'
        ]
	});
	
    $( "#strStartDate" ).datepicker({format: 'dd/mm/yyyy'});
    $( "#strEndDate" ).datepicker({format: 'dd/mm/yyyy'});
    
}); 

</script>


	<%
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, List<String>> hmEmpDetail = (Map<String, List<String>>)request.getAttribute("hmEmpDetail");
		Map<String, List<List<String>>> hmEmpSalaryData = (Map<String, List<List<String>>>)request.getAttribute("hmEmpSalaryData");
	
	%>

	<section class="content">
          <!-- title row -->
	        <div class="row">

			<div class="col-md-12">
				<div class="box box-body" >
				<a class="fa fa-file-excel-o" style="float: right; margin-right: 30px;" href="EmployeeSalaryHeadDetails.action?strDownload=download">&nbsp;</a>
				<br/>
			<table class="table">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Employee Code</th>
						<th>Employee Name</th>
						<th>Level</th>
						<th>Designation</th>
						<th>Grade</th>
						<th>Salary Head Name</th>
						<th>Amount</th>
						<th>Salary Head Amount Type</th>
						<th>Pay Type</th>
						<th>Is Display</th>
						<th>Is Approved</th>
						<th>Effective Date</th>
						<th>Earning/ Deduction</th>
						<th>Salary Type</th>
					</tr>
				</thead>
				
				<tbody>
				<% Iterator<String> it = hmEmpDetail.keySet().iterator();
					int cnt = 0;
					while(it.hasNext()) {
						String strEmpId = it.next();
						List<String> empDataList = hmEmpDetail.get(strEmpId);
						cnt++;
				%>
					<tr>
						<td><%=cnt %></td>
						<td><%=empDataList.get(1) %></td>
						<td><%=empDataList.get(2) %></td>
						<td><%=empDataList.get(3) %></td>
						<td><%=empDataList.get(4) %></td>
						<td><%=empDataList.get(5) %></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<% List<List<String>> empSalHeadDataList = hmEmpSalaryData.get(strEmpId);
						for(int i=0; empSalHeadDataList != null && i<empSalHeadDataList.size(); i++) {
							List<String> innerList = empSalHeadDataList.get(i);
					%>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td><%=innerList.get(1) %></td>
							<td><%=innerList.get(2) %></td>
							<td><%=innerList.get(3) %></td>
							<td><%=innerList.get(4) %></td>
							<td><%=innerList.get(5) %></td>
							<td><%=innerList.get(6) %></td>
							<td><%=innerList.get(7) %></td>
							<td><%=innerList.get(8) %></td>
							<td><%=innerList.get(9) %></td>
						</tr>
					<% } %>
				<% } %>
				</tbody>
			
			</table>
        </div>
	</div>
	</div>
</section>
	