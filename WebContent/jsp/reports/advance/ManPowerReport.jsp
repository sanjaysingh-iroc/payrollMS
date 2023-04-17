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


function viewEmployee(empId) {
//	alert("panel id"+panelid+"candidateid"+candidateid);

			
			var dialogEdit = '#viewEmployeeDiv';
			
			$(dialogEdit).dialog({
				autoOpen : false,
				bgiframe : true,
				resizable : false,
				height : 600,
				width : 800,
				modal : true,
				title : 'View Employee',
				open : function() {
					var xhr = $.ajax({
						url : "ViewEmployeeDetails.action?empId="+empId ,
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
	<jsp:param value="Man Power Report" name="title"/>
</jsp:include>


	


    <div id="printDiv" class="leftbox reportWidth">
    
<s:form name="frm_from" action="ManPowerReport" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
				
			<s:select theme="simple" name="f_org" listKey="orgId" 
                         listValue="orgName" 
                        headerKey="-1" headerValue="All Organisation" 
                         list="orgList" key=""  onchange="window.location='ManPowerReport.action?f_org='+this.value"  cssStyle="width:200px;"/>
						
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
	            
	             
	            
	          <s:submit value="Submit" cssClass="input_button"></s:submit>
				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

		</div>

		</s:form>
 <%List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
alInnerExport.add(new DataStyle("Man Power Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	alInnerExport.add(new DataStyle("Emp Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Full Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Grade",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Designation",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("BirthDate",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Joining Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Age",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Blood Group",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Level",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Qualification",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	alInnerExport.add(new DataStyle("Experience",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

	reportListExport.add(alInnerExport);  %>
    
	    <table class="display" id="lt">
				<thead>
					<tr> 
						<th style="text-align: left;">Emp Code</th>
						<th style="text-align: left;">Full Name</th>
						<th style="text-align: left;">Grade</th>
						<th style="text-align: left;">Designation</th>
						<th style="text-align: left;">BirthDate</th>
						<th style="text-align: left;">Joining Date</th>
						<th style="text-align: left;">Age</th>
						<th style="text-align: left;">Blood Group</th>
						<th style="text-align: left;">Level</th>
						<th style="text-align: left;">Qualification</th>
						<th style="text-align: left;">Experience</th>
						
					</tr>
				</thead>
				<tbody>
				<% java.util.List<List<String>> couterlist = (java.util.List<List<String>>)request.getAttribute("reportList"); %>
				 <% for (int i=0; i<couterlist.size(); i++) { %>
				 <% java.util.List<String> cinnerlist = couterlist.get(i); 
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(cinnerlist.get(1),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(2),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(3),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(4),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(5),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(6),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(7),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(8),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(9),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(10),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(cinnerlist.get(11),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

					reportListExport.add(alInnerExport); %>
				 
					<tr id = <%= cinnerlist.get(0) %> >
						<td><%= cinnerlist.get(1) %></td>
						<td><a href="javascript:void(0)"onclick="viewEmployee('<%= cinnerlist.get(0) %>')" ><%= cinnerlist.get(2) %></a></td>
						<td><%= cinnerlist.get(3) %></td>
						<td><%= cinnerlist.get(4) %></td>
						<td><%= cinnerlist.get(5) %></td>
						<td><%= cinnerlist.get(6) %></td>
						
						<td><%= cinnerlist.get(7) %></td>
						<td><%= cinnerlist.get(8) %></td>
						<td><%= cinnerlist.get(9) %></td>
						<td><%= cinnerlist.get(10) %></td>
						<td><%= cinnerlist.get(11) %></td>
					</tr>
					<% } %>
				</tbody>
			</table> 
   
     <%session.setAttribute("reportListExport",reportListExport); %>
		   
			

    </div>
<div id="viewEmployeeDiv"></div>
    