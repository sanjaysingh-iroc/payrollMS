<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
Map hmIncentives = (Map)request.getAttribute("hmIncentives");
Map hmPaycycle = (Map)request.getAttribute("hmPaycycle");
Map hmPaidIncentives = (Map)request.getAttribute("hmPaidIncentives");
List alIncentives = (List)request.getAttribute("alIncentives");
CommonFunctions CF = (CommonFunctions)session.getAttribute(IConstants.CommonFunctions);
UtilityFunctions uF = new UtilityFunctions();
String strCurrency = (String) request.getAttribute("strCurrency"); 

String roundOffCondition = (String)request.getAttribute("roundOffCondition");
%>  

<div id="printDiv" class="leftbox reportWidth">
      
    <table class="table table-bordered" style="float:left;width:100%"> 
	    <tr>	
		    <th align="center">Paycycle</th>
		   	<th nowrap align="center">Approved Amount</th> 
		   	<th align="center" nowrap>Paid Amount</th>
		</tr>
	    
	<%	int i=0;
		for(i=0; i<alIncentives.size(); i++){
	%>
			<tr>
				<td nowrap align="center"><%=(String)hmPaycycle.get((String)alIncentives.get(i)) %></td>
				<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmIncentives.get((String)alIncentives.get(i))))%></td>
				<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmPaidIncentives.get((String)alIncentives.get(i)))) %></td>
			</tr>
	<%
		}
		if(i==0){ %>
	    	<tr><td  colspan="3"><div class="msg nodata" style="width:96%"><span>No incentives paid for this employee.</span></div></td></tr>
	    <%}%>       
    </table>    
</div>	