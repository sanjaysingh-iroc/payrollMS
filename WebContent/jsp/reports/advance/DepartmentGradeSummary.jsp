<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@ page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>

<script type="text/javascript" charset="utf-8">
			
			
function generateReportExcel(){
	window.location="ExportExcelReport.action";
}


</script>
<%
UtilityFunctions uF = new UtilityFunctions();
Map<String,String> empCountMp=(Map<String,String>)request.getAttribute("empCountMp");
Map<String,String> gradeMp=(Map<String,String>)request.getAttribute("gradeMp");

Map<String,String> departMp=(Map<String,String>)request.getAttribute("departMp");
List<String> gradeList = (List<String>)request.getAttribute("gradeList");
List<String> departList = (List<String>)request.getAttribute("gradeList");


%>


<!-- Custom form for adding new records -->

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="Depart/Grade Summary Report" name="title"/>
</jsp:include>
   


<div id="printDiv" class="leftbox reportWidth">


		<s:form name="frm_from" action="DepartmentGradeSummary" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
				
			<s:select theme="simple" name="f_org" listKey="orgId" 
                         listValue="orgName" 
                        headerKey="-1" headerValue="All Organisation" 
                         list="orgList" key=""  onchange="window.location='DepartmentGradeSummary.action?f_org='+this.value"  cssStyle="width:200px;"/>			
						
			<s:select name="f_strWLocation" listKey="wLocationId" 
						listValue="wLocationName" 
						headerKey="-1" headerValue="All WorkLocation" 
						list="wLocationList" key=""  cssStyle="width:200px;"/>
						
			
    			
			<s:select theme="simple" name="level" listKey="levelId" 
	             listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             list="levelList" key="" /> 
	             
	          <s:submit value="Submit" cssClass="input_button"></s:submit>
				<!-- <a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-repeat: no-repeat;float: right;"><i class="fa fa-file-excel-o" aria-hidden="true"></i></a>

		</div>

		</s:form>

		
		
		<br/>
		
		<%
		List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		alInnerExport.add(new DataStyle("Depart/Grade Summary Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	   	alInnerExport.add(new DataStyle("Department/Grade",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
	  
	   
	   	
	   	
		%>
		<div  style="width:100%;overflow:scroll;">
	
		<table cellpadding="2" cellspacing="2" border="0" class="tb_style" width="100%">
<thead>
			<tr>
				<th nowrap="nowrap" class="alignCenter">Department/Grade</th>
				<%for(int i=0;i<gradeList.size();i++){
					
					if(gradeMp.get(gradeList.get(i))!=null){
						alInnerExport.add(new DataStyle(gradeMp.get(gradeList.get(i)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					%>
				<th nowrap="nowrap" class="alignCenter"><%=gradeMp.get(gradeList.get(i)) %></th>
				<%} }
				reportListExport.add(alInnerExport); 
				%>
				
			</tr>		
			</thead>
			<tbody>			
			<%for(int j=0;j<departList.size();j++){ 
				if(departMp.get(departList.get(j))!=null){
					alInnerExport = new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(departMp.get(departList.get(j)),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			%>
			<tr>
				<th nowrap="nowrap" class="alignCenter"><%=departMp.get(departList.get(j)) %></th>
				<%for(int i=0;i<gradeList.size();i++){
					if(gradeMp.get(gradeList.get(i))!=null){
						alInnerExport.add(new DataStyle(uF.showData(empCountMp.get(departList.get(j)+"_"+gradeList.get(i)),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));

					%>
				<th nowrap="nowrap" class="alignCenter"><%=uF.showData(empCountMp.get(departList.get(j)+"_"+gradeList.get(i)),"") %></th>
				<%}} %>
				</tr>
				<%reportListExport.add(alInnerExport); }} %>
			</tbody>     
		</table>
	<%session.setAttribute("reportListExport",reportListExport); %>
		</div>
    </div>
   
<%-- 
<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../../reports/ReportNavigation.jsp"></jsp:include>
   </div> --%>