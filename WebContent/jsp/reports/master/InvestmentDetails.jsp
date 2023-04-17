<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.List"%>
<script src="scripts/customAjax.js"></script>
<%

	UtilityFunctions uF = (UtilityFunctions) request.getAttribute("uF");
	List InvestmentDetails = (List) request.getAttribute("alInvestment");
	Map<String, List<Map<String, String>>> hmSubInvestment = (Map<String, List<Map<String, String>>>) request.getAttribute("hmSubInvestment");
	if (hmSubInvestment == null)hmSubInvestment = new HashMap<String, List<Map<String, String>>>();
	

	List OtherInvestmentDetails = (List) request.getAttribute("alOtherInvestment");
	Map<String, List<Map<String, String>>> hmOtherSubInvestment = (Map<String, List<Map<String, String>>>) request.getAttribute("hmOtherSubInvestment");
	if (hmOtherSubInvestment == null)hmOtherSubInvestment = new HashMap<String, List<Map<String, String>>>();
	boolean isApproveRelease = uF.parseToBoolean((String) request.getAttribute("isApproveRelease"));
%>



<div class="attendance">
<table align="left" width="100%" class="table table-bordered">
 
<tr class="darktable">
   	<td align="center">Under Section</td>
	<td align="center">Section</td>
	<td align="center">Amount</td>
	<td align="center">Amount</td>
	<td align="center">Action</td>
	<td align="center">Documents</td>
	<td align="center">Last Updated on </td>
</tr>	
  
