<%@ page import="java.util.*, com.konnect.jpms.util.*,com.konnect.jpms.select.FillMonth"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<script type="text/javascript">
function generateTDSReport()
{
	var financialYear=document.frm_fromTDS.financialYear.value;
	var strMonth=document.frm_fromTDS.strMonth.value;	
	var f_strWLocation=document.frm_fromTDS.f_strWLocation.value;
	var f_department=document.frm_fromTDS.f_department.value;
	//var f_level=document.frm_fromTDS.f_level.value;
	var f_org=document.frm_fromTDS.f_org.value;
	var f_service=document.frm_fromTDS.f_service.value; 
	
	var url='TDSPdfReports.action?financialYear='+financialYear;
	url+="&strMonth="+strMonth;
	url+="&f_strWLocation="+f_strWLocation+"&f_department="+f_department;
	//url+="&f_level="+f_level+"&f_org="+f_org;
	url+="&f_service="+f_service+"&f_org="+f_org;
	window.location = url;
}

function generateReportExcel(){
	var financialYear=document.frm_fromTDS.financialYear.value;
	var strMonth=document.frm_fromTDS.strMonth.value;	
	var f_strWLocation=document.frm_fromTDS.f_strWLocation.value;
	var f_department=document.frm_fromTDS.f_department.value;
	//var f_level=document.frm_fromTDS.f_level.value;
	var f_org=document.frm_fromTDS.f_org.value;
	var f_service=document.frm_fromTDS.f_service.value; 
	
	var url='TDSExcelReports.action?financialYear='+financialYear;
	url+="&strMonth="+strMonth;
	url+="&f_strWLocation="+f_strWLocation+"&f_department="+f_department;
	//url+="&f_level="+f_level+"&f_org="+f_org;
	url+="&f_service="+f_service+"&f_org="+f_org;
	window.location = url;
}
</script> 
<%
UtilityFunctions uF = new UtilityFunctions();
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);

String  strFinancialYearStart = (String)request.getAttribute("strFinancialYearStart");
String  strFinancialYearEnd = (String)request.getAttribute("strFinancialYearEnd");
String  strYear = (String)request.getAttribute("strYear");
String  strMonth = (String)request.getAttribute("strMonth");

Map hmEmpTDS = (Map)request.getAttribute("hmEmpTDS");
Map hmEmpCode = (Map)request.getAttribute("hmEmpCode");
Map hmEmpName = (Map)request.getAttribute("hmEmpName");

if(strFinancialYearStart!=null && strFinancialYearEnd!=null){
	strFinancialYearStart = uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
	strFinancialYearEnd = uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT, CF.getStrReportDateFormat());
}

String strOrg=(String)request.getAttribute("f_org");
Map<String, String> hmOrg=(Map<String, String>)request.getAttribute("hmOrg");

String strLocation=(String)request.getAttribute("f_strWLocation");
String strDepart=(String)request.getAttribute("f_department");
String strLevel=(String)request.getAttribute("f_level");
String strServices=(String)request.getAttribute("f_service");

Map<String, String> hmWLocation =(Map<String, String>)request.getAttribute("hmWLocation");
Map<String, String> hmDept =(Map<String, String>)request.getAttribute("hmDept");
Map<String, String> hmLevelMap =(Map<String, String>)request.getAttribute("hmLevelMap");
Map<String, String> hmServicesMap =(Map<String, String>)request.getAttribute("hmServicesMap");




%>





<!-- Custom form for adding new records -->

<jsp:include page="../../common/SubHeader.jsp">
	<jsp:param value="TDS Register" name="title"/>
</jsp:include>
   


