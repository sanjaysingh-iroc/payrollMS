<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
Map hmOtherDeduction = (Map)request.getAttribute("hmOtherDeduction");
Map hmPaycycle = (Map)request.getAttribute("hmPaycycle");
Map hmPaidOtherDeduction = (Map)request.getAttribute("hmPaidOtherDeduction");
List alOtherDeduction = (List)request.getAttribute("alOtherDeduction");
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
String strCurrency = (String) request.getAttribute("strCurrency"); 
%>  

<div id="printDiv" class="leftbox reportWidth">
    
  <table class="tb_style" style="float:left;width:100%">
  <tr>	
    <th align="center">Paycycle</th>
   	<th nowrap align="center">Approved Amount</th> 
    <th align="center" nowrap>Paid Amount</th>
  
   </tr>
   
   	
<%int i = 0;
	for(i=0; i<alOtherDeduction.size(); i++){
		%>
		<tr>
			<td nowrap align="center"><%=(String)hmPaycycle.get((String)alOtherDeduction.get(i)) %></td>
			<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.showData((String)hmOtherDeduction.get((String)alOtherDeduction.get(i)), "0")%></td>
			<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.showData((String)hmPaidOtherDeduction.get((String)alOtherDeduction.get(i)), "0") %></td>
		</tr>
		<%
	}
%>
    
    <%if(i==0){ %>
    <tr><td  colspan="3"><div class="msg nodata" style="width:96%"><span>No other deduction paid for this employee.</span></div></td></tr>
    <%}%>
    
    </table>
    
   
    
</div>	

