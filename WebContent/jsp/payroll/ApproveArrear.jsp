<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.export.DataStyle,com.itextpdf.text.BaseColor,com.itextpdf.text.Element" %>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%> 
<%@page import="java.util.*"%>
<%@taglib uri="/struts-tags" prefix="s"%>
<%@ taglib uri="http://granule.com/tags" prefix="g" %>
<%
 
	UtilityFunctions uF = new UtilityFunctions();	

	Map hmEmpNameMap = (Map)request.getAttribute("hmEmpNameMap");
	Map hmEmpCodeMap = (Map)request.getAttribute("hmEmpCodeMap");
	Map hmSalaryDetails = (Map)request.getAttribute("hmSalaryDetails");
	Map hmEmpSalary = (Map)request.getAttribute("hmEmpSalary");
	Map<String,Map<String, Map<String, String>>> hmTotalSalary = (Map<String,Map<String, Map<String, String>>>)request.getAttribute("hmTotalSalary");
	Map hmEmpPaymentMode = (Map)request.getAttribute("hmEmpPaymentMode");
	Map hmPaymentModeMap = (Map)request.getAttribute("hmPaymentModeMap");
	Map hmLoanPoliciesMap = (Map)request.getAttribute("hmLoanPoliciesMap");
	
	   
	Map hmPresentDays	= (Map)request.getAttribute("hmPresentDays");
	Map hmLeaveDays 	= (Map)request.getAttribute("hmLeaveDays");
	Map hmLeaveTypeDays 	= (Map)request.getAttribute("hmLeaveTypeDays");
	 
	Map<String, String> hmTotalDays = (Map<String, String>)request.getAttribute("hmTotalDays");
	if(hmTotalDays==null)hmTotalDays=new HashMap<String, String>();
	
	List alEmp = (List)request.getAttribute("alEmp");
	List alEmpSalaryDetailsEarning = (List)request.getAttribute("alEmpSalaryDetailsEarning");
	List alEmpSalaryDetailsDeduction = (List)request.getAttribute("alEmpSalaryDetailsDeduction");
	List alEmpIdPayrollG = (List)request.getAttribute("alEmpIdPayrollG");
	Map hmEmpLoan = (Map)request.getAttribute("hmEmpLoan");
	
	List alLoans = (List)request.getAttribute("alLoans");
	
	if(alEmpSalaryDetailsEarning==null)alEmpSalaryDetailsEarning=new ArrayList();
	if(alEmpSalaryDetailsDeduction==null)alEmpSalaryDetailsDeduction=new ArrayList();
	if(alEmpIdPayrollG==null)alEmpIdPayrollG=new ArrayList();
	if(alLoans==null)alLoans=new ArrayList();
	
	Map<String,String> hmGrossNet=(Map<String,String>)request.getAttribute("hmGrossNet");
	if(hmGrossNet==null)hmGrossNet=new HashMap<String,String>();
	
	 String strR = (String)request.getParameter("R");
 	 
	    List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		List<DataStyle> alInnerExport = new ArrayList<DataStyle>();

	
%>

