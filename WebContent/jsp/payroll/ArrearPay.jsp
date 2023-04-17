<%@page import="com.itextpdf.text.BaseColor"%>
<%@page import="com.itextpdf.text.Element"%>
<%@page import="com.konnect.jpms.export.DataStyle"%>
<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
UtilityFunctions uF = new UtilityFunctions();
Map hmEmpMap = (Map)request.getAttribute("hmEmpMap");
Map hmEmpCodeMap = (Map)request.getAttribute("hmEmpCodeMap");
Map hmPayPayroll = (Map)request.getAttribute("hmPayPayroll");
Map hmSalaryDetails = (Map)request.getAttribute("hmSalaryDetails");
Map hmIsApprovedSalary = (Map)request.getAttribute("hmIsApprovedSalary");
if(hmIsApprovedSalary==null)hmIsApprovedSalary = new HashMap();
List alEarnings = (List)request.getAttribute("alEarnings");
List alDeductions = (List)request.getAttribute("alDeductions");
Map hmLoanPoliciesMap = (Map)request.getAttribute("hmLoanPoliciesMap");
Map hmEmpLoan = (Map)request.getAttribute("hmEmpLoan");
List alLoans = (List)request.getAttribute("alLoans");
if(alLoans==null)alLoans=new ArrayList();

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



