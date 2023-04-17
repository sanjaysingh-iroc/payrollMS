<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
Map hmOvertime = (Map)request.getAttribute("hmOvertime");
Map hmPaycycle = (Map)request.getAttribute("hmPaycycle");
Map hmPaidOvertime = (Map)request.getAttribute("hmPaidOvertime");
List alOvertime = (List)request.getAttribute("alOvertime");
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
String strCurrency = (String) request.getAttribute("strCurrency"); 
String roundOffCondition = (String)request.getAttribute("roundOffCondition");

%>  

<div id="printDiv" class="leftbox reportWidth">
 <table class="table table-bordered" style="float:left;width:100%">
  <tr>	
    <th align="center">Paycycle</th>
    <!-- <th align="center">Salary Head</th> -->
   	<th nowrap align="center">Approved Amount</th> 
    <th align="center" nowrap>Paid Amount</th>
  
   </tr>
   
   	
<%int i = 0;
	for(i=0; i<alOvertime.size(); i++){
		%>
		<tr>
			<td nowrap align="center"><%=(String)hmPaycycle.get((String)alOvertime.get(i)) %></td>
			<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmOvertime.get((String)alOvertime.get(i))))%></td>
			<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmPaidOvertime.get((String)alOvertime.get(i)))) %></td>
		</tr>
		<%
	}
%>
    
    <%if(i==0){ %>
    <tr><td  colspan="3"><div class="msg nodata" style="width:96%"><span>No other Earning paid for this employee.</span></div></td></tr>
    <%}%>
    
    </table>
    
   
    
</div>	