<%
	  	String cssClass = "lighttable";
	List<String> alUnderSection=new ArrayList<String>();
	  	for (int i = 0; InvestmentDetails != null && i < InvestmentDetails.size(); i++) {
	  		List alInvestmentInner = (List) InvestmentDetails.get(i);
	  		List<Map<String, String>> subInvestList = (List<Map<String, String>>) hmSubInvestment.get("" + alInvestmentInner.get(7));
	  		String strUnderSection="";
	  		String strStyle="";
	  		if(i==0){
	  			alUnderSection.add((String)alInvestmentInner.get(6));
				strUnderSection=(String)alInvestmentInner.get(6);
	  		}else if(!alUnderSection.contains(alInvestmentInner.get(6))){
				alUnderSection.add((String)alInvestmentInner.get(6));
				strStyle="style=\"border-top: 2px solid #616161;\"";
				strUnderSection=(String)alInvestmentInner.get(6);
			}
	  		String investmentId=(String)alInvestmentInner.get(10);
	  %>
<tr class="lighttable" <%=((uF.parseToInt((String) alInvestmentInner.get(5)) == -1) ? " style=\"background-color:#ccc\"" : "")%> <%=strStyle%>>
	<td nowrap="nowrap"><%=strUnderSection %></td>
	<td><%=alInvestmentInner.get(0)%></td>
	<td align="right" style="padding-right:10px">&nbsp;</td>
	<td align="right" style="padding-right:10px"><%if(subInvestList==null){%><%=alInvestmentInner.get(1)%><%} %></td>
	<td align="center" nowrap="nowrap">
		<%if(subInvestList==null){ %>
			<div id="myDiv_<%=i%>">
				<%
				if(uF.parseToInt((String)alInvestmentInner.get(8))>0){%>
					<img src="images1/icons/denied.png"/>					
				<%}else  if(uF.parseToBoolean((String)alInvestmentInner.get(9))){%>
					<!-- <img src="images1/icons/approved.png"/> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
					&nbsp;
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else { %>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=investmentId%>'):'')"><img src="images1/icons/hd_cross_16x16.png"/></a>
					<%} %>
				<%}else{%>
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_tick_20x20.png" onclick="alert('Already approved and released Form16.');"/>&nbsp;
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else { %>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to approve this amount?')?getContent('myDiv_<%=i%>', 'UpdateInvestment.action?status=true&investment_id=<%=investmentId%>'):'')">
							<img src="images1/icons/hd_tick_20x20.png"/>
						</a>&nbsp;
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=investmentId%>'):'')">
							<img src="images1/icons/hd_cross_16x16.png"/>
						</a>
					<%} %>
				<%}%>
			</div>
		<%} %>
		</td>
	<td><%if(subInvestList==null){%><%=alInvestmentInner.get(3)%><%} %></td>
	<td><%if(subInvestList==null){%><%=alInvestmentInner.get(4)%><%} %></td>
</tr>	
	<%
		
		for (int j = 0; subInvestList != null && j < subInvestList.size(); j++) {
			Map<String, String> hm = (Map<String, String>) subInvestList.get(j);
			investmentId+=","+hm.get("INVESTMENT_ID");
	%>
	<tr class="lighttable" <%=((uF.parseToInt((String) alInvestmentInner.get(5)) == -1) ? " style=\"background-color:#ccc\"" : "")%>>
		<td>&nbsp;</td>
		<td><%=(j+1)%>. <%=uF.showData(hm.get("SECTION_NAME"), "")%></td>
		<td align="right" style="padding-right: 10px;"><%=uF.showData(hm.get("PAID_AMOUNT"), "")%></td>
		<td align="right" style="padding-right: 10px">&nbsp;</td>
		<td align="center">&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<%
		}
	
		if(subInvestList!=null){
	%>
	<tr class="lighttable" <%=((uF.parseToInt((String) alInvestmentInner.get(5)) == -1) ? " style=\"background-color:#ccc\"" : "")%>>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="right" style="padding-right:10px; border-top: 2px solid;">&nbsp;</td>
		<td align="right" style="padding-right:10px"><%=alInvestmentInner.get(1)%></td>
		<td align="center" nowrap="nowrap">
			<div id="myDiv_<%=i%>">
				<%
				if(uF.parseToInt((String)alInvestmentInner.get(8))>0){%>
					<img src="images1/icons/denied.png"/>					
				<%}else if(uF.parseToBoolean((String)alInvestmentInner.get(9))){%>
					<!-- <img src="images1/icons/approved.png"/>  -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
					&nbsp;
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else { %>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=investmentId%>'):'')"><img src="images1/icons/hd_cross_16x16.png"/></a>
					<%} %>					
				<%}else{%>
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_tick_20x20.png" onclick="alert('Already approved and released Form16.');"/>&nbsp;
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else { %>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to approve this amount?')?getContent('myDiv_<%=i%>', 'UpdateInvestment.action?status=true&investment_id=<%=investmentId%>'):'')">
							<img src="images1/icons/hd_tick_20x20.png"/>
						</a>&nbsp;
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=investmentId%>'):'')">
							<img src="images1/icons/hd_cross_16x16.png"/>
						</a>
					<%} %>
				<%}%>
			</div>
		</td>
		<td><%=alInvestmentInner.get(3)%></td>
		<td><%=alInvestmentInner.get(4)%></td>
	</tr>   
<%}
	   	}
	   %> 
	   
	   <%
	   // other investment
	  	cssClass = "lighttable";
		List<String> alOtherUnderSection=new ArrayList<String>();
	  	for (int i = 0; OtherInvestmentDetails != null && i < OtherInvestmentDetails.size(); i++) {
	  		List alOtherInvestmentInner = (List) OtherInvestmentDetails.get(i);
	  		List<Map<String, String>> subOtherInvestList = (List<Map<String, String>>) hmOtherSubInvestment.get("" + alOtherInvestmentInner.get(7));
	  		String strOtherUnderSection="";
	  		String strOtherStyle="";
	  		if(i==0){
	  			alOtherUnderSection.add((String)alOtherInvestmentInner.get(6));
				strOtherUnderSection=(String)alOtherInvestmentInner.get(6);
	  		}else if(!alOtherUnderSection.contains(alOtherInvestmentInner.get(6))){
				alOtherUnderSection.add((String)alOtherInvestmentInner.get(6));
				strOtherStyle="style=\"border-top: 2px solid #616161;\"";
				strOtherUnderSection=(String)alOtherInvestmentInner.get(6);
			}
	  		String OtherinvestmentId=(String)alOtherInvestmentInner.get(10);
	  %>
<tr class="lighttable" <%=((uF.parseToInt((String) alOtherInvestmentInner.get(5)) == -1) ? " style=\"background-color:#ccc\"" : "")%> <%=strOtherStyle%>>
	<td nowrap="nowrap"><%=strOtherUnderSection %></td>
	<td><%=alOtherInvestmentInner.get(0)%></td>
	<td align="right" style="padding-right:10px">&nbsp;</td>
	<td align="right" style="padding-right:10px"><%if(subOtherInvestList==null){%><%=alOtherInvestmentInner.get(1)%><%} %></td>
	<td align="center" nowrap="nowrap">
		<%if(subOtherInvestList==null){ %>
			<div id="myOtherDiv_<%=i%>">
				<%
				if(uF.parseToInt((String)alOtherInvestmentInner.get(8))>0){%>
					<img src="images1/icons/denied.png">					
				<%}else if(uF.parseToBoolean((String)alOtherInvestmentInner.get(9))){%>
					<!-- <img src="images1/icons/approved.png"> -->
					<i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i>
					&nbsp;
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else { %>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myOtherDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=OtherinvestmentId%>'):'')"><img src="images1/icons/hd_cross_16x16.png"/></a>
					<%} %>
				<%}else{%>
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_tick_20x20.png" onclick="alert('Already approved and released Form16.');"/>&nbsp;
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else {%>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to approve this amount?')?getContent('myOtherDiv_<%=i%>', 'UpdateInvestment.action?status=true&investment_id=<%=OtherinvestmentId%>'):'')">
							<img src="images1/icons/hd_tick_20x20.png"/>
						</a>&nbsp;
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myOtherDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=OtherinvestmentId%>'):'')">
							<img src="images1/icons/hd_cross_16x16.png"/>
						</a>
					<%} %>
				<%}%>
			</div>
		<%} %>
		</td>
	<td><%if(subOtherInvestList==null){%><%=alOtherInvestmentInner.get(3)%><%} %></td>
	<td><%if(subOtherInvestList==null){%><%=alOtherInvestmentInner.get(4)%><%} %></td>
</tr>	
	<%
		
		for (int j = 0; subOtherInvestList != null && j < subOtherInvestList.size(); j++) {
			Map<String, String> hmOther = (Map<String, String>) subOtherInvestList.get(j);
			OtherinvestmentId+=","+hmOther.get("INVESTMENT_ID");
	%>
	<tr class="lighttable" <%=((uF.parseToInt((String) alOtherInvestmentInner.get(5)) == -1) ? " style=\"background-color:#ccc\"" : "")%>>
		<td>&nbsp;</td>
		<td><%=(j+1)%>. <%=uF.showData(hmOther.get("SECTION_NAME"), "")%></td>
		<td align="right" style="padding-right: 10px;"><%=uF.showData(hmOther.get("PAID_AMOUNT"), "")%></td>
		<td align="right" style="padding-right: 10px">&nbsp;</td>
		<td align="center">&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<%
		}
	
		if(subOtherInvestList!=null){
	%>
	<tr class="lighttable" <%=((uF.parseToInt((String) alOtherInvestmentInner.get(5)) == -1) ? " style=\"background-color:#ccc\"" : "")%>>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="right" style="padding-right:10px; border-top: 2px solid;">&nbsp;</td>
		<td align="right" style="padding-right:10px"><%=alOtherInvestmentInner.get(1)%></td>
		<td align="center" nowrap="nowrap">
			<div id="myOtherDiv_<%=i%>">
				<%
				if(uF.parseToInt((String)alOtherInvestmentInner.get(8))>0){%>
					<img src="images1/icons/denied.png">					
				<%}else if(uF.parseToBoolean((String)alOtherInvestmentInner.get(9))){%>
					<!-- <img src="images1/icons/approved.png"> --><i class="fa fa-circle" aria-hidden="true" style="color:#54aa0d"></i></a>
					&nbsp;
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else { %>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myOtherDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=OtherinvestmentId%>'):'')"><img src="images1/icons/hd_cross_16x16.png"/></a>
					<%} %>
				<%}else{%>
					<%if(isApproveRelease){ %>
						<img src="images1/icons/hd_tick_20x20.png" onclick="alert('Already approved and released Form16.');"/>&nbsp;
						<img src="images1/icons/hd_cross_16x16.png" onclick="alert('Already approved and released Form16.');"/>
					<%} else { %>
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to approve this amount?')?getContent('myOtherDiv_<%=i%>', 'UpdateInvestment.action?status=true&investment_id=<%=OtherinvestmentId%>'):'')">
							<img src="images1/icons/hd_tick_20x20.png"/>
						</a>&nbsp;
						<a href="javascript:void(0)" onclick="(confirm('Are you sure you want to disapprove this amount?')?getContent('myOtherDiv_<%=i%>', 'UpdateInvestment.action?status=false&investment_id=<%=OtherinvestmentId%>'):'')">
							<img src="images1/icons/hd_cross_16x16.png"/>
						</a>
					<%} %>
				<%}%>
			</div>
		</td>
		<td><%=alOtherInvestmentInner.get(3)%></td>
		<td><%=alOtherInvestmentInner.get(4)%></td>
	</tr>   
<%}
	   	}
	   %>   

<tr>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td nowrap="nowrap"><b>Total Amount</b></td>
	<td align="right" style="padding-right:10px"><b><%=request.getAttribute("TOTAL_INVESTMENT")%></b></td>
	<td colspan="3">(Declared Amount + Approved Amount)</td>
</tr>


</table>
</div>