<script type="text/javascript" charset="utf-8">
			$(document).ready( function () {
				$('#lt').dataTable({ bJQueryUI: true, 
					"sPaginationType": "full_numbers",
					"iDisplayLength": 1000,
					"aLengthMenu": [
					                [1, 2, -1],
					                [1, 2, "All"]
					            ],
					"aaSorting": [[0, 'asc']],
					/* "sDom": '<"H"lTf>rt<"F"ip>', */
					"sDom": '<"H"f>rt<"F"ip>',
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

function selectall(x,strEmpId){
	
	 	var  status=x.checked;
		var  arr= document.getElementsByName(strEmpId);
			for(i=0;i<arr.length;i++)
		 	{
		  		arr[i].checked=status;
		 	}
 
}
function exportpdf(){
  window.location="ExportExcelReport.action";
}

</script>



<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Approve Arrear" name="title"/>
</jsp:include>

<div id="printDiv" class="leftbox reportWidth">

<div class="filter_div">
<div class="filter_caption">Filter</div>
		<s:form theme="simple" method="post" name="frm_approve_payroll">
		
		
		<%-- <s:select theme="simple" name="strPaycycleDuration" listKey="paycycleDurationId" 
	             listValue="paycycleDurationName"  cssStyle="width:80px"
	             onchange="document.frm_approve_payroll.submit();"
	             list="paycycleDurationList" key="" />
 
 	             
		<s:select label="Select PayCycle" name="paycycle" listKey="paycycleId"
				listValue="paycycleName"  headerValue="Select Paycycle"
				onchange="document.frm_approve_payroll.submit();"
				list="paycycleList" key="" /> --%>
				
		
		<s:select theme="simple" name="f_org" listKey="orgId" listValue="orgName" cssStyle="width:140px"
				headerKey="" headerValue="All Organisations"
                onchange="document.frm_approve_payroll.submit();" 	
                list="organisationList" key=""  />
				
		<s:select theme="simple" name="wLocation" listKey="wLocationId" cssStyle="width:140px"
	             listValue="wLocationName" headerKey="-1" headerValue="All Locations"  
	             onchange="document.frm_approve_payroll.submit();"
	             list="wLocationList" key="" />
	    
	    <s:select name="f_department" list="departmentList" listKey="deptId" cssStyle="width:140px"
    			listValue="deptName" headerKey="0" headerValue="All Departments"
    			onchange="document.frm_approve_payroll.submit();" 
    			></s:select>
    			         
	    <s:select name="f_service" list="serviceList" listKey="serviceId"  
    			listValue="serviceName" headerKey="0" headerValue="All Services"
    			onchange="document.frm_approve_payroll.submit();" cssStyle="width:100px"
    			></s:select>        
	             
		<s:select theme="simple" name="level" listKey="levelId" 
	             listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             onchange="document.frm_approve_payroll.submit();"
	             list="levelList" key=""  cssStyle="width:100px"/>
	             

		<s:select theme="simple" name="f_paymentMode" listKey="payModeId" 
	             listValue="payModeName" headerKey="-1" headerValue="All Modes" 
	             onchange="document.frm_approve_payroll.submit();"
	             list="paymentModeList" key=""  cssStyle="width:100px"/>
	             
	    <!-- <a href="javascript:void(0)" style="background-image: url('images1/file-xls.png'); background-repeat: no-repeat; float: right; width: 30px; position: static;" title="Export to Excel" class="excel" onclick="exportpdf();">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
				
		</s:form>
</div>		
		
		<%if(hmSalaryDetails!=null && hmSalaryDetails.size()>0){ %>
			<div  style="width:100%; overflow:scroll">
		<%}else{ %>
			<div  style="width:100%;float:left;">
		<%} %>
		
		
		<s:form action="ApproveArrear" method="post">
		<input type="hidden" name="f_paymentMode" value="<%=request.getAttribute("f_paymentMode")%>"/>
		
		<%if(strR==null) {%>
		<input type="hidden" name="approve" value="approve" onclick="return confirm('Are you sure you wish to approve payroll for selected employees?')" />
		<input type="hidden" name="approvePC" value="<%=request.getParameter("paycycle") %>" />
		<input type="hidden" name="strPaycycleDuration" value="<%=request.getParameter("strPaycycleDuration") %>" />
		<input type="hidden" name="wLocation" value="<%=request.getParameter("wLocation") %>" />
		<input type="hidden" name="level" value="<%=request.getParameter("level") %>" />
		<input type="hidden" name="f_service" value="<%=request.getParameter("f_service") %>" />
		<input type="hidden" name="f_department" value="<%=request.getParameter("f_department") %>" />

		<%} %>
		
		
		<%if(hmSalaryDetails!=null && hmSalaryDetails.size()>0){ %>
		<input type="submit" value="Approve" class="input_button" style="margin-bottom: 10px" onclick="return confirm('Are you sure you wish to approve arrear for selected employees?')"/>
		<%} %>
		
		<table id ="lt" cellpadding="2" cellspacing="2" border="0" class="tb_style" width="100%">
		<%if(hmSalaryDetails!=null && hmSalaryDetails.size()>0 && alEmpSalaryDetailsEarning.size()>0){ %>
		<thead>
				<tr>
					<th class="alignCenter" nowrap>Employee Code</th>
					<th class="alignCenter" nowrap>Employee Name</th>
					<th class="alignCenter" nowrap>Approve<br/><input type="checkbox" onclick="selectall(this,'chbox')" checked="checked"/></th>
					<th class="alignLeft" nowrap>Payment Mode</th>
					<th class="alignCenter" nowrap>Total Days</th>
					<!-- <th class="alignCenter" nowrap>Present</th>
					<th class="alignCenter" nowrap>Leaves</th>
					<th class="alignCenter" nowrap>Absent</th> -->
					
					<th class="alignCenter" nowrap>Net Pay</th>
					<th class="alignCenter" nowrap>Gross Pay</th>
					
					<%
						for(int i=0; i<alEmpSalaryDetailsEarning.size(); i++){
					%>
						<th class="alignCenter" nowrap>
						<%=(String)hmSalaryDetails.get((String)alEmpSalaryDetailsEarning.get(i))%>
						<br/>(+)
						</th>
					<%} %>
					
					
					<%
						for(int i=0; i<alEmpSalaryDetailsDeduction.size(); i++){
					 %>
						<th class="alignCenter" nowrap>
							<%=(String)hmSalaryDetails.get((String)alEmpSalaryDetailsDeduction.get(i))%>
							<br/>(-)
						</th>
						<%} %>
						
						
				</tr>
				
				</thead>
			<% } %>

<tbody>  
					<%
						double dblNet=0;
						double dblGross=0;
							
						for(int eid=0; eid<alEmp.size() && alEmpSalaryDetailsEarning.size()>0; eid++){
							String strEmpId = (String)alEmp.get(eid);
							Map<String, Map<String, String>> hmInner = (Map<String, Map<String, String>>)hmTotalSalary.get(strEmpId);
							if(hmInner==null)hmInner = new HashMap<String, Map<String, String>>();
					%>
					
					<tr>
						<td class=" alignLeft" nowrap><%=(String)hmEmpCodeMap.get(strEmpId)%></td>
						<td class=" alignLeft" nowrap><%=(String)hmEmpNameMap.get(strEmpId)%></td>
						
						<td class="alignCenter">
						<%if(!alEmpIdPayrollG.contains(strEmpId)){ 
							if(strR!=null){
						%>
						Pending
						<%
							}else{
							%>
								<input type="checkbox" name="chbox" style="width:10px; height:10px" value="<%=strEmpId%>" checked="checked" />
							<%
							}
						%>
						<%} else{%>Approved
						<%}%>
						</td>
						<%
						%>
						<td class="alignCenter"><input type="hidden" name="paymentMode" value="<%=uF.parseToInt((String)hmEmpPaymentMode.get(strEmpId))%>"><%=uF.showData((String)hmPaymentModeMap.get((String)hmEmpPaymentMode.get(strEmpId)), "N/a")%></td>
						<td class="alignCenter"><%=uF.parseToDouble(hmTotalDays.get(strEmpId))%></td>
						<td class="alignRight" nowrap="nowrap"><%=uF.showData(hmGrossNet.get(strEmpId+"_NET"),"0") %></td>
						<td class="alignRight"><%=uF.showData(hmGrossNet.get(strEmpId+"_GROSS"),"0") %></td>
					
					<%
						for(int i=0; i<alEmpSalaryDetailsEarning.size(); i++){
							Map<String, String> hmArrearInner =(Map<String, String>)hmInner.get((String)alEmpSalaryDetailsEarning.get(i));
							if(hmArrearInner==null)hmArrearInner=new HashMap<String, String>();
							String strAmount = ""+uF.parseToDouble(hmArrearInner.get("AMOUNT"));
					%>
						<td class="alignRight">
							<%=uF.formatIntoTwoDecimal(uF.parseToDouble(strAmount))%>
						</td>
					<%} %>
					
					<%
						for(int i=0; i<alEmpSalaryDetailsDeduction.size(); i++){
							Map<String, String> hmArrearInner =(Map<String, String>)hmInner.get((String)alEmpSalaryDetailsEarning.get(i));
							if(hmArrearInner==null)hmArrearInner=new HashMap<String, String>();
							String strAmount = ""+uF.parseToDouble(hmArrearInner.get("AMOUNT"));
						 %>
						<td class="alignRight">
							<%=uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(strAmount))%>
						</td>
							
						
					<%} %>
					</tr>
					<% }%>
					<%if(alEmp.size()==0 && alEmp.size()==0){ %>
					<tr><td colspan="10" class="msg nodata"><span>No employee found for payroll</span></td></tr>
					<%} %>
					
					
			 		</tbody>
			</table>
		
		
		<%if(hmSalaryDetails!=null && hmSalaryDetails.size()==0){ %>
		<table width="80%">
			<tr>
				<th>No employees found</th>
			</tr>
		</table>
		<%} %>
		
		
			</s:form>
		</div>

</div>	
</div>	