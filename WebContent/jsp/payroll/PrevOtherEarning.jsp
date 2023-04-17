<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
Map hmOtherEarning = (Map)request.getAttribute("hmOtherEarning");
Map hmPaycycle = (Map)request.getAttribute("hmPaycycle");
Map hmPaidOtherEarning = (Map)request.getAttribute("hmPaidOtherEarning");
List alOtherEarning = (List)request.getAttribute("alOtherEarning");
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();

Map<String, String> hmSalaryHead =(Map<String, String>)request.getAttribute("hmSalaryHead");
String strCurrency = (String) request.getAttribute("strCurrency"); 
String roundOffCondition = (String)request.getAttribute("roundOffCondition");
%>  

<div id="printDiv" class="leftbox reportWidth">
	<p><strong>Salary Head :</strong> <%=request.getAttribute("hmSalaryHeadName")!=null ? request.getAttribute("hmSalaryHeadName") : "" %></p>
	<table class="table table-bordered" style="float:left;width:100%">
	  	<tr>	
		    <th style="text-align: center;">Paycycle</th>
		   	<th style="text-align: center;" nowrap>Approved Amount</th> 
		    <th style="text-align: center;" nowrap>Paid Amount</th>
	   </tr>
   
   	
<%int i = 0;
	for(i=0; i<alOtherEarning.size(); i++){
		%>
		<tr>
			<td style="text-align: center;" nowrap><%=(String)hmPaycycle.get((String)alOtherEarning.get(i)) %></td>
			<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmOtherEarning.get((String)alOtherEarning.get(i))))%></td>
			<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmPaidOtherEarning.get((String)alOtherEarning.get(i)))) %></td>
		</tr>
		<%
	}
%>
    <%if(i==0){ %>
    	<tr><td  colspan="3"><div class="msg nodata" style="width:96%"><span>No other Earning paid for this employee.</span></div></td></tr>
    <%}%>
    
    </table>
</div>	