<div id="printDiv" class="leftbox reportWidth">

		<s:form name="frm_fromTDS" action="TDSReportQuarterly" theme="simple">
		
		<div class="filter_div">
		<div class="filter_caption">Filter</div>
		
			<s:select label="Select Financial Year" name="financialYear" listKey="financialYearId"
						listValue="financialYearName" headerKey="0" 
						onchange="document.frm_fromTDS.submit();"
						list="financialYearList" key="" cssStyle="width:200px;"/>
			<s:select theme="simple" name="f_org" listKey="orgId" headerValue="Organization Wise"
                         listValue="orgName" headerKey="-1"
                         onchange="document.frm_fromTDS.submit();" 
                         list="orgList" key=""  cssStyle="width:200px;"/>			
						
			<s:select name="f_strWLocation" listKey="wLocationId" 
						listValue="wLocationName" 
						onchange="document.frm_fromTDS.submit();"
						list="wLocationList" key="" cssStyle="width:200px;"/>
						
			<s:select name="f_department" listKey="deptId"
						listValue="deptName"
						onchange="document.frm_fromTDS.submit();"
						list="departmentList" key="" cssStyle="width:200px;"/>
						
						
			<%-- <s:select name="f_level" listKey="levelId"
						listValue="levelCodeName"
						onchange="document.frm_fromTDS.submit();"
						list="levelList" key="" cssStyle="width:200px;"/> --%>
			<s:select name="f_service" list="serviceList" listKey="serviceId"  
    			listValue="serviceName"
    			onchange="document.frm_fromTDS.submit();"
    			cssStyle="width:200px;" key=""/> 	
						
			<s:select label="Select Month" name="strQuarterlyMonth" listKey="monthId"
							listValue="monthName" headerKey="1" 
							onchange="document.frm_fromTDS.submit();"
							list="monthsList" key="" cssStyle="width:200px;"/>
				<!-- <a onclick="generateTDSReport()" href="javascript:void(0)" class="pdf" >Pdf </a>
				<a onclick="generateReportExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a>
		 -->	</div>	

		</s:form>

	
		
		<br/>
		<%List<FillMonth> monthsList=(List<FillMonth> )request.getAttribute("monthsList");
			String strQuarterlyMonth=(String )request.getAttribute("strQuarterlyMonth");
		%>
		
		
		<div style="text-align:center;margin:10px" > <h2>Tax Deducted at Source for the month of
		 <%for(FillMonth Fillmonth:monthsList){ 
		 	if(Fillmonth.getMonthId().equals(strQuarterlyMonth)){
		 %>
		 <%=Fillmonth.getMonthName()%> 
		 <%}} %>
		 </h2></div>
		
		<%-- <%if(uF.parseToInt(strOrg)>-1 && uF.parseToInt(strLocation)>-1 && uF.parseToInt(strDepart)>-1 && uF.parseToInt(strLevel)>-1){ %> --%>
		<%if(uF.parseToInt(strOrg)>-1 && uF.parseToInt(strLocation)>-1 && uF.parseToInt(strDepart)>-1 && uF.parseToInt(strServices)>-1){ %>
		<table cellpadding="3" cellspacing="0" width="100%">
			<tr>
				<td width="10%" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Sr. No.</td>
				<td width="10%" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Employee Code</td>
				<td width="40%" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Employee Name</td>
				<td width="20%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Salary</td>
				<td width="20%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Amount</td>
			</tr>
			
			
			<%
				Set set = hmEmpTDS.keySet();
				Iterator it = set.iterator();
				int count=0;
				double dblGrossAmountTotal = 0;
				double dblTDSAmountTotal = 0;
				while(it.hasNext()){
					String strEmpId = (String)it.next();
					Map hmInner = (Map)hmEmpTDS.get(strEmpId);
					if(hmInner==null)hmInner=new HashMap();
					dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
					dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TDS_AMOUNT"));
			%>
			
			<tr>
				<td style="border-bottom:dashed 1px #ccc"><%=++count%></td>
				<td style="border-bottom:dashed 1px #ccc"><%=uF.showData((String)hmEmpCode.get(strEmpId), "")%></td>
				<td style="border-bottom:dashed 1px #ccc"><%=uF.showData((String)hmEmpName.get(strEmpId), "")%></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0")%></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=uF.showData((String)hmInner.get("TDS_AMOUNT"), "0")%></td>
			</tr>
			<%} if(count==0){ %>
			<tr>
				<td colspan="5" align="center" style="border-bottom:dashed 1px #ccc">No Employees found</td>
			</tr>
			<%}else{ %>
			
			<tr>
				<td style="border-bottom:solid 1px #000;border-top:solid 1px #000"></td>
				<td style="border-bottom:solid 1px #000;border-top:solid 1px #000"></td>
				<td style="border-bottom:solid 1px #000;border-top:solid 1px #000"></td>
				<td align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000"><strong><%=uF.formatIntoTwoDecimal(dblGrossAmountTotal)%></strong></td>
				<td align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000"><strong><%=uF.formatIntoTwoDecimal(dblTDSAmountTotal)%></strong></td>
			</tr>
			<%} %>
			
		</table>
		
		<%}else{
			String title="";
			if(uF.parseToInt(strOrg)==-1){
				title ="Organization";
			}else if(uF.parseToInt(strLocation)==-1){
				title ="Location";
			}else if(uF.parseToInt(strDepart)==-1){
				title ="Department";
			}else if(uF.parseToInt(strLevel)==-1){
				title ="Level";
			}else if(uF.parseToInt(strServices)==-1){
				title ="Service";
			}
			
			%>
			<table cellpadding="3" cellspacing="0" width="100%">
			<tr>
				<td width="10%" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Sr. No.</td>
				<td width="50%" style="border-bottom:solid 1px #000;border-top:solid 1px #000"><%=title %></td>
				<td width="20%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Salary</td>
				<td width="20%" align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000">Amount</td>
			</tr>
			
			
			<%
				Set set = hmEmpTDS.keySet();
				Iterator it = set.iterator();
				int count=0;
				double dblGrossAmountTotal = 0;
				double dblTDSAmountTotal = 0;
				while(it.hasNext()){
					String strEmpId = (String)it.next();
					Map hmInner = (Map)hmEmpTDS.get(strEmpId);
					if(hmInner==null)hmInner=new HashMap();
					count++;
					
					dblGrossAmountTotal+=uF.parseToDouble((String)hmInner.get("GROSS_AMOUNT"));
					dblTDSAmountTotal+=uF.parseToDouble((String)hmInner.get("TDS_AMOUNT"));
					
					String strName="";
					if(uF.parseToInt(strOrg)==-1){
						strName =uF.showData((String)hmOrg.get(strEmpId), "");
					}else if(uF.parseToInt(strLocation)==-1){
						strName =uF.showData((String)hmWLocation.get(strEmpId), "");
					}else if(uF.parseToInt(strDepart)==-1){
						strName =uF.showData((String)hmDept.get(strEmpId), "");
					}else if(uF.parseToInt(strLevel)==-1){
						strName =uF.showData((String)hmLevelMap.get(strEmpId), "");
					}else if(uF.parseToInt(strServices)==-1){
						strName =uF.showData((String)hmServicesMap.get(strEmpId), "");
					}
					
			%>
			
			<tr>
				<td style="border-bottom:dashed 1px #ccc"><%=count%></td>
				<td style="border-bottom:dashed 1px #ccc"><%=strName %></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=uF.showData((String)hmInner.get("GROSS_AMOUNT"), "0")%></td>
				<td align="right" class="padRight20" style="border-bottom:dashed 1px #ccc"><%=uF.showData((String)hmInner.get("TDS_AMOUNT"), "0")%></td>
			</tr>
			<%} if(count==0){ %>
			<tr>
				<td colspan="5" style="border-bottom:dashed 1px #ccc"><div class="msg nodata"><span>No Data found</span></div></td>
			</tr>
			<%}else{ %>
			
			<tr>
				<td style="border-bottom:solid 1px #000;border-top:solid 1px #000"></td>
				<td style="border-bottom:solid 1px #000;border-top:solid 1px #000"></td>
				<td align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000"><strong><%=uF.formatIntoTwoDecimal(dblGrossAmountTotal)%></strong></td>
				<td align="right" class="padRight20" style="border-bottom:solid 1px #000;border-top:solid 1px #000"><strong><%=uF.formatIntoTwoDecimal(dblTDSAmountTotal)%></strong></td>
			</tr>
			<%} %>
		</table>
		
		<%} %>
		
		
		
		
    </div>
   

<a href="#" class="report_trigger"> Reports </a>
   <div class="report_panel">
		<jsp:include page="../../reports/ReportNavigation.jsp"></jsp:include>
   </div>