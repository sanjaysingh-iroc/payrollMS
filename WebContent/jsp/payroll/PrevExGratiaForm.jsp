<%@page import="java.util.*, com.konnect.jpms.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="/struts-tags" prefix="s"%>


<%
UtilityFunctions uF = new UtilityFunctions();
Map<String, String> hmExGratia = (Map<String, String>)request.getAttribute("hmExGratia");
if(hmExGratia == null) hmExGratia = new HashMap<String, String>();
Map<String, String> hmPaycycle = (Map<String, String>)request.getAttribute("hmPaycycle");
if(hmPaycycle == null) hmPaycycle = new HashMap<String, String>();
Map<String, String> hmPaidExGratia = (Map<String, String>)request.getAttribute("hmPaidExGratia");
if(hmPaidExGratia == null) hmPaidExGratia = new HashMap<String, String>();
List<String> alExGratia = (List<String>)request.getAttribute("alExGratia");
if(alExGratia == null) alExGratia = new ArrayList<String>();
String strCurrency = (String) request.getAttribute("strCurrency"); 

String roundOffCondition = (String)request.getAttribute("roundOffCondition");
%>  


<div id="printDiv" class="leftbox reportWidth">
    
  <table class="table" style="float:left;width:100%">
	  <tr>	
	    <th>Paycycle</th>
	   	<th>Approved Amount</th> 
	    <th>Paid Amount</th>
	  </tr>
	   
	   <%int i = 0;
		for(i=0; i<alExGratia.size(); i++){
		%>
			<tr>
				<td nowrap align="center"><%=(String)hmPaycycle.get((String)alExGratia.get(i)) %></td>
				<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmExGratia.get((String)alExGratia.get(i))))%></td>
				<td align="right" style="padding-right:10px"><%=uF.showData(strCurrency,"")%> <%=uF.getRoundOffValue(uF.parseToInt(roundOffCondition),uF.parseToDouble((String)hmPaidExGratia.get((String)alExGratia.get(i)))) %></td>
			</tr>
		<%
		}
		
		if(i==0){ %>
	    	<tr><td  colspan="3"><div class="msg nodata" style="width:96%"><span>No ex-gratia paid for this employee.</span></div></td></tr>
	    <%}%>    
    </table>
</div>