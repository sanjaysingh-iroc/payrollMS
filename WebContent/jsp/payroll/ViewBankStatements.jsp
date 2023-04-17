<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<script type="text/javascript">
$(function() {
	$("input[name=chequeDate]").datepicker({dateFormat: 'dd/mm/yy'});
});
</script>

   <%List alOuter = (List)request.getAttribute("alOuter"); %> 
    
    
    
    <table class="table" width="100%">
    	<tr>
    		<th>Sr. No</th>
    		<th>Bank Orders</th>
    	</tr>
    	
    	<%
    	int i=0;
    	for(i=0; alOuter!=null && i<alOuter.size(); i++){
    	List alInner = (List)alOuter.get(i);
    	%>
    	<tr>
    		<td align="center"><%=i+1%></td>
    		<td><%=alInner.get(0)%></td>
    	</tr>
    	<%} if(i==0){%>
    	<tr>
    		<tr><td colspan="2"><div class="msg nodata" style="width:96%" ><span>No statements generated yet.</span></div></td></tr>
    	</tr>
    	<%}%>
    </table>