function donwloadBankStatement(paycycle) {

	var dialogEdit = '#bankstatement';
	dialogEdit = $('<div id="the_div"><div id="ajaxLoadImage"></div></div>').appendTo('body');
	$(dialogEdit).dialog({
		autoOpen : false,
		bgiframe : true,
		resizable : true,
		height : 450,
		width : 650,
		modal : true,
		title : 'Bank Orders',
		open : function() {
			var xhr = $.ajax({
				url : "ViewBankStatements.action?strPaycycle="+paycycle,
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


	function generateSalaryExcel(){
		
		/* var paycycle=document.frm_PayPayroll.paycycle.value;
		var wLocation=document.frm_PayPayroll.wLocation.value;
		var f_department=document.frm_PayPayroll.f_department.value;
		var f_service=document.frm_PayPayroll.f_service.value;
		var level=document.frm_PayPayroll.level.value;
		var url='SalaryPaidExcel.action?paycycle='+paycycle+'&wLocation='+wLocation+'&f_department='+f_department+'&f_service='+f_service+'&level='+level;
		
		window.location = url; */
		
		 window.location="ExportExcelReport.action";
	}
</script>

<jsp:include page="../common/SubHeader.jsp">
	<jsp:param value="Pay Payroll" name="title"/>
</jsp:include> 

  
<div id="printDiv" class="leftbox reportWidth">
    
    
    <s:form name="frm_PayPayroll" action="ArrearPay" theme="simple" method="post">
    
	<input type="hidden" name="approvePC" value="<%=request.getParameter("paycycle") %>" />

<div class="filter_div">
<div class="filter_caption">Filter</div>
 
 		<s:select theme="simple" name="strPaycycleDuration" listKey="paycycleDurationId" 
	             listValue="paycycleDurationName"  cssStyle="width:80px"
	             onchange="document.frm_PayPayroll.submit();"
	             list="paycycleDurationList" key="" /> 
	             
		<s:select name="paycycle" listKey="paycycleId"
					listValue="paycycleName" headerValue="Select Paycycle"					
					list="paycycleList" key=""
					onchange="document.frm_PayPayroll.submit();" 
					/>
		
		<s:select theme="simple" name="f_org" listKey="orgId" listValue="orgName" cssStyle="width:140px"
				headerKey="" headerValue="All Organisations"
                onchange="document.frm_PayPayroll.submit();" 	
                list="organisationList" key=""  />
                		
		<s:select theme="simple" name="wLocation" listKey="wLocationId" cssStyle="width:140px"
	             listValue="wLocationName" headerKey="-1" headerValue="All Locations" 
	             onchange="document.frm_PayPayroll.submit();"
	             list="wLocationList" key="" />
	             
	    <s:select name="f_department" list="departmentList" listKey="deptId" cssStyle="width:140px"
    			listValue="deptName" headerKey="0" headerValue="All Departments"
    			onchange="document.frm_PayPayroll.submit();"
    			></s:select>
    			         
	    <s:select name="f_service" list="serviceList" listKey="serviceId"  
    			listValue="serviceName" headerKey="0" headerValue="All Services"
    			onchange="document.frm_PayPayroll.submit();" cssStyle="width:100px"
    			></s:select>      
	             
		<s:select theme="simple" name="level" listKey="levelId" 
	             listValue="levelCodeName" headerKey="-1" headerValue="All Levels" 
	             onchange="document.frm_PayPayroll.submit();"
	             list="levelList" key="" cssStyle="width:100px"/>
	    
	     <s:select theme="simple" name="f_paymentMode" listKey="payModeId" 
	             listValue="payModeName" headerKey="-1" headerValue="All Modes" 
	             onchange="document.frm_PayPayroll.submit();"
	             list="paymentModeList" key=""  cssStyle="width:100px"/>            
	     
	      <!-- <a onclick="generateSalaryExcel();" href="javascript:void(0)" style="background-image: url('images1/file-xls.png');background-repeat: no-repeat;float: right;" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</a> -->
		
</div>


	
	<%if(alEarnings.size()>0){ %>


<div style="float:left;margin-right:50px">
Choose Bank to pay from: <s:select theme="simple" name="bankAccount" listKey="bankId" 
	             listValue="bankName" 
	             list="bankList" key=""/>
</div>

    <s:submit name="strApprove" cssStyle="margin-top:0px;margin-bottom:10px;float:left" cssClass="input_button" value="PAY" onclick="return confirm('Are you sure you wish to pay for selected employees?')"/>
    
    
    <%-- <a href="javascript:void(0)" onclick="donwloadBankStatement('<%=request.getParameter("paycycle")%>')"><img src="images1/payslip.png" style="float:right" title="Bank Orders"/></a> --%>
    
    <div style="width:100%;min-height:500px;overflow:scroll;float:left;">
    
    <table cellpadding="5" cellspacing="2" class="tb_style" width="100%" id="lt">
    	<thead>
    	<tr>
    		<th class="alignCenter" nowrap="nowrap">Employee Code</th>
    		<th class="alignCenter" nowrap="nowrap">Employee Name</th>
    		<th class="alignCenter" nowrap="nowrap">Approve<br/><input type="checkbox" onclick="selectall(this,'chbxApprove')" checked="checked"/></th>
    		<th class="alignCenter" nowrap="nowrap">Payment Mode</th>
    		<th class="alignCenter" nowrap="nowrap">Net</th>
    		<th class="alignCenter" nowrap="nowrap">Gross</th>
    		<%for(int i=0; i<alEarnings.size(); i++){ 
    		%>
    			<th class="alignCenter" nowrap="nowrap"><%=(String)hmSalaryDetails.get((String)alEarnings.get(i))%> <br/>(+)</th>
    		<%} %>
    	
    		<%for(int i=0; i<alDeductions.size(); i++){ %>
    			<%if(uF.parseToInt((String)alDeductions.get(i))==IConstants.LOAN && hmEmpLoan!=null){ %>
    				<%for(int l=0; l<alLoans.size(); l++){ 
    				%>
    				<th class="alignCenter" nowrap="nowrap"><%=hmLoanPoliciesMap.get((String)alLoans.get(l))%> <br/>(-)</th>
    				<%} %>
    			<%}else{ 
    			%>
    				<th class="alignCenter" nowrap="nowrap"><%=(String)hmSalaryDetails.get((String)alDeductions.get(i))%> <br/>(-)</th>
    			<%}%>
    			
    		<%} %>
    	</tr>
    	<thead>
    	<tbody>
    		
    		<%
    			Set set = hmPayPayroll.keySet();
    			Iterator it = set.iterator();
    			while(it.hasNext()){
    				String strEmpId = (String)it.next();
    				Map hmPayroll = (Map)hmPayPayroll.get(strEmpId);
    				if(hmPayroll==null)hmPayroll=new HashMap();
    				%>
    				
    				
    				<tr>
    					<td class="" nowrap="nowrap"><%=(String)hmEmpCodeMap.get(strEmpId) %></td>
    					<td class="" nowrap="nowrap"><%=(String)hmEmpMap.get(strEmpId) %></td>
    					<td class="alignCenter">
    					
    					<%
    					String paidUnpaid="";
    					if(uF.parseToBoolean((String)hmIsApprovedSalary.get(strEmpId))){ 
    						paidUnpaid="Paid";
    					%>
    						Paid 
    					<%}else{ %>
    						<input type="checkbox" name="chbxApprove" value="<%=strEmpId%>" checked="checked" />
    					<%} 
    					%>
    					</td>
    					
    					<td class="alignCenter" nowrap="nowrap"><%=(String)hmPayroll.get("PAYMENT_MODE")%></td>
    					<td class="alignCenter" nowrap="nowrap"><%=(String)hmPayroll.get("NET")%></td>
    					<td class="alignCenter" nowrap="nowrap"><%=(String)hmPayroll.get("GROSS")%></td>
    					
    					
	    				<%for(int i=0; i<alEarnings.size(); i++){ 
	    				%>
		    			<td class="alignRight" nowrap="nowrap"><%=uF.showData((String)hmPayroll.get((String)alEarnings.get(i)), "0")%> </td>
			    		<%} %>
			    	
			    		<%for(int i=0; i<alDeductions.size(); i++){ %>
			    			<%if(uF.parseToInt((String)alDeductions.get(i))==IConstants.LOAN && hmEmpLoan!=null){ %>
			    				<%Map hmEmpLoanInner = (Map)hmEmpLoan.get(strEmpId);if(hmEmpLoanInner==null)hmEmpLoanInner=new HashMap();%>
			    				<%for(int l=0; l<alLoans.size(); l++){ 
			    				%>
			    					<td class="alignRight" nowrap="nowrap"><%=uF.showData((String)hmEmpLoanInner.get((String)alLoans.get(l)), "0")%> </td>
			    				<%} %>		    			
			    			<%}else{ 
			    			%>
			    			<td class="alignRight" nowrap="nowrap"><%=uF.showData((String)hmPayroll.get((String)alDeductions.get(i)), "0")%> </td>
			    			<%} %>
			    		<%} %>
		    		</tr>
	    			<%
    			}
    		%>
    		
    		</tbody>
    	
    	
    </table>

</div>
    
    <%}else{ %>
    
    <div class="filter"><div class="msg nodata"><span> No data available for the current selection </span></div> </div>
    <%} %>


    
  </s:form>  
    
    
</div>	
<div id="bankstatement"></div>