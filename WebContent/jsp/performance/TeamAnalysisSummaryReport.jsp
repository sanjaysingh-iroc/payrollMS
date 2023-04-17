
<%@page import="com.konnect.jpms.util.IConstants"%>
<%@page import="com.konnect.jpms.util.UtilityFunctions"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>

<style>
 
#greenbox {
height: 18px;
background-color:#00FF00; /* the critical component */
}
#redbox {
height: 18px;
background-color:#FF0000; /* the critical component */
}
#yellowbox {
height: 18px;
background-color:#FFFF00; /* the critical component */
}
#outbox {
/* color: #FFF;
font-size: 80px;
text-align: center; */
height: 18px;
width: 100%;
background-color:#D8D8D8; /* the critical component */
/*border-top: 1px solid #99B4FF;
border-left: 1px solid #99B4FF;
border-right: 1px solid #99B4FF;
border-bottom: 1px solid #99B4FF;*/
}

.emps {
text-align:center;
font-size: 26px;
color: #3F82BF;/* none repeat scroll 0 0 #3F82BF */
font-family: digital;
font-weight: bold;
}

/* .anaAttrib {
text-align:center;
font-size: 18px;
font-family: digital;
font-weight: bold;
} */
.anaAttrib1 {
/* text-align:center; */
font-size: 14px;
font-family: digital;
color: #3F82BF;
font-weight: bold;
}
</style>



	<%
	String strSessionUserType = (String)session.getAttribute(IConstants.USERTYPE);
		int cnt=0;
		UtilityFunctions uF=new UtilityFunctions();
		String empCount = (String) request.getAttribute("empCount");
		List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
		Map<String, List<String>> checkselect = (Map<String, List<String>>) request.getAttribute("checkselect");
		Map<String, String> hmAnalysisSummaryMap = (Map<String, String>) request.getAttribute("hmAnalysisSummaryMap");
		%>
		<table>
			<tr>
				<td class="alignRight"><strong>
				<%if(strSessionUserType.equals(IConstants.MANAGER)) { %>
					Team:
				<% } else { %>
					Employees:
				<% } %>
				</strong>
				</td>
				<td class="emps"><%=uF.showData(empCount, "0") %></td>
			</tr>
			
			<tr>
				<td>&nbsp;</td>
				<td>&nbsp;</td>
			</tr>
			
			<tr>
				<td><strong>Analysis</strong>
				</td>
				<td style="text-align:center;">&nbsp;</td>
			</tr>
			<%
			//System.out.println("elementouterList ---> "+elementouterList);
				for (int i = 0; i < elementouterList.size(); i++) {
						List<String> innerList = elementouterList.get(i);
						List<String> check = null;
						if (checkselect != null) {
							check = checkselect.get(innerList.get(0));
						}
						cnt++;
			%>
			<tr>
				<td class="alignRight"><%=innerList.get(1)%>:
				</td>
				<td width="80px" class="emps"><%=hmAnalysisSummaryMap != null ? uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmAnalysisSummaryMap.get(innerList.get(0)))) : "0.00" %></td>
			</tr>
			<%
				}
			%>
		</table>
		<% String totAverage = (String)request.getAttribute("totAverage");
			double dbltotAverage = uF.parseToDouble(totAverage);
			double totProgressAvg = dbltotAverage / cnt;
			String twoDeciTotProgressAvg =  uF.formatIntoTwoDecimalWithOutComma(totProgressAvg);
			//System.out.println("totProgressAvg ========>" +totProgressAvg);
		%>
		
		<div style="padding:10px;min-height:100px">
		
		<div style="width: 100%;">
			<div id="outbox">
			<%if(uF.parseToDouble(twoDeciTotProgressAvg) < 33.33){ %>
			<div id="redbox" style="width: <%=twoDeciTotProgressAvg%>%;"></div>
			<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 33.33 && uF.parseToDouble(twoDeciTotProgressAvg) < 66.67){ %>
			<div id="yellowbox" style="width: <%=twoDeciTotProgressAvg%>%;"></div>
			<%}else if(uF.parseToDouble(twoDeciTotProgressAvg) >= 66.67){ %>
			<div id="greenbox" style="width: <%=twoDeciTotProgressAvg%>%;"></div>
			<%} %>
			</div>
			<div class="anaAttrib1"><span style="float: left;">0</span>
			<span style="margin-left:75px;"><%=twoDeciTotProgressAvg%></span>
			<span style="float: right;">100</span></div>
			<!-- <p style="font-weight: bold;">&nbsp;</p><div style="height:20px;width:95%;" id="c_progressbar_1"></div> -->
			<%-- <div class="anaAttrib1">0&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<%=twoDeciTotProgressAvg %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;100</div> --%>
			<span style="color: #808080;">Slow</span>
			<span style="margin-left:50px; color: #808080;">Steady</span>
			<span style="float: right; color: #808080;">Momentum</span>
			
		</div>
		</div>
					
					<%-- <script type="text/javascript">
					$(function() {
					    $( "#c_progressbar_1").progressbar({
					      value: <%=uF.formatIntoTwoDecimalWithOutComma(totProgressAvg*10) %>
					    
					    });
					  });
					</script>
 --%>