<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags"%>
<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%String strUserType = (String)session.getAttribute(IConstants.USERTYPE); %>


<script type="text/javascript">


function generateReportExcel(){
	window.location="ExportExcelReport.action";
}


</script>


	<script type="text/javascript" charset="utf-8">
				$(document).ready(function () {
					
						$('#lt').dataTable({ bJQueryUI: true, 
							  								
							"sPaginationType": "full_numbers",
							"aaSorting": [],
							"sDom": '<"H"lTf>rt<"F"ip>',
							oTableTools: { "sSwfPath": "<%=request.getContextPath()%>/media/copy_cvs_xls_pdf.swf",
							aButtons: [
									"csv", "xls", {
										sExtends: "pdf",
										sPdfOrientation: "landscape"
										//sPdfMessage: "Your custom message would go here."
	 								}, "print" 
								]
							}
						});
				});
				
	</script>

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Medical/LTA Report" name="title"/>
</jsp:include>


	


    <div id="printDiv" class="leftbox reportWidth">
    
<s:form name="frm_from" action="MedicalPolicyReport" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
				
			<s:select theme="simple" name="f_org" listKey="orgId" 
                         listValue="orgName" 
                        headerKey="-1" headerValue="All Organisation" 
                         list="orgList" key=""  onchange="window.location='MedicalPolicyReport.action?f_org='+this.value"  cssStyle="width:200px;"/>
						
			<s:select name="f_strWLocation" listKey="wLocationId" 
						listValue="wLocationName" 
						headerKey="-1" headerValue="All WorkLocation" 
						list="wLocationList" key=""  cssStyle="width:200px;"/>
						
			 <s:select name="f_department" list="departmentList" listKey="deptId"  
    			listValue="deptName" headerKey="0" headerValue="All Departments"
    			
    			></s:select> 
    			
			<s:select theme="simple" name="f_level" listKey="levelId" 
	             listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             list="levelList" key="" /> 
	            
	          <s:select list="#{'215':'LTA', '216':'Medical'}" name="salaryHead"   />
	             
	            
	          <s:submit value="Submit" cssClass="input_button"></s:submit>
				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

		</div>

		</s:form>
 <%List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
String salaryHead=(String)request.getAttribute("salaryHead");

	 if(salaryHead!=null && salaryHead.equals("216")){
		alInnerExport.add(new DataStyle("Medical Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	}else{
		alInnerExport.add(new DataStyle("LTA Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	}
	alInnerExport.add(new DataStyle("Sr.No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	alInnerExport.add(new DataStyle("Emp Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Full Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Opening Balance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Paid Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Paid Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Closing Balance",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	reportListExport.add(alInnerExport);  %>
    
	    <table class="display" id="lt">
				<thead>
					<tr> 
					<th style="text-align: left;">Sr. No.</th>
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Full Name</th>
						<th style="text-align: left;">Opening Balance</th>
						<th style="text-align: left;">Paid Date</th>
						<th style="text-align: left;">Paid Amount</th>
						<th style="text-align: left;">Closing Balance</th>
						
					</tr>
				</thead>
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) { %>
				 <% java.util.List<String> cinnerlist = couterlist.get(i); 
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(i+1+"",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

					alInnerExport.add(new DataStyle(cinnerlist.get(2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(3),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(4),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(5),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(6),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(7),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

					reportListExport.add(alInnerExport); %>
				 
					<tr id = <%= cinnerlist.get(0) %> >
					<td><%= i+1 %></td>
						<td><%= cinnerlist.get(2) %></td>
						<td><%= cinnerlist.get(3) %></td>
						<td><%= cinnerlist.get(4) %></td>
						<td><%= cinnerlist.get(5) %></td>
						<td><%= cinnerlist.get(6) %></td>
						<td><%= cinnerlist.get(7) %></td>
						
					</tr>
					<% } %>
				</tbody>
			</table> 
   
     <%session.setAttribute("reportListExport",reportListExport); %>
		   
			

    </div>
<div id="viewEmployeeDiv"></div>
    