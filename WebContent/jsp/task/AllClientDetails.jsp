<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.List"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<script type="text/javascript" charset="utf-8">
function generateSalaryExcel(){
	window.location="ExportExcelReport.action";
}

jQuery(document).ready(function() {

	jQuery(".content1").hide();
	//toggle the componenet with class msg_body
	jQuery(".heading_dash").click(function() {
		jQuery(this).next(".content1").slideToggle(500);
		$(this).toggleClass("filter_close");
	});
});


</script>


<%
UtilityFunctions uF = new UtilityFunctions();

List<List<String>> reportList=(List<List<String>>)request.getAttribute("reportList"); 
List<List<DataStyle>> reportListExport =(List<List<DataStyle>>)request.getAttribute("reportListExport");
%> 

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="All Client Details" name="title"/>
</jsp:include>

   
<div id="printDiv" class="leftbox reportWidth">
    
    
<div  style="width:100%;">


<display:table name="reportList" cellspacing="1" class="itis" export="true" pagesize="20" id="lt1" requestURI="AllClientDetails.action" width="100%">
	
	<display:setProperty name="export.excel.filename" value="AllClientDetails.xls" />
	<%-- <display:setProperty name="export.xml.filename" value="MonthlyReceiptReport.xml" />
	<display:setProperty name="export.csv.filename" value="MonthlyReceiptReport.csv" /> --%>
	
	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Client Name"><%=((java.util.List) pageContext.getAttribute("lt1")).get(0)%></display:column>
	<display:column style="text-align:left;" valign="top" title="Client Address"><%=((java.util.List) pageContext.getAttribute("lt1")).get(1)%></display:column>	
	<display:column style="text-align:left;" valign="top" title="Client Industry"><%=((java.util.List) pageContext.getAttribute("lt1")).get(2)%></display:column>	
	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Contact Person"><%=((java.util.List) pageContext.getAttribute("lt1")).get(3)%></display:column>	
	<display:column style="text-align:left;" valign="top" nowrap="nowrap" title="Contact Number"><%=((java.util.List) pageContext.getAttribute("lt1")).get(4)%></display:column>	
	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Email Id"><%=((java.util.List) pageContext.getAttribute("lt1")).get(5)%></display:column>	
	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Designation"><%=((java.util.List) pageContext.getAttribute("lt1")).get(6)%></display:column>	
	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Department"><%=((java.util.List) pageContext.getAttribute("lt1")).get(7)%></display:column>	
	<display:column style="text-align:right;" valign="top" nowrap="nowrap" title="Location"><%=((java.util.List) pageContext.getAttribute("lt1")).get(8)%></display:column>	
		
		 <!-- sort="true" -->

</display:table>


		<%-- <table  cellpadding="2" cellspacing="2" border="0" class="tb_style" width="100%">
			<tr>
				<th class="alignCenter" nowrap="nowrap">Receipt No.</th>
				<th class="alignCenter" nowrap="nowrap">Month</th>
				<th class="alignCenter" nowrap="nowrap">Date</th>
				<th class="alignCenter" nowrap="nowrap">Cost Center</th>
				<th class="alignCenter" nowrap="nowrap">Invoice No.</th>
				<th class="alignCenter" nowrap="nowrap">Gross</th>
				<th class="alignCenter" nowrap="nowrap">Professional Fees</th>
				<th class="alignCenter" nowrap="nowrap">OPE</th>
				<th class="alignCenter" nowrap="nowrap">Service Tax Charged</th> 
				<th class="alignCenter" nowrap="nowrap">Cess 2% on S. Tax</th>
				<th class="alignCenter" nowrap="nowrap">Cess 1% on S. Tax</th>
				<th class="alignCenter" nowrap="nowrap">Current Financial Year</th>
				<th class="alignCenter" nowrap="nowrap">Previous Financial Year</th>
				<th class="alignCenter" nowrap="nowrap">Total TDS</th>
				<th class="alignCenter" nowrap="nowrap">Other Deductions</th>
				<th class="alignCenter" nowrap="nowrap">Professional Fees- W/OFF</th> 
				<th class="alignCenter" nowrap="nowrap">OPE- W/OFF</th>
				<th class="alignCenter" nowrap="nowrap">Service Tax Charged- W/OFF</th>
				<th class="alignCenter" nowrap="nowrap">Total W/OFF</th>
				<th class="alignCenter" nowrap="nowrap">TOTAL NET (Cheque Amt)</th>
			</tr>
			
			<%
				int i=0;
				for(;reportList!=null && i<reportList.size();i++){
					List<String> innerList=(List<String>)reportList.get(i);
			%>
			<tr>
				<td class="alignCenter" nowrap="nowrap"><%=innerList.get(0) %></td>
				<td class="alignCenter" nowrap="nowrap"><%=innerList.get(1) %></td>
				<td class="alignCenter" nowrap="nowrap"><%=innerList.get(2) %></td>
				<td class="alignLeft" nowrap="nowrap"><%=innerList.get(3) %></td>
				<td class="alignLeft" nowrap="nowrap"><%=innerList.get(4) %></td>
				<td class="alignLeft" nowrap="nowrap"><%=innerList.get(5) %></td>
				<td class="alignLeft" nowrap="nowrap"><%=innerList.get(6) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(7) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(8) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(9) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(10) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(11) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(12) %></td>
				<td class="alignCenter" nowrap="nowrap"><%=innerList.get(13) %></td>
				<td class="alignCenter" nowrap="nowrap"><%=innerList.get(14) %></td>
				<td class="alignCenter" nowrap="nowrap"><%=innerList.get(15) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(16) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(17) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(18) %></td>
				<td class="alignRight" nowrap="nowrap"><%=innerList.get(19) %></td>
				
			</tr>
			<% } if(i==0) { %>
			<tr> <td nowrap="nowrap" colspan="36"><div class="nodata msg">No Data Found</div></td> </tr>
			<% } %>
		</table>  --%>
    

	</div>
    
    
</div>	

