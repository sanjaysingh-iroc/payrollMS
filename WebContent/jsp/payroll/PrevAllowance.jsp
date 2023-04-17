<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
Map<String, String> hmAllowance = (Map<String, String>)request.getAttribute("hmAllowance");
if(hmAllowance == null) hmAllowance = new HashMap<String, String>();
Map<String, String> hmPaycycle = (Map<String, String>)request.getAttribute("hmPaycycle");
if(hmPaycycle == null) hmPaycycle = new HashMap<String, String>();
Map<String, String> hmPaidAllowance = (Map<String, String>)request.getAttribute("hmPaidAllowance");
if(hmPaidAllowance == null) hmPaidAllowance = new HashMap<String, String>();
List<String> alAllowance = (List<String>)request.getAttribute("alAllowance");
if(alAllowance == null) alAllowance = new ArrayList<String>();

CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();

Map<String, String> hmSalaryHead =(Map<String, String>)request.getAttribute("hmSalaryHead");
if(hmSalaryHead == null) hmSalaryHead = new HashMap<String, String>();
String strCurrency = (String) request.getAttribute("strCurrency"); 
Map<String, String> hmAllowanceStatus =(Map<String, String>)request.getAttribute("hmAllowanceStatus");
if(hmAllowanceStatus == null) hmAllowanceStatus = new HashMap<String, String>();
String isProductionLine = (String)request.getAttribute("isProductionLine");
String isSalaryHeadProdLine = (String)request.getAttribute("isSalaryHeadProdLine");
%>  

<div id="printDiv" class="leftbox reportWidth">
   <p><strong>Salary Head :</strong> <%=uF.showData((String)request.getAttribute("hmSalaryHeadName"),"") %></p>
   <%if(uF.parseToBoolean(isProductionLine) && uF.parseToBoolean(isSalaryHeadProdLine)){ %>
   		<p><strong>Production Line :</strong> <%=uF.showData((String)request.getAttribute("ProductionLineName"),"") %></p>
   <%} %>
 	<table class="table table-bordered" style="float:left;width:100%">
	  	<tr>	 
		    <th align="center">Paycycle</th>
		   	<th nowrap align="center">Approved Amount</th> 
		    <th align="center" nowrap>Paid Amount</th>
	   </tr>
   	
	<%	int i = 0;
		for(i=0; i<alAllowance.size(); i++){
			String strPaidAmount = "0";
			if(uF.parseToBoolean(isProductionLine)){ 
				if(uF.parseToInt(hmAllowance.get(alAllowance.get(i))) == 1){
					strPaidAmount = uF.showData(hmAllowance.get(alAllowance.get(i)), "0");
				} else {
					strPaidAmount = "0";
				}
			} else {
				strPaidAmount = uF.showData(hmPaidAllowance.get(alAllowance.get(i)), "0");
			}
	%>
			<tr>
				<td nowrap align="center"><%=hmPaycycle.get(alAllowance.get(i)) %></td>
				<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.showData(hmAllowance.get(alAllowance.get(i)), "0")%></td>
				<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=strPaidAmount %></td>
			</tr>
	<%
		}
	
		if(i==0){ %>
	    	<tr><td  colspan="3"><div class="msg nodata" style="width:96%"><span>No other Earning paid for this employee.</span></div></td></tr>
	    <%}%>    
    </table>   
</